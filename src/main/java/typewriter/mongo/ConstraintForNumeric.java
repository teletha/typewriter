/*
 * Copyright (C) 2022 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package typewriter.mongo;

import com.mongodb.client.model.Filters;

import typewriter.api.Constraint;
import typewriter.api.Specifier;
import typewriter.api.Constraint.NumericConstraint;

/**
 * The specialized {@link Constraint} for {@link Number}.
 */
class ConstraintForNumeric<V extends Number> extends MongoConstraint<V, NumericConstraint<V>> implements NumericConstraint<V> {

    protected ConstraintForNumeric(Specifier specifier) {
        super(specifier);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NumericConstraint<V> lessThan(V value) {
        filters.add(Filters.lt(propertyName, value));
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NumericConstraint<V> lessThanOrEqual(V value) {
        filters.add(Filters.lte(propertyName, value));
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NumericConstraint<V> greaterThan(V value) {
        filters.add(Filters.gt(propertyName, value));
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NumericConstraint<V> greaterThanOrEqual(V value) {
        filters.add(Filters.gte(propertyName, value));
        return this;
    }
}