/*
 * Copyright (C) 2023 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package typewriter.rdb;

import java.io.OutputStreamWriter;
import java.sql.Connection;

import kiss.I;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;

public class Migration {

    /** Check if Liquibase exists on the classpath. */
    private static boolean canLiquibase;

    static {
        try {
            Class.forName("liquibase.Liquibase");
            canLiquibase = true;
        } catch (ClassNotFoundException e) {
            canLiquibase = false;
        }
    }

    @SuppressWarnings("resource")
    public static void run(Connection url) {
        if (canLiquibase) {
            try {
                Database db = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(url));

                Liquibase liquibase = new Liquibase("db/changeLog.xml", new ClassLoaderResourceAccessor(), db);
                liquibase.update(new Contexts(), new OutputStreamWriter(System.out));

                System.out.println("OK");
            } catch (Exception e) {
                throw I.quiet(e);
            }
        }
    }
}
