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

import typewriter.api.Constraint;
import typewriter.api.Constraint.ListConstraint;
import typewriter.api.Specifier;
import typewriter.api.Specifier.ListSpecifier;
import typewriter.rdb.Dialect;
import typewriter.rdb.RDBConstraint;

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
    public <M, N> ListConstraint<N> createListConstraint(ListSpecifier<M, N> specifier) {
        return new ForList(specifier, this);
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
     * The specialized {@link Constraint} for {@link List}.
     */
    private static class ForList<M> extends RDBConstraint<List<M>, ListConstraint<M>> implements ListConstraint<M> {
        private ForList(Specifier specifier, Dialect dialect) {
            super(specifier, dialect);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ListConstraint<M> contains(M value) {
            expression.add("list_any_value(" + propertyName + ") = '1'");
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ListConstraint<M> size(int value) {
            expression.add("len(" + propertyName + ") = " + value);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ListConstraint<M> isMoreThan(int value) {
            expression.add("len(" + propertyName + ") > " + value);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ListConstraint<M> isLessThan(int value) {
            expression.add("len(" + propertyName + ") < " + value);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ListConstraint<M> isOrLessThan(int value) {
            expression.add("len(" + propertyName + ") <= " + value);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ListConstraint<M> isOrMoreThan(int value) {
            expression.add("len(" + propertyName + ") >= " + value);
            return this;
        }
    }
}