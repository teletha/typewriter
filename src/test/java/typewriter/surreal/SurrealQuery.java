/*
 * Copyright (C) 2025 The TYPEWRITER Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package typewriter.surreal;

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
import typewriter.surreal.SurrealConstraint.ForDate;
import typewriter.surreal.SurrealConstraint.ForList;
import typewriter.surreal.SurrealConstraint.ForLocalDate;
import typewriter.surreal.SurrealConstraint.ForLocalDateTime;
import typewriter.surreal.SurrealConstraint.ForLocalTime;
import typewriter.surreal.SurrealConstraint.ForNumeric;
import typewriter.surreal.SurrealConstraint.ForOffsetDateTime;
import typewriter.surreal.SurrealConstraint.ForString;
import typewriter.surreal.SurrealConstraint.ForZonedDateTime;
import typewriter.surreal.SurrealConstraint.GenericType;

/**
 * {@link Queryable} for mongodb.
 */
public class SurrealQuery<M> implements Queryable<M, SurrealQuery<M>> {

    /** The all constraint set. */
    protected final List<SurrealConstraint<?, ?>> constraints = new ArrayList();

    /** The limit size. */
    private int limit;

    /** The offset position. */
    private int offset;

    /** The sorting property. */
    private List<Ⅱ<String, Boolean>> sorts;

    /**
     * Hide constructor.
     */
    SurrealQuery() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <QUERYABLE extends Queryable<M, QUERYABLE>> SurrealQuery<M> query(Function<QUERYABLE, QUERYABLE> constraint) {
        return (SurrealQuery<M>) constraint.apply((QUERYABLE) this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SurrealQuery<M> findBy(Constraint constraint) {
        if (constraint != null) {
            constraints.add((SurrealConstraint<?, ?>) constraint);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SurrealQuery<M> findBy(BooleanSpecifier<M> constraint) {
        if (constraint != null) {
            constraints.add((SurrealConstraint<?, ?>) constraint);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <N extends Number> SurrealQuery<M> findBy(NumericSpecifier<M, N> specifier, UnaryOperator<NumericConstraint<N>> constraint) {
        return findBy(constraint.apply(new ForNumeric(specifier)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SurrealQuery<M> findBy(CharSpecifier<M> specifier, UnaryOperator<TypeConstraint<Character>> constraint) {
        return findBy(constraint.apply(new GenericType(specifier)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SurrealQuery<M> findBy(BooleanSpecifier<M> specifier, UnaryOperator<TypeConstraint<Boolean>> constraint) {
        return findBy(constraint.apply(new GenericType(specifier)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SurrealQuery<M> findBy(StringSpecifier<M> specifier, UnaryOperator<StringConstraint> constraint) {
        return findBy(constraint.apply(new ForString(specifier)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SurrealQuery<M> findBy(DateSpecifier<M> specifier, UnaryOperator<DateConstraint> constraint) {
        return findBy(constraint.apply(new ForDate(specifier)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SurrealQuery<M> findBy(LocalDateSpecifier<M> specifier, UnaryOperator<LocalDateConstraint> constraint) {
        return findBy(constraint.apply(new ForLocalDate(specifier)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SurrealQuery<M> findBy(LocalTimeSpecifier<M> specifier, UnaryOperator<LocalTimeConstraint> constraint) {
        return findBy(constraint.apply(new ForLocalTime(specifier)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SurrealQuery<M> findBy(LocalDateTimeSpecifier<M> specifier, UnaryOperator<LocalDateTimeConstraint> constraint) {
        return findBy(constraint.apply(new ForLocalDateTime(specifier)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SurrealQuery<M> findBy(OffsetDateTimeSpecifier<M> specifier, UnaryOperator<OffsetDateTimeConstraint> constraint) {
        return findBy(constraint.apply(new ForOffsetDateTime(specifier)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SurrealQuery<M> findBy(ZonedDateTimeSpecifier<M> specifier, UnaryOperator<ZonedDateTimeConstraint> constraint) {
        return findBy(constraint.apply(new ForZonedDateTime(specifier)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <N> SurrealQuery<M> findBy(ListSpecifier<M, N> specifier, UnaryOperator<ListConstraint<N>> constraint) {
        return findBy(constraint.apply(new ForList(specifier)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SurrealQuery<M> limit(long size) {
        if (0 < size) {
            this.limit = (int) size;
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SurrealQuery<M> offset(long position) {
        if (0 < position) {
            this.offset = (int) position;
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <N> SurrealQuery<M> sortBy(Specifier<M, N> specifier, boolean ascending) {
        if (sorts == null) {
            sorts = new ArrayList();
        }
        sorts.add(I.pair(specifier.propertyName(null), ascending));

        return this;
    }
}