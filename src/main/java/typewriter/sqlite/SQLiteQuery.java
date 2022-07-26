/*
 * Copyright (C) 2022 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package typewriter.sqlite;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import typewriter.api.Constraint;
import typewriter.api.Constraint.DateConstraint;
import typewriter.api.Constraint.LocalDateConstraint;
import typewriter.api.Constraint.LocalDateTimeConstraint;
import typewriter.api.Constraint.LocalTimeConstraint;
import typewriter.api.Constraint.NumericConstraint;
import typewriter.api.Constraint.StringConstraint;
import typewriter.api.Queryable;
import typewriter.api.Specifier;
import typewriter.api.model.IdentifiableModel;
import typewriter.sqlite.SQLiteConstraint.ForDate;
import typewriter.sqlite.SQLiteConstraint.ForLocalDate;
import typewriter.sqlite.SQLiteConstraint.ForLocalDateTime;
import typewriter.sqlite.SQLiteConstraint.ForLocalTime;
import typewriter.sqlite.SQLiteConstraint.ForNumeric;
import typewriter.sqlite.SQLiteConstraint.ForString;
import typewriter.sqlite.SQLiteConstraint.GenericType;

/**
 * {@link Queryable} for mongodb.
 */
public class SQLiteQuery<M extends IdentifiableModel> extends Queryable<M, SQLiteQuery<M>> {

    /** The all constraint set. */
    protected final List<SQLiteConstraint<?, ?>> constraints = new ArrayList();

    /**
     * Hide constructor.
     */
    private SQLiteQuery() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SQLiteQuery<M> findBy(Constraint constraint) {
        constraints.add((SQLiteConstraint<?, ?>) constraint);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected <C extends Constraint<T, C>, T> C createConstraint(Class<C> constraintType, Specifier specifier) {
        if (NumericConstraint.class == constraintType) {
            return (C) new ForNumeric(specifier);
        } else if (StringConstraint.class == constraintType) {
            return (C) new ForString(specifier);
        } else if (DateConstraint.class == constraintType) {
            return (C) new ForDate(specifier);
        } else if (LocalDateConstraint.class == constraintType) {
            return (C) new ForLocalDate(specifier);
        } else if (LocalDateTimeConstraint.class == constraintType) {
            return (C) new ForLocalDateTime(specifier);
        } else if (LocalTimeConstraint.class == constraintType) {
            return (C) new ForLocalTime(specifier);
        } else {
            return (C) new GenericType(specifier);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringJoiner builder = new StringJoiner(" AND ", " WHERE ", "");
        for (SQLiteConstraint<?, ?> constraint : constraints) {
            for (String e : constraint.expression) {
                builder.add(e);
            }
        }
        return builder.toString();
    }
}