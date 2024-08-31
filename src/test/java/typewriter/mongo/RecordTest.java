/*
 * Copyright (C) 2023 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package typewriter.mongo;

import org.junit.jupiter.api.Disabled;

import typewriter.api.RecordTestSet;

public class RecordTest extends MongoTestBase implements RecordTestSet {
    @Override
    @Disabled
    public void linq() {
        RecordTestSet.super.linq();
    }
}