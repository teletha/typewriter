/*
 * Copyright (C) 2022 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package typewriter.sqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import kiss.I;
import kiss.Signal;
import kiss.model.Model;
import kiss.model.Property;
import typewriter.api.QueryExecutor;
import typewriter.api.Specifier;
import typewriter.api.model.IdentifiableModel;

public class SQLite<M extends IdentifiableModel> extends QueryExecutor<M, Signal<M>, SQLiteQuery<M>> {

    /** The connection pool. */
    private static final Map<String, Connection> ConnectionPool = new ConcurrentHashMap();

    /** The reusabel {@link SQLite} cache. */
    private static final Map<Class, SQLite> Cache = new ConcurrentHashMap();

    /** The document model. */
    private final Model<M> model;

    /** The table name. */
    private final String tableName;

    /** The reusable DB connection. */
    private final Connection connection;

    /** The reusable DB query executor. */
    private final Statement statement;

    /**
     * @param model
     */
    private SQLite(Class<M> model) {
        this(model, "jdbc:sqlite::sample.db");
    }

    /**
     * @param model
     * @param url
     */
    SQLite(Class<M> model, String url) {
        try {
            this.model = Model.of(model);
            this.tableName = '"' + model.getName() + '"';
            this.connection = ConnectionPool.computeIfAbsent(url, key -> {
                try {
                    return DriverManager.getConnection(key);
                } catch (SQLException e) {
                    throw I.quiet(e);
                }
            });
            this.statement = connection.createStatement();

            StringJoiner types = new StringJoiner(",", "(", ")");
            for (Property property : this.model.properties()) {
                types.add(property.name + " " + computeSQLType(property.model) + (property.name.equals("id") ? " primary key" : ""));
            }

            // create table
            statement.execute("CREATE TABLE IF NOT EXISTS " + tableName + types);
        } catch (SQLException e) {
            throw I.quiet(e);
        }
    }

    /**
     * @param model
     * @return
     */
    private String computeSQLType(Model model) {
        if (model.type == int.class) {
            return "integer";
        } else if (model.type == long.class) {
            return "bigint";
        } else if (model.type == boolean.class) {
            return "bit";
        } else if (model.type == String.class) {
            return "string";
        } else {
            throw new Error(model.type.getName());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long count() {
        try {
            ResultSet result = statement.executeQuery("SELECT COUNT(*) N FROM " + tableName);
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
    public Signal<M> findBy(SQLiteQuery<M> query) {
        return new Signal<>((observer, disposer) -> {
            try {
                List<Property> properties = model.properties();
                ResultSet result = statement.executeQuery("SELECT * FROM " + tableName + " " + query);
                while (result.next()) {
                    M instance = I.make(this.model.type);
                    for (Property property : properties) {
                        this.model.set(instance, property, decode(property, result));
                    }
                    observer.accept(instance);
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

                StringBuilder builder = new StringBuilder().append("SELECT ")
                        .append(properties.stream().map(p -> p.name).collect(Collectors.joining(",")))
                        .append(" FROM ")
                        .append(tableName)
                        .append(" WHERE id=")
                        .append(instance.getId());

                ResultSet result = statement.executeQuery(builder.toString());
                if (result.next()) {
                    for (Property property : properties) {
                        this.model.set(instance, property, decode(property, result));
                    }
                    observer.accept(instance);
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

        StringBuilder builder = new StringBuilder();

        if (specifiers == null || specifiers.length == 0) {
            // delete model
            builder.append("DELETE FROM ").append(tableName).append(" WHERE id=").append(instance.getId());
        } else {
            // delete properties
            builder.append("UPDATE ").append(tableName).append(" SET ");
            int count = 0;
            for (Specifier<M, ?> specifier : specifiers) {
                if (specifier != null) {
                    if (count++ != 0) builder.append(',');
                    Property property = model.property(specifier.propertyName());
                    builder.append(property.name).append("=NULL");
                }
            }
            builder.append(" WHERE id=").append(instance.getId());
        }

        execute(builder);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(M instance, Specifier<M, ?>... specifiers) {
        if (instance == null) {
            return;
        }

        StringBuilder builder = new StringBuilder();

        if (specifiers == null || specifiers.length == 0) {
            // update model
            builder.append("REPLACE INTO ").append(tableName).append(" VALUES(");

            List<Property> properties = model.properties();
            for (int i = 0; i < properties.size(); i++) {
                if (i != 0) builder.append(',');
                Property property = properties.get(i);
                builder.append(encode(property.model.type, model.get(instance, property)));
            }
            builder.append(")");
        } else {
            // update properties
            builder.append("UPDATE ").append(tableName).append(" SET ");
            int count = 0;
            for (Specifier<M, ?> specifier : specifiers) {
                if (specifier != null) {
                    if (count++ != 0) builder.append(',');
                    Property property = model.property(specifier.propertyName());
                    builder.append(property.name).append("=").append(encode(property.model.type, model.get(instance, property)));
                }
            }
            builder.append(" WHERE id=").append(instance.getId());
        }

        execute(builder);
    }

    private String encode(Class type, Object value) {
        if (type == String.class) {
            return "'" + value + "'";
        } else if (type == boolean.class || type == Boolean.class) {
            return value == Boolean.TRUE ? "1" : "0";
        } else {
            return value.toString();
        }
    }

    /**
     * @param property
     * @param result
     * @return
     */
    private Object decode(Property property, ResultSet result) throws SQLException {
        String name = property.name;
        Class type = property.model.type;

        if (type == int.class) {
            return result.getInt(name);
        } else if (type == long.class) {
            return result.getLong(name);
        } else if (type == boolean.class) {
            return result.getBoolean(name);
        } else if (type == String.class) {
            return result.getString(name);
        } else {
            throw new Error();
        }
    }

    /**
     * Execute query.
     * 
     * @param builder
     */
    private void execute(CharSequence builder) {
        try {
            statement.executeUpdate(builder.toString());
        } catch (SQLException e) {
            throw I.quiet(new SQLException(builder.toString(), e));
        }
    }

    /**
     * Get the collection.
     * 
     * @param <M>
     * @param model The model type.
     * @return
     */
    public static <M extends IdentifiableModel> SQLite<M> of(Class<M> model) {
        return Cache.computeIfAbsent(model, key -> new SQLite(key));
    }

    /**
     * Close all related system resources.
     */
    public static void close() {
        Iterator<Connection> iterator = ConnectionPool.values().iterator();
        while (iterator.hasNext()) {
            I.quiet(iterator.next());
            iterator.remove();
        }
    }
}
