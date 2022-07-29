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
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import kiss.I;
import kiss.Signal;
import kiss.WiseFunction;
import kiss.WiseSupplier;
import kiss.model.Model;
import kiss.model.Property;
import typewriter.api.QueryExecutor;
import typewriter.api.Queryable;
import typewriter.api.Specifier;
import typewriter.api.model.IdentifiableModel;

/**
 * Data Access Object for RDBMS.
 */
public abstract class RDB<M extends IdentifiableModel, Q extends Queryable<M, Q>> extends QueryExecutor<M, Signal<M>, Q> {

    /** The connection pool. */
    protected static final Map<String, Connection> CONNECTION_POOL = new ConcurrentHashMap();

    /** The document model. */
    protected final Model<M> model;

    /** The table name. */
    protected final String tableName;

    /** The reusable DB connection. */
    protected final Connection connection;

    /** The JAVA-SQL type mapper. */
    private final Map<Class, String> typeMapper;

    /**
     * Data Access Object.
     * 
     * @param type A target model.
     * @param url A user specified backend address.
     * @param defaultURL A default backend address.
     * @param typeMapper Type mappings between JAVA and SQL.
     */
    protected RDB(Class<M> type, String url, String defaultURL, Map<Class, String> typeMapper) {
        url = Objects.requireNonNullElse(url, I.env("typewriter." + getClass().getSimpleName().toLowerCase(), defaultURL));

        this.model = Model.of(type);
        this.tableName = '"' + type.getName() + '"';
        this.connection = CONNECTION_POOL.computeIfAbsent(url, (WiseFunction<String, Connection>) DriverManager::getConnection);
        this.typeMapper = typeMapper;
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
    public Signal<M> findBy(Q query) {
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
            execute("UPDATE", tableName, SETNULL(model, specifiers), WHERE(instance));
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
            execute("REPLACE INTO", tableName, VALUES(model, instance));
        } else {
            // update properties
            execute("UPDATE", tableName, SET(model, specifiers, instance), WHERE(instance));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized <R> R transact(WiseSupplier<R> operation) {
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
            connection.createStatement().executeUpdate(builder.toString());
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
            return connection.createStatement().executeQuery(builder.toString());
        } catch (SQLException e) {
            throw I.quiet(new SQLException(builder.toString(), e));
        }
    }

    /**
     * Helper to write column definitions.
     * 
     * @return
     */
    protected CharSequence defineColumns() {
        StringBuilder builder = new StringBuilder();
        builder.append('(');
        for (Property property : model.properties()) {
            RDBCodec<?> codec = RDBCodec.by(property.model.type);
            for (int i = 0; i < codec.types.size(); i++) {
                Class columnType = codec.types.get(i);
                String columnName = codec.names.get(i);

                String type = typeMapper.get(columnType);
                if (type == null) throw error("SQL type is not found for [", columnType, "]");

                builder.append(property.name).append(columnName).append(' ').append(type).append(',');
            }
        }
        builder.append("PRIMARY KEY(id))");

        return builder;
    }

    /**
     * Build error.
     * 
     * @param messages
     * @return
     */
    private Error error(Object... messages) {
        StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName()).append(" : ");

        for (Object message : messages) {
            builder.append(message);
        }

        return new Error(builder.toString());
    }
}
