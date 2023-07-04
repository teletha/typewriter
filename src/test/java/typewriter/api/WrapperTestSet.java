/*
 * Copyright (C) 2023 The TYPEWRITER Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package typewriter.api;

import org.junit.jupiter.api.Test;

import kiss.Signal;
import typewriter.api.model.DerivableModel;

public interface WrapperTestSet extends Testable {

    @Test
    default void wrapper() {
        Values model = new Values();
        model.boolValue = true;
        model.intValue = 10;
        model.longValue = 20L;
        model.floatValue = 0.5f;
        model.doubleValue = 0.23d;
        model.byteValue = 2;
        model.shortValue = 120;

        QueryExecutor<Values, Signal<Values>, ?, ?> dao = createEmptyDB(Values.class);
        dao.update(model);
        assert dao.count() == 1;

        Values restored = dao.findBy(model.getId()).to().exact();
        assert restored.boolValue == true;
        assert restored.intValue == 10;
        assert restored.longValue == 20L;
        assert restored.floatValue == 0.5f;
        assert restored.doubleValue == 0.23d;
        assert restored.byteValue == 2;
        assert restored.shortValue == 120;
    }

    /**
     * 
     */
    class Values extends DerivableModel {

        public Boolean boolValue;

        public Integer intValue;

        public Long longValue;

        public Float floatValue;

        public Double doubleValue;

        public Byte byteValue;

        public Short shortValue;
    }
}