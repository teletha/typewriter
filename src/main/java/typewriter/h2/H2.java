/*
 * Copyright (C) 2023 The TYPEWRITER Development Team
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
import typewriter.api.Constraint;
import typewriter.api.Constraint.ListConstraint;
import typewriter.api.Specifier;
import typewriter.api.Specifier.ListSpecifier;
import typewriter.rdb.Dialect;
import typewriter.rdb.RDBConstraint;

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
    public String types(Class type) {
        return TYPES.get(type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Connection createConnection(String url, Properties properties) throws Exception {
        Connection con = super.createConnection(url + ";database_to_upper=false", properties);

        try {
            Statement stat = con.createStatement();
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
    public <M, N> ListConstraint<N> createListConstraint(ListSpecifier<M, N> specifier) {
        return new ForList(specifier);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String commandReplace() {
        return "MERGE INTO";
    }

    /**
     * The specialized {@link Constraint} for {@link List}.
     */
    private static class ForList<M> extends RDBConstraint<List<M>, ListConstraint<M>> implements ListConstraint<M> {
        private ForList(Specifier specifier) {
            super(specifier);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ListConstraint<M> contains(M value) {
            expression.add("json_array_contains(" + propertyName + ", '" + I.transform(value, String.class) + "')");
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ListConstraint<M> size(int value) {
            expression.add("json_array_length(" + propertyName + ") = " + value);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ListConstraint<M> isMoreThan(int value) {
            expression.add("json_array_length(" + propertyName + ") > " + value);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ListConstraint<M> isLessThan(int value) {
            expression.add("json_array_length(" + propertyName + ") < " + value);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ListConstraint<M> isOrLessThan(int value) {
            expression.add("json_array_length(" + propertyName + ") <= " + value);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ListConstraint<M> isOrMoreThan(int value) {
            expression.add("json_array_length(" + propertyName + ") >= " + value);
            return this;
        }
    }
}