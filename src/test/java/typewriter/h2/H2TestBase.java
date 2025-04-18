/*
 * Copyright (C) 2024 The TYPEWRITER Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package typewriter.h2;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

import kiss.Signal;
import typewriter.api.Identifiable;
import typewriter.api.QueryExecutor;
import typewriter.api.Testable;
import typewriter.rdb.RDB;

public class H2TestBase implements Testable {

    /** The temporary database address. */
    private String db;

    @BeforeEach
    void setup(TestInfo info) {
        db = "jdbc:h2:mem:test" + Testable.randomInt();

        Testable.configure(info, db);
    }

    @AfterEach
    void cleanup() {
        RDB.release(db);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <M extends Identifiable, Q extends QueryExecutor<M, Signal<M>, ?, Q>> Q createEmptyDB(Class<M> type, String name) {
        return (Q) new RDB(type, name, RDB.H2, db);
    }
}