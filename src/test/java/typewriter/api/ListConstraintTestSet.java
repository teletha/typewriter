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

import kiss.I;
import kiss.Signal;
import typewriter.api.model.DerivableModel;

public interface ListConstraintTestSet extends Testable {

    @Test
    default void codec() {
        Person model1 = new Person("one", "first", "1", "一");
        Person model2 = new Person("two", "second", "2", "二");

        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);
        dao.update(model1);
        dao.update(model2);

        List<Person> found = dao.findAll().toList();
        assert found.size() == 2;
        Person person = found.get(0);
        assert person.name.equals("one");
        assert person.alias.get(0).equals("first");
        assert person.alias.get(1).equals("1");
        assert person.alias.get(2).equals("一");

        person = found.get(1);
        assert person.name.equals("two");
        assert person.alias.get(0).equals("second");
        assert person.alias.get(1).equals("2");
        assert person.alias.get(2).equals("二");
    }

    @Test
    default void contains() {
        Person model1 = new Person("one", "first", "1", "common");
        Person model2 = new Person("two", "second", "2", "common");
        Person model3 = new Person("three", "third", "3");

        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);
        dao.update(model1);
        dao.update(model2);
        dao.update(model3);

        List<Person> founds = dao.findBy(Person::getAlias, c -> c.contains("2")).toList();
        assert founds.size() == 1;
        assert founds.get(0).equals(model2);

        founds = dao.findBy(Person::getAlias, c -> c.contains("common")).toList();
        assert founds.size() == 2;
        assert founds.get(0).equals(model1);
        assert founds.get(1).equals(model2);

        List<Person> notFound = dao.findBy(Person::getAlias, c -> c.contains("not found")).toList();
        assert notFound.size() == 0;
    }

    @Test
    default void size() {
        Person model1 = new Person("one", "first", "1", "一");
        Person model2 = new Person("two", "second");

        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);
        dao.update(model1);
        dao.update(model2);

        List<Person> founds = dao.findBy(Person::getAlias, c -> c.size(1)).toList();
        assert founds.size() == 1;
        assert founds.get(0).equals(model2);
    }

    @Test
    default void isEmpty() {
        Person model1 = new Person("one", "first", "1", "一");
        Person model2 = new Person("two");

        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);
        dao.update(model1);
        dao.update(model2);

        List<Person> founds = dao.findBy(Person::getAlias, c -> c.isEmpty()).toList();
        assert founds.size() == 1;
        assert founds.get(0).equals(model2);
    }

    @Test
    default void isNotEmpty() {
        Person model1 = new Person("one", "first", "1", "一");
        Person model2 = new Person("two");

        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);
        dao.update(model1);
        dao.update(model2);

        List<Person> founds = dao.findBy(Person::getAlias, c -> c.isNotEmpty()).toList();
        assert founds.size() == 1;
        assert founds.get(0).equals(model1);
    }

    @Test
    default void isMoreThan() {
        Person model1 = new Person("one", "first", "common", "iti");
        Person model2 = new Person("two", "second", "common");
        Person model3 = new Person("three", "third");

        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);
        dao.update(model1);
        dao.update(model2);
        dao.update(model3);

        List<Person> founds = dao.findBy(Person::getAlias, c -> c.isMoreThan(2)).toList();
        assert founds.size() == 1;
        assert founds.get(0).equals(model1);
    }

    @Test
    default void isOrMoreThan() {
        Person model1 = new Person("one", "first", "common", "iti");
        Person model2 = new Person("two", "second", "common");
        Person model3 = new Person("three", "third");

        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);
        dao.update(model1);
        dao.update(model2);
        dao.update(model3);

        List<Person> founds = dao.findBy(Person::getAlias, c -> c.isOrMoreThan(2)).toList();
        assert founds.size() == 2;
        assert founds.get(0).equals(model1);
        assert founds.get(1).equals(model2);
    }

    @Test
    default void isLessThan() {
        Person model1 = new Person("one", "first", "common", "iti");
        Person model2 = new Person("two", "second", "common");
        Person model3 = new Person("three", "third");

        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);
        dao.update(model1);
        dao.update(model2);
        dao.update(model3);

        List<Person> founds = dao.findBy(Person::getAlias, c -> c.isLessThan(2)).toList();
        assert founds.size() == 1;
        assert founds.get(0).equals(model3);
    }

    @Test
    default void isOrLessThan() {
        Person model1 = new Person("one", "first", "common", "iti");
        Person model2 = new Person("two", "second", "common");
        Person model3 = new Person("three", "third");

        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);
        dao.update(model1);
        dao.update(model2);
        dao.update(model3);

        List<Person> founds = dao.findBy(Person::getAlias, c -> c.isOrLessThan(2)).toList();
        assert founds.size() == 2;
        assert founds.get(0).equals(model2);
        assert founds.get(1).equals(model3);
    }

    /**
     * 
     */
    class Person extends DerivableModel {

        public String name;

        public List<String> alias;

        public Person() {
        }

        public Person(String name, String... alias) {
            this.name = name;
            this.alias = I.list(alias);
        }

        /**
         * Get the alias property of this {@link ListConstraintTestSet.Person}.
         * 
         * @return The alias property.
         */
        public final List<String> getAlias() {
            return alias;
        }
    }
}