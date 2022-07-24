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

public interface IntConstraintTestSet extends Testable {

    @Test
    default void is() {
        Person model1 = new Person(10);
        Person model2 = new Person(20);
        Person model3 = new Person(30);

        QueryExecutor<Person, Signal<Person>, ?> dao = createEmptyDB(Person.class);
        dao.update(model1);
        dao.update(model2);
        dao.update(model3);

        List<Person> founds = dao.findBy(Person::getAge, c -> c.is(10)).toList();
        assert founds.size() == 1;
        assert founds.get(0).equals(model1);
    }

    @Test
    default void isNot() {
        Person model1 = new Person(10);
        Person model2 = new Person(20);
        Person model3 = new Person(30);

        QueryExecutor<Person, Signal<Person>, ?> dao = createEmptyDB(Person.class);
        dao.update(model1);
        dao.update(model2);
        dao.update(model3);

        List<Person> founds = dao.findBy(Person::getAge, c -> c.isNot(10)).toList();
        assert founds.size() == 2;
        assert founds.get(0).equals(model2);
        assert founds.get(1).equals(model3);
    }

    @Test
    default void lessThan() {
        Person model1 = new Person(10);
        Person model2 = new Person(20);
        Person model3 = new Person(30);

        QueryExecutor<Person, Signal<Person>, ?> dao = createEmptyDB(Person.class);
        dao.update(model1);
        dao.update(model2);
        dao.update(model3);

        List<Person> founds = dao.findBy(Person::getAge, c -> c.lessThan(20)).toList();
        assert founds.size() == 1;
        assert founds.get(0).equals(model1);
    }

    @Test
    default void lessThanOrEqual() {
        Person model1 = new Person(10);
        Person model2 = new Person(20);
        Person model3 = new Person(30);

        QueryExecutor<Person, Signal<Person>, ?> dao = createEmptyDB(Person.class);
        dao.update(model1);
        dao.update(model2);
        dao.update(model3);

        List<Person> founds = dao.findBy(Person::getAge, c -> c.lessThanOrEqual(20)).toList();
        assert founds.size() == 2;
        assert founds.get(0).equals(model1);
        assert founds.get(1).equals(model2);
    }

    @Test
    default void greaterThan() {
        Person model1 = new Person(10);
        Person model2 = new Person(20);
        Person model3 = new Person(30);

        QueryExecutor<Person, Signal<Person>, ?> dao = createEmptyDB(Person.class);
        dao.update(model1);
        dao.update(model2);
        dao.update(model3);

        List<Person> founds = dao.findBy(Person::getAge, c -> c.greaterThan(20)).toList();
        assert founds.size() == 1;
        assert founds.get(0).equals(model3);
    }

    @Test
    default void greaterThanOrEqual() {
        Person model1 = new Person(10);
        Person model2 = new Person(20);
        Person model3 = new Person(30);

        QueryExecutor<Person, Signal<Person>, ?> dao = createEmptyDB(Person.class);
        dao.update(model1);
        dao.update(model2);
        dao.update(model3);

        List<Person> founds = dao.findBy(Person::getAge, c -> c.greaterThanOrEqual(20)).toList();
        assert founds.size() == 2;
        assert founds.get(0).equals(model2);
        assert founds.get(1).equals(model3);
    }

    /**
     * 
     */
    class Person extends DerivableModel {

        public int age;

        /**
         * Create empty model.
         */
        private Person() {
        }

        /**
         */
        private Person(int age) {
            this.age = age;
        }

        /**
         * Get the age property of this {@link IntConstraintTestSet.Person}.
         * 
         * @return The age property.
         */
        public int getAge() {
            return age;
        }
    }
}
