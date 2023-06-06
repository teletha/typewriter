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

public class Migration {

    private static boolean canMigrate;

    static {
        try {
            Class.forName("org.flywaydb.core.Flyway");
            canMigrate = true;
        } catch (ClassNotFoundException e) {
            canMigrate = false;
        }
    }

    public static void run(String url) {
        if (canMigrate) {
            // Flyway.configure().dataSource(url, "", "").load().migrate();
        }
    }
}
