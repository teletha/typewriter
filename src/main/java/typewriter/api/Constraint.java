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
     * The specialized {@link Constraint} for {@link Date}.
     */
    interface DateConstraint extends Constraint<Date, DateConstraint> {

        /**
         * Describes conditions for the specified property.
         * 
         * @param year A conditional value.
         * @param month A conditional value
         * @param day A conditional value
         * @return Chainable API.
         */
        default DateConstraint is(int year, int month, int day) {
            return is(parse(year, month, day));
        }

        /**
         * Describes conditions for the specified property.
         * 
         * @param year A conditional value.
         * @param month A conditional value
         * @param day A conditional value
         * @return Chainable API.
         */
        default DateConstraint isNot(int year, int month, int day) {
            return isNot(parse(year, month, day));
        }

        /**
         * Describes conditions for the specified property.
         * 
         * @param year A conditional value.
         * @param month A conditional value
         * @param day A conditional value
         * @return Chainable API.
         */
        default DateConstraint isBefore(int year, int month, int day) {
            return isBefore(parse(year, month, day));
        }

        /**
         * Describes conditions for the specified property.
         * 
         * @param date A conditional value.
         * @return Chainable API.
         */
        DateConstraint isBefore(Date date);

        /**
         * Describes conditions for the specified property.
         * 
         * @param year A conditional value.
         * @param month A conditional value
         * @param day A conditional value
         * @return Chainable API.
         */
        default DateConstraint isBeforeOrSame(int year, int month, int day) {
            return isBeforeOrSame(parse(year, month, day));
        }

        /**
         * Describes conditions for the specified property.
         * 
         * @param date A conditional value.
         * @return Chainable API.
         */
        DateConstraint isBeforeOrSame(Date date);

        /**
         * Describes conditions for the specified property.
         * 
         * @param year A conditional value.
         * @param month A conditional value
         * @param day A conditional value
         * @return Chainable API.
         */
        default DateConstraint isAfter(int year, int month, int day) {
            return isAfter(parse(year, month, day));
        }

        /**
         * Describes conditions for the specified property.
         * 
         * @param date A conditional value.
         * @return Chainable API.
         */
        DateConstraint isAfter(Date date);

        /**
         * Describes conditions for the specified property.
         * 
         * @param year A conditional value.
         * @param month A conditional value
         * @param day A conditional value
         * @return Chainable API.
         */
        default DateConstraint isAfterOrSame(int year, int month, int day) {
            return isAfterOrSame(parse(year, month, day));
        }

        /**
         * Describes conditions for the specified property.
         * 
         * @param date A conditional value.
         * @return Chainable API.
         */
        DateConstraint isAfterOrSame(Date date);

        /**
         * Converter.
         * 
         * @param year
         * @param month
         * @param day
         * @return
         */
        private Date parse(int year, int month, int day) {
            return Date.from(LocalDateTime.of(year, month, day, 0, 0, 0, 0).toInstant(ZoneOffset.UTC));
        }
    }

    /**
     * The specialized {@link Constraint} for {@link LocalDate}.
     */
    interface LocalDateConstraint extends Constraint<LocalDate, LocalDateConstraint> {

        /**
         * Describes conditions for the specified property.
         * 
         * @param year A conditional value.
         * @param month A conditional value
         * @param day A conditional value
         * @return Chainable API.
         */
        default LocalDateConstraint is(int year, int month, int day) {
            return is(parse(year, month, day));
        }

        /**
         * Describes conditions for the specified property.
         * 
         * @param year A conditional value.
         * @param month A conditional value
         * @param day A conditional value
         * @return Chainable API.
         */
        default LocalDateConstraint isNot(int year, int month, int day) {
            return isNot(parse(year, month, day));
        }

        /**
         * Describes conditions for the specified property.
         * 
         * @param year A conditional value.
         * @param month A conditional value
         * @param day A conditional value
         * @return Chainable API.
         */
        default LocalDateConstraint isBefore(int year, int month, int day) {
            return isBefore(parse(year, month, day));
        }

        /**
         * Describes conditions for the specified property.
         * 
         * @param date A conditional value.
         * @return Chainable API.
         */
        LocalDateConstraint isBefore(LocalDate date);

        /**
         * Describes conditions for the specified property.
         * 
         * @param year A conditional value.
         * @param month A conditional value
         * @param day A conditional value
         * @return Chainable API.
         */
        default LocalDateConstraint isBeforeOrSame(int year, int month, int day) {
            return isBeforeOrSame(parse(year, month, day));
        }

        /**
         * Describes conditions for the specified property.
         * 
         * @param date A conditional value.
         * @return Chainable API.
         */
        LocalDateConstraint isBeforeOrSame(LocalDate date);

        /**
         * Describes conditions for the specified property.
         * 
         * @param year A conditional value.
         * @param month A conditional value
         * @param day A conditional value
         * @return Chainable API.
         */
        default LocalDateConstraint isAfter(int year, int month, int day) {
            return isAfter(parse(year, month, day));
        }

        /**
         * Describes conditions for the specified property.
         * 
         * @param date A conditional value.
         * @return Chainable API.
         */
        LocalDateConstraint isAfter(LocalDate date);

        /**
         * Describes conditions for the specified property.
         * 
         * @param year A conditional value.
         * @param month A conditional value
         * @param day A conditional value
         * @return Chainable API.
         */
        default LocalDateConstraint isAfterOrSame(int year, int month, int day) {
            return isAfterOrSame(parse(year, month, day));
        }

        /**
         * Describes conditions for the specified property.
         * 
         * @param date A conditional value.
         * @return Chainable API.
         */
        LocalDateConstraint isAfterOrSame(LocalDate date);

        /**
         * Converter.
         * 
         * @param year
         * @param month
         * @param day
         * @return
         */
        private LocalDate parse(int year, int month, int day) {
            return LocalDate.of(year, month, day);
        }
    }
}
