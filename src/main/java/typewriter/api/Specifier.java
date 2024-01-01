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

import java.beans.Introspector;
import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

import kiss.I;

/**
 * Generic property specifier.
 */
public interface Specifier<S, T> extends Function<S, T>, Serializable {

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
     * Estimate the property name.
     * 
     * @return
     */
    default String propertyName() {
        Method method = method();
        String name = method.getName();
        if (method.getReturnType() == boolean.class) {
            if (name.startsWith("is")) {
                name = name.substring(2);
            }
        } else {
            if (name.startsWith("get")) {
                name = name.substring(3);
            }
        }
        return Introspector.decapitalize(name);
    }

    /**
     * The specialized {@link Specifier}.
     */
    interface NumericSpecifier<S, N extends Number> extends Specifier<S, N> {
    }

    /**
     * The specialized {@link Specifier}.
     */
    interface CharSpecifier<S> extends Specifier<S, Character> {
    }

    /**
     * The specialized {@link Specifier}.
     */
    interface BooleanSpecifier<S> extends Specifier<S, Boolean> {
    }

    /**
     * The specialized {@link Specifier}.
     */
    interface StringSpecifier<S> extends Specifier<S, String> {
    }

    /**
     * The specialized {@link Specifier}.
     */
    interface DateSpecifier<S> extends Specifier<S, Date> {
    }

    /**
     * The specialized {@link Specifier}.
     */
    interface LocalDateSpecifier<S> extends Specifier<S, LocalDate> {
    }

    /**
     * The specialized {@link Specifier}.
     */
    interface LocalTimeSpecifier<S> extends Specifier<S, LocalTime> {
    }

    /**
     * The specialized {@link Specifier}.
     */
    interface LocalDateTimeSpecifier<S> extends Specifier<S, LocalDateTime> {
    }

    /**
     * The specialized {@link Specifier}.
     */
    interface OffsetDateTimeSpecifier<S> extends Specifier<S, OffsetDateTime> {
    }

    /**
     * The specialized {@link Specifier}.
     */
    interface ZonedDateTimeSpecifier<S> extends Specifier<S, ZonedDateTime> {
    }

    /**
     * The specialized {@link Specifier}.
     */
    interface ListSpecifier<S, X> extends Specifier<S, List<X>> {
    }
}