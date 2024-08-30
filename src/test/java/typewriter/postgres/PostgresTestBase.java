/*
 * Copyright (C) 2024 The TYPEWRITER Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package typewriter.postgres;

import org.apache.commons.lang3.RandomStringUtils;
import org.postgresql.ds.PGSimpleDataSource;

import de.softwareforge.testing.postgres.embedded.EmbeddedPostgres;
import kiss.I;
import kiss.Signal;
import typewriter.api.QueryExecutor;
import typewriter.api.Testable;
import typewriter.api.model.IdentifiableModel;
import typewriter.rdb.ConnectionPool;
import typewriter.rdb.RDB;

public class PostgresTestBase implements Testable {

    private static final EmbeddedPostgres host;

    private static final PGSimpleDataSource source;

    static {
        try {
            host = EmbeddedPostgres.defaultInstance();
            source = (PGSimpleDataSource) host.createDefaultDataSource();
        } catch (Exception e) {
            throw I.quiet(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <M extends IdentifiableModel, Q extends QueryExecutor<M, Signal<M>, ?, Q>> Q createEmptyDB(Class<M> type, String name) {
        return (Q) new RDB(type, RandomStringUtils.randomAlphanumeric(10), RDB.PostgreSQL, source
                .getURL(), new ConnectionPool(source, RDB.PostgreSQL));
    }
}