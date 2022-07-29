/*
 * Copyright (C) 2022 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package typewriter.h2;

import static typewriter.rdb.SQLTemplate.*;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import kiss.I;
import typewriter.api.Specifier;
import typewriter.api.model.IdentifiableModel;
import typewriter.rdb.RDB;
import typewriter.rdb.RDBQuery;

public class H2<M extends IdentifiableModel> extends RDB<M, RDBQuery<M>> {

    /**
     * Hide constructor.
     * 
     * @param type A model type.
     * @param url A database location.
     */
    protected H2(Class<M> type, String url) {
        super(type, url, "h2", "jdbc:h2:mem:temporary");

        // create table
        execute("CREATE TABLE IF NOT EXISTS", tableName, tableDefinition(model, this));
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
            execute("MERGE INTO", tableName, VALUES(model, instance));
        } else {
            // update properties
            execute("UPDATE", tableName, SET(model, specifiers, instance), WHERE(instance));
        }
    }

    /**
     * Define JAVA-SQL type mapping.
     * 
     * @param type
     * @return
     */
    @Override
    protected String computeSQLType(Class type) {
        if (type == int.class) {
            return "int";
        } else if (type == long.class) {
            return "bigint";
        } else if (type == float.class) {
            return "real";
        } else if (type == double.class) {
            return "double";
        } else if (type == boolean.class) {
            return "boolean";
        } else if (type == String.class) {
            return "varchar";
        } else if (type == LocalDateTime.class || type == LocalDate.class || type == LocalTime.class || type == Date.class || type == ZonedDateTime.class) {
            return "bigint";
        } else if (type == byte.class) {
            return "tinyint";
        } else if (type == short.class) {
            return "smallint";
        } else {
            throw new Error(type.getName());
        }
    }

    /** The reusabel {@link H2} cache. */
    private static final Map<Class, H2> CACHE = new ConcurrentHashMap();

    /**
     * Get the collection.
     * 
     * @param <M>
     * @param model The model type.
     * @return
     */
    public static <M extends IdentifiableModel> H2<M> of(Class<M> model) {
        return CACHE.computeIfAbsent(model, key -> new H2(key, null));
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

        CACHE.clear();
    }
}
