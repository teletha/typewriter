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

import org.junit.jupiter.api.Test;

import kiss.Signal;
import typewriter.api.model.DerivableModel;

public interface RestorableTestSet extends Testable {

    @Test
    default void restoreModel() {
        Person model = new Person("one", 10);

        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);
        dao.update(model);

        // change local model
        model.age = 20;
        model.name = "change";

        // restore model from backend
        dao.restore(model).to();
        assert model.age == 10;
        assert model.name.equals("one");
    }

    @Test
    default void restoreSpecifedProperty() {
        Person model = new Person("one", 10);

        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);
        dao.update(model);

        // change local model
        model.age = 20;
        model.name = "change";

        // restore model from backend
        dao.restore(model, Person::getName).to();
        assert model.age == 20;
        assert model.name.equals("one");
    }

    @Test
    default void restoreSpecifedProperties() {
        Person model = new Person("one", 10);

        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);
        dao.update(model);

        // change local model
        model.age = 20;
        model.name = "change";

        // restore model from backend
        dao.restore(model, Person::getName, Person::getAge).to();
        assert model.age == 10;
        assert model.name.equals("one");
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
         * Get the name property of this {@link RestorableTestSet.Person}.
         * 
         * @return The name property.
         */
        public String getName() {
            return name;
        }

        /**
         * Get the age property of this {@link RestorableTestSet.Person}.
         * 
         * @return The age property.
         */
        public int getAge() {
            return age;
        }
    }
}