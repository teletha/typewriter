
/*
 * Copyright (C) 2024 The TYPEWRITER Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
import static bee.api.License.*;

public class Project extends bee.api.Project {
    {
        product("com.github.teletha", "typewriter", ref("version.txt"));
        license(MIT);
        versionControlSystem("https://github.com/teletha/typewriter");
        describe("""
                Provides a general-purpose, type-safe DAO. Currently supported databases are the following:
                * [H2](https://github.com/h2database/h2database)
                * [SQLite](https://github.com/sqlite/sqlite)
                * [MariaDB](https://github.com/MariaDB/)
                * [MongoDB](https://github.com/mongodb/mongo)
                * [DuckDB](https://duckdb.org/)
                """);

        require("com.github.teletha", "sinobu");
        require("com.github.teletha", "reincarnation");
        require("org.jinq", "api");
        require("org.jinq", "jinq-jooq");
        require("org.jooq", "jooq");
        require("org.vineflower", "vineflower");
        require("org.mongodb", "mongodb-driver-sync").atProvided();
        require("org.xerial", "sqlite-jdbc").atProvided();
        require("com.h2database", "h2").atProvided();
        require("org.duckdb", "duckdb_jdbc").atProvided();
        require("org.mariadb.jdbc", "mariadb-java-client").atProvided();
        require("ch.vorburger.mariaDB4j", "mariaDB4j").atProvided();
        require("com.github.teletha", "antibug").atTest();
        require("de.bwaldvogel", "mongo-java-server").atTest();
        require("com.github.teletha", "psychopath").atTest();
        require("org.apache.commons", "commons-lang3").atTest();
        require("org.slf4j", "slf4j-nop").atTest();
    }
}