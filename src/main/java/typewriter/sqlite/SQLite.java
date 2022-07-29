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
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import org.sqlite.Function;

import kiss.I;
import typewriter.api.model.IdentifiableModel;
import typewriter.rdb.RDB;
import typewriter.rdb.RDBQuery;

public class SQLite<M extends IdentifiableModel> extends RDB<M, RDBQuery<M>> {

    /** The JAVA-SQL type mapping. */
    private static final Map<Class, String> TYPES = new HashMap();

    static {
        TYPES.put(int.class, "integer");
        TYPES.put(long.class, "integer");
        TYPES.put(float.class, "real");
        TYPES.put(double.class, "real");
        TYPES.put(short.class, "integer");
        TYPES.put(byte.class, "integer");
        TYPES.put(boolean.class, "bit");
        TYPES.put(String.class, "text");
    }

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

    /**
     * Hide constructor.
     * 
     * @param type A model type.
     * @param url A database location.
     */
    protected SQLite(Class<M> type, String url) {
        super(type, url, "jdbc:sqlite::memory:", TYPES);

        try {
            // pragma
            execute("PRAGMA journal_mode=wal");
            execute("PRAGMA sync_mode=off");

            // register extra functions
            Function.create(connection, "REGEXP", REGEXP_FUNCTION);

            // create table
            execute("CREATE TABLE IF NOT EXISTS", tableName, defineColumns());
        } catch (SQLException e) {
            throw I.quiet(e);
        }
    }

    /** The reusabel {@link SQLite} cache. */
    private static final Map<Class, SQLite> CACHE = new ConcurrentHashMap();

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
