/*
 * Copyright (C) 2024 The TYPEWRITER Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package typewriter.maria;

import java.io.IOException;

import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;
import kiss.Signal;
import psychopath.Directory;
import psychopath.Locator;
import typewriter.api.Identifiable;
import typewriter.api.QueryExecutor;
import typewriter.api.Testable;
import typewriter.rdb.RDB;

public class MariaDBTestBase implements Testable {

    /** The invoked test manager. */
    private static int count;

    /** The test database. */
    private static DB db;

    /** The test data directory. */
    private static Directory dir;

    /** The temporary database address. */
    private String url;

    @BeforeAll
    static synchronized void before() throws ManagedProcessException, InterruptedException {
        if (count++ == 0) {
            dir = Locator.temporaryDirectory();

            DBConfigurationBuilder builder = DBConfigurationBuilder.newBuilder()
                    .setPort(RandomUtils.secure().randomInt(1024, 49151))
                    .setDataDir(dir.asJavaFile())
                    .setDeletingTemporaryBaseAndDataDirsOnShutdown(true);

            db = DB.newEmbeddedDB(builder.build());
            db.start();
        }
    }

    @AfterAll
    static synchronized void after() throws ManagedProcessException {
        if (--count == 0) {
            db.stop();
            dir.delete();
        }
    }

    @BeforeEach
    void init(TestInfo info) throws IOException, ManagedProcessException {
        url = "jdbc:mariadb://localhost:" + db.getConfiguration().getPort() + "/" + Testable
                .random() + "?useUnicode=true&characterEncoding=utf8";

        Testable.configure(info, url);
    }

    @AfterEach
    void release() throws ManagedProcessException {
        RDB.release(url);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <M extends Identifiable, Q extends QueryExecutor<M, Signal<M>, ?, Q>> Q createEmptyDB(Class<M> type, String name) {
        return (Q) new RDB(type, name, RDB.MariaDB, url);
    }
}