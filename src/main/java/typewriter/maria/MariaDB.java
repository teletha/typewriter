/*
 * Copyright (C) 2024 The TYPEWRITER Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package typewriter.maria;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kiss.I;
import typewriter.rdb.Dialect;
import typewriter.rdb.SQL;

public class MariaDB extends Dialect {

    /** The URL parser. */
    private static final Pattern PARSER = Pattern.compile("(jdbc:mariadb://.+/)([^\\?]+)");

    /** The JAVA-SQL type mapping. */
    private static final Map<Class, String> TYPES = new HashMap();

    static {
        TYPES.put(int.class, "integer");
        TYPES.put(long.class, "bigint");
        TYPES.put(float.class, "float");
        TYPES.put(double.class, "double");
        TYPES.put(short.class, "tinyint");
        TYPES.put(byte.class, "smallint");
        TYPES.put(boolean.class, "boolean");
        TYPES.put(Integer.class, "integer");
        TYPES.put(Long.class, "bigint");
        TYPES.put(Float.class, "float");
        TYPES.put(Double.class, "double");
        TYPES.put(Short.class, "tinyint");
        TYPES.put(Byte.class, "smallint");
        TYPES.put(BigInteger.class, "bigint");
        TYPES.put(BigDecimal.class, "decimal");
        TYPES.put(Boolean.class, "boolean");
        TYPES.put(String.class, "text");
        TYPES.put(List.class, "json");
    }

    /**
     * Hide constructor.
     */
    private MariaDB() {
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
    protected String defaultLocation() {
        return "jdbc:mariadb:mem:temporary";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createDatabase(String url) {
        Matcher matcher = PARSER.matcher(url);
        if (matcher.find()) {
            String baseURL = matcher.group(1);
            String databaseName = matcher.group(2);

            try (Connection connection = DriverManager.getConnection(baseURL)) {
                connection.createStatement()
                        .executeUpdate("CREATE DATABASE IF NOT EXISTS " + databaseName + " DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_bin");
            } catch (SQLException e) {
                throw I.quiet(e);
            }
        } else {
            throw new IllegalArgumentException("Invalid MariaDB URL [" + url + "]");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void commandLimitAndOffset(SQL sql, long limit, long offset) {
        sql.limit(limit);
        if (0 < offset) {
            if (limit <= 0) sql.limit(Long.MAX_VALUE);
            sql.offset(offset);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String commnadListLength() {
        return "json_length";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String commnadListContains(String propertyName, Object value) {
        return "JSON_CONTAINS(" + propertyName + ", " + convert(value) + ", '$')";
    }

    private String convert(Object value) {
        if (value instanceof String) {
            return "'\"" + value + "\"'";
        } else {
            return I.transform(value, String.class);
        }
    }
}