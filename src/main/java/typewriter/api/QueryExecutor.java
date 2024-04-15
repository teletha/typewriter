/*
 * Copyright (C) 2024 The TYPEWRITER Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package typewriter.api;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import kiss.I;
import kiss.Model;
import kiss.Signal;
import kiss.WiseFunction;
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
import typewriter.rdb.AVGOption;

public abstract class QueryExecutor<M extends Identifiable, R, Q extends Queryable<M, Q>, Self extends QueryExecutor<M, R, Q, Self>>
        implements Queryable<M, R>, Accumulable<M>, Updatable<M>, Deletable<M>, Restorable<M>, Transactional<Self> {

    /**
     * Create {@link Queryable}.
     * 
     * @return
     */
    protected Q createQueryable() {
        Type type = Model.collectParameters(getClass(), QueryExecutor.class)[2];
        if (type instanceof ParameterizedType param) {
            return I.make((Class<Q>) param.getRawType());
        } else {
            return I.make((Class<Q>) type);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <QUERYABLE extends Queryable<M, QUERYABLE>> R query(Function<QUERYABLE, QUERYABLE> constraint) {
        return findBy((Q) constraint.apply((QUERYABLE) createQueryable()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R findBy(Constraint constraint) {
        return findBy(createQueryable().findBy(constraint));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <N extends Number> R findBy(NumericSpecifier<M, N> specifier, UnaryOperator<NumericConstraint<N>> constraint) {
        return findBy(createQueryable().findBy(specifier, constraint));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R findBy(CharSpecifier<M> specifier, UnaryOperator<TypeConstraint<Character>> constraint) {
        return findBy(createQueryable().findBy(specifier, constraint));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R findBy(BooleanSpecifier<M> specifier, UnaryOperator<TypeConstraint<Boolean>> constraint) {
        return findBy(createQueryable().findBy(specifier, constraint));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R findBy(StringSpecifier<M> specifier, UnaryOperator<StringConstraint> constraint) {
        return findBy(createQueryable().findBy(specifier, constraint));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R findBy(DateSpecifier<M> specifier, UnaryOperator<DateConstraint> constraint) {
        return findBy(createQueryable().findBy(specifier, constraint));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R findBy(LocalDateSpecifier<M> specifier, UnaryOperator<LocalDateConstraint> constraint) {
        return findBy(createQueryable().findBy(specifier, constraint));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R findBy(LocalTimeSpecifier<M> specifier, UnaryOperator<LocalTimeConstraint> constraint) {
        return findBy(createQueryable().findBy(specifier, constraint));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R findBy(LocalDateTimeSpecifier<M> specifier, UnaryOperator<LocalDateTimeConstraint> constraint) {
        return findBy(createQueryable().findBy(specifier, constraint));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R findBy(OffsetDateTimeSpecifier<M> specifier, UnaryOperator<OffsetDateTimeConstraint> constraint) {
        return findBy(createQueryable().findBy(specifier, constraint));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R findBy(ZonedDateTimeSpecifier<M> specifier, UnaryOperator<ZonedDateTimeConstraint> constraint) {
        return findBy(createQueryable().findBy(specifier, constraint));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <N> R findBy(ListSpecifier<M, N> specifier, UnaryOperator<ListConstraint<N>> constraint) {
        return findBy(createQueryable().findBy(specifier, constraint));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <R> R transactWith(WiseFunction<Self, R> operation) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Signal<M> restore(M model, Specifier<M, ?>... specifiers) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(M model, Specifier<M, ?>... specifiers) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(M model, Specifier<M, ?>... specifiers) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long count() {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <V> Signal<V> distinct(Specifier<M, V> specifier) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <C extends Comparable> C min(Specifier<M, C> specifier) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <C extends Comparable> C max(Specifier<M, C> specifier) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <N extends Number> double avg(Specifier<M, N> specifier, UnaryOperator<AVGOption> option) {
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <N extends Number> N sum(Specifier<M, N> specifier) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R limit(long size) {
        return findBy(createQueryable().limit(size));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R offset(long position) {
        return findBy(createQueryable().offset(position));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <N> R sortBy(Specifier<M, N> specifier, boolean ascending) {
        return findBy(createQueryable().sortBy(specifier, ascending));
    }

    /**
     * Find model by id.
     * 
     * @param id An identifier of the target model.
     * @return A reuslt stream.
     */
    public R findBy(long id) {
        return findBy(M::getId, v -> v.is(id));
    }

    /**
     * Find model by query.
     * 
     * @param query A query builder.
     * @return A result stream.
     */
    public abstract R findBy(Q query);

    /**
     * Helper to convert from {@link Specifier} to property names.
     * 
     * @param specifiers
     * @return
     */
    protected static Signal<String> names(Specifier[] specifiers) {
        return I.signal(specifiers).skipNull().map(specifier -> specifier.propertyName());
    }
}