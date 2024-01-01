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

public interface DeletableTestSet extends Testable {

    @Test
    default void deleteModel() {
        Person model = new Person("one", 10);

        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);
        dao.update(model);
        assert dao.count() == 1;

        dao.delete(model);
        assert dao.count() == 0;
    }

    @Test
    default void deleteSpecifedProperty() {
        Person model = new Person("one", 10);

        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);
        dao.update(model);
        dao.delete(model, Person::getAge);

        Person found = dao.findBy(model.getId()).to().exact();
        assert found.age == 0;
        assert found.name.equals("one");
    }

    @Test
    default void deleteSpecifedProperties() {
        Person model = new Person("one", 10);

        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);
        dao.update(model);
        dao.delete(model, Person::getAge, Person::getName);

        Person found = dao.findBy(model.getId()).to().exact();
        assert found.age == 0;
        assert found.name == null;
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
         * Get the name property of this {@link DeletableTestSet.Person}.
         * 
         * @return The name property.
         */
        public String getName() {
            return name;
        }

        /**
         * Get the age property of this {@link DeletableTestSet.Person}.
         * 
         * @return The age property.
         */
        public int getAge() {
            return age;
        }
    }
}