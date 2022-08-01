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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import kiss.I;
import kiss.Signal;
import kiss.WiseFunction;
import kiss.WiseSupplier;
import kiss.model.Model;
import kiss.model.Property;
import typewriter.api.QueryExecutor;
import typewriter.api.Specifier;
import typewriter.api.model.IdentifiableModel;
import typewriter.h2.H2;
import typewriter.sqlite.SQLite;

/**
 * Data Access Object for RDBMS.
 */
public class RDB<M extends IdentifiableModel> extends QueryExecutor<M, Signal<M>, RDBQuery<M>, RDB<M>> {

    /** The supported RDBMS. */
    public static final Dialect H2 = I.make(H2.class);

    /** The supported RDBMS. */
    public static final Dialect SQLite = I.make(SQLite.class);

    /** The reusable DAO cache. */
    private static final Map<Dialect, Map<Class, RDB>> DAO = Map.of(H2, new ConcurrentHashMap(), SQLite, new ConcurrentHashMap());

    /** The document model. */
    protected final Model<M> model;

    /** The table name. */
    protected final String tableName;

    /** The associated {@link Dialect}. */
    protected final Dialect dialect;

    /** The connection provider. */
    protected final WiseSupplier<Connection> provider;

    /**
     * Data Access Object.
     * 
     * @param model A target model.
     * @param dialect
     * @param url A user specified backend address.
     */
    public RDB(Model<M> model, Dialect dialect, String url) {
        this(model, dialect, ConnectionPool.by(url), true);
    }

    /**
     * Data Access Object.
     * 
     * @param model A target model.
     * @param dialect A dialect of RDBMS.
     * @param provider A user specified backend connection.
     * @param createTable Should I create table?
     */
    private RDB(Model<M> model, Dialect dialect, WiseSupplier<Connection> provider, boolean createTable) {
        this.model = model;
        this.tableName = '"' + model.type.getName() + '"';
        this.dialect = dialect;
        this.provider = provider;

        if (createTable) {
            execute(dialect.createTable(tableName, model));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long count() {
        return query("SELECT COUNT(*) N FROM " + tableName).map(result -> result.getLong("N")).to().exact();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Signal<M> limit(long size) {
        return super.limit(size);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Signal<M> findBy(RDBQuery<M> query) {
        return query("SELECT * FROM " + tableName + " " + query)
                .map(result -> decode(model, model.properties(), I.make(model.type), result));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Signal<M> restore(M instance, Specifier<M, ?>... specifiers) {
        if (instance == null) {
            return I.signal();
        }

        List<Property> properties = names(specifiers).map(model::property).or(I.signal(model.properties())).toList();

        return query("SELECT " + column(properties) + " FROM " + tableName + WHERE(instance))
                .map(result -> decode(model, properties, instance, result));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(M instance, Specifier<M, ?>... specifiers) {
        if (instance == null) {
            return;
        }

        if (specifiers == null || specifiers.length == 0) {
            // delete model
            execute("DELETE FROM " + tableName + " " + WHERE(instance));
        } else {
            // delete properties
            execute(dialect.commandUpdate() + " " + tableName + " " + SETNULL(model, specifiers) + " " + WHERE(instance));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(M instance, Specifier<M, ?>... specifiers) {
        if (instance == null) {
            return;
        }

        if (specifiers == null || specifiers.length == 0) {
            // update model
            execute(dialect.commandReplace() + " " + tableName + " " + VALUES(model, instance));
        } else {
            // update properties
            execute(dialect.commandUpdate() + " " + tableName + " " + SET(model, specifiers, instance) + " " + WHERE(instance));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized <R> R transact(WiseFunction<RDB<M>, R> operation) {
        Connection connection = provider.get();
        try {
            connection.setAutoCommit(false);

            R result = operation.apply(new RDB<>(model, dialect, () -> connection, false));
            connection.commit();
            return result;
        } catch (Throwable e) {
            try {
                connection.rollback();
            } catch (Throwable x) {
                throw I.quiet(x);
            }
            throw I.quiet(e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (Throwable e) {
                throw I.quiet(e);
            }
        }
    }

    /**
     * Decode from {@link ResultSet} to model data.
     * 
     * @param <V>
     * @param model
     * @param properties
     * @param instance
     * @param result
     * @return
     */
    private <V> V decode(Model model, List<Property> properties, V instance, ResultSet result) throws SQLException {
        for (Property property : properties) {
            RDBCodec codec = RDBCodec.by(property.model.type);
            model.set(instance, property, codec.decode(result, property.name));
        }
        return instance;
    }

    /**
     * Execute query.
     * 
     * @param statements
     */
    private void execute(String query) {
        try (Connection connection = provider.get()) {
            connection.createStatement().executeUpdate(query);
        } catch (SQLException e) {
            throw I.quiet(new SQLException(query, e));
        }
    }

    /**
     * Execute query.
     * 
     * @param query
     * @param A result stream.
     */
    private Signal<ResultSet> query(String query) {
        return new Signal<>((observer, disposer) -> {
            try (Connection connection = provider.get()) {
                ResultSet result = connection.createStatement().executeQuery(query);
                while (!disposer.isDisposed() && result.next()) {
                    observer.accept(result);
                }
                observer.complete();
            } catch (SQLException e) {
                observer.error(new SQLException(query, e));
            }
            return disposer;
        });
    }

    /**
     * Get the collection.
     * 
     * @param <M>
     * @param type The model type.
     * @return
     */
    public static <M extends IdentifiableModel> RDB<M> of(Class<M> type, Dialect dialect) {
        return DAO.get(dialect).computeIfAbsent(type, key -> new RDB(Model.of(type), dialect, (String) null));
    }

    /**
     * Release all system resources related to RDB.
     */
    public static void release() {
        ConnectionPool.release();
        DAO.clear();
    }

    /**
     * Release all system resources related to the specified URL.
     */
    public static void release(String url) {
        if (url != null && url.startsWith("jdbc:")) {
            ConnectionPool.release(url);
        }
    }

    /**
     * Helper to write WHERE statement.
     * 
     * @param model
     * @return
     */
    private static CharSequence WHERE(IdentifiableModel model) {
        StringBuilder builder = new StringBuilder();
        builder.append("WHERE id=").append(model.getId());
        return builder;
    }

    /**
     * Helper to write name of columns.
     * 
     * @param properties
     * @return
     */
    private static CharSequence column(List<Property> properties) {
        StringBuilder builder = new StringBuilder();

        for (Property property : properties) {
            RDBCodec<?> codec = RDBCodec.by(property.model.type);
            for (int j = 0; j < codec.types.size(); j++) {
                builder.append(property.name).append(codec.names.get(j)).append(',');
            }
        }

        return deleteTailComma(builder);
    }

    /**
     * Delete comma character at tail.
     * 
     * @param builder
     */
    private static StringBuilder deleteTailComma(StringBuilder builder) {
        int last = builder.length() - 1;
        if (builder.charAt(last) == ',') {
            builder.deleteCharAt(last);
        }
        return builder;
    }

    /**
     * Helper to write delete columns.
     * 
     * @return
     */
    private static CharSequence SETNULL(Model model, Specifier[] specifiers) {
        StringBuilder builder = new StringBuilder("SET ");

        for (Property property : names(specifiers).map(model::property).toList()) {
            RDBCodec codec = RDBCodec.by(property.model.type);

            for (int i = 0; i < codec.types.size(); i++) {
                builder.append(property.name).append(codec.names.get(i)).append("=NULL,");
            }
        }

        return deleteTailComma(builder);
    }

    /**
     * Helper to write set columns.
     * 
     * @return
     */
    private static CharSequence SET(Model model, Specifier[] specifiers, Object instance) {
        Map<String, Object> result = new HashMap();
        for (Property property : names(specifiers).map(model::property).toList()) {
            RDBCodec codec = RDBCodec.by(property.model.type);
            codec.encode(result, property.name, model.get(instance, property));
        }

        StringBuilder builder = new StringBuilder("SET ");
        for (Entry<String, Object> entry : result.entrySet()) {
            builder.append(entry.getKey()).append('=').append(I.transform(entry.getValue(), String.class)).append(',');
        }

        return deleteTailComma(builder);
    }

    /**
     * Helper to write VALUES statement.
     * 
     * @param model
     * @return
     */
    private static <V> CharSequence VALUES(Model<V> model, V instance) {
        Map<String, Object> result = new LinkedHashMap();
        for (Property property : model.properties()) {
            RDBCodec codec = RDBCodec.by(property.model.type);
            codec.encode(result, property.name, model.get(instance, property));
        }

        StringBuilder builder = new StringBuilder("VALUES(");
        for (Entry<String, Object> entry : result.entrySet()) {
            builder.append(I.transform(entry.getValue(), String.class)).append(",");
        }

        // remove tail comma
        int last = builder.length() - 1;
        if (builder.charAt(last) == ',') {
            builder.deleteCharAt(last);
        }

        builder.append(")");

        return builder;
    }
}
