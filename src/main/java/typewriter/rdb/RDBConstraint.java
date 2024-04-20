/*
 * Copyright (C) 2024 The TYPEWRITER Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package typewriter.rdb;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
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
public abstract class RDBConstraint<V, Self> implements Constraint<V, Self> {

    /** The name of target property. */
    protected final String propertyName;

    /** The dialiect. */
    protected final Dialect dialect;

    /** The additional expression. */
    protected final List<String> expression = new ArrayList();

    /**
     * Hide constructor.
     * 
     * @param specifier The property specifier.
     */
    protected RDBConstraint(Specifier specifier, Dialect dialect) {
        this.propertyName = specifier.propertyName(dialect);
        this.dialect = dialect;
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
    static class GenericType<T> extends RDBConstraint<T, TypeConstraint<T>> implements TypeConstraint<T> {
        GenericType(Specifier specifier, Dialect dialect) {
            super(specifier, dialect);
        }
    }

    /**
     * The specialized {@link Constraint} for {@link Number}.
     */
    static class ForNumeric<V extends Number> extends RDBConstraint<V, NumericConstraint<V>> implements NumericConstraint<V> {

        protected ForNumeric(Specifier specifier, Dialect dialect) {
            super(specifier, dialect);
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
        public NumericConstraint<V> isOrLessThan(V value) {
            expression.add(propertyName + "<=" + value);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public NumericConstraint<V> isMoreThan(V value) {
            expression.add(propertyName + ">" + value);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public NumericConstraint<V> isOrMoreThan(V value) {
            expression.add(propertyName + ">=" + value);
            return this;
        }
    }

    /**
     * The specialized {@link Constraint} for {@link String}.
     */
    static class ForString extends RDBConstraint<String, StringConstraint> implements StringConstraint {
        ForString(Specifier specifier, Dialect dialect) {
            super(specifier, dialect);
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
            expression.add(propertyName + " LIKE ''");
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public StringConstraint isNotEmpty() {
            expression.add(propertyName + " NOT LIKE ''");
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
            expression.add(dialect.commandRegex(propertyName, regex));
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
        public StringConstraint isOrLessThan(int value) {
            expression.add("LENGTH(" + propertyName + ")<=" + value + "");
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public StringConstraint isMoreThan(int value) {
            expression.add("LENGTH(" + propertyName + ")>" + value + "");
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public StringConstraint isOrMoreThan(int value) {
            expression.add("LENGTH(" + propertyName + ")>=" + value + "");
            return this;
        }
    }

    /**
     * The specialized {@link Constraint} for {@link TemporalAccessor}.
     */
    static abstract class ForTermporal<T, Self extends TemporalConstraint<T, Self>> extends RDBConstraint<T, Self>
            implements TemporalConstraint<T, Self> {

        protected ForTermporal(Specifier specifier, Dialect dialect) {
            super(specifier, dialect);
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

        protected ForDate(Specifier specifier, Dialect dialect) {
            super(specifier, dialect);
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

        protected ForLocalDate(Specifier specifier, Dialect dialect) {
            super(specifier, dialect);
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

        protected ForLocalTime(Specifier specifier, Dialect dialect) {
            super(specifier, dialect);
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

        protected ForLocalDateTime(Specifier specifier, Dialect dialect) {
            super(specifier, dialect);
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
     * The specialized {@link Constraint} for {@link OffsetDateTime}.
     */
    static class ForOffsetDateTime extends ForTermporal<OffsetDateTime, OffsetDateTimeConstraint> implements OffsetDateTimeConstraint {

        protected ForOffsetDateTime(Specifier specifier, Dialect dialect) {
            super(specifier, dialect);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected String build(String operator, OffsetDateTime date) {
            return propertyName + "DATE" + operator + date.toInstant().toEpochMilli();
        }
    }

    /**
     * The specialized {@link Constraint} for {@link ZonedDateTime}.
     */
    static class ForZonedDateTime extends ForTermporal<ZonedDateTime, ZonedDateTimeConstraint> implements ZonedDateTimeConstraint {

        protected ForZonedDateTime(Specifier specifier, Dialect dialect) {
            super(specifier, dialect);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected String build(String operator, ZonedDateTime date) {
            return propertyName + "DATE" + operator + date.toInstant().toEpochMilli();
        }
    }

    /**
     * The specialized {@link Constraint} for {@link List}.
     */
    static class ForList<M> extends RDBConstraint<List<M>, ListConstraint<M>> implements ListConstraint<M> {

        protected ForList(Specifier specifier, Dialect dialect) {
            super(specifier, dialect);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ListConstraint<M> contains(M value) {
            expression.add(dialect.commnadListContains(propertyName, value));
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ListConstraint<M> size(int value) {
            expression.add(dialect.commnadListLength() + "(" + propertyName + ") = " + value);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ListConstraint<M> isMoreThan(int value) {
            expression.add(dialect.commnadListLength() + "(" + propertyName + ") > " + value);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ListConstraint<M> isLessThan(int value) {
            expression.add(dialect.commnadListLength() + "(" + propertyName + ") < " + value);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ListConstraint<M> isOrLessThan(int value) {
            expression.add(dialect.commnadListLength() + "(" + propertyName + ") <= " + value);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ListConstraint<M> isOrMoreThan(int value) {
            expression.add(dialect.commnadListLength() + "(" + propertyName + ") >= " + value);
            return this;
        }
    }
}