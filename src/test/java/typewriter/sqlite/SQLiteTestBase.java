/*
 * Copyright (C) 2022 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package typewriter.sqlite;

import org.junit.jupiter.api.BeforeEach;

import kiss.Signal;
import typewriter.api.QueryExecutor;
import typewriter.api.Testable;
import typewriter.api.model.IdentifiableModel;

public class SQLiteTestBase implements Testable {

    @BeforeEach
    void setup() {
        SQLite.close();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <M extends IdentifiableModel> QueryExecutor<M, Signal<M>, ?> createEmptyDB(Class<M> type) {
        return new SQLite<>(type, "jdbc:sqlite::memory:");
    }
}