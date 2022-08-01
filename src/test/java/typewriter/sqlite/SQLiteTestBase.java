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

import org.junit.jupiter.api.AfterEach;

import kiss.Signal;
import kiss.model.Model;
import typewriter.api.QueryExecutor;
import typewriter.api.Testable;
import typewriter.api.model.IdentifiableModel;
import typewriter.rdb.RDB;

public class SQLiteTestBase implements Testable {

    /** The temporary database address. */
    private final String db = "jdbc:sqlite:" + createTemporaryFile();

    @AfterEach
    void release() {
        RDB.release(db);;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <M extends IdentifiableModel, Q extends QueryExecutor<M, Signal<M>, ?, Q>> Q createEmptyDB(Class<M> type) {
        return (Q) new RDB(Model.of(type), RDB.SQLite, db);
    }
}
