/*
 * Copyright (C) 2022 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package typewriter.h2;

import org.junit.jupiter.api.BeforeEach;

import kiss.Signal;
import kiss.model.Model;
import typewriter.api.QueryExecutor;
import typewriter.api.Testable;
import typewriter.api.model.IdentifiableModel;
import typewriter.rdb.RDB;

public class H2TestBase implements Testable {

    @BeforeEach
    void setup() {
        RDB.close(RDB.H2);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <M extends IdentifiableModel> QueryExecutor<M, Signal<M>, ?> createEmptyDB(Class<M> type) {
        return new RDB<>(Model.of(type), RDB.H2, "jdbc:h2:mem:test");
    }
}
