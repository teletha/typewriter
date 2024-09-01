/*
 * Copyright (C) 2024 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package typewriter.api;

import java.beans.Introspector;
import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;

import kiss.I;
import kiss.Ⅱ;
import reincarnation.Reincarnation;
import typewriter.rdb.Dialect;
import typewriter.rdb.RDB;
import typewriter.rdb.SQLCoder;
import typewriter.rdb.SQLCommand;
import typewriter.rdb.SQLCommandTarget;

public interface QueryDSL<M extends Identifiable> extends Serializable {

    enum OrderType {
        ASC, DESC;
    }

    void define(M model);

    /**
     * Estimate the property name.
     * 
     * @return
     */
    default String propertyName(Dialect dialect) {
        return SpecifierCache.NAME.computeIfAbsent(this, key -> {
            Ⅱ<Method, SerializedLambda> method = method();
            if (method.ⅰ.isSynthetic()) {
                // lambda expression
                SQLCoder coder = new SQLCoder(method.ⅰ, method.ⅱ, dialect);
                Reincarnation.exhume(method.ⅰ.getDeclaringClass()).rebirth(coder);
                return coder.toString();
            } else {
                // method reference
                return inspectPropertyName(method.ⅰ);
            }
        });
    }

    /**
     * Get the implementation of this lambda.
     * 
     * @return The implementation method of this lambda.
     */
    private Ⅱ<Method, SerializedLambda> method() {
        try {
            Method m = getClass().getDeclaredMethod("writeReplace");
            m.setAccessible(true);
            SerializedLambda s = (SerializedLambda) m.invoke(this);

            return I.pair(I.signal(I.type(s.getImplClass().replaceAll("/", ".")).getDeclaredMethods())
                    .take(x -> x.getName().equals(s.getImplMethodName()))
                    .first()
                    .to()
                    .exact(), s);
        } catch (Exception e) {
            throw I.quiet(e);
        }
    }

    /**
     * Utility method to inspect property name from {@link Method}.
     * 
     * @param method
     * @return
     */
    static String inspectPropertyName(Method method) {
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

    OrderType ASC = OrderType.ASC;

    OrderType DESC = OrderType.DESC;

    @SQLCommandTarget(SQLCommand.SELECT)
    static void Select(Object... values) {
    }

    @SQLCommandTarget(SQLCommand.FROM)
    static void From(RDB<?> table) {
    }

    @SQLCommandTarget(SQLCommand.WHERE)
    static void Where(boolean condition) {
    }

    @SQLCommandTarget(SQLCommand.ORDER_BY)
    static void OrderBy(Object key, OrderType type) {
    }

}
