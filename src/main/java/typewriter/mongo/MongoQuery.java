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

import java.util.ArrayList;
import java.util.List;

import org.bson.conversions.Bson;

import com.mongodb.client.model.Filters;

import kiss.I;
import typewriter.api.Constraint;
import typewriter.api.Constraint.NumericConstraint;
import typewriter.api.Constraint.StringConstraint;
import typewriter.api.Queryable;
import typewriter.api.Specifier;
import typewriter.mongo.MongoConstraint.ForNumeric;
import typewriter.mongo.MongoConstraint.ForString;
import typewriter.mongo.MongoConstraint.GenericType;

/**
 * {@link Queryable} for mongodb.
 */
public class MongoQuery<M> extends Queryable<M, MongoQuery<M>> {

    /** The all constraint set. */
    protected final List<MongoConstraint<?, ?>> constraints = new ArrayList();

    /** The cache. */
    protected Bson cache;

    /** The cache state. */
    protected boolean modified = true;

    /**
     * Hide constructor.
     */
    private MongoQuery() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MongoQuery<M> findBy(Constraint constraint) {
        constraints.add((MongoConstraint<?, ?>) constraint);
        modified = true;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected <C extends Constraint<T, C>, T> C createConstraint(Class<C> constraintType, Specifier specifier) {
        if (NumericConstraint.class.isAssignableFrom(constraintType)) {
            return (C) new ForNumeric(specifier);
        } else if (StringConstraint.class.isAssignableFrom(constraintType)) {
            return (C) new ForString(specifier);
        } else {
            return (C) new GenericType(specifier);
        }
    }

    /**
     * Build the actual query.
     * 
     * @return
     */
    Bson build() {
        if (modified) {
            modified = false;
            cache = Filters.and(I.signal(constraints).flatIterable(c -> c.filters).toList());
        }
        return cache;
    }
}