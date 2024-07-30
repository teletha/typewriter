/*
 * Copyright (C) 2024 The TYPEWRITER Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package typewriter.sqlite;

import java.util.Random;

import org.junit.jupiter.api.AfterEach;

import kiss.Signal;
import typewriter.api.QueryExecutor;
import typewriter.api.Testable;
import typewriter.api.model.IdentifiableModel;
import typewriter.rdb.RDB;

public class SQLiteTestBase implements Testable {

    /** The postfix generator. */
    private static final Random RANDOM = new Random();

    /** The temporary database address. */
    private final String db = "jdbc:sqlite:file:memdb" + Math.abs(RANDOM.nextInt()) + "?mode=memory&cache=shared";

    @AfterEach
    void release() {
        RDB.release(db);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <M extends IdentifiableModel, Q extends QueryExecutor<M, Signal<M>, ?, Q>> Q createEmptyDB(Class<M> type) {
        return (Q) new RDB(type, type.getName(), RDB.SQLite, db);
    }
}