/*
 * Copyright (C) 2022 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package typewriter.maria;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
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
        TYPES.put(String.class, "text");
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
    public void commandLimitAndOffset(SQL builder, long limit, long offset) {
        if (0 < limit) builder.write("LIMIT").write(limit);
        if (0 < offset) {
            if (limit <= 0) builder.write("LIMIT 18446744073709551615");
            builder.write(" OFFSET ").write(offset);
        }
    }
}
