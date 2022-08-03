/*
 * Copyright (C) 2022 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package typewriter.maria;

import org.junit.jupiter.api.Test;

import typewriter.api.StringStoreTestSet;

public class StringStoreTest extends MariaDBTestBase implements StringStoreTestSet {

    /**
     * {@inheritDoc}
     */
    @Override
    @Test
    public void tab() {
        StringStoreTestSet.super.tab();
    }
}
