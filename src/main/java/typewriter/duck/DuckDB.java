/*
 * Copyright (C) 2024 The TYPEWRITER Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package typewriter.duck;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kiss.I;
import typewriter.rdb.Dialect;

public class DuckDB extends Dialect {

    /** The JAVA-SQL type mapping. */
    private static final Map<Class, String> TYPES = new HashMap();

    static {
        TYPES.put(int.class, "integer");
        TYPES.put(long.class, "long");
        TYPES.put(float.class, "float");
        TYPES.put(double.class, "double");
        TYPES.put(short.class, "short");
        TYPES.put(byte.class, "tinyint");
        TYPES.put(boolean.class, "boolean");
        TYPES.put(Integer.class, "integer");
        TYPES.put(Long.class, "long");
        TYPES.put(Float.class, "float");
        TYPES.put(Double.class, "double");
        TYPES.put(Short.class, "short");
        TYPES.put(Byte.class, "tinyint");
        TYPES.put(BigInteger.class, "decimal");
        TYPES.put(BigDecimal.class, "decimal");
        TYPES.put(Boolean.class, "boolean");
        TYPES.put(String.class, "varchar");
        TYPES.put(List.class, "varchar[]");
    }

    /**
     * Hide constructor.
     */
    private DuckDB() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String defaultLocation() {
        return "jdbc:duckdb:";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String quote() {
        return "'";
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
    public String commandReplace() {
        return "INSERT OR REPLACE INTO";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String commandRegex(String propertyName, String regex) {
        return "regexp_matches(" + propertyName + ", '" + regex + "')";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String commnadListLength() {
        return "len";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String commnadListContains(String propertyName, Object value) {
        return "list_contains(" + propertyName + ", " + convert(value) + ")";
    }

    private String convert(Object value) {
        if (value instanceof String) {
            return "'\"" + value + "\"'";
        } else {
            return I.transform(value, String.class);
        }
    }
}