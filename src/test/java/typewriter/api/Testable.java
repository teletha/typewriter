/*
 * Copyright (C) 2025 The TYPEWRITER Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package typewriter.api;

import java.lang.reflect.Method;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.TestInfo;

import kiss.I;
import kiss.Signal;
import typewriter.rdb.RDB;

public interface Testable {

    /**
     * Create the {@link QueryExecutor} for test.
     * 
     * @param <M>
     * @param type
     * @return
     */
    default <M extends Identifiable, Q extends QueryExecutor<M, Signal<M>, ?, Q>> Q createEmptyDB(Class<M> type) {
        return createEmptyDB(type, random());
    }

    /**
     * Create the {@link QueryExecutor} for test.
     * 
     * @param <M>
     * @param type
     * @return
     */
    <M extends Identifiable, Q extends QueryExecutor<M, Signal<M>, ?, Q>> Q createEmptyDB(Class<M> type, String name);

    /**
     * Create the {@link QueryExecutor} for test with initial models.
     * 
     * @param <M>
     * @param <Q>
     * @param models
     * @return
     */
    default <M extends Identifiable, Q extends QueryExecutor<M, Signal<M>, ?, Q>> Q createDB(M... models) {
        Q db = createEmptyDB((Class<M>) models.getClass().getComponentType());
        db.updateAll(models);
        return db;
    }

    /**
     * Create the {@link QueryExecutor} for test.
     * 
     * @param <M>
     * @param type
     * @return
     */
    default <M extends Identifiable, Q extends RDB<M>> Q createEmptyRDB(Class<M> type) {
        return (Q) (Object) createEmptyDB(type, random());
    }

    /**
     * Create the {@link QueryExecutor} for test with initial models.
     * 
     * @param <M>
     * @param <Q>
     * @param models
     * @return
     */
    default <M extends Identifiable, Q extends RDB<M>> Q createRDB(M... models) {
        Q db = createEmptyRDB((Class<M>) models.getClass().getComponentType());
        db.updateAll(models);
        return db;
    }

    /**
     * Helper method to generate random name.
     * 
     * @return
     */
    static String random() {
        return RandomStringUtils.secure().nextAlphabetic(10);
    }

    /**
     * Helper method to generate random name.
     * 
     * @return
     */
    static int randomInt() {
        return RandomUtils.secure().randomInt();
    }

    static void configure(TestInfo info, String url) {
        Method method = info.getTestMethod().get();
        Environment[] envs = method.getAnnotationsByType(Environment.class);
        for (Environment env : envs) {
            I.env(env.key() + "." + url, env.value());
        }
    }
}