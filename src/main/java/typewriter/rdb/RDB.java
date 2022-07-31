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

import static typewriter.rdb.SQLTemplate.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
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
import typewriter.sqlite.SQLite;

/**
 * Data Access Object for RDBMS.
 */
public class RDB<M extends IdentifiableModel> extends QueryExecutor<M, Signal<M>, RDBQuery<M>> {

    /** The supported RDBMS. */
    public static final Dialect H2 = I.make(H2.class);

    /** The supported RDBMS. */
    public static final Dialect SQLite = I.make(SQLite.class);

    /** The reusable DAO cache. */
    private static final Map<Dialect, Map<Class, RDB>> DAO = Map.of(H2, new ConcurrentHashMap(), SQLite, new ConcurrentHashMap());

    /** The connection pool. */
    protected static final Map<String, Connection> CONNECTION_POOL = new ConcurrentHashMap();

    /** The document model. */
    protected final Model<M> model;

    /** The table name. */
    protected final String tableName;

    /** The associated {@link Dialect}. */
    protected final Dialect dialect;

    /** The connection provider. */
    protected final WiseSupplier<Connection> connectionProvider;

    /**
     * Data Access Object.
     * 
     * @param model A target model.
     * @param dialect
     * @param url A user specified backend address.
     */
    public RDB(Model<M> model, Dialect dialect, String url) {
        this(model, dialect, () -> {
            return CONNECTION_POOL
                    .computeIfAbsent(dialect.configureLocation(url), (WiseFunction<String, Connection>) dialect::createConnection);
        });

        // create table
        execute(dialect.createTable(tableName, model));
    }

    /**
     * Data Access Object.
     * 
     * @param model A target model.
     * @param dialect A dialect of RDBMS.
     * @param connectionProvider A user specified backend address.
     */
    public RDB(Model<M> model, Dialect dialect, WiseSupplier<Connection> connectionProvider) {
        this.model = model;
        this.tableName = '"' + model.type.getName() + '"';
        this.dialect = dialect;
        this.connectionProvider = connectionProvider;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long count() {
        try {
            ResultSet result = query("SELECT COUNT(*) N FROM", tableName);
            result.next();
            return result.getLong("N");
        } catch (SQLException e) {
            throw I.quiet(e);
        }
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
        return new Signal<>((observer, disposer) -> {
            try {
                ResultSet result = query("SELECT * FROM", tableName, query);

                while (!disposer.isDisposed() && result.next()) {
                    observer.accept(decode(result));
                }
                observer.complete();
            } catch (Throwable e) {
                observer.error(e);
            }
            return disposer;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Signal<M> restore(M instance, Specifier<M, ?>... specifiers) {
        return new Signal<>((observer, disposer) -> {
            try {
                List<Property> properties = new ArrayList();
                if (specifiers != null) {
                    for (Specifier<M, ?> specifier : specifiers) {
                        if (specifier != null) {
                            properties.add(model.property(specifier.propertyName()));
                        }
                    }
                }

                if (properties.isEmpty()) {
                    properties = model.properties();
                }

                ResultSet result = query("SELECT", column(properties), "FROM", tableName, WHERE(instance));
                if (result.next()) {
                    observer.accept(decode(model, properties, instance, result));
                }
                observer.complete();
            } catch (Throwable e) {
                observer.error(e);
            }
            return disposer;
        });
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
            execute("DELETE FROM", tableName, WHERE(instance));
        } else {
            // delete properties
            execute(dialect.commandUpdate(), tableName, SETNULL(model, specifiers), WHERE(instance));
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
            execute(dialect.commandReplace(), tableName, VALUES(model, instance));
        } else {
            // update properties
            execute(dialect.commandUpdate(), tableName, SET(model, specifiers, instance), WHERE(instance));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized <R> R transact(WiseSupplier<R> operation) {
        Connection connection = connectionProvider.get();
        try {
            connection.setAutoCommit(false);

            R result = operation.get();
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

    protected final M decode(ResultSet result) {
        return decode(model, model.properties(), I.make(model.type), result);
    }

    protected final <V> V decode(Model model, List<Property> properties, V instance, ResultSet result) {
        try {
            for (Property property : properties) {
                RDBCodec codec = RDBCodec.by(property.model.type);
                model.set(instance, property, codec.decode(result, property.name));
            }
            return instance;
        } catch (SQLException e) {
            throw I.quiet(e);
        }
    }

    /**
     * Execute query.
     * 
     * @param statements
     */
    protected final void execute(Object... statements) {
        StringBuilder builder = new StringBuilder();
        for (Object statement : statements) {
            if (statement != null) {
                builder.append(statement).append(' ');
            }
        }

        try {
            connectionProvider.get().createStatement().executeUpdate(builder.toString());
        } catch (SQLException e) {
            throw I.quiet(new SQLException(builder.toString(), e));
        }
    }

    /**
     * Execute query.
     * 
     * @param statements
     */
    protected final ResultSet query(Object... statements) {
        StringBuilder builder = new StringBuilder();
        for (Object statement : statements) {
            if (statement != null) {
                builder.append(statement).append(' ');
            }
        }

        try {
            return connectionProvider.get().createStatement().executeQuery(builder.toString());
        } catch (SQLException e) {
            throw I.quiet(new SQLException(builder.toString(), e));
        }
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
     * Close all related system resources.
     */
    public static void close(Dialect dialect) {
        Iterator<Connection> iterator = CONNECTION_POOL.values().iterator();
        while (iterator.hasNext()) {
            I.quiet(iterator.next());
            iterator.remove();
        }

        DAO.get(dialect).clear();
    }
}
