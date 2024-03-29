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

import java.util.List;

import org.junit.jupiter.api.Test;

import kiss.Signal;
import typewriter.api.model.DerivableModel;

public interface AccumulableTestSet extends Testable {

    @Test
    default void distinct() {
        Person model1 = new Person("A", 10);
        Person model2 = new Person("A", 20);
        Person model3 = new Person("B", 30);
        Person model4 = new Person("B", 40);
        Person model5 = new Person("B", 50);
        Person model6 = new Person("C", 60);

        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);
        dao.update(model1);
        dao.update(model2);
        dao.update(model3);
        dao.update(model4);
        dao.update(model5);
        dao.update(model6);

        List<String> found = dao.distinct(Person::getName).toList();
        assert found.size() == 3;
        assert found.get(0).equals("A");
        assert found.get(1).equals("B");
        assert found.get(2).equals("C");
    }

    @Test
    default void min() {
        Person model1 = new Person("A", 10);
        Person model2 = new Person("B", 20);
        Person model3 = new Person("C", 30);
        Person model4 = new Person("D", 40);

        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);
        dao.update(model1);
        dao.update(model2);
        dao.update(model3);
        dao.update(model4);

        int calculated = dao.min(Person::getAge);
        assert calculated == 10;
    }

    @Test
    default void max() {
        Person model1 = new Person("A", 10);
        Person model2 = new Person("B", 20);
        Person model3 = new Person("C", 30);
        Person model4 = new Person("D", 40);

        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);
        dao.update(model1);
        dao.update(model2);
        dao.update(model3);
        dao.update(model4);

        int calculated = dao.max(Person::getAge);
        assert calculated == 40;
    }

    @Test
    default void avg() {
        Person model1 = new Person("A", 10);
        Person model2 = new Person("B", 20);
        Person model3 = new Person("C", 30);

        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);
        dao.update(model1);
        dao.update(model2);
        dao.update(model3);

        double calculated = dao.avg(Person::getAge);
        assert calculated == 20d;
    }

    @Test
    default void sum() {
        Person model1 = new Person("A", 10);
        Person model2 = new Person("B", 20);
        Person model3 = new Person("C", 30);

        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);
        dao.update(model1);
        dao.update(model2);
        dao.update(model3);

        int calculated = dao.sum(Person::getAge);
        assert calculated == 60;
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
         * Get the name property of this {@link AccumulableTestSet.Person}.
         * 
         * @return The name property.
         */
        public final String getName() {
            return name;
        }

        /**
         * Set the name property of this {@link AccumulableTestSet.Person}.
         * 
         * @param name The name value to set.
         */
        public final void setName(String name) {
            this.name = name;
        }

        /**
         * Get the age property of this {@link AccumulableTestSet.Person}.
         * 
         * @return The age property.
         */
        public final int getAge() {
            return age;
        }

        /**
         * Set the age property of this {@link AccumulableTestSet.Person}.
         * 
         * @param age The age value to set.
         */
        public final void setAge(int age) {
            this.age = age;
        }

        /**
         * Get the marked property of this {@link AccumulableTestSet.Person}.
         * 
         * @return The marked property.
         */
        public final boolean isMarked() {
            return marked;
        }

        /**
         * Set the marked property of this {@link AccumulableTestSet.Person}.
         * 
         * @param marked The marked value to set.
         */
        public final void setMarked(boolean marked) {
            this.marked = marked;
        }
    }
}