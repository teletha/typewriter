/*
 * Copyright (C) 2023 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package typewriter.h2;

import kiss.I;

public class Functions {

    public static int jsonArrayLength(String json) {
        return I.json(json).find("*").size();
    }

    public static boolean jsonArrayContains(String json, String value) {
        return I.json(json).asMap(String.class).containsValue(value);
    }
}