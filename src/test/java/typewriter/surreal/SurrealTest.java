/*
 * Copyright (C) 2025 The TYPEWRITER Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package typewriter.surreal;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import typewriter.api.QueryExecutorTestSet;

@Disabled
public class SurrealTest extends SurrealTestBase implements QueryExecutorTestSet {

    @Override
    @Test
    public void saveModel() {
        QueryExecutorTestSet.super.saveModel();
    }
}