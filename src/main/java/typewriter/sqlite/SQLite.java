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
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;

import kiss.I;
import kiss.Signal;
import kiss.model.Model;
import kiss.model.Property;
import typewriter.api.QueryExecutor;
import typewriter.api.Specifier;
import typewriter.api.model.IdentifiableModel;

public class SQLite<M extends IdentifiableModel> extends QueryExecutor<M, Signal<M>, SQLiteQuery<M>> {

    /** The reusabel {@link SQLite} cache. */
    private static final Map<Class, SQLite> Cache = new ConcurrentHashMap();

    /** The document model. */
    private final Model<M> model;

    /** The table name. */
    private final String tableName;

    /** The associated database. */
    private final Connection db;

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
            this.db = DriverManager.getConnection(url);
            this.statement = db.createStatement();

            StringJoiner types = new StringJoiner(",", "(", ")");
            for (Property property : this.model.properties()) {
                types.add(property.name + " " + computeSQLType(property.model) + (property.name.equals("id") ? " primary key" : ""));
            }

            // create table
            statement.execute("create table if not exists " + tableName + types);
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
            ResultSet result = statement.executeQuery("SELECT COUNT(*) num FROM " + tableName);
            result.next();
            return result.getLong("num");
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
                List<Property> properties = this.model.properties();
                ResultSet result = statement.executeQuery("select * from " + tableName + " " + query);
                while (result.next()) {
                    M model = I.make(this.model.type);
                    for (Property property : properties) {
                        this.model.set(model, property, decode(property, result));
                    }
                    observer.accept(model);
                }
                observer.complete();
            } catch (Throwable e) {
                observer.error(e);
            }
            return disposer;
        });
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
     * {@inheritDoc}
     */
    @Override
    public Signal<M> restore(M model, Specifier<M, ?>... specifiers) {
        throw new Error();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(M model, Specifier<M, ?>... specifiers) {
        if (model == null) {
            return;
        }

        StringBuilder builder = new StringBuilder();

        if (specifiers == null || specifiers.length == 0) {
            // delete model
            builder.append("DELETE FROM ").append(tableName).append(" WHERE id=").append(model.getId());
        } else {
            // delete properties
            builder.append("UPDATE ").append(tableName).append(" SET ");
            int count = 0;
            for (Specifier<M, ?> specifier : specifiers) {
                if (specifier != null) {
                    if (count++ != 0) builder.append(',');
                    Property property = this.model.property(specifier.propertyName());
                    builder.append(property.name).append("=NULL");
                }
            }
            builder.append(" WHERE id=").append(model.getId());
        }

        execute(builder);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(M model, Specifier<M, ?>... specifiers) {
        if (model == null) {
            return;
        }

        StringBuilder builder = new StringBuilder();

        if (specifiers == null || specifiers.length == 0) {
            // update model
            builder.append("REPLACE INTO ").append(tableName).append(" VALUES(");

            List<Property> properties = this.model.properties();
            for (int i = 0; i < properties.size(); i++) {
                if (i != 0) builder.append(',');
                Property property = properties.get(i);
                builder.append(encode(property.model.type, this.model.get(model, property)));
            }
            builder.append(")");
        } else {
            // update properties
            builder.append("UPDATE ").append(tableName).append(" SET ");
            int count = 0;
            for (Specifier<M, ?> specifier : specifiers) {
                if (specifier != null) {
                    if (count++ != 0) builder.append(',');
                    Property property = this.model.property(specifier.propertyName());
                    builder.append(property.name).append("=").append(encode(property.model.type, this.model.get(model, property)));
                }
            }
            builder.append(" WHERE id=").append(model.getId());
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
}
