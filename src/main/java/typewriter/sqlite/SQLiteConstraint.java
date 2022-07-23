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

import typewriter.api.Constraint;
import typewriter.api.Specifier;

/**
 * {@link Constraint} for mongdb.
 */
abstract class SQLiteConstraint<V, Self> implements Constraint<V, Self> {

    /** The name of target property. */
    protected final String propertyName;

    final StringBuilder expression = new StringBuilder();

    /**
     * Hide constructor.
     * 
     * @param specifier The property specifier.
     */
    protected SQLiteConstraint(Specifier specifier) {
        this.propertyName = specifier.propertyName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Self isNull() {
        expression.append(propertyName).append(" IS NULL");
        return (Self) this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Self notNull() {
        expression.append(propertyName).append(" IS NOT NULL");
        return (Self) this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Self is(V value) {
        expression.append(propertyName).append('=').append(value);
        return (Self) this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Self isNot(V value) {
        expression.append(propertyName).append("!=").append(value);
        return (Self) this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Self oneOf(V... values) {
        throw new Error();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Self oneOf(Iterable<V> values) {
        throw new Error();
    }

    /**
     * The specialized {@link Constraint}.
     */
    static class GenericType<T> extends SQLiteConstraint<T, TypeConstraint<T>> implements TypeConstraint<T> {
        GenericType(Specifier specifier) {
            super(specifier);
        }
    }

    /**
     * The specialized {@link Constraint} for {@link Number}.
     */
    static class ForNumeric<V extends Number> extends SQLiteConstraint<V, NumericConstraint<V>> implements NumericConstraint<V> {

        protected ForNumeric(Specifier specifier) {
            super(specifier);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public NumericConstraint<V> lessThan(V value) {
            expression.append(propertyName).append(" < ").append(value);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public NumericConstraint<V> lessThanOrEqual(V value) {
            expression.append(propertyName).append(" <= ").append(value);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public NumericConstraint<V> greaterThan(V value) {
            expression.append(propertyName).append(" > ").append(value);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public NumericConstraint<V> greaterThanOrEqual(V value) {
            expression.append(propertyName).append(" >= ").append(value);
            return this;
        }
    }

    /**
     * The specialized {@link Constraint} for {@link String}.
     */
    static class ForString extends SQLiteConstraint<String, StringConstraint> implements StringConstraint {
        ForString(Specifier specifier) {
            super(specifier);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public StringConstraint is(String value) {
            expression.append(propertyName).append("='").append(value).append("'");
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public StringConstraint isNot(String value) {
            expression.append(propertyName).append("!='").append(value).append("'");
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public StringConstraint regex(String regex) {
            throw new Error();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public StringConstraint notEmpty() {
            expression.append(propertyName).append(" != ''");
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public StringConstraint lessThan(int value) {
            expression.append("LENGTH(").append(propertyName).append(") < ").append(value);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public StringConstraint lessThanOrEqual(int value) {
            expression.append("LENGTH(").append(propertyName).append(") <= ").append(value);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public StringConstraint greaterThan(int value) {
            expression.append("LENGTH(").append(propertyName).append(") > ").append(value);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public StringConstraint greaterThanOrEqual(int value) {
            expression.append("LENGTH(").append(propertyName).append(") >= ").append(value);
            return this;
        }
    }
}