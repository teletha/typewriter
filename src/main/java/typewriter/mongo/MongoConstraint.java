/*
 * Copyright (C) 2024 The TYPEWRITER Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package typewriter.mongo;

import static java.util.Objects.requireNonNull;
import static typewriter.api.Constraint.ZonedDateTimeConstraint.UTC;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import org.bson.BsonDocument;
import org.bson.conversions.Bson;

import com.mongodb.client.model.Filters;

import typewriter.api.Constraint;
import typewriter.api.Specifier;

/**
 * {@link Constraint} for mongdb.
 */
abstract class MongoConstraint<V, Self> implements Constraint<V, Self> {

    /** The name of target property. */
    protected String propertyName;

    /** All filters. */
    protected final List<Bson> filters = new ArrayList();

    /**
     * Hide constructor.
     * 
     * @param specifier The property specifier.
     */
    protected MongoConstraint(Specifier specifier) {
        this.propertyName = specifier.propertyName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Self isNull() {
        filters.add(Filters.not(Filters.exists(propertyName)));
        return (Self) this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Self isNotNull() {
        filters.add(Filters.exists(propertyName));
        return (Self) this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Self is(V value) {
        filters.add(Filters.eq(propertyName, validate(value)));
        return (Self) this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Self isNot(V value) {
        filters.add(Filters.ne(propertyName, validate(value)));
        return (Self) this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Self oneOf(V... values) {
        filters.add(Filters.in(propertyName, values));
        return (Self) this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Self oneOf(Iterable<V> values) {
        filters.add(Filters.in(propertyName, values));
        return (Self) this;
    }

    /**
     * Validate and convert the value.
     * 
     * @param value
     * @return
     */
    protected Object validate(V value) {
        return Objects.requireNonNull(value);
    }

    /**
     * The specialized {@link Constraint}.
     */
    static class GenericType<T> extends MongoConstraint<T, TypeConstraint<T>> implements TypeConstraint<T> {
        GenericType(Specifier specifier) {
            super(specifier);
        }
    }

    /**
     * The specialized {@link Constraint} for {@link Number}.
     */
    static class ForNumeric<V extends Number> extends MongoConstraint<V, NumericConstraint<V>> implements NumericConstraint<V> {

        protected ForNumeric(Specifier specifier) {
            super(specifier);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public NumericConstraint<V> isLessThan(V value) {
            filters.add(Filters.lt(propertyName, value));
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public NumericConstraint<V> isOrLessThan(V value) {
            filters.add(Filters.lte(propertyName, value));
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public NumericConstraint<V> isMoreThan(V value) {
            filters.add(Filters.gt(propertyName, value));
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public NumericConstraint<V> isOrMoreThan(V value) {
            filters.add(Filters.gte(propertyName, value));
            return this;
        }
    }

    /**
     * The specialized {@link Constraint} for {@link String}.
     */
    static class ForString extends MongoConstraint<String, StringConstraint> implements StringConstraint {
        ForString(Specifier specifier) {
            super(specifier);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public StringConstraint isEmpty() {
            filters.add(Filters.eq(propertyName, ""));
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public StringConstraint isNotEmpty() {
            filters.add(Filters.not(Filters.eq(propertyName, "")));
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public StringConstraint contains(String value) {
            return regex(".*" + value + ".*");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public StringConstraint regex(String regex) {
            filters.add(Filters.regex(propertyName, Pattern.compile(regex)));
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public StringConstraint isLessThan(int value) {
            filters.add(Filters.expr(BsonDocument.parse("{$lt: [{ $strLenCP : '$" + propertyName + "' }, " + value + "]} ")));
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public StringConstraint isOrLessThan(int value) {
            filters.add(Filters.expr(BsonDocument.parse("{$lte: [{ $strLenCP : '$" + propertyName + "' }, " + value + "]} ")));
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public StringConstraint isMoreThan(int value) {
            filters.add(Filters.expr(BsonDocument.parse("{$gt: [{ $strLenCP : '$" + propertyName + "' }, " + value + "]} ")));
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public StringConstraint isOrMoreThan(int value) {
            filters.add(Filters.expr(BsonDocument.parse("{$gte: [{ $strLenCP : '$" + propertyName + "' }, " + value + "]} ")));
            return this;
        }
    }

    /**
     * The specialized {@link Constraint} for {@link TemporalAccessor}.
     */
    static abstract class ForTermporal<T, Self extends TemporalConstraint<T, Self>> extends MongoConstraint<T, Self>
            implements TemporalConstraint<T, Self> {

        protected ForTermporal(Specifier specifier) {
            super(specifier);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Self isBefore(T date) {
            filters.add(Filters.lt(propertyName, validate(date)));
            return (Self) this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Self isBeforeOrSame(T date) {
            filters.add(Filters.lte(propertyName, validate(date)));
            return (Self) this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Self isAfter(T date) {
            filters.add(Filters.gt(propertyName, validate(date)));
            return (Self) this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Self isAfterOrSame(T date) {
            filters.add(Filters.gte(propertyName, validate(date)));
            return (Self) this;
        }
    }

    /**
     * The specialized {@link Constraint} for {@link Date}.
     */
    static class ForDate extends ForTermporal<Date, DateConstraint> implements DateConstraint {
        ForDate(Specifier specifier) {
            super(specifier);
        }
    }

    /**
     * The specialized {@link Constraint} for {@link LocalDate}.
     */
    static class ForLocalDate extends ForTermporal<LocalDate, LocalDateConstraint> implements LocalDateConstraint {
        ForLocalDate(Specifier specifier) {
            super(specifier);
        }
    }

    /**
     * The specialized {@link Constraint} for {@link LocalTime}.
     */
    static class ForLocalTime extends ForTermporal<LocalTime, LocalTimeConstraint> implements LocalTimeConstraint {
        ForLocalTime(Specifier specifier) {
            super(specifier);
        }
    }

    /**
     * The specialized {@link Constraint} for {@link LocalDateTime}.
     */
    static class ForLocalDateTime extends ForTermporal<LocalDateTime, LocalDateTimeConstraint> implements LocalDateTimeConstraint {
        ForLocalDateTime(Specifier specifier) {
            super(specifier);
        }
    }

    /**
     * The specialized {@link Constraint} for {@link OffsetDateTime}.
     */
    static class ForOffsetDateTime extends ForTermporal<OffsetDateTime, OffsetDateTimeConstraint> implements OffsetDateTimeConstraint {
        ForOffsetDateTime(Specifier specifier) {
            super(specifier);

            propertyName = propertyName + ".date";
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected Object validate(OffsetDateTime value) {
            return requireNonNull(value).atZoneSameInstant(UTC).toLocalDateTime();
        }
    }

    /**
     * The specialized {@link Constraint} for {@link ZonedDateTime}.
     */
    static class ForZonedDateTime extends ForTermporal<ZonedDateTime, ZonedDateTimeConstraint> implements ZonedDateTimeConstraint {
        ForZonedDateTime(Specifier specifier) {
            super(specifier);

            propertyName = propertyName + ".date";
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected Object validate(ZonedDateTime value) {
            return requireNonNull(value).withZoneSameInstant(UTC).toLocalDateTime();
        }
    }

    /**
     * The specialized {@link Constraint} for {@link List}.
     */
    static class ForList<M> extends MongoConstraint<List<M>, ListConstraint<M>> implements ListConstraint<M> {
        ForList(Specifier specifier) {
            super(specifier);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ListConstraint<M> contains(M value) {
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ListConstraint<M> size(int value) {
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ListConstraint<M> isMoreThan(int value) {
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ListConstraint<M> isLessThan(int value) {
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ListConstraint<M> isOrLessThan(int value) {
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public ListConstraint<M> isOrMoreThan(int value) {
            return null;
        }
    }
}