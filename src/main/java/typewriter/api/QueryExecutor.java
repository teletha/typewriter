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
import typewriter.api.model.IdentifiableModel;

public abstract class QueryExecutor<M extends IdentifiableModel, R, Q extends Queryable<M, Q>> extends Queryable<M, R>
        implements Operatable<M>, Updatable<M>, Deletable<M>, Restorable<M>, Transactional {

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
    protected <C extends Constraint<T, C>, T> C createConstraint(Class<C> type, Specifier specifier) {
        return createQueryable().createConstraint(type, specifier);
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
