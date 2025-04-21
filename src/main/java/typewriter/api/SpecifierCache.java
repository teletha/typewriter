/*
 * Copyright (C) 2025 The TYPEWRITER Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package typewriter.api;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class SpecifierCache {
    static final Map<Object, String> NAME = new ConcurrentHashMap();
}