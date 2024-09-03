/*
 * Copyright (C) 2024 The TYPEWRITER Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package typewriter.h2;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import kiss.I;
import kiss.Property;
import typewriter.api.Identifiable;
import typewriter.rdb.Dialect;
import typewriter.rdb.SQL;

public class H2 extends Dialect {

    /** The JAVA-SQL type mapping. */
    private static final Map<Class, String> TYPES = new HashMap();

    static {
        TYPES.put(int.class, "int");
        TYPES.put(long.class, "bigint");
        TYPES.put(float.class, "real");
        TYPES.put(double.class, "double");
        TYPES.put(short.class, "tinyint");
        TYPES.put(byte.class, "smallint");
        TYPES.put(boolean.class, "boolean");
        TYPES.put(Integer.class, "int");
        TYPES.put(Long.class, "bigint");
        TYPES.put(Float.class, "real");
        TYPES.put(Double.class, "double");
        TYPES.put(Short.class, "tinyint");
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
    private H2() {
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
    public Connection createConnection(String url, Properties properties) throws Exception {
        Connection con = super.createConnection(url + ";database_to_upper=false", properties);

        try (Statement stat = con.createStatement()) {
            stat.execute("CREATE ALIAS json_array_length FOR '" + Functions.class.getName() + ".jsonArrayLength'");
            stat.execute("CREATE ALIAS json_array_contains FOR '" + Functions.class.getName() + ".jsonArrayContains'");
        } catch (SQLException e) {
            // ignore
        }

        return con;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String defaultLocation() {
        return "jdbc:h2:mem:temporary";
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
        return "json_array_contains(" + propertyName + ", '" + I.transform(value, String.class) + "')";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <M extends Identifiable> SQL commandUpsert(SQL<M> sql, Iterable<M> models, Iterable<Property> properties) {
        return sql.write("MERGE INTO ", sql.tableName).write("(").names(properties).write(")").values(models, properties);
    }
}