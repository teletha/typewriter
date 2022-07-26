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
}
