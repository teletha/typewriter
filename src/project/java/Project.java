
/*
 * Copyright (C) 2022 The TYPEWRITER Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
import static bee.api.License.MIT;

public class Project extends bee.api.Project {
    {
        product("com.github.teletha", "typewriter", ref("version.txt"));
        license(MIT);
        versionControlSystem("https://github.com/teletha/typewriter");
        describe("""
                General DAO for various database.
                """);

        require("com.github.teletha", "sinobu");
        require("org.mongodb", "mongo-java-driver").atProvided();
        require("org.xerial", "sqlite-jdbc").atProvided();
        require("com.h2database", "h2").atProvided();
        require("org.mariadb.jdbc", "mariadb-java-client").atProvided();
        require("ch.vorburger.mariaDB4j", "mariaDB4j").atProvided();
        require("com.github.teletha", "antibug").atTest();
        require("de.bwaldvogel", "mongo-java-server").atTest();
        require("com.github.teletha", "psychopath").atTest();
        require("org.apache.commons", "commons-lang3").atTest();
    }
}