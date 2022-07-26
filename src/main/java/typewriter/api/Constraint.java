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
import java.time.ZoneOffset;
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
            return is(assembleTemporalValue(year, month, day));
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
            return isNot(assembleTemporalValue(year, month, day));
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
            return isBefore(assembleTemporalValue(year, month, day));
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
            return isBeforeOrSame(assembleTemporalValue(year, month, day));
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
            return isAfter(assembleTemporalValue(year, month, day));
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
            return isAfterOrSame(assembleTemporalValue(year, month, day));
        }

        /**
         * Describes conditions for the specified property.
         * 
         * @param date A conditional value.
         * @return Chainable API.
         */
        Self isAfterOrSame(T date);

        /**
         * Converter.
         * 
         * @param year
         * @param month
         * @param day
         * @return
         */
        T assembleTemporalValue(int year, int month, int day);
    }

    /**
     * The specialized {@link Constraint} for {@link Date}.
     */
    interface DateConstraint extends TemporalConstraint<Date, DateConstraint> {

        /**
         * {@inheritDoc}
         */
        @Override
        default Date assembleTemporalValue(int year, int month, int day) {
            return Date.from(LocalDateTime.of(year, month, day, 0, 0, 0, 0).toInstant(ZoneOffset.UTC));
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
        default LocalDate assembleTemporalValue(int year, int month, int day) {
            return LocalDate.of(year, month, day);
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
        default LocalDateTime assembleTemporalValue(int year, int month, int day) {
            return LocalDateTime.of(year, month, day, 0, 0, 0, 0);
        }
    }
}
