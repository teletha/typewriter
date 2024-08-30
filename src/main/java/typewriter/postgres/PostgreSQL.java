/*
 * Copyright (C) 2024 The TYPEWRITER Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package typewriter.postgres;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import typewriter.api.Identifiable;
import typewriter.rdb.Dialect;
import typewriter.rdb.SQL;

public class PostgreSQL extends Dialect {

    /** The JAVA-SQL type mapping. */
    private static final Map<Class, String> TYPES = new HashMap();

    static {
        TYPES.put(int.class, "int");
        TYPES.put(long.class, "bigint");
        TYPES.put(float.class, "real");
        TYPES.put(double.class, "double precision");
        TYPES.put(short.class, "smallint");
        TYPES.put(byte.class, "smallint");
        TYPES.put(boolean.class, "boolean");
        TYPES.put(Integer.class, "int");
        TYPES.put(Long.class, "bigint");
        TYPES.put(Float.class, "real");
        TYPES.put(Double.class, "double precision");
        TYPES.put(Short.class, "smallint");
        TYPES.put(Byte.class, "smallint");
        TYPES.put(BigInteger.class, "bigint");
        TYPES.put(BigDecimal.class, "decimal");
        TYPES.put(Boolean.class, "boolean");
        TYPES.put(String.class, "varchar");
        TYPES.put(List.class, "varchar");
    }

    /**
     * Hide constructor.
     */
    private PostgreSQL() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String quote() {
        return "\"";
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
        return "jdbc:postgresql://localhost:9999/";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String normalizeColumnName(String name) {
        return name.toLowerCase();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String commnadListLength() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String commnadListContains(String propertyName, Object value) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String commandReplace() {
        return "INSERT INTO";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String commandRegex(String propertyName, String regex) {
        return propertyName + " ~ '" + regex + "'";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <M extends Identifiable> SQL commandUpsert(SQL<M> sql, Iterable<M> models) {
        return sql.write("INSERT INTO", sql.tableName).values(models).onConflictDoUpdate().setExcluded(sql.model.properties());
    }
}