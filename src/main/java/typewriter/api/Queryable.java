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

import java.util.function.UnaryOperator;

import typewriter.api.Constraint.NumericConstraint;
import typewriter.api.Constraint.StringConstraint;
import typewriter.api.Constraint.TypeConstraint;
import typewriter.api.Specifier.BooleanSpecifier;
import typewriter.api.Specifier.CharSpecifier;
import typewriter.api.Specifier.NumericSpecifier;
import typewriter.api.Specifier.StringSpecifier;

public abstract class Queryable<M, R> {

    protected abstract <C extends Constraint<T, C>, T> C createConstraint(Class<C> type, Specifier specifier);

    /**
     * Specify search conditions for the specified property.
     * 
     * @param constraint Describes conditions for the target property.
     * @return Chainable API.
     */
    public abstract R findBy(Constraint constraint);

    /**
     * Specify search conditions for the specified property.
     * 
     * @param specifier Specify the target property type-safely.
     * @param constraint Describes conditions for the target property.
     * @return Chainable API.
     */
    public <N extends Number> R findBy(NumericSpecifier<M, N> specifier, UnaryOperator<NumericConstraint<N>> constraint) {
        return findBy(constraint.apply(createConstraint(NumericConstraint.class, specifier)));
    }

    /**
     * Specify search conditions for the specified property.
     * 
     * @param specifier Specify the target property type-safely.
     * @param constraint Describes conditions for the target property.
     * @return Chainable API.
     */
    public R findBy(CharSpecifier<M> specifier, UnaryOperator<TypeConstraint<Character>> constraint) {
        return findBy(constraint.apply(createConstraint(TypeConstraint.class, specifier)));
    }

    /**
     * Specify search conditions for the specified property.
     * 
     * @param specifier Specify the target property type-safely.
     * @param constraint Describes conditions for the target property.
     * @return Chainable API.
     */
    public R findBy(BooleanSpecifier<M> specifier, UnaryOperator<TypeConstraint<Boolean>> constraint) {
        return findBy(constraint.apply(createConstraint(TypeConstraint.class, specifier)));
    }

    /**
     * Specify search conditions for the specified property.
     * 
     * @param specifier Specify the target property type-safely.
     * @param constraint Describes conditions for the target property.
     * @return Chainable API.
     */
    public R findBy(StringSpecifier<M> specifier, UnaryOperator<StringConstraint> constraint) {
        return findBy(constraint.apply(createConstraint(StringConstraint.class, specifier)));
    }
}
