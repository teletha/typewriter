/*
 * Copyright (C) 2022 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package typewriter.api;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

public interface Constraint<V, Self> {

    /**
     * Describes conditions for the specified property.
     * 
     * @return Chainable API.
     */
    Self isNull();

    /**
     * Describes conditions for the specified property.
     * 
     * @return Chainable API.
     */
    Self isNotNull();

    /**
     * Describes conditions for the specified property.
     * 
     * @param value A conditional value.
     * @return Chainable API.
     */
    Self is(V value);

    /**
     * Describes conditions for the specified property.
     * 
     * @param value A conditional value.
     * @return Chainable API.
     */
    Self isNot(V value);

    /**
     * Describes conditions for the specified property.
     * 
     * @param values A conditional value set.
     * @return Chainable API.
     */
    Self oneOf(V... values);

    /**
     * Describes conditions for the specified property.
     * 
     * @param values A conditional value set.
     * @return Chainable API.
     */
    Self oneOf(Iterable<V> values);

    /**
     * The specialized {@link Constraint}.
     */
    interface TypeConstraint<T> extends Constraint<T, TypeConstraint<T>> {
    }

    /**
     * The specialized {@link Constraint} for {@link Number}.
     */
    interface NumericConstraint<V extends Number> extends Constraint<V, NumericConstraint<V>> {

        /**
         * Describes conditions for the specified property.
         * 
         * @param value A conditional value.
         * @return Chainable API.
         */
        NumericConstraint<V> isLessThan(V value);

        /**
         * Describes conditions for the specified property.
         * 
         * @param value A conditional value.
         * @return Chainable API.
         */
        NumericConstraint<V> isLessThanOrEqual(V value);

        /**
         * Describes conditions for the specified property.
         * 
         * @param value A conditional value.
         * @return Chainable API.
         */
        NumericConstraint<V> isGreaterThan(V value);

        /**
         * Describes conditions for the specified property.
         * 
         * @param value A conditional value.
         * @return Chainable API.
         */
        NumericConstraint<V> isGreaterThanOrEqual(V value);
    }

    /**
     * The specialized {@link Constraint} for {@link String}.
     */
    interface StringConstraint extends Constraint<String, StringConstraint> {

        /**
         * Describes conditions for the specified property.
         * 
         * @param value A conditional value.
         * @return Chainable API.
         */
        StringConstraint contains(String value);

        /**
         * Describes conditions for the specified property.
         * 
         * @param regex A conditional value.
         * @return Chainable API.
         */
        StringConstraint regex(String regex);

        /**
         * Describes conditions for the specified property.
         * 
         * @return Chainable API.
         */
        StringConstraint isEmpty();

        /**
         * Describes conditions for the specified property.
         * 
         * @return Chainable API.
         */
        StringConstraint isNotEmpty();

        /**
         * Describes conditions for the specified property.
         * 
         * @param value A conditional value.
         * @return Chainable API.
         */
        StringConstraint isLessThan(int value);

        /**
         * Describes conditions for the specified property.
         * 
         * @param value A conditional value.
         * @return Chainable API.
         */
        StringConstraint isLessThanOrEqual(int value);

        /**
         * Describes conditions for the specified property.
         * 
         * @param value A conditional value.
         * @return Chainable API.
         */
        StringConstraint isGreaterThan(int value);

        /**
         * Describes conditions for the specified property.
         * 
         * @param value A conditional value.
         * @return Chainable API.
         */
        StringConstraint isGreaterThanOrEqual(int value);
    }

    /**
     * The specialized {@link Constraint} for temporal value.
     */
    interface TemporalConstraint<T, Self extends TemporalConstraint<T, Self>> extends Constraint<T, Self> {

        /**
         * Describes conditions for the specified property.
         * 
         * @param year A conditional value.
         * @param month A conditional value
         * @param day A conditional value
         * @return Chainable API.
         */
        default Self is(int year, int month, int day) {
            return is(assembleTemporalValue(year, month, day, 0, 0, 0, 0));
        }

        /**
         * Describes conditions for the specified property.
         * 
         * @param year A conditional value.
         * @param month A conditional value
         * @param day A conditional value
         * @return Chainable API.
         */
        default Self isNot(int year, int month, int day) {
            return isNot(assembleTemporalValue(year, month, day, 0, 0, 0, 0));
        }

        /**
         * Describes conditions for the specified property.
         * 
         * @param year A conditional value.
         * @param month A conditional value
         * @param day A conditional value
         * @return Chainable API.
         */
        default Self isBefore(int year, int month, int day) {
            return isBefore(assembleTemporalValue(year, month, day, 0, 0, 0, 0));
        }

        /**
         * Describes conditions for the specified property.
         * 
         * @param date A conditional value.
         * @return Chainable API.
         */
        Self isBefore(T date);

        /**
         * Describes conditions for the specified property.
         * 
         * @param year A conditional value.
         * @param month A conditional value
         * @param day A conditional value
         * @return Chainable API.
         */
        default Self isBeforeOrSame(int year, int month, int day) {
            return isBeforeOrSame(assembleTemporalValue(year, month, day, 0, 0, 0, 0));
        }

        /**
         * Describes conditions for the specified property.
         * 
         * @param date A conditional value.
         * @return Chainable API.
         */
        Self isBeforeOrSame(T date);

        /**
         * Describes conditions for the specified property.
         * 
         * @param year A conditional value.
         * @param month A conditional value
         * @param day A conditional value
         * @return Chainable API.
         */
        default Self isAfter(int year, int month, int day) {
            return isAfter(assembleTemporalValue(year, month, day, 0, 0, 0, 0));
        }

        /**
         * Describes conditions for the specified property.
         * 
         * @param date A conditional value.
         * @return Chainable API.
         */
        Self isAfter(T date);

        /**
         * Describes conditions for the specified property.
         * 
         * @param year A conditional value.
         * @param month A conditional value
         * @param day A conditional value
         * @return Chainable API.
         */
        default Self isAfterOrSame(int year, int month, int day) {
            return isAfterOrSame(assembleTemporalValue(year, month, day, 0, 0, 0, 0));
        }

        /**
         * Describes conditions for the specified property.
         * 
         * @param date A conditional value.
         * @return Chainable API.
         */
        Self isAfterOrSame(T date);

        /**
         * This is internal API. Don't call in client.
         * 
         * @param year
         * @param month
         * @param day
         * @param hour
         * @param minute
         * @param second
         * @param milli
         * @return
         */
        T assembleTemporalValue(int year, int month, int day, int hour, int minute, int second, int milli);
    }

    /**
     * The specialized {@link Constraint} for {@link Date}.
     */
    interface DateConstraint extends TemporalConstraint<Date, DateConstraint> {

        /**
         * {@inheritDoc}
         */
        @Override
        default Date assembleTemporalValue(int year, int month, int day, int hour, int minute, int second, int milli) {
            return Date.from(LocalDateTime.of(year, month, day, hour, minute, second, milli).toInstant(ZoneOffset.UTC));
        }
    }

    /**
     * The specialized {@link Constraint} for {@link LocalDate}.
     */
    interface LocalDateConstraint extends TemporalConstraint<LocalDate, LocalDateConstraint> {

        /**
         * {@inheritDoc}
         */
        @Override
        default LocalDate assembleTemporalValue(int year, int month, int day, int hour, int minute, int second, int milli) {
            return LocalDate.of(year, month, day);
        }
    }

    /**
     * The specialized {@link Constraint} for {@link LocalTime}.
     */
    interface LocalTimeConstraint extends TemporalConstraint<LocalTime, LocalTimeConstraint> {

        /**
         * {@inheritDoc}
         */
        @Override
        default LocalTimeConstraint is(int hour, int minute, int second) {
            return is(assembleTemporalValue(0, 0, 0, hour, minute, second, 0));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        default LocalTimeConstraint isNot(int hour, int minute, int second) {
            return isNot(assembleTemporalValue(0, 0, 0, hour, minute, second, 0));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        default LocalTimeConstraint isBefore(int hour, int minute, int second) {
            return isBefore(assembleTemporalValue(0, 0, 0, hour, minute, second, 0));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        default LocalTimeConstraint isBeforeOrSame(int hour, int minute, int second) {
            return isBeforeOrSame(assembleTemporalValue(0, 0, 0, hour, minute, second, 0));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        default LocalTimeConstraint isAfter(int hour, int minute, int second) {
            return isAfter(assembleTemporalValue(0, 0, 0, hour, minute, second, 0));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        default LocalTimeConstraint isAfterOrSame(int hour, int minute, int second) {
            return isAfterOrSame(assembleTemporalValue(0, 0, 0, hour, minute, second, 0));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        default LocalTime assembleTemporalValue(int year, int month, int day, int hour, int minute, int second, int milli) {
            return LocalTime.of(hour, minute, second, milli * 1000000);
        }
    }

    /**
     * The specialized {@link Constraint} for {@link LocalDateTime}.
     */
    interface LocalDateTimeConstraint extends TemporalConstraint<LocalDateTime, LocalDateTimeConstraint> {

        /**
         * {@inheritDoc}
         */
        @Override
        default LocalDateTime assembleTemporalValue(int year, int month, int day, int hour, int minute, int second, int milli) {
            return LocalDateTime.of(year, month, day, hour, minute, second, milli);
        }
    }

    /**
     * The specialized {@link Constraint} for {@link OffsetDateTime}.
     */
    interface OffsetDateTimeConstraint extends TemporalConstraint<OffsetDateTime, OffsetDateTimeConstraint> {

        /**
         * {@inheritDoc}
         */
        @Override
        default OffsetDateTime assembleTemporalValue(int year, int month, int day, int hour, int minute, int second, int milli) {
            return OffsetDateTime.of(year, month, day, hour, minute, second, milli, ZoneOffset.UTC);
        }
    }

    /**
     * The specialized {@link Constraint} for {@link ZonedDateTime}.
     */
    interface ZonedDateTimeConstraint extends TemporalConstraint<ZonedDateTime, ZonedDateTimeConstraint> {

        /** Default zone */
        ZoneId UTC = ZoneId.of("UTC");

        /**
         * {@inheritDoc}
         */
        @Override
        default ZonedDateTime assembleTemporalValue(int year, int month, int day, int hour, int minute, int second, int milli) {
            return ZonedDateTime.of(year, month, day, hour, minute, second, milli, UTC);
        }
    }
}
