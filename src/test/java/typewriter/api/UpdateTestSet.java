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

public interface UpdateTestSet extends Testable {

    @Test
    default void insert() {
        Person model1 = new Person("one", 10);
        Person model2 = new Person("two", 20);

        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);
        dao.update(model1);
        assert dao.count() == 1;

        dao.update(model2);
        assert dao.count() == 2;
    }

    @Test
    default void update() {
        Person model = new Person("one", 10);

        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);
        dao.update(model);
        Person restored = dao.findBy(model.getId()).to().exact();
        assert restored.name.equals("one");
        assert restored.age == 10;

        // update
        model.name = "updated";
        model.age = 20;
        dao.update(model);

        restored = dao.findBy(model.getId()).to().exact();
        assert restored.name.equals("updated");
        assert restored.age == 20;
    }

    @Test
    default void updateAll() {
        Person model1 = new Person("one", 10);
        Person model2 = new Person("two", 20);

        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);
        dao.updateAll(model1, model2);

        List<Person> list = dao.findAll().toList();
        model1 = list.get(0);
        model2 = list.get(1);
        assert model1.age == 10;
        assert model1.name.equals("one");
        assert model2.age == 20;
        assert model2.name.equals("two");
    }

    @Test
    default void updatePartial() {
        Person model1 = new Person("one", 10);
        Person model2 = new Person("two", 20);

        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);
        dao.update(model1);

        model1.age = 15;
        dao.updateAll(model1, model2);

        List<Person> list = dao.findAll().toList();
        model1 = list.get(0);
        model2 = list.get(1);
        assert model1.age == 15;
        assert model1.name.equals("one");
        assert model2.age == 20;
        assert model2.name.equals("two");
    }

    @Test
    default void updateLazy() {
        Person model1 = new Person("one", 10);
        Person model2 = new Person("two", 20);

        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);
        dao.updateLazy(model1);
        assert dao.count() == 0;

        dao.updateLazy(model2);
        assert dao.count() == 0;

        dao.flush();
        assert dao.count() == 2;
    }

    @Test
    default void updateLazyLimitSize() {
        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);
        dao.setMax(20);
        for (int i = 0; i < 10; i++) {
            dao.updateLazy(new Person(Testable.random(), Testable.randomInt()));
        }
        assert dao.count() == 0;

        for (int i = 0; i < 20; i++) {
            dao.updateLazy(new Person(Testable.random(), Testable.randomInt()));
        }
        assert dao.count() == 20;

        dao.flush();
        assert dao.count() == 30;
    }

    @Test
    default void insertSpecifiedPropertyOnly() {
        Person model1 = new Person("one", 10);
        Person model2 = new Person("two", 20);

        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);
        dao.update(model1, Person::getAge);
        dao.update(model2, Person::getName);

        List<Person> list = dao.findAll().toList();
        model1 = list.get(0);
        model2 = list.get(1);
        assert model1.age == 10;
        assert model1.name == null;
        assert model2.age == 0;
        assert model2.name.equals("two");
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
         * Get the name property of this {@link UpdateTestSet.Person}.
         * 
         * @return The name property.
         */
        public String getName() {
            return name;
        }

        /**
         * Get the age property of this {@link UpdateTestSet.Person}.
         * 
         * @return The age property.
         */
        public int getAge() {
            return age;
        }
    }
}