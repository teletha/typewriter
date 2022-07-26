/*
 * Copyright (C) 2022 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package typewriter.api;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.function.UnaryOperator;

import kiss.I;
import kiss.model.Model;
import typewriter.api.Constraint.DateConstraint;
import typewriter.api.Constraint.LocalDateConstraint;
import typewriter.api.Constraint.LocalDateTimeConstraint;
import typewriter.api.Constraint.LocalTimeConstraint;
import typewriter.api.Constraint.NumericConstraint;
import typewriter.api.Constraint.StringConstraint;
import typewriter.api.Constraint.TypeConstraint;
import typewriter.api.Specifier.BooleanSpecifier;
import typewriter.api.Specifier.CharSpecifier;
import typewriter.api.Specifier.DateSpecifier;
import typewriter.api.Specifier.LocalDateSpecifier;
import typewriter.api.Specifier.LocalDateTimeSpecifier;
import typewriter.api.Specifier.LocalTimeSpecifier;
import typewriter.api.Specifier.NumericSpecifier;
import typewriter.api.Specifier.StringSpecifier;
import typewriter.api.model.IdentifiableModel;

public abstract class QueryExecutor<M extends IdentifiableModel, R, Q extends Queryable<M, Q>>
        implements Queryable<M, R>, Operatable<M>, Updatable<M>, Deletable<M>, Restorable<M>, Transactional {

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
     * Find model by id.
     * 
     * @param id An identifier of the target model.
     * @return A reuslt stream.
     */
    public R findBy(long id) {
        return findBy(M::getId, v -> v.is(id));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R findBy(Constraint constraint) {
        return findBy(createQueryable().findBy(constraint));
    }

    /**
     * Shorthand for {@link #findBy(Queryable)},
     * 
     * @param query A query builder.
     * @return A result stream.
     */
    public R findBy(UnaryOperator<Q> query) {
        return findBy(query.apply(createQueryable()));
    }

    /**
     * Find model by query.
     * 
     * @param query A query builder.
     * @return A result stream.
     */
    public abstract R findBy(Q query);
}
