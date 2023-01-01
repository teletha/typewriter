/*
 * Copyright (C) 2023 The TYPEWRITER Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package typewriter.maria;

import java.io.IOException;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import antibug.powerassert.PowerAssertOff;
import ch.vorburger.exec.ManagedProcessException;
import ch.vorburger.mariadb4j.DB;
import ch.vorburger.mariadb4j.DBConfigurationBuilder;
import kiss.Signal;
import psychopath.Directory;
import psychopath.Locator;
import typewriter.api.QueryExecutor;
import typewriter.api.Testable;
import typewriter.api.model.IdentifiableModel;
import typewriter.rdb.RDB;

@PowerAssertOff
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
    static synchronized void before() throws ManagedProcessException {
        if (count++ == 0) {
            dir = Locator.temporaryDirectory();

            DBConfigurationBuilder builder = DBConfigurationBuilder.newBuilder()
                    .setPort(RandomUtils.nextInt(1024, 49151))
                    .setDataDir(dir.toString())
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
    void init() throws IOException, ManagedProcessException {
        url = "jdbc:mariadb://localhost:" + db.getConfiguration().getPort() + "/" + RandomStringUtils
                .randomAlphabetic(32) + "?useUnicode=true&characterEncoding=utf8";
    }

    @AfterEach
    void release() throws ManagedProcessException {
        RDB.release(url);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <M extends IdentifiableModel, Q extends QueryExecutor<M, Signal<M>, ?, Q>> Q createEmptyDB(Class<M> type) {
        return (Q) new RDB(type, RDB.MariaDB, url);
    }
}