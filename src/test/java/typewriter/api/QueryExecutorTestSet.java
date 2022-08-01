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

import java.util.List;

import org.junit.jupiter.api.Test;

import kiss.Signal;

public interface QueryExecutorTestSet extends Testable {

    @Test
    default void saveModel() {
        Person model = new Person("one", 10);

        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);
        dao.update(model);

        Person found = dao.findBy(model.getId()).to().exact();
        assert found.equals(model);
    }

    @Test
    default void saveMultipleModels() {
        Person model1 = new Person("one", 10);
        Person model2 = new Person("two", 20);
        Person model3 = new Person("three", 30);
        Person model4 = new Person("four", 40);
        Person model5 = new Person("five", 50);

        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);
        dao.update(model1);
        dao.update(model2);
        dao.update(model3);
        dao.update(model4);
        dao.update(model5);

        Person found = dao.findBy(model3.getId()).to().exact();
        assert found.equals(model3);

        found = dao.findBy(model5.getId()).to().exact();
        assert found.equals(model5);
    }

    @Test
    default void updateSpecifedPropertyOnly() {
        Person model = new Person("one", 10);

        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);
        dao.update(model);

        model.age = 20;
        model.name = "don't update";
        dao.update(model, Person::getAge);

        Person found = dao.findBy(model.getId()).to().exact();
        assert found.age == 20;
        assert found.name.equals("one");
    }

    @Test
    default void findAll() {
        Person model1 = new Person("one", 10);
        Person model2 = new Person("two", 20);
        Person model3 = new Person("three", 30);
        Person model4 = new Person("four", 40);
        Person model5 = new Person("five", 50);

        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);
        dao.update(model1);
        dao.update(model2);
        dao.update(model3);
        dao.update(model4);
        dao.update(model5);

        List<Person> found = dao.findAll().toList();
        assert found.size() == 5;
    }

    @Test
    default void limit() {
        Person model1 = new Person("one", 10);
        Person model2 = new Person("two", 20);
        Person model3 = new Person("three", 30);
        Person model4 = new Person("four", 40);
        Person model5 = new Person("five", 50);

        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);
        dao.update(model1);
        dao.update(model2);
        dao.update(model3);
        dao.update(model4);
        dao.update(model5);

        List<Person> found = dao.findBy(o -> o.limit(3)).toList();
        assert found.size() == 3;
    }

    @Test
    default void offset() {
        Person model1 = new Person("one", 10);
        Person model2 = new Person("two", 20);
        Person model3 = new Person("three", 30);
        Person model4 = new Person("four", 40);
        Person model5 = new Person("five", 50);

        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);
        dao.update(model1);
        dao.update(model2);
        dao.update(model3);
        dao.update(model4);
        dao.update(model5);

        List<Person> found = dao.findBy(o -> o.offset(3)).toList();
        assert found.size() == 2;
        assert found.get(0).equals(model4);
        assert found.get(1).equals(model5);
    }

    @Test
    default void limitAndOffset() {
        Person model1 = new Person("one", 10);
        Person model2 = new Person("two", 20);
        Person model3 = new Person("three", 30);
        Person model4 = new Person("four", 40);
        Person model5 = new Person("five", 50);

        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);
        dao.update(model1);
        dao.update(model2);
        dao.update(model3);
        dao.update(model4);
        dao.update(model5);

        List<Person> found = dao.findBy(o -> o.limit(2).offset(2)).toList();
        assert found.size() == 2;
        assert found.get(0).equals(model3);
        assert found.get(1).equals(model4);

        found = dao.findBy(o -> o.offset(1).limit(2)).toList();
        assert found.size() == 2;
        assert found.get(0).equals(model2);
        assert found.get(1).equals(model3);
    }

    /**
     * 
     */
    class Person extends DerivableModel {

        private String name;

        private int age;

        private boolean marked;

        /**
         * Create empty model.
         */
        private Person() {
        }

        /**
         * @param name
         * @param age
         */
        private Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        /**
         * Get the name property of this {@link QueryExecutorTestSet.Person}.
         * 
         * @return The name property.
         */
        public final String getName() {
            return name;
        }

        /**
         * Set the name property of this {@link QueryExecutorTestSet.Person}.
         * 
         * @param name The name value to set.
         */
        public final void setName(String name) {
            this.name = name;
        }

        /**
         * Get the age property of this {@link QueryExecutorTestSet.Person}.
         * 
         * @return The age property.
         */
        public final int getAge() {
            return age;
        }

        /**
         * Set the age property of this {@link QueryExecutorTestSet.Person}.
         * 
         * @param age The age value to set.
         */
        public final void setAge(int age) {
            this.age = age;
        }

        /**
         * Get the marked property of this {@link QueryExecutorTestSet.Person}.
         * 
         * @return The marked property.
         */
        public final boolean isMarked() {
            return marked;
        }

        /**
         * Set the marked property of this {@link QueryExecutorTestSet.Person}.
         * 
         * @param marked The marked value to set.
         */
        public final void setMarked(boolean marked) {
            this.marked = marked;
        }
    }
}
