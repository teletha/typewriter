/*
 * Copyright (C) 2024 The TYPEWRITER Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package typewriter.rdb;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import kiss.I;
import kiss.WiseSupplier;

class ConnectionPool implements WiseSupplier<Connection> {

    /** The address. */
    private final String url;

    /** The dialect. */
    private final Dialect dialect;

    /** The readable name. */
    private final String category;

    /** The max size of pooled connections. */
    private final int max;

    /** The min size of pooled connections. */
    private final int min;

    /** The automatic commit. */
    private final boolean autoCommit;

    /** The read-only mode. */
    private final boolean readOnly;

    /** The connection timeout. */
    private final long timeout;

    /** The default transaction isolation level. */
    private final int isolation;

    /** The actual connection pool. */
    private final ArrayBlockingQueue<ManagedConnection> idles;

    /** The actual connection pool. */
    private final Set<ManagedConnection> busy;

    /** The connection type. */
    private ThreadLocal<ManagedConnection> threads;

    public ConnectionPool(String url) {
        this.url = url;
        this.dialect = detectDialect(url);
        this.max = config("typewriter.connection.maxPool", 8);
        this.min = config("typewriter.connection.minPool", 0);
        this.autoCommit = config("typewriter.connection.autoCommit", true);
        this.readOnly = config("typewriter.connection.readOnly", false);
        this.timeout = config("typewriter.connection.timeout", 1000 * 15L);
        this.isolation = config("typewriter.connection.isolation", -1);
        this.threads = config("typewriter.connection.perThread", false) ? ThreadLocal.withInitial(ManagedConnection::new) : null;
        this.idles = new ArrayBlockingQueue(max);
        this.busy = ConcurrentHashMap.newKeySet();
        this.category = "[" + dialect.kind + "-" + (threads == null ? "pool" : "per-thread") + "] ";
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
        return I.env(key + "." + url, I.env(key + "." + dialect.kind, I.env(key, defaultValue)));
    }

    /**
     * Detect {@link Dialect}.
     * 
     * @param url
     * @return
     */
    private Dialect detectDialect(String url) {
        String kind = url.substring(5, url.indexOf(':', 5));

        if (RDB.H2.kind.equals(kind)) {
            return RDB.H2;
        } else if (RDB.SQLite.kind.equals(kind)) {
            return RDB.SQLite;
        } else if (RDB.MariaDB.kind.equals(kind)) {
            return RDB.MariaDB;
        } else if (RDB.PostgreSQL.kind.equals(kind)) {
            return RDB.PostgreSQL;
        } else if (RDB.DuckDB.kind.equals(kind)) {
            return RDB.DuckDB;
        } else {
            throw new Error("Unknown dialect [" + kind + "]");
        }
    }

    /**
     * Close system resources.
     */
    private void close() {
        for (ManagedConnection connecton : busy) {
            try {
                connecton.delegation.close();
            } catch (SQLException e) {
                throw I.quiet(e);
            }
        }
        busy.clear();

        for (ManagedConnection connecton : idles) {
            try {
                connecton.delegation.close();
            } catch (SQLException e) {
                throw I.quiet(e);
            }
        }
        idles.clear();
    }

    /**
     * Get the idled connection.
     * 
     * @return
     */
    @Override
    public synchronized Connection call() throws Exception {
        if (threads != null) {
            return threads.get();
        } else {
            ManagedConnection connection = idles.poll();
            if (connection == null) {
                if (max <= busy.size()) {
                    log("Wait for idle connection");
                    connection = idles.poll(timeout, TimeUnit.MILLISECONDS);

                    if (connection == null) {
                        throw new SQLException(log("Timeout waiting for idle connection"));
                    }
                } else {
                    connection = new ManagedConnection();
                }
            }

            connection.processing = true;
            busy.add(connection);

            log("Borrowed connection");

            return connection;
        }
    }

    private String log(String message) {
        int idle = idles.size();
        int active = busy.size();
        int total = idle + active;
        message = category + message + " (" + active + "/" + total + " max:" + max + " min:" + min + " auto:" + autoCommit + " write:" + !readOnly + " timeout:" + timeout + "ms)";

        I.debug(message);
        return message;
    }

    /** The connnection pool manager. */
    private static final Map<String, ConnectionPool> CACHE = new ConcurrentHashMap();

    /**
     * Get the connection pool for the specified URL.
     * 
     * @param url
     * @return
     */
    static ConnectionPool by(String url) {
        if (url == null || !url.startsWith("jdbc:")) {
            throw new Error("Invalid JDBC URL [" + url + "]");
        }
        return CACHE.computeIfAbsent(url, key -> new ConnectionPool(key));
    }

    /**
     * Release all system resources related to RDB.
     */
    static void release() {
        Iterator<Entry<String, ConnectionPool>> iterator = CACHE.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, ConnectionPool> entry = iterator.next();
            entry.getValue().close();
            iterator.remove();
        }
    }

    /**
     * Release all system resources related to the specified URL.
     */
    static void release(String url) {
        ConnectionPool removed = CACHE.remove(url);
        if (removed != null) {
            removed.close();
        }
    }

    /**
     * Connection delegator.
     */
    private class ManagedConnection implements Connection {

        /** The backend. */
        private final Connection delegation;

        /** The managed resources. */
        private final List<AutoCloseable> resources = new ArrayList();

        /** State. */
        private boolean processing;

        /**
         * @param delegation
         * @throws SQLException
         */
        private ManagedConnection() {
            try {
                this.delegation = dialect.createConnection(url, null);
                this.delegation.setAutoCommit(true);
                this.delegation.setReadOnly(readOnly);
                if (0 <= isolation) this.delegation.setTransactionIsolation(isolation);

                log("Create connection");
            } catch (Exception e) {
                throw I.quiet(e);
            }
        }

        /**
         * Manage any resource.
         * 
         * @param <R>
         * @param resource
         * @return
         */
        private <R extends AutoCloseable> R manage(R resource) {
            resources.add(resource);
            return resource;
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
        public void beginRequest() throws SQLException {
            delegation.beginRequest();
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
        public void close() throws SQLException {
            if (!processing) {
                return;
            }

            for (AutoCloseable resource : resources) {
                try {
                    resource.close();
                } catch (Exception e) {
                    I.error(e);
                }
            }
            resources.clear();

            idles.offer(this);
            busy.remove(this);
            processing = false;

            log("Refunded connection");
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
        public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
            return delegation.createArrayOf(typeName, elements);
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
        public Clob createClob() throws SQLException {
            return delegation.createClob();
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
        public Statement createStatement() throws SQLException {
            return manage(delegation.createStatement());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
            return manage(delegation.createStatement(resultSetType, resultSetConcurrency));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
            return manage(delegation.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability));
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
        public void endRequest() throws SQLException {
            delegation.endRequest();
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
        public String getCatalog() throws SQLException {
            return delegation.getCatalog();
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
        public String getClientInfo(String name) throws SQLException {
            return delegation.getClientInfo(name);
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
        public DatabaseMetaData getMetaData() throws SQLException {
            return delegation.getMetaData();
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
        public String getSchema() throws SQLException {
            return delegation.getSchema();
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
        public Map<String, Class<?>> getTypeMap() throws SQLException {
            return delegation.getTypeMap();
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
        public boolean isClosed() throws SQLException {
            return processing;
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
        public boolean isValid(int timeout) throws SQLException {
            return delegation.isValid(timeout);
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
        public String nativeSQL(String sql) throws SQLException {
            return delegation.nativeSQL(sql);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public CallableStatement prepareCall(String sql) throws SQLException {
            return manage(delegation.prepareCall(sql));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
            return manage(delegation.prepareCall(sql, resultSetType, resultSetConcurrency));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
                throws SQLException {
            return manage(delegation.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public PreparedStatement prepareStatement(String sql) throws SQLException {
            return manage(delegation.prepareStatement(sql));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
            return manage(delegation.prepareStatement(sql, autoGeneratedKeys));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
            return manage(delegation.prepareStatement(sql, resultSetType, resultSetConcurrency));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability)
                throws SQLException {
            return manage(delegation.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
            return manage(delegation.prepareStatement(sql, columnIndexes));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
            return manage(delegation.prepareStatement(sql, columnNames));
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
        public void rollback() throws SQLException {
            delegation.rollback();
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
        public void setAutoCommit(boolean autoCommit) throws SQLException {
            delegation.setAutoCommit(autoCommit);
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
        public void setClientInfo(Properties properties) throws SQLClientInfoException {
            delegation.setClientInfo(properties);
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
        public void setHoldability(int holdability) throws SQLException {
            delegation.setHoldability(holdability);
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
        public void setReadOnly(boolean readOnly) throws SQLException {
            delegation.setReadOnly(readOnly);
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
        public void setSchema(String schema) throws SQLException {
            delegation.setSchema(schema);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setShardingKey(ShardingKey shardingKey) throws SQLException {
            delegation.setShardingKey(shardingKey);
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
        public boolean setShardingKeyIfValid(ShardingKey shardingKey, int timeout) throws SQLException {
            return delegation.setShardingKeyIfValid(shardingKey, timeout);
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
        public void setTransactionIsolation(int level) throws SQLException {
            delegation.setTransactionIsolation(level);
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
        public <T> T unwrap(Class<T> iface) throws SQLException {
            return delegation.unwrap(iface);
        }
    }
}