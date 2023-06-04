/*
 * Copyright (C) 2023 The TYPEWRITER Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package typewriter.api;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.function.Function;
import java.util.function.UnaryOperator;

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

public interface Queryable<M, R> {

    /**
     * Collect all items without any conditions.
     * 
     * @return
     */
    default R findAll() {
        return findBy((Constraint) null);
    }

    /**
     * Specify search conditions for the specified property.
     * 
     * @param constraint Describes conditions for the target property.
     * @return Chainable API.
     */
    <QUERYABLE extends Queryable<M, QUERYABLE>> R query(Function<QUERYABLE, QUERYABLE> constraint);

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

    /**
     * Specify search conditions for the specified property.
     * 
     * @param specifier Specify the target property type-safely.
     * @param constraint Describes conditions for the target property.
     * @return Chainable API.
     */
    R findBy(OffsetDateTimeSpecifier<M> specifier, UnaryOperator<OffsetDateTimeConstraint> constraint);

    /**
     * Specify search conditions for the specified property.
     * 
     * @param specifier Specify the target property type-safely.
     * @param constraint Describes conditions for the target property.
     * @return Chainable API.
     */
    R findBy(ZonedDateTimeSpecifier<M> specifier, UnaryOperator<ZonedDateTimeConstraint> constraint);

    /**
     * Specify search conditions for the specified property.
     * 
     * @param specifier Specify the target property type-safely.
     * @param constraint Describes conditions for the target property.
     * @return Chainable API.
     */
    <N> R findBy(ListSpecifier<M, N> specifier, UnaryOperator<ListConstraint<N>> constraint);

    /**
     * Limit size of query result.
     * 
     * @param size
     * @return
     */
    R limit(long size);

    /**
     * Offset starting position of query result.
     * 
     * @param position
     * @return
     */
    R offset(long position);

    /**
     * Paging helper.
     * 
     * @param number A page number.
     * @param size A item size for each page.
     * @return
     */
    default R page(long number, long size) {
        return query(sub -> sub.offset((number - 1) * size).limit(size));
    }

    /**
     * Sort by the specified property.
     * 
     * @param specifier Specify the target property type-safely.
     * @param ascending Ascending or descending.
     * @return Chainable API.
     */
    default <N extends Number> R sortBy(NumericSpecifier<M, N> specifier, boolean ascending) {
        return sortBy((Specifier<M, N>) specifier, ascending);
    }

    /**
     * Sort by the specified property.
     * 
     * @param specifier Specify the target property type-safely.
     * @param ascending Ascending or descending.
     * @return Chainable API.
     */
    default R sortBy(StringSpecifier<M> specifier, boolean ascending) {
        return sortBy((Specifier<M, String>) specifier, ascending);
    }

    /**
     * Sort by the specified property.
     * 
     * @param specifier Specify the target property type-safely.
     * @param ascending Ascending or descending.
     * @return Chainable API.
     */
    default R sortBy(DateSpecifier<M> specifier, boolean ascending) {
        return sortBy((Specifier<M, Date>) specifier, ascending);
    }

    /**
     * Sort by the specified property.
     * 
     * @param specifier Specify the target property type-safely.
     * @param ascending Ascending or descending.
     * @return Chainable API.
     */
    default R sortBy(LocalDateSpecifier<M> specifier, boolean ascending) {
        return sortBy((Specifier<M, LocalDate>) specifier, ascending);
    }

    /**
     * Sort by the specified property.
     * 
     * @param specifier Specify the target property type-safely.
     * @param ascending Ascending or descending.
     * @return Chainable API.
     */
    default R sortBy(LocalTimeSpecifier<M> specifier, boolean ascending) {
        return sortBy((Specifier<M, LocalTime>) specifier, ascending);
    }

    /**
     * Sort by the specified property.
     * 
     * @param specifier Specify the target property type-safely.
     * @param ascending Ascending or descending.
     * @return Chainable API.
     */
    default R sortBy(LocalDateTimeSpecifier<M> specifier, boolean ascending) {
        return sortBy((Specifier<M, LocalDateTime>) specifier, ascending);
    }

    /**
     * Sort by the specified property.
     * 
     * @param specifier Specify the target property type-safely.
     * @param ascending Ascending or descending.
     * @return Chainable API.
     */
    default R sortBy(OffsetDateTimeSpecifier<M> specifier, boolean ascending) {
        return sortBy((Specifier<M, OffsetDateTime>) specifier, ascending);
    }

    /**
     * Sort by the specified property.
     * 
     * @param specifier Specify the target property type-safely.
     * @param ascending Ascending or descending.
     * @return Chainable API.
     */
    default R sortBy(ZonedDateTimeSpecifier<M> specifier, boolean ascending) {
        return sortBy((Specifier<M, ZonedDateTime>) specifier, ascending);
    }

    /**
     * Sort by the specified property.
     * 
     * @param specifier Specify the target property type-safely.
     * @param ascending Ascending or descending.
     * @return Chainable API.
     */
    <N> R sortBy(Specifier<M, N> specifier, boolean ascending);
}