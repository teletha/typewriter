/*
 * Copyright (C) 2022 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package typewriter.maria;

import java.util.Random;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import antibug.powerassert.PowerAssertOff;
import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;
import kiss.Signal;
import kiss.model.Model;
import typewriter.api.QueryExecutor;
import typewriter.api.Testable;
import typewriter.api.model.IdentifiableModel;
import typewriter.rdb.RDB;

@PowerAssertOff
public class MariaTestBase implements Testable {

    private static final Random R = new Random();

    private static DB db;

    @BeforeAll
    static void start() throws ManagedProcessException {
        DBConfigurationBuilder builder = DBConfigurationBuilder.newBuilder()
                .setPort(3210)
                .setDataDir(Testable.createTemporaryDir().toString())
                .setDeletingTemporaryBaseAndDataDirsOnShutdown(true);

        db = DB.newEmbeddedDB(builder.build());
        db.start();
    }

    @AfterAll
    static void stop() throws ManagedProcessException {
        db.stop();
    }

    /** The temporary database address. */
    private String url;

    @BeforeEach
    void init() {
        String name = "test" + Math.abs(R.nextInt());
        url = "jdbc:mariadb://localhost:3210/" + name;
    }

    @AfterEach
    void release() {
        RDB.release(url);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <M extends IdentifiableModel, Q extends QueryExecutor<M, Signal<M>, ?, Q>> Q createEmptyDB(Class<M> type) {
        return (Q) new RDB(Model.of(type), RDB.MariaDB, url);
    }

}
