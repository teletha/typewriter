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
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import org.sqlite.Function;

import typewriter.rdb.Dialect;

public class SQLiteDialect extends Dialect {

    /** SINGLETON */
    public static final SQLiteDialect SINGLETON = new SQLiteDialect();

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

    /**
     * {@inheritDoc}
     */
    @Override
    public String defaultLocation() {
        return "jdbc:sqlite::memory:";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String types(Class type) {
        return TYPES.get(type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initializeConnection(Connection connection) throws Exception {
        // pragma
        connection.createStatement().executeUpdate("PRAGMA journal_mode=wal");
        connection.createStatement().executeUpdate("PRAGMA sync_mode=off");

        // register extra functions
        Function.create(connection, "REGEXP", REGEXP_FUNCTION);
    }
}