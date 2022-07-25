
/*
 * Copyright (C) 2022 The TYPEWRITER Development Team
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
        product("com.github.teletha", "typewriter", "0.1");
        license(MIT);
        versionControlSystem("https://github.com/teletha/typewriter");

        require("com.github.teletha", "sinobu");
        require("org.mongodb", "mongo-java-driver");
        require("org.xerial", "sqlite-jdbc");
        require("com.github.teletha", "antibug").atTest();
        require("de.bwaldvogel", "mongo-java-server").atTest();
        require("org.apache.commons", "commons-lang3").atTest();
    }
}