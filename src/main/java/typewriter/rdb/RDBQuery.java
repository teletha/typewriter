/*
 * Copyright (C) 2024 The TYPEWRITER Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package typewriter.rdb;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import kiss.I;
import kiss.Ⅱ;
import typewriter.api.Constraint;
import typewriter.api.Constraint.DateConstraint;
import typewriter.api.Constraint.ListConstraint;
import typewriter.api.Constraint.LocalDateConstraint;
import typewriter.api.Constraint.LocalDateTimeConstraint;
import typewriter.api.Constraint.LocalTimeConstraint;
import typewriter.api.Constraint.NumericConstraint;
import typewriter.api.Constraint.OffsetDateTimeConstraint;
import typewriter.api.Constraint.StringConstraint;
import typewriter.api.Constraint.TypeConstraint;
import typewriter.api.Constraint.ZonedDateTimeConstraint;
import typewriter.api.Identifiable;
import typewriter.api.Queryable;
import typewriter.api.Specifier;
import typewriter.api.Specifier.BooleanSpecifier;
import typewriter.api.Specifier.CharSpecifier;
import typewriter.api.Specifier.DateSpecifier;
import typewriter.api.Specifier.ListSpecifier;
import typewriter.api.Specifier.LocalDateSpecifier;
import typewriter.api.Specifier.LocalDateTimeSpecifier;
import typewriter.api.Specifier.LocalTimeSpecifier;
import typewriter.api.Specifier.NumericSpecifier;
import typewriter.api.Specifier.OffsetDateTimeSpecifier;
import typewriter.api.Specifier.StringSpecifier;
import typewriter.api.Specifier.ZonedDateTimeSpecifier;
import typewriter.rdb.RDBConstraint.ForDate;
import typewriter.rdb.RDBConstraint.ForLocalDate;
import typewriter.rdb.RDBConstraint.ForLocalDateTime;
import typewriter.rdb.RDBConstraint.ForLocalTime;
import typewriter.rdb.RDBConstraint.ForNumeric;
import typewriter.rdb.RDBConstraint.ForOffsetDateTime;
import typewriter.rdb.RDBConstraint.ForString;
import typewriter.rdb.RDBConstraint.ForZonedDateTime;
import typewriter.rdb.RDBConstraint.GenericType;

/**
 * {@link Queryable} for mongodb.
 */
public class RDBQuery<M extends Identifiable> implements Queryable<M, RDBQuery<M>> {

    /** The all constraint set. */
    protected final List<RDBConstraint<?, ?>> constraints = new ArrayList();

    protected final Dialect dialect;

    /** The limit size. */
    long limit;

    /** The starting position. */
    long offset;

    /** The sorting property. */
    List<Ⅱ<Specifier, Boolean>> sorts;

    /**
     * Hide constructor.
     */
    RDBQuery(Dialect dialect) {
        this.dialect = dialect;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <QUERYABLE extends Queryable<M, QUERYABLE>> RDBQuery<M> query(Function<QUERYABLE, QUERYABLE> constraint) {
        return (RDBQuery<M>) constraint.apply((QUERYABLE) this);
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
        System.out.println(specifier.propertyName(dialect));
        return findBy(constraint.apply(new ForNumeric(specifier, dialect)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RDBQuery<M> findBy(CharSpecifier<M> specifier, UnaryOperator<TypeConstraint<Character>> constraint) {
        return findBy(constraint.apply(new GenericType(specifier, dialect)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RDBQuery<M> findBy(BooleanSpecifier<M> specifier, UnaryOperator<TypeConstraint<Boolean>> constraint) {
        return findBy(constraint.apply(new GenericType(specifier, dialect)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RDBQuery<M> findBy(StringSpecifier<M> specifier, UnaryOperator<StringConstraint> constraint) {
        return findBy(constraint.apply(new ForString(specifier, dialect)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RDBQuery<M> findBy(DateSpecifier<M> specifier, UnaryOperator<DateConstraint> constraint) {
        return findBy(constraint.apply(new ForDate(specifier, dialect)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RDBQuery<M> findBy(LocalDateSpecifier<M> specifier, UnaryOperator<LocalDateConstraint> constraint) {
        return findBy(constraint.apply(new ForLocalDate(specifier, dialect)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RDBQuery<M> findBy(LocalTimeSpecifier<M> specifier, UnaryOperator<LocalTimeConstraint> constraint) {
        return findBy(constraint.apply(new ForLocalTime(specifier, dialect)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RDBQuery<M> findBy(LocalDateTimeSpecifier<M> specifier, UnaryOperator<LocalDateTimeConstraint> constraint) {
        return findBy(constraint.apply(new ForLocalDateTime(specifier, dialect)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RDBQuery<M> findBy(OffsetDateTimeSpecifier<M> specifier, UnaryOperator<OffsetDateTimeConstraint> constraint) {
        return findBy(constraint.apply(new ForOffsetDateTime(specifier, dialect)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RDBQuery<M> findBy(ZonedDateTimeSpecifier<M> specifier, UnaryOperator<ZonedDateTimeConstraint> constraint) {
        return findBy(constraint.apply(new ForZonedDateTime(specifier, dialect)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <N> RDBQuery<M> findBy(ListSpecifier<M, N> specifier, UnaryOperator<ListConstraint<N>> constraint) {
        return findBy(constraint.apply(dialect.createListConstraint(specifier)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RDBQuery<M> limit(long size) {
        if (0 < size) {
            this.limit = size;
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RDBQuery<M> offset(long position) {
        if (0 < position) {
            this.offset = position;
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <N> RDBQuery<M> sortBy(Specifier<M, N> specifier, boolean ascending) {
        if (sorts == null) {
            sorts = new ArrayList();
        }
        sorts.add(I.pair(specifier, ascending));

        return this;
    }
}