/*
 * Copyright (C) 2024 The TYPEWRITER Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package typewriter.api;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import kiss.Signal;
import typewriter.api.model.DerivableModel;

public interface NullTestSet extends Testable {

    @Test
    default void nullLocalDate() {
        Person model = new Person();
        model.name = "Iris";

        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);
        dao.update(model);

        Person restored = dao.findBy(model.getId()).to().exact();
        assert restored.name.equals("Iris");
        assert restored.date == null;
    }

    /**
     * 
     */
    class Person extends DerivableModel {

        public String name;

        public LocalDate date;
    }
}