/*
 * Copyright (C) 2025 The TYPEWRITER Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package typewriter.api;

import java.time.ZonedDateTime;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import kiss.Signal;
import typewriter.api.model.DerivableModel;

public interface MigrationTestSet extends Testable {

    @Test
    default void addProperty() {
        String name = Testable.random();

        Person model = new Person("one");
        QueryExecutor<Person, Signal<Person>, ?, ?> old = createEmptyDB(Person.class, name);
        old.update(model);
        assert old.count() == 1;

        QueryExecutor<AgePerson, Signal<AgePerson>, ?, ?> latest = createEmptyDB(AgePerson.class, name);
        assert latest.count() == 1;
        AgePerson newModel = latest.findBy(model.getId()).to().exact();
        assert newModel.name.equals("one");
        assert newModel.age == 0;
    }

    @Test
    default void addMultiColumnProperty() {
        String name = Testable.random();

        Person model = new Person("one");
        QueryExecutor<Person, Signal<Person>, ?, ?> old = createEmptyDB(Person.class, name);
        old.update(model);
        assert old.count() == 1;

        QueryExecutor<ZonedDateTimePerson, Signal<ZonedDateTimePerson>, ?, ?> latest = createEmptyDB(ZonedDateTimePerson.class, name);
        assert latest.count() == 1;
        ZonedDateTimePerson newModel = latest.findBy(model.getId()).to().exact();
        assert newModel.name.equals("one");
        assert newModel.date == null;
    }

    @Test
    default void addMultiProperties() {
        String name = Testable.random();

        Person model = new Person("one");
        QueryExecutor<Person, Signal<Person>, ?, ?> old = createEmptyDB(Person.class, name);
        old.update(model);
        assert old.count() == 1;

        QueryExecutor<AgeGenderPerson, Signal<AgeGenderPerson>, ?, ?> latest = createEmptyDB(AgeGenderPerson.class, name);
        assert latest.count() == 1;
        AgeGenderPerson newModel = latest.findBy(model.getId()).to().exact();
        assert newModel.name.equals("one");
        assert newModel.male == false;
    }

    @Test
    default void deleteProperty() {
        String name = Testable.random();

        AgePerson model = new AgePerson("one", 10);
        QueryExecutor<AgePerson, Signal<AgePerson>, ?, ?> old = createEmptyDB(AgePerson.class, name);
        old.update(model);
        assert old.count() == 1;

        QueryExecutor<Person, Signal<Person>, ?, ?> latest = createEmptyDB(Person.class, name);
        assert latest.count() == 1;
        Person newModel = latest.findBy(model.getId()).to().exact();
        assert newModel.name.equals("one");
    }

    @Test
    @Disabled
    default void changeProeprtyType() {
        String name = Testable.random();

        AgePerson model = new AgePerson("one", 10);
        QueryExecutor<AgePerson, Signal<AgePerson>, ?, ?> old = createEmptyDB(AgePerson.class, name);
        old.update(model);
        assert old.count() == 1;

        QueryExecutor<StringAgePerson, Signal<StringAgePerson>, ?, ?> latest = createEmptyDB(StringAgePerson.class, name);
        assert latest.count() == 1;
        StringAgePerson newModel = latest.findBy(model.getId()).to().exact();
        assert newModel.name.equals("one");
        assert newModel.age.equals("10");
    }

    class Person extends DerivableModel {

        public String name;

        /**
         * Create empty model.
         */
        public Person() {
        }

        /**
         * @param name
         */
        public Person(String name) {
            this.name = name;
        }
    }

    class AgePerson extends DerivableModel {

        public String name;

        public int age;

        /**
         * Create empty model.
         */
        public AgePerson() {
        }

        /**
         * @param name
         * @param age
         */
        public AgePerson(String name, int age) {
            this.name = name;
            this.age = age;
        }
    }

    class StringAgePerson extends DerivableModel {

        public String name;

        public String age;

        /**
         * Create empty model.
         */
        public StringAgePerson() {
        }

        /**
         * @param name
         * @param age
         */
        public StringAgePerson(String name, String age) {
            this.name = name;
            this.age = age;
        }
    }

    class AgeGenderPerson extends DerivableModel {

        public String name;

        public int age;

        public boolean male;

        /**
         * Create empty model.
         */
        public AgeGenderPerson() {
        }

        /**
         * @param name
         * @param age
         */
        public AgeGenderPerson(String name, int age, boolean male) {
            this.name = name;
            this.age = age;
            this.male = male;
        }
    }

    class ZonedDateTimePerson extends DerivableModel {

        public String name;

        public ZonedDateTime date;

        /**
         * Create empty model.
         */
        public ZonedDateTimePerson() {
        }

        /**
         * @param name
         * @param date
         */
        public ZonedDateTimePerson(String name, ZonedDateTime date) {
            this.name = name;
            this.date = date;
        }
    }
}