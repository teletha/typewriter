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
    <M extends IdentifiableModel, Q extends QueryExecutor<M, Signal<M>, ?, Q>> Q createEmptyDB(Class<M> type);
}