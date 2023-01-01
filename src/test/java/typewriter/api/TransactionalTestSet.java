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

public interface TransactionalTestSet extends Testable {

    @Test
    default void transaction() {
        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);

        dao.transact(on -> {
            for (int i = 0; i < 100; i++) {
                on.update(new Person("No" + i, i));
            }
        });
        assert dao.count() == 100;
    }

    /**
     * 
     */
    class Person extends DerivableModel {

        public String name;

        public int age;

        /**
         * Create empty model.
         */
        public Person() {
        }

        /**
         * @param name
         * @param age
         */
        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        /**
         * Get the name property of this {@link TransactionalTestSet.Person}.
         * 
         * @return The name property.
         */
        public String getName() {
            return name;
        }

        /**
         * Get the age property of this {@link TransactionalTestSet.Person}.
         * 
         * @return The age property.
         */
        public int getAge() {
            return age;
        }
    }
}