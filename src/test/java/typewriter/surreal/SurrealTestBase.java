/*
 * Copyright (C) 2024 The TYPEWRITER Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package typewriter.surreal;

import kiss.Signal;
import typewriter.api.Identifiable;
import typewriter.api.QueryExecutor;
import typewriter.api.Testable;

public class SurrealTestBase implements Testable {

    /**
     * {@inheritDoc}
     */
    @Override
    public <M extends Identifiable, Q extends QueryExecutor<M, Signal<M>, ?, Q>> Q createEmptyDB(Class<M> type, String name) {
        return (Q) new Surreal(type);
    }
}