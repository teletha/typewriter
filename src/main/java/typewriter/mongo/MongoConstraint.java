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

import java.beans.Introspector;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.bson.conversions.Bson;

import com.mongodb.client.model.Filters;

import typewriter.api.Constraint;
import typewriter.api.Specifier;

/**
 * {@link Constraint} for mongdb.
 */
abstract class MongoConstraint<V, Self> implements Constraint<V, Self> {

    /** The name of target property. */
    protected final String propertyName;

    /** All filters. */
    protected final List<Bson> filters = new ArrayList();

    /**
     * Hide constructor.
     * 
     * @param specifier The property specifier.
     */
    protected MongoConstraint(Specifier specifier) {
        Method method = specifier.method();
        String name = method.getName();
        if (method.getReturnType() == boolean.class) {
            if (name.startsWith("is")) {
                name = name.substring(2);
            }
        } else {
            if (name.startsWith("get")) {
                name = name.substring(3);
            }
        }

        this.propertyName = Introspector.decapitalize(name);
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
    public Self notNull() {
        filters.add(Filters.exists(propertyName));
        return (Self) this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Self is(V value) {
        filters.add(Filters.eq(propertyName, value));
        return (Self) this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Self isNot(V value) {
        filters.add(Filters.ne(propertyName, value));
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

}