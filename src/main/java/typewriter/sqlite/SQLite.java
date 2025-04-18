/*
 * Copyright (C) 2024 The TYPEWRITER Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package typewriter.sqlite;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import org.sqlite.Function;
import org.sqlite.SQLiteConfig;

import kiss.I;
import typewriter.rdb.Dialect;
import typewriter.rdb.SQL;

public class SQLite extends Dialect {

    /** The compiled regular expression manager. */
    private static final Map<String, Pattern> REGEX = new ConcurrentHashMap();

    /** The JAVA-SQL type mapping. */
    private static final Map<Class, String> TYPES = new HashMap();

    static {
        // Since the SQLite developers seem to think that threads are evil, you would be better off
        // having one thread handle all your database operations and serialize DB tasks on your own
        // using Java code.
        I.env("typewriter.connection.maxPool.sqlite", 10);

        TYPES.put(int.class, "integer");
        TYPES.put(long.class, "integer");
        TYPES.put(float.class, "real");
        TYPES.put(double.class, "real");
        TYPES.put(short.class, "integer");
        TYPES.put(byte.class, "integer");
        TYPES.put(boolean.class, "bit");
        TYPES.put(Integer.class, "integer");
        TYPES.put(Long.class, "integer");
        TYPES.put(Float.class, "real");
        TYPES.put(Double.class, "real");
        TYPES.put(Short.class, "integer");
        TYPES.put(Byte.class, "integer");
        TYPES.put(BigInteger.class, "integer");
        TYPES.put(BigDecimal.class, "real");
        TYPES.put(Boolean.class, "bit");
        TYPES.put(String.class, "text");
        TYPES.put(List.class, "json");
    }

    /**
     * Hide constructor.
     */
    private SQLite() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String defaultLocation() {
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
    public synchronized Connection createConnection(String url, Properties properties) throws Exception {
        Connection connection = super.createConnection(url, Lazy.CONFIG.toProperties());

        // register extra functions
        Function.create(connection, "REGEXP", Lazy.REGEXP_FUNCTION);

        return connection;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void commandLimitAndOffset(SQL builder, long limit, long offset) {
        if (0 < limit) builder.write("LIMIT").write(limit);
        if (0 < offset) {
            if (limit <= 0) builder.write("LIMIT -1");
            builder.write("OFFSET").write(offset);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String commnadListLength() {
        return "json_array_length";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String commnadListContains(String propertyName, Object value) {
        return "(SELECT key FROM json_each(alias) WHERE json_each.value = '" + value + "') IS NOT NULL ";
    }

    /**
     * Custom SQLite specific configuration.
     * 
     * @param configuration
     */
    public static void configure(Consumer<SQLiteConfig> configuration) {
        configuration.accept(Lazy.CONFIG);
    }

    /**
     * In-direct lazy initializer to avoid {@link ClassNotFoundException} in no sqlite environment.
     */
    private static class Lazy {

        /** The global config for SQLite. */
        private static final SQLiteConfig CONFIG = new SQLiteConfig();

        /** Support REGEXP function. */
        private static final Function REGEXP_FUNCTION = new Function() {
            @Override
            protected void xFunc() throws SQLException {
                String value = Objects.requireNonNullElse(value_text(1), "");
                Pattern pattern = REGEX.computeIfAbsent(value_text(0), Pattern::compile);

                result(pattern.matcher(value).find() ? 1 : 0);
            }
        };
    }
}