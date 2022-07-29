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

import static typewriter.rdb.SQLTemplate.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import org.sqlite.Function;

import kiss.I;
import kiss.Signal;
import kiss.WiseSupplier;
import kiss.model.Property;
import typewriter.api.Specifier;
import typewriter.api.model.IdentifiableModel;
import typewriter.rdb.RDB;
import typewriter.rdb.RDBTypeCodec;

public class SQLite<M extends IdentifiableModel> extends RDB<M, SQLiteQuery<M>> {

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

    /** The reusabel {@link SQLite} cache. */
    private static final Map<Class, SQLite> CACHE = new ConcurrentHashMap();

    /**
     * Hide constructor.
     * 
     * @param type A model type.
     * @param url A database location.
     */
    SQLite(Class<M> type, String url) {
        super(type, Objects.requireNonNullElse(url, I.env("sqlite", "jdbc:sqlite::memory:")));

        try {
            // pragma
            execute("PRAGMA journal_mode=wal");
            execute("PRAGMA sync_mode=off");

            // register extra functions
            Function.create(connection, "REGEXP", REGEXP_FUNCTION);

            // create table
            execute("CREATE TABLE IF NOT EXISTS", tableName, tableDefinition(model, this::computeSQLType));
        } catch (SQLException e) {
            throw I.quiet(e);
        }
    }

    /**
     * Define JAVA-SQL type mapping.
     * 
     * @param type
     * @return
     */
    private String computeSQLType(Class type) {
        if (type == int.class) {
            return "integer";
        } else if (type == long.class) {
            return "bigint";
        } else if (type == boolean.class) {
            return "bit";
        } else if (type == String.class) {
            return "string";
        } else if (type == LocalDateTime.class || type == LocalDate.class || type == LocalTime.class || type == Date.class || type == ZonedDateTime.class) {
            return "integer";
        } else {
            throw new Error(type.getName());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long count() {
        try {
            ResultSet result = executeQuery("SELECT COUNT(*) N FROM", tableName);
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
                ResultSet result = executeQuery("SELECT * FROM", tableName, query);
                while (result.next()) {
                    M instance = I.make(this.model.type);
                    for (Property property : properties) {
                        this.model.set(instance, property, RDBTypeCodec.decode(property, result));
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

                ResultSet result = executeQuery("SELECT", column(properties), "FROM", tableName, WHERE(instance));
                if (result.next()) {
                    for (Property property : properties) {
                        this.model.set(instance, property, RDBTypeCodec.decode(property, result));
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

    /**
     * Execute query.
     * 
     * @param builder
     */
    private void execute(Object... objects) {
        StringBuilder builder = new StringBuilder();
        for (Object object : objects) {
            builder.append(object).append(' ');
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
     * @param builder
     */
    private ResultSet executeQuery(Object... objects) {
        StringBuilder builder = new StringBuilder();
        for (Object object : objects) {
            builder.append(object).append(' ');
        }

        try {
            return connection.createStatement().executeQuery(builder.toString());
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
