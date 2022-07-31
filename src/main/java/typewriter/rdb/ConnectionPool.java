/*
 * Copyright (C) 2022 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package typewriter.rdb;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.ShardingKey;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import kiss.I;
import kiss.WiseConsumer;
import kiss.WiseSupplier;
import typewriter.api.QueryExecutor;

public class ConnectionPool implements WiseSupplier<Connection> {

    /** The connnection pool manager. */
    private static final Map<String, ConnectionPool> CACHE = new ConcurrentHashMap();

    /** The address. */
    private final String url;

    /** The database kind. */
    private final String kind;

    /** The max size of pooled connections. */
    private final int max;

    /** The min size of pooled connections. */
    private final int min;

    /** The connection timeout. */
    private final long timeout;

    /** The actual connection pool. */
    private final ArrayBlockingQueue<Proxy> idle;

    /** The actual connection pool. */
    private final Set<Proxy> busy;

    /**
     * 
     */
    private ConnectionPool(String url) {
        this.url = url;
        this.kind = url.substring(5, url.indexOf(':', 5));
        this.max = config("typewriter.connection.maxsize", 8);
        this.min = config("typewriter.connection.minsize", 2);
        this.timeout = config("typewriter.connection.timeout", 1000 * 10L);
        this.idle = new ArrayBlockingQueue(max);
        this.busy = ConcurrentHashMap.newKeySet();
    }

    /**
     * Cascade configuration.
     * 
     * @param <V>
     * @param key
     * @param defaultValue
     * @return
     */
    private <V> V config(String key, V defaultValue) {
        return I.env(key + "." + url, I.env(key + "." + kind, I.env(key, defaultValue)));
    }

    /**
     * Get the idled connection.
     * 
     * @return
     */
    @Override
    public Connection call() throws Exception {
        Proxy connection = idle.poll();
        if (connection == null) {
            if (max <= busy.size()) {
                connection = idle.poll(timeout, TimeUnit.MILLISECONDS);
            } else {
                connection = new Proxy();
            }
        }

        connection.processing = true;
        busy.add(connection);

        System.out.println("Retrive " + connection);
        return connection;
    }

    /**
     * Get the connection pool for the specified URL.
     * 
     * @param url
     * @return
     */
    public static ConnectionPool by(String url) {
        if (url == null || !url.startsWith("jdbc:")) {
            throw new Error("Invalid JDBC URL [" + url + "]");
        }
        return CACHE.computeIfAbsent(url, key -> new ConnectionPool(key));
    }

    public static void init(Class<? extends QueryExecutor> kind, WiseConsumer<Connection> initialization) {

    }

    /**
     * Release all resources for the specified backend.
     * 
     * @param kind
     */
    public static void clear(String kind) {
        Iterator<Entry<String, ConnectionPool>> iterator = CACHE.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ConnectionPool> entry = iterator.next();
            if (entry.getKey().startsWith("jdbc:" + kind + ":")) {
                iterator.remove();

                ConnectionPool pool = entry.getValue();
                for (Proxy connection : pool.idle) {
                    try {
                        connection.delegation.close();
                    } catch (SQLException e) {
                        throw I.quiet(e);
                    }
                }
                pool.idle.clear();

                for (Proxy connection : pool.busy) {
                    try {
                        connection.delegation.close();
                    } catch (SQLException e) {
                        throw I.quiet(e);
                    }
                }
                pool.busy.clear();
            }
        }
    }

    /**
     * Connection delegator.
     */
    private class Proxy implements Connection {

        /** The backend. */
        private final Connection delegation;

        /** State. */
        private boolean processing;

        /**
         * @param delegation
         * @throws SQLException
         */
        private Proxy() throws SQLException {
            this.delegation = DriverManager.getConnection(url);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public <T> T unwrap(Class<T> iface) throws SQLException {
            return delegation.unwrap(iface);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isWrapperFor(Class<?> iface) throws SQLException {
            return delegation.isWrapperFor(iface);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Statement createStatement() throws SQLException {
            return delegation.createStatement();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public PreparedStatement prepareStatement(String sql) throws SQLException {
            return delegation.prepareStatement(sql);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public CallableStatement prepareCall(String sql) throws SQLException {
            return delegation.prepareCall(sql);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String nativeSQL(String sql) throws SQLException {
            return delegation.nativeSQL(sql);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setAutoCommit(boolean autoCommit) throws SQLException {
            delegation.setAutoCommit(autoCommit);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean getAutoCommit() throws SQLException {
            return delegation.getAutoCommit();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void commit() throws SQLException {
            delegation.commit();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void rollback() throws SQLException {
            delegation.rollback();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void close() throws SQLException {
            idle.offer(this);
            busy.remove(this);
            processing = false;

            System.out.println("Close connection");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isClosed() throws SQLException {
            return processing;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public DatabaseMetaData getMetaData() throws SQLException {
            return delegation.getMetaData();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setReadOnly(boolean readOnly) throws SQLException {
            delegation.setReadOnly(readOnly);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isReadOnly() throws SQLException {
            return delegation.isReadOnly();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setCatalog(String catalog) throws SQLException {
            delegation.setCatalog(catalog);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getCatalog() throws SQLException {
            return delegation.getCatalog();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setTransactionIsolation(int level) throws SQLException {
            delegation.setTransactionIsolation(level);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int getTransactionIsolation() throws SQLException {
            return delegation.getTransactionIsolation();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public SQLWarning getWarnings() throws SQLException {
            return delegation.getWarnings();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void clearWarnings() throws SQLException {
            delegation.clearWarnings();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
            return delegation.createStatement(resultSetType, resultSetConcurrency);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
            return delegation.prepareStatement(sql, resultSetType, resultSetConcurrency);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
            return delegation.prepareCall(sql, resultSetType, resultSetConcurrency);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Map<String, Class<?>> getTypeMap() throws SQLException {
            return delegation.getTypeMap();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
            delegation.setTypeMap(map);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setHoldability(int holdability) throws SQLException {
            delegation.setHoldability(holdability);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int getHoldability() throws SQLException {
            return delegation.getHoldability();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Savepoint setSavepoint() throws SQLException {
            return delegation.setSavepoint();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Savepoint setSavepoint(String name) throws SQLException {
            return delegation.setSavepoint(name);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void rollback(Savepoint savepoint) throws SQLException {
            delegation.rollback(savepoint);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void releaseSavepoint(Savepoint savepoint) throws SQLException {
            delegation.releaseSavepoint(savepoint);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
            return delegation.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
                throws SQLException {
            return delegation.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
                throws SQLException {
            return delegation.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
            return delegation.prepareStatement(sql, autoGeneratedKeys);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
            return delegation.prepareStatement(sql, columnIndexes);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
            return delegation.prepareStatement(sql, columnNames);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Clob createClob() throws SQLException {
            return delegation.createClob();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Blob createBlob() throws SQLException {
            return delegation.createBlob();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public NClob createNClob() throws SQLException {
            return delegation.createNClob();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public SQLXML createSQLXML() throws SQLException {
            return delegation.createSQLXML();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isValid(int timeout) throws SQLException {
            return delegation.isValid(timeout);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setClientInfo(String name, String value) throws SQLClientInfoException {
            delegation.setClientInfo(name, value);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setClientInfo(Properties properties) throws SQLClientInfoException {
            delegation.setClientInfo(properties);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getClientInfo(String name) throws SQLException {
            return delegation.getClientInfo(name);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Properties getClientInfo() throws SQLException {
            return delegation.getClientInfo();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
            return delegation.createArrayOf(typeName, elements);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
            return delegation.createStruct(typeName, attributes);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setSchema(String schema) throws SQLException {
            delegation.setSchema(schema);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getSchema() throws SQLException {
            return delegation.getSchema();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void abort(Executor executor) throws SQLException {
            delegation.abort(executor);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
            delegation.setNetworkTimeout(executor, milliseconds);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int getNetworkTimeout() throws SQLException {
            return delegation.getNetworkTimeout();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void beginRequest() throws SQLException {
            delegation.beginRequest();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void endRequest() throws SQLException {
            delegation.endRequest();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean setShardingKeyIfValid(ShardingKey shardingKey, ShardingKey superShardingKey, int timeout) throws SQLException {
            return delegation.setShardingKeyIfValid(shardingKey, superShardingKey, timeout);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean setShardingKeyIfValid(ShardingKey shardingKey, int timeout) throws SQLException {
            return delegation.setShardingKeyIfValid(shardingKey, timeout);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setShardingKey(ShardingKey shardingKey, ShardingKey superShardingKey) throws SQLException {
            delegation.setShardingKey(shardingKey, superShardingKey);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setShardingKey(ShardingKey shardingKey) throws SQLException {
            delegation.setShardingKey(shardingKey);
        }
    }
}
