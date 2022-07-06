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

import java.util.regex.Pattern;

import org.bson.BsonDocument;

import com.mongodb.client.model.Filters;

import typewriter.api.Constraint;
import typewriter.api.Constraint.StringConstraint;
import typewriter.api.Specifier;

/**
 * The specialized {@link Constraint} for {@link String}.
 */
class ConstraintForString extends MongoConstraint<String, StringConstraint> implements StringConstraint {
    ConstraintForString(Specifier specifier) {
        super(specifier);
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
    public StringConstraint notEmpty() {
        filters.add(Filters.not(Filters.eq(propertyName, "")));
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StringConstraint lessThan(int value) {
        filters.add(Filters.expr(BsonDocument.parse("{$lt: [{ $strLenCP : '$" + propertyName + "' }, " + value + "]} ")));
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StringConstraint lessThanOrEqual(int value) {
        filters.add(Filters.expr(BsonDocument.parse("{$lte: [{ $strLenCP : '$" + propertyName + "' }, " + value + "]} ")));
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StringConstraint greaterThan(int value) {
        filters.add(Filters.expr(BsonDocument.parse("{$gt: [{ $strLenCP : '$" + propertyName + "' }, " + value + "]} ")));
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StringConstraint greaterThanOrEqual(int value) {
        filters.add(Filters.expr(BsonDocument.parse("{$gte: [{ $strLenCP : '$" + propertyName + "' }, " + value + "]} ")));
        return this;
    }
}