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
import java.util.regex.Pattern;

import org.sqlite.Function;
import org.sqlite.SQLiteConfig;

import typewriter.api.Constraint;
import typewriter.api.Constraint.ListConstraint;
import typewriter.api.Specifier;
import typewriter.api.Specifier.ListSpecifier;
import typewriter.rdb.Dialect;
import typewriter.rdb.RDBConstraint;
import typewriter.rdb.SQL;

public class SQLite extends Dialect {

    /** The global config for SQLite. */
    public static final SQLiteConfig CONFIG = new SQLiteConfig();

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
    public Connection createConnection(String url, Properties properties) throws Exception {
        Connection connection = super.createConnection(url, CONFIG.toProperties());

        // register extra functions
        Function.create(connection, "REGEXP", REGEXP_FUNCTION);

        return connection;
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
    public void commandLimitAndOffset(SQL builder, long limit, long offset) {
        if (0 < limit) builder.write("LIMIT").write(limit);
        if (0 < offset) {
            if (limit <= 0) builder.write("LIMIT -1");
            builder.write("OFFSET").write(offset);
        }
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
            expression.add("(SELECT key FROM json_each(alias) WHERE json_each.value = '" + value + "') IS NOT NULL ");
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