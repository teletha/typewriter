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

import java.lang.annotation.RetentionPolicy;

import org.junit.jupiter.api.Test;

import kiss.Signal;
import typewriter.api.model.DerivableModel;

public interface EnumTestSet extends Testable {

    @Test
    default void codec() {
        QueryExecutor<EnumValue, Signal<EnumValue>, ?, ?> dao = createEmptyDB(EnumValue.class);
        dao.update(new EnumValue(RetentionPolicy.CLASS));

        EnumValue person = dao.limit(1).waitForTerminate().to().v;
        assert person.policy.equals(RetentionPolicy.CLASS);
    }

    @Test
    default void nullProperty() {
        QueryExecutor<EnumValue, Signal<EnumValue>, ?, ?> dao = createEmptyDB(EnumValue.class);
        dao.update(new EnumValue(null));

        EnumValue person = dao.limit(1).waitForTerminate().to().v;
        assert person.policy == null;
    }

    static class EnumValue extends DerivableModel {

        public RetentionPolicy policy;

        private EnumValue() {
        }

        private EnumValue(RetentionPolicy policy) {
            this.policy = policy;
        }
    }
}