/*
 * Copyright (C) 2025 The TYPEWRITER Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package typewriter.mongo;

import org.junit.jupiter.api.Disabled;

import typewriter.api.SortTestSet;

public class SortTest extends MongoTestBase implements SortTestSet {

    /**
     * {@inheritDoc}
     */
    @Override
    @Disabled
    public void withFilter() {
        SortTestSet.super.withFilter();
    }
}