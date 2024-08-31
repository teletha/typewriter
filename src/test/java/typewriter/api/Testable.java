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

import org.apache.commons.lang3.RandomStringUtils;

import kiss.Signal;
import typewriter.api.model.IdentifiableModel;

public interface Testable {

    /**
     * Create the {@link QueryExecutor} for test.
     * 
     * @param <M>
     * @param type
     * @return
     */
    default <M extends IdentifiableModel, Q extends QueryExecutor<M, Signal<M>, ?, Q>> Q createEmptyDB(Class<M> type) {
        return createEmptyDB(type, type.getName());
    }

    /**
     * Create the {@link QueryExecutor} for test.
     * 
     * @param <M>
     * @param type
     * @return
     */
    <M extends IdentifiableModel, Q extends QueryExecutor<M, Signal<M>, ?, Q>> Q createEmptyDB(Class<M> type, String name);

    /**
     * Create the {@link QueryExecutor} for test with initial models.
     * 
     * @param <M>
     * @param <Q>
     * @param models
     * @return
     */
    default <M extends IdentifiableModel, Q extends QueryExecutor<M, Signal<M>, ?, Q>> Q createDB(M... models) {
        Q db = createEmptyDB((Class<M>) models.getClass().getComponentType());
        db.updateAll(models);
        return db;
    }

    /**
     * Helper method to generate random name.
     * 
     * @return
     */
    static String random() {
        return RandomStringUtils.secure().nextAlphabetic(15);
    }
}