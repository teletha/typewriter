/*
 * Copyright (C) 2022 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package typewriter.rdb;

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
import typewriter.api.Constraint.ZonedDateTimeConstraint;
import typewriter.api.Queryable;
import typewriter.api.Specifier.BooleanSpecifier;
import typewriter.api.Specifier.CharSpecifier;
import typewriter.api.Specifier.DateSpecifier;
import typewriter.api.Specifier.LocalDateSpecifier;
import typewriter.api.Specifier.LocalDateTimeSpecifier;
import typewriter.api.Specifier.LocalTimeSpecifier;
import typewriter.api.Specifier.NumericSpecifier;
import typewriter.api.Specifier.StringSpecifier;
import typewriter.api.Specifier.ZonedDateTimeSpecifier;
import typewriter.api.model.IdentifiableModel;
import typewriter.rdb.RDBConstraint.ForDate;
import typewriter.rdb.RDBConstraint.ForLocalDate;
import typewriter.rdb.RDBConstraint.ForLocalDateTime;
import typewriter.rdb.RDBConstraint.ForLocalTime;
import typewriter.rdb.RDBConstraint.ForNumeric;
import typewriter.rdb.RDBConstraint.ForString;
import typewriter.rdb.RDBConstraint.ForZonedDateTime;
import typewriter.rdb.RDBConstraint.GenericType;

/**
 * {@link Queryable} for mongodb.
 */
public class RDBQuery<M extends IdentifiableModel> implements Queryable<M, RDBQuery<M>> {

    /** The all constraint set. */
    protected final List<RDBConstraint<?, ?>> constraints = new ArrayList();

    /** The limit size. */
    private long limit = -1;

    /**
     * Hide constructor.
     */
    private RDBQuery() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RDBQuery<M> findBy(Constraint constraint) {
        if (constraint != null) {
            constraints.add((RDBConstraint<?, ?>) constraint);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <N extends Number> RDBQuery<M> findBy(NumericSpecifier<M, N> specifier, UnaryOperator<NumericConstraint<N>> constraint) {
        return findBy(constraint.apply(new ForNumeric(specifier)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RDBQuery<M> findBy(CharSpecifier<M> specifier, UnaryOperator<TypeConstraint<Character>> constraint) {
        return findBy(constraint.apply(new GenericType(specifier)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RDBQuery<M> findBy(BooleanSpecifier<M> specifier, UnaryOperator<TypeConstraint<Boolean>> constraint) {
        return findBy(constraint.apply(new GenericType(specifier)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RDBQuery<M> findBy(StringSpecifier<M> specifier, UnaryOperator<StringConstraint> constraint) {
        return findBy(constraint.apply(new ForString(specifier)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RDBQuery<M> findBy(DateSpecifier<M> specifier, UnaryOperator<DateConstraint> constraint) {
        return findBy(constraint.apply(new ForDate(specifier)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RDBQuery<M> findBy(LocalDateSpecifier<M> specifier, UnaryOperator<LocalDateConstraint> constraint) {
        return findBy(constraint.apply(new ForLocalDate(specifier)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RDBQuery<M> findBy(LocalTimeSpecifier<M> specifier, UnaryOperator<LocalTimeConstraint> constraint) {
        return findBy(constraint.apply(new ForLocalTime(specifier)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RDBQuery<M> findBy(LocalDateTimeSpecifier<M> specifier, UnaryOperator<LocalDateTimeConstraint> constraint) {
        return findBy(constraint.apply(new ForLocalDateTime(specifier)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RDBQuery<M> findBy(ZonedDateTimeSpecifier<M> specifier, UnaryOperator<ZonedDateTimeConstraint> constraint) {
        return findBy(constraint.apply(new ForZonedDateTime(specifier)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RDBQuery<M> limit(long size) {
        if (0 <= size) {
            this.limit = size;
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        if (!constraints.isEmpty()) {
            StringJoiner joiner = new StringJoiner(" AND ", " WHERE ", "");
            for (RDBConstraint<?, ?> constraint : constraints) {
                for (String e : constraint.expression) {
                    joiner.add(e);
                }
            }
            builder.append(joiner);
        }

        if (0 < limit) {
            builder.append(" LIMIT ").append(limit);
        }
        return builder.toString();

    }
}