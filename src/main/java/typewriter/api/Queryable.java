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

public interface Queryable<M, R> {

    /**
     * Specify search conditions for the specified property.
     * 
     * @param constraint Describes conditions for the target property.
     * @return Chainable API.
     */
    R findBy(Constraint constraint);

    /**
     * Specify search conditions for the specified property.
     * 
     * @param specifier Specify the target property type-safely.
     * @param constraint Describes conditions for the target property.
     * @return Chainable API.
     */
    <N extends Number> R findBy(NumericSpecifier<M, N> specifier, UnaryOperator<NumericConstraint<N>> constraint);

    /**
     * Specify search conditions for the specified property.
     * 
     * @param specifier Specify the target property type-safely.
     * @param constraint Describes conditions for the target property.
     * @return Chainable API.
     */
    R findBy(CharSpecifier<M> specifier, UnaryOperator<TypeConstraint<Character>> constraint);

    /**
     * Specify search conditions for the specified property.
     * 
     * @param specifier Specify the target property type-safely.
     * @param constraint Describes conditions for the target property.
     * @return Chainable API.
     */
    R findBy(BooleanSpecifier<M> specifier, UnaryOperator<TypeConstraint<Boolean>> constraint);

    /**
     * Specify search conditions for the specified property.
     * 
     * @param specifier Specify the target property type-safely.
     * @param constraint Describes conditions for the target property.
     * @return Chainable API.
     */
    R findBy(StringSpecifier<M> specifier, UnaryOperator<StringConstraint> constraint);

    /**
     * Specify search conditions for the specified property.
     * 
     * @param specifier Specify the target property type-safely.
     * @param constraint Describes conditions for the target property.
     * @return Chainable API.
     */
    R findBy(DateSpecifier<M> specifier, UnaryOperator<DateConstraint> constraint);

    /**
     * Specify search conditions for the specified property.
     * 
     * @param specifier Specify the target property type-safely.
     * @param constraint Describes conditions for the target property.
     * @return Chainable API.
     */
    R findBy(LocalDateSpecifier<M> specifier, UnaryOperator<LocalDateConstraint> constraint);

    /**
     * Specify search conditions for the specified property.
     * 
     * @param specifier Specify the target property type-safely.
     * @param constraint Describes conditions for the target property.
     * @return Chainable API.
     */
    R findBy(LocalTimeSpecifier<M> specifier, UnaryOperator<LocalTimeConstraint> constraint);

    /**
     * Specify search conditions for the specified property.
     * 
     * @param specifier Specify the target property type-safely.
     * @param constraint Describes conditions for the target property.
     * @return Chainable API.
     */
    R findBy(LocalDateTimeSpecifier<M> specifier, UnaryOperator<LocalDateTimeConstraint> constraint);
}
