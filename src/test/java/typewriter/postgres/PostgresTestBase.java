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

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;

import de.softwareforge.testing.postgres.embedded.EmbeddedPostgres;
import kiss.I;
import kiss.Signal;
import typewriter.api.QueryExecutor;
import typewriter.api.Testable;
import typewriter.api.model.IdentifiableModel;
import typewriter.rdb.RDB;

public class PostgresTestBase implements Testable {

    /** The test database. */
    private static EmbeddedPostgres db;

    static {
        try {
            db = EmbeddedPostgres.defaultInstance();
        } catch (IOException e) {
            throw I.quiet(e);
        }
    }

    private String url;

    @BeforeEach
    void setup() throws SQLException {
        String databaseName = Testable.random().toLowerCase();
        url = "jdbc:postgresql://localhost:" + db.getPort() + "/" + databaseName + "?user=postgres";

        DataSource source = db.createDefaultDataSource();
        try (Connection con = source.getConnection(); Statement stmt = con.createStatement()) {
            stmt.execute("CREATE DATABASE " + databaseName);
        } catch (SQLException e) {
            throw I.quiet(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <M extends IdentifiableModel, Q extends QueryExecutor<M, Signal<M>, ?, Q>> Q createEmptyDB(Class<M> type, String name) {
        return (Q) new RDB(type, name, RDB.PostgreSQL, url);
    }
}