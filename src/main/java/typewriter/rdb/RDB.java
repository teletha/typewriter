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
import java.util.List;
import java.util.Map;
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
import typewriter.maria.MariaDB;
import typewriter.sqlite.SQLite;

/**
 * Data Access Object for RDBMS.
 */
public class RDB<M extends IdentifiableModel> extends QueryExecutor<M, Signal<M>, RDBQuery<M>, RDB<M>> {

    /** The supported RDBMS. */
    public static final Dialect H2 = I.make(H2.class);

    /** The supported RDBMS. */
    public static final Dialect SQLite = I.make(SQLite.class);

    /** The supported RDBMS. */
    public static final Dialect MariaDB = I.make(MariaDB.class);

    /** The reusable DAO cache. */
    private static final Map<Dialect, Map<Class, RDB>> DAO = Map
            .of(H2, new ConcurrentHashMap(), SQLite, new ConcurrentHashMap(), MariaDB, new ConcurrentHashMap());

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
     * @param type A target model type.
     * @param dialect
     * @param url A user specified backend address.
     */
    public RDB(Class<M> type, Dialect dialect, String url) {
        this(Model.of(type), dialect, ConnectionPool.by(url));

        dialect.createDatabase(url);
        SQL.define() //
                .write(dialect.commandCreateTable(tableName, model))
                .execute(provider);
    }

    /**
     * Data Access Object.
     * 
     * @param model A target model.
     * @param dialect A dialect of RDBMS.
     * @param provider A user specified backend connection.
     * @param createTable Should I create table?
     */
    private RDB(Model<M> model, Dialect dialect, WiseSupplier<Connection> provider) {
        this.model = model;
        this.tableName = '`' + model.type.getName() + '`';
        this.dialect = dialect;
        this.provider = provider;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long count() {
        return SQL.define().write("SELECT COUNT(*) N FROM", tableName).qurey(provider).map(result -> result.getLong("N")).to().exact();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Signal<M> findBy(RDBQuery<M> query) {
        return SQL.define()
                .write("SELECT * FROM", tableName)
                .write(query, model, dialect)
                .qurey(provider)
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
            SQL.define().write("DELETE FROM", tableName).where(instance).execute(provider);
        } else {
            // delete properties
            SQL.define()
                    .write(dialect.commandUpdate(), tableName)
                    .setNull(names(specifiers).map(model::property).toList())
                    .where(instance)
                    .execute(provider);
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
            // execute(dialect.commandReplace() + " " + tableName + " " + VALUES(model, instance));

            SQL.define().write(dialect.commandReplace(), tableName).values(model, instance).execute(provider);
        } else {
            // update properties
            SQL.define()
                    .write(dialect.commandUpdate(), tableName)
                    .set(model, names(specifiers).map(model::property).toList(), instance)
                    .where(instance)
                    .execute(provider);
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

            R result = operation.apply(new RDB<>(model, dialect, () -> connection));
            connection.commit();
            return result;
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException x) {
                throw I.quiet(x);
            }
            throw I.quiet(e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
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
     * @param query
     * @param A result stream.
     */
    private Signal<ResultSet> query(String query) {
        if (query == null || query.isBlank()) {
            return I.signal();
        }

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
        return DAO.get(dialect).computeIfAbsent(type, key -> new RDB(type, dialect, (String) null));
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
}
