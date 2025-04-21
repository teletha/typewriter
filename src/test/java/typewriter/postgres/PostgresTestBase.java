/*
 * Copyright (C) 2025 The TYPEWRITER Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package typewriter.postgres;

import de.softwareforge.testing.postgres.embedded.EmbeddedPostgres;
import kiss.I;
import kiss.Signal;
import typewriter.api.Identifiable;
import typewriter.api.QueryExecutor;
import typewriter.api.Testable;
import typewriter.rdb.RDB;

public class PostgresTestBase implements Testable {

    /** The test database. */
    private static EmbeddedPostgres db;

    private static String url;

    static {
        try {
            db = EmbeddedPostgres.defaultInstance();
            url = "jdbc:postgresql://localhost:" + db.getPort() + "/postgres?user=postgres";
        } catch (Exception e) {
            throw I.quiet(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <M extends Identifiable, Q extends QueryExecutor<M, Signal<M>, ?, Q>> Q createEmptyDB(Class<M> type, String name) {
        return (Q) new RDB(type, name, RDB.PostgreSQL, url);
    }
}