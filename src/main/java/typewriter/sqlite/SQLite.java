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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.sqlite.Function;

import kiss.I;
import kiss.Signal;
import kiss.WiseFunction;
import kiss.WiseSupplier;
import kiss.model.Model;
import kiss.model.Property;
import typewriter.api.QueryExecutor;
import typewriter.api.Specifier;
import typewriter.api.model.IdentifiableModel;

public class SQLite<M extends IdentifiableModel> extends QueryExecutor<M, Signal<M>, SQLiteQuery<M>> {

    /** The java.util date formatter. */
    static final DateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    /** The java.time date formatter. */
    static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss").withZone(ZoneId.of("UTC"));

    /** The compiled regular expression manager. */
    private static final Map<String, Pattern> REGEX = new ConcurrentHashMap();

    /** Support REGEXP function. */
    private static final Function REGEXP_FUNCTION = new Function() {
        @Override
        protected void xFunc() throws SQLException {
            String value = Objects.requireNonNullElse(value_text(1), "");
            Pattern pattern = REGEX.computeIfAbsent(value_text(0), Pattern::compile);

            result(pattern.matcher(value).find() ? 1 : 0);
        }
    };

    /** The connection pool. */
    private static final Map<String, Connection> CONNECTION_POOL = new ConcurrentHashMap();

    /** The reusabel {@link SQLite} cache. */
    private static final Map<Class, SQLite> CACHE = new ConcurrentHashMap();

    /** The document model. */
    private final Model<M> model;

    /** The table name. */
    private final String tableName;

    /** The reusable DB connection. */
    private final Connection connection;

    /**
     * Hide constructor.
     * 
     * @param type A model type.
     * @param url A database location.
     */
    SQLite(Class<M> type, String url) {
        url = Objects.requireNonNullElse(url, I.env("sqlite", "jdbc:sqlite::memory:"));

        this.model = Model.of(type);
        this.tableName = '"' + type.getName() + '"';
        this.connection = CONNECTION_POOL.computeIfAbsent(url, (WiseFunction<String, Connection>) DriverManager::getConnection);

        try {
            // pragma
            execute("PRAGMA journal_mode=wal");
            execute("PRAGMA sync_mode=off");

            // register extra functions
            Function.create(connection, "REGEXP", REGEXP_FUNCTION);

            // create table
            execute("CREATE TABLE IF NOT EXISTS", tableName, definition(model.properties()));
        } catch (SQLException e) {
            throw I.quiet(e);
        }

    }

    private static <V> String definition(Iterable<Property> properties) {
        return join("(", ",", ")", properties, property -> {
            return property.name + " " + computeSQLType(property.model) + (property.name.equals("id") ? " primary key" : "");
        });
    }

    private static <V> String values(Iterable<V> values, WiseFunction<V, String> converter) {
        return join("(", ",", ")", values, converter);
    }

    private static <V> String join(String prefix, String separator, String suffix, Iterable<V> values, WiseFunction<V, String> converter) {
        StringJoiner joiner = new StringJoiner(separator, prefix, suffix);
        for (V value : values) {
            joiner.add(converter.apply(value));
        }
        return joiner.toString();
    }

    /**
     * Execute your query.
     * 
     * @param query
     */
    private void execute(Object... query) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < query.length; i++) {
            if (i != 0) builder.append(' ');
            builder.append(query[i].toString());
        }

        try (Statement statement = connection.createStatement()) {
            statement.execute(builder.toString());
        } catch (SQLException e) {
            throw I.quiet(new SQLException(builder.toString(), e));
        }
    }

    /**
     * Execute your query.
     * 
     * @param query
     */
    private ResultSet query(Object... query) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < query.length; i++) {
            if (i != 0) builder.append(' ');
            builder.append(query[i].toString());
        }

        try {
            return connection.createStatement().executeQuery(builder.toString());
        } catch (SQLException e) {
            throw I.quiet(new SQLException(builder.toString(), e));
        }
    }

    /**
     * @param model
     * @return
     */
    private static String computeSQLType(Model model) {
        if (model.type == int.class) {
            return "integer";
        } else if (model.type == long.class) {
            return "bigint";
        } else if (model.type == boolean.class) {
            return "bit";
        } else if (model.type == String.class) {
            return "string";
        } else if (model.type == LocalDateTime.class || model.type == LocalDate.class || model.type == LocalTime.class || model.type == Date.class) {
            return "integer";
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
            ResultSet result = query("SELECT COUNT(*) N FROM " + tableName);
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
                ResultSet result = query("SELECT * FROM " + tableName + " " + query);
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

                ResultSet result = query(builder.toString());
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

    private String encode(Class type, Object value) {
        if (type == String.class) {
            return "'" + value + "'";
        } else if (type == boolean.class || type == Boolean.class) {
            return value == Boolean.TRUE ? "1" : "0";
        } else if (type == Date.class) {
            return String.valueOf(((Date) value).getTime());
        } else if (type == LocalDate.class) {
            return String.valueOf(((LocalDate) value).toEpochDay());
        } else if (type == LocalDateTime.class) {
            return String.valueOf(((LocalDateTime) value).toInstant(ZoneOffset.UTC).toEpochMilli());
        } else if (type == LocalTime.class) {
            return String.valueOf(((LocalTime) value).toNanoOfDay());
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
        } else if (type == Date.class) {
            return new Date(result.getLong(name));
        } else if (type == LocalDate.class) {
            return LocalDate.ofEpochDay(result.getLong(name));
        } else if (type == LocalDateTime.class) {
            Instant instant = Instant.ofEpochMilli(result.getLong(name));
            return instant.atOffset(ZoneOffset.UTC).toLocalDateTime();
        } else if (type == LocalTime.class) {
            return LocalTime.ofNanoOfDay(result.getLong(name));
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
            connection.createStatement().executeUpdate(builder.toString());
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
        return CACHE.computeIfAbsent(model, key -> new SQLite(key, null));
    }

    /**
     * Close all related system resources.
     */
    public static void close() {
        Iterator<Connection> iterator = CONNECTION_POOL.values().iterator();
        while (iterator.hasNext()) {
            I.quiet(iterator.next());
            iterator.remove();
        }

        REGEX.clear();
        CACHE.clear();
    }
}
