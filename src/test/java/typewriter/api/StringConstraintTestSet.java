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

public interface StringConstraintTestSet extends Testable {

    @Test
    default void is() {
        Person model1 = new Person("one");
        Person model2 = new Person("two");
        Person model3 = new Person("three");

        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);
        dao.update(model1);
        dao.update(model2);
        dao.update(model3);

        List<Person> founds = dao.findBy(Person::getName, c -> c.is("one")).toList();
        assert founds.size() == 1;
        assert founds.get(0).equals(model1);
    }

    @Test
    default void isNot() {
        Person model1 = new Person("one");
        Person model2 = new Person("two");
        Person model3 = new Person("three");

        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);
        dao.update(model1);
        dao.update(model2);
        dao.update(model3);

        List<Person> founds = dao.findBy(Person::getName, c -> c.isNot("one")).toList();
        assert founds.size() == 2;
        assert founds.get(0).equals(model2);
        assert founds.get(1).equals(model3);
    }

    @Test
    default void isEmpty() {
        Person model1 = new Person("");
        Person model2 = new Person(" ");
        Person model3 = new Person("\t");
        Person model4 = new Person(" not empty");

        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);
        dao.update(model1);
        dao.update(model2);
        dao.update(model3);
        dao.update(model4);

        List<Person> founds = dao.findBy(Person::getName, c -> c.isEmpty()).toList();
        assert founds.size() == 1;
        assert founds.get(0).equals(model1);
    }

    @Test
    default void isNotEmpty() {
        Person model1 = new Person("");
        Person model2 = new Person(" ");
        Person model3 = new Person("\t");
        Person model4 = new Person(" not empty");

        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);
        dao.update(model1);
        dao.update(model2);
        dao.update(model3);
        dao.update(model4);

        List<Person> founds = dao.findBy(Person::getName, c -> c.isNotEmpty()).toList();
        assert founds.size() == 3;
        assert founds.get(0).equals(model2);
        assert founds.get(1).equals(model3);
        assert founds.get(2).equals(model4);
    }

    @Test
    default void isLessThan() {
        Person model1 = new Person("one");
        Person model2 = new Person("two");
        Person model3 = new Person("three");

        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);
        dao.update(model1);
        dao.update(model2);
        dao.update(model3);

        List<Person> founds = dao.findBy(Person::getName, c -> c.isLessThan(4)).toList();
        assert founds.size() == 2;
        assert founds.get(0).equals(model1);
        assert founds.get(1).equals(model2);
    }

    @Test
    default void isOrLessThan() {
        Person model1 = new Person("one");
        Person model2 = new Person("two");
        Person model3 = new Person("three");

        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);
        dao.update(model1);
        dao.update(model2);
        dao.update(model3);

        List<Person> founds = dao.findBy(Person::getName, c -> c.isOrLessThan(3)).toList();
        assert founds.size() == 2;
        assert founds.get(0).equals(model1);
        assert founds.get(1).equals(model2);
    }

    @Test
    default void isMoreThan() {
        Person model1 = new Person("one");
        Person model2 = new Person("two");
        Person model3 = new Person("three");

        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);
        dao.update(model1);
        dao.update(model2);
        dao.update(model3);

        List<Person> founds = dao.findBy(Person::getName, c -> c.isMoreThan(3)).toList();
        assert founds.size() == 1;
        assert founds.get(0).equals(model3);
    }

    @Test
    default void isOrMoreThan() {
        Person model1 = new Person("one");
        Person model2 = new Person("two");
        Person model3 = new Person("three");

        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);
        dao.update(model1);
        dao.update(model2);
        dao.update(model3);

        List<Person> founds = dao.findBy(Person::getName, c -> c.isOrMoreThan(4)).toList();
        assert founds.size() == 1;
        assert founds.get(0).equals(model3);
    }

    @Test
    default void contains() {
        Person model1 = new Person("one");
        Person model2 = new Person("two");
        Person model3 = new Person("three");

        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);
        dao.update(model1);
        dao.update(model2);
        dao.update(model3);

        List<Person> founds = dao.findBy(Person::getName, c -> c.contains("t")).toList();
        assert founds.size() == 2;
        assert founds.get(0).equals(model2);
        assert founds.get(1).equals(model3);
    }

    @Test
    default void regex() {
        Person model1 = new Person("one");
        Person model2 = new Person("two");
        Person model3 = new Person("three");

        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);
        dao.update(model1);
        dao.update(model2);
        dao.update(model3);

        List<Person> founds = dao.findBy(Person::getName, c -> c.regex("one")).toList();
        assert founds.size() == 1;
        assert founds.get(0).equals(model1);

        founds = dao.findBy(Person::getName, c -> c.regex("t.+")).toList();
        assert founds.size() == 2;
        assert founds.get(0).equals(model2);
        assert founds.get(1).equals(model3);
    }

    @Test
    default void multipleConditions() {
        Person model1 = new Person("one");
        Person model2 = new Person("two");
        Person model3 = new Person("three");
        Person model4 = new Person("four");

        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);
        dao.update(model1);
        dao.update(model2);
        dao.update(model3);
        dao.update(model4);

        List<Person> founds = dao.findBy(Person::getName, c -> c.isOrMoreThan(4).isLessThan(5)).toList();
        assert founds.size() == 1;
        assert founds.get(0).equals(model4);
    }

    /**
     * 
     */
    class Person extends DerivableModel {

        public String name;

        /**
         * Create empty model.
         */
        private Person() {
        }

        /**
         * @param name
         */
        private Person(String name) {
            this.name = name;
        }

        /**
         * Get the name property of this {@link StringConstraintTestSet.Person}.
         * 
         * @return The name property.
         */
        public String getName() {
            return name;
        }
    }
}