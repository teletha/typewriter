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
import java.util.function.UnaryOperator;

import typewriter.api.Constraint;
import typewriter.api.Constraint.DateConstraint;
import typewriter.api.Constraint.LocalDateConstraint;
import typewriter.api.Constraint.LocalDateTimeConstraint;
import typewriter.api.Constraint.LocalTimeConstraint;
import typewriter.api.Constraint.NumericConstraint;
import typewriter.api.Constraint.StringConstraint;
import typewriter.api.Constraint.TypeConstraint;
import typewriter.api.Queryable;
import typewriter.api.Specifier.BooleanSpecifier;
import typewriter.api.Specifier.CharSpecifier;
import typewriter.api.Specifier.DateSpecifier;
import typewriter.api.Specifier.LocalDateSpecifier;
import typewriter.api.Specifier.LocalDateTimeSpecifier;
import typewriter.api.Specifier.LocalTimeSpecifier;
import typewriter.api.Specifier.NumericSpecifier;
import typewriter.api.Specifier.StringSpecifier;
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
public class SQLiteQuery<M extends IdentifiableModel> implements Queryable<M, SQLiteQuery<M>> {

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
    public <N extends Number> SQLiteQuery<M> findBy(NumericSpecifier<M, N> specifier, UnaryOperator<NumericConstraint<N>> constraint) {
        return findBy(constraint.apply(new ForNumeric(specifier)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SQLiteQuery<M> findBy(CharSpecifier<M> specifier, UnaryOperator<TypeConstraint<Character>> constraint) {
        return findBy(constraint.apply(new GenericType(specifier)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SQLiteQuery<M> findBy(BooleanSpecifier<M> specifier, UnaryOperator<TypeConstraint<Boolean>> constraint) {
        return findBy(constraint.apply(new GenericType(specifier)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SQLiteQuery<M> findBy(StringSpecifier<M> specifier, UnaryOperator<StringConstraint> constraint) {
        return findBy(constraint.apply(new ForString(specifier)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SQLiteQuery<M> findBy(DateSpecifier<M> specifier, UnaryOperator<DateConstraint> constraint) {
        return findBy(constraint.apply(new ForDate(specifier)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SQLiteQuery<M> findBy(LocalDateSpecifier<M> specifier, UnaryOperator<LocalDateConstraint> constraint) {
        return findBy(constraint.apply(new ForLocalDate(specifier)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SQLiteQuery<M> findBy(LocalTimeSpecifier<M> specifier, UnaryOperator<LocalTimeConstraint> constraint) {
        return findBy(constraint.apply(new ForLocalTime(specifier)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SQLiteQuery<M> findBy(LocalDateTimeSpecifier<M> specifier, UnaryOperator<LocalDateTimeConstraint> constraint) {
        return findBy(constraint.apply(new ForLocalDateTime(specifier)));
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