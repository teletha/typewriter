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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import typewriter.api.Constraint;
import typewriter.api.Specifier;

/**
 * {@link Constraint} for mongdb.
 */
abstract class SQLiteConstraint<V, Self> implements Constraint<V, Self> {

    /** The name of target property. */
    protected final String propertyName;

    final List<String> expression = new ArrayList();

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
        expression.add(propertyName + " IS NULL");
        return (Self) this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Self isNotNull() {
        expression.add(propertyName + " IS NOT NULL");
        return (Self) this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Self is(V value) {
        expression.add(propertyName + "=" + value);
        return (Self) this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Self isNot(V value) {
        expression.add(propertyName + "!=" + value);
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
        public NumericConstraint<V> isLessThan(V value) {
            expression.add(propertyName + "<" + value);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public NumericConstraint<V> isLessThanOrEqual(V value) {
            expression.add(propertyName + "<=" + value);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public NumericConstraint<V> isGreaterThan(V value) {
            expression.add(propertyName + ">" + value);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public NumericConstraint<V> isGreaterThanOrEqual(V value) {
            expression.add(propertyName + ">=" + value);
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
            expression.add(propertyName + "='" + value + "'");
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public StringConstraint isNot(String value) {
            expression.add(propertyName + "!='" + value + "'");
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public StringConstraint isEmpty() {
            expression.add(propertyName + "=''");
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public StringConstraint isNotEmpty() {
            expression.add(propertyName + "!=''");
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public StringConstraint contains(String value) {
            expression.add(propertyName + " LIKE '%" + value + "%'");
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public StringConstraint regex(String regex) {
            expression.add(propertyName + " REGEXP '" + regex + "'");
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public StringConstraint isLessThan(int value) {
            expression.add("LENGTH(" + propertyName + ")<" + value + "");
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public StringConstraint isLessThanOrEqual(int value) {
            expression.add("LENGTH(" + propertyName + ")<=" + value + "");
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public StringConstraint isGreaterThan(int value) {
            expression.add("LENGTH(" + propertyName + ")>" + value + "");
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public StringConstraint isGreaterThanOrEqual(int value) {
            expression.add("LENGTH(" + propertyName + ")>=" + value + "");
            return this;
        }
    }

    /**
     * The specialized {@link Constraint} for {@link TemporalAccessor}.
     */
    static abstract class ForTermporal<T, Self extends TemporalConstraint<T, Self>> extends SQLiteConstraint<T, Self>
            implements TemporalConstraint<T, Self> {

        protected ForTermporal(Specifier specifier) {
            super(specifier);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Self is(T date) {
            expression.add(build("=", date));
            return (Self) this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Self isNot(T date) {
            expression.add(build("!=", date));
            return (Self) this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Self isBefore(T date) {
            expression.add(build("<", date));
            return (Self) this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Self isBeforeOrSame(T date) {
            expression.add(build("<=", date));
            return (Self) this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Self isAfter(T date) {
            expression.add(build(">", date));
            return (Self) this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Self isAfterOrSame(T date) {
            expression.add(build(">=", date));
            return (Self) this;
        }

        /**
         * Build datetime comparing operation.
         * 
         * @param operator
         * @param date
         * @return
         */
        protected abstract String build(String operator, T date);
    }

    /**
     * The specialized {@link Constraint} for {@link Date}.
     */
    static class ForDate extends ForTermporal<Date, DateConstraint> implements DateConstraint {

        protected ForDate(Specifier specifier) {
            super(specifier);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected String build(String operator, Date date) {
            return propertyName + operator + date.getTime();
        }
    }

    /**
     * The specialized {@link Constraint} for {@link LocalDate}.
     */
    static class ForLocalDate extends ForTermporal<LocalDate, LocalDateConstraint> implements LocalDateConstraint {

        protected ForLocalDate(Specifier specifier) {
            super(specifier);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected String build(String operator, LocalDate date) {
            return propertyName + operator + date.toEpochDay();
        }
    }

    /**
     * The specialized {@link Constraint} for {@link LocalTime}.
     */
    static class ForLocalTime extends ForTermporal<LocalTime, LocalTimeConstraint> implements LocalTimeConstraint {

        protected ForLocalTime(Specifier specifier) {
            super(specifier);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected String build(String operator, LocalTime date) {
            return propertyName + operator + date.toNanoOfDay();
        }
    }

    /**
     * The specialized {@link Constraint} for {@link LocalDateTime}.
     */
    static class ForLocalDateTime extends ForTermporal<LocalDateTime, LocalDateTimeConstraint> implements LocalDateTimeConstraint {

        protected ForLocalDateTime(Specifier specifier) {
            super(specifier);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected String build(String operator, LocalDateTime date) {
            return propertyName + operator + date.toInstant(ZoneOffset.UTC).toEpochMilli();
        }
    }

    /**
     * The specialized {@link Constraint} for {@link ZonedDateTime}.
     */
    static class ForZonedDateTime extends ForTermporal<ZonedDateTime, ZonedDateTimeConstraint> implements ZonedDateTimeConstraint {

        protected ForZonedDateTime(Specifier specifier) {
            super(specifier);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected String build(String operator, ZonedDateTime date) {
            return propertyName + operator + date.toInstant().toEpochMilli();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ZonedDateTimeConstraint isZone(ZoneId id) {
            expression.add(propertyName + "_ZONEID='" + id + "'");
            return this;
        }
    }

}