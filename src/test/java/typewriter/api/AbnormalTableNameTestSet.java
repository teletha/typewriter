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

import org.junit.jupiter.api.Test;

import typewriter.api.model.DerivableModel;

public interface AbnormalTableNameTestSet extends Testable {

    @Test
    default void dot() {
        createEmptyDB(Abnormal.class, "dot...");
    }

    @Test
    default void doller() {
        createEmptyDB(Abnormal.class, "$doller$");
    }

    @Test
    default void whiteSpace() {
        createEmptyDB(Abnormal.class, "white space");
    }

    @Test
    default void reservedWord() {
        createEmptyDB(Abnormal.class, "SELECT");
    }

    @Test
    default void doubleQuote() {
        createEmptyDB(Abnormal.class, "\"quoted\"");
    }

    @Test
    default void singleQuote() {
        createEmptyDB(Abnormal.class, "someone's");
    }

    class Abnormal extends DerivableModel {
        public int item;
    }
}