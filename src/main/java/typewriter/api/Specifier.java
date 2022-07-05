/*
 * Copyright (C) 2022 The SINOBU Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package typewriter.api;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.function.Function;

import kiss.I;
import typewriter.api.Constraint.NumericConstraint;
import typewriter.api.Constraint.StringConstraint;
import typewriter.api.Constraint.TypeConstraint;

/**
 * Generic property specifier.
 */
public interface Specifier<S, T, C extends Constraint<T, C>> extends Function<S, T>, Serializable {

    /**
     * Get the implementation of this lambda.
     * 
     * @return The implementation method of this lambda.
     */
    default Method method() {
        try {
            Method m = getClass().getDeclaredMethod("writeReplace");
            m.setAccessible(true);
            SerializedLambda s = (SerializedLambda) m.invoke(this);

            return I.signal(I.type(s.getImplClass().replaceAll("/", ".")).getDeclaredMethods())
                    .take(x -> x.getName().equals(s.getImplMethodName()))
                    .first()
                    .to()
                    .exact();
        } catch (Exception e) {
            throw I.quiet(e);
        }
    }

    /**
     * The specialized {@link Specifier}.
     */
    interface NumericSpecifier<S, N extends Number> extends Specifier<S, N, NumericConstraint<N>> {
    }

    /**
     * The specialized {@link Specifier}.
     */
    interface CharSpecifier<S> extends Specifier<S, Character, TypeConstraint<Character>> {
    }

    /**
     * The specialized {@link Specifier}.
     */
    interface BooleanSpecifier<S> extends Specifier<S, Boolean, TypeConstraint<Boolean>> {
    }

    /**
     * The specialized {@link Specifier}.
     */
    interface StringSpecifier<S> extends Specifier<S, String, StringConstraint> {
    }

    /**
     * The specialized {@link Specifier}.
     */
    interface LocalDateSpecifier<S> extends Specifier<S, LocalDate, TypeConstraint<LocalDate>> {
    }

    /**
     * The specialized {@link Specifier}.
     */
    interface LocalTimeSpecifier<S> extends Specifier<S, LocalTime, TypeConstraint<LocalTime>> {
    }

    /**
     * The specialized {@link Specifier}.
     */
    interface LocalDateTimeSpecifier<S> extends Specifier<S, LocalDateTime, TypeConstraint<LocalDateTime>> {
    }

    /**
     * The specialized {@link Specifier}.
     */
    interface ZonedDateTimeSpecifier<S> extends Specifier<S, ZonedDateTime, TypeConstraint<ZonedDateTime>> {
    }

    /**
     * The specialized {@link Specifier}.
     */
    interface DateSpecifier<S> extends Specifier<S, Date, TypeConstraint<Date>> {
    }
}