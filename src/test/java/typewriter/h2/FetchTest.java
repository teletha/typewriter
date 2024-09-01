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

import org.junit.jupiter.api.Test;

import typewriter.api.FetchTestSet;

public class FetchTest extends H2TestBase implements FetchTestSet {

    /**
     * {@inheritDoc}
     */
    @Override
    @Test
    public void orderBy() {
        FetchTestSet.super.orderBy();
    }
}
