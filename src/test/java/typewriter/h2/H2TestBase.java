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

import org.junit.jupiter.api.AfterEach;

import kiss.Signal;
import psychopath.File;
import psychopath.Locator;
import typewriter.api.QueryExecutor;
import typewriter.api.Testable;
import typewriter.api.model.IdentifiableModel;
import typewriter.rdb.RDB;

public class H2TestBase implements Testable {

    /** The database file. */
    private final File file = Locator.temporaryFile();

    /** The temporary database address. */
    private final String db = "jdbc:h2:" + file;

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
        return (Q) new RDB(type, RDB.H2, db);
    }
}