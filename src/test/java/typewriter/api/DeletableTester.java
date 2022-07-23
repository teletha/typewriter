/*
 * Copyright (C) 2022 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package typewriter.api;

import org.junit.jupiter.api.Test;

import kiss.Signal;

public interface DeletableTester extends Testable {

    @Test
    default void deleteModel() {
        Person model = new Person("one", 10);

        QueryExecutor<Person, Signal<Person>, ?> mongo = createEmptyDB(Person.class);
        mongo.update(model);
        assert mongo.count() == 1;

        mongo.delete(model);
        assert mongo.count() == 0;
    }

    @Test
    default void deleteSpecifedProperty() {
        Person model = new Person("one", 10);

        QueryExecutor<Person, Signal<Person>, ?> mongo = createEmptyDB(Person.class);
        mongo.update(model);
        mongo.delete(model, Person::getAge);

        Person found = mongo.findBy(model.getId()).to().exact();
        assert found.age == 0;
        assert found.name.equals("one");
    }

    @Test
    default void deleteSpecifedProperties() {
        Person model = new Person("one", 10);

        QueryExecutor<Person, Signal<Person>, ?> mongo = createEmptyDB(Person.class);
        mongo.update(model);
        mongo.delete(model, Person::getAge, Person::getName);

        Person found = mongo.findBy(model.getId()).to().exact();
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
         * Get the name property of this {@link DeletableTester.Person}.
         * 
         * @return The name property.
         */
        public String getName() {
            return name;
        }

        /**
         * Get the age property of this {@link DeletableTester.Person}.
         * 
         * @return The age property.
         */
        public int getAge() {
            return age;
        }
    }
}
