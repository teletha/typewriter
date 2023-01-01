/*
 * Copyright (C) 2023 The TYPEWRITER Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package typewriter.sqlite;

import org.junit.jupiter.api.AfterEach;

import kiss.Signal;
import psychopath.File;
import psychopath.Locator;
import typewriter.api.QueryExecutor;
import typewriter.api.Testable;
import typewriter.api.model.IdentifiableModel;
import typewriter.rdb.RDB;

public class SQLiteTestBase implements Testable {

    /** The database file. */
    private final File file = Locator.temporaryFile();

    /** The temporary database address. */
    private final String db = "jdbc:sqlite:" + file;

    @AfterEach
    void release() {
        RDB.release(db);
        file.delete();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <M extends IdentifiableModel, Q extends QueryExecutor<M, Signal<M>, ?, Q>> Q createEmptyDB(Class<M> type) {
        return (Q) new RDB(type, RDB.SQLite, db);
    }
}