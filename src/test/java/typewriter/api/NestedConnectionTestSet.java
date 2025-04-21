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

import org.junit.jupiter.api.Test;

import kiss.Signal;
import typewriter.api.model.DerivableModel;

public interface NestedConnectionTestSet extends Testable {

    @Test
    @Environment(key = "typewriter.connection.maxPool", value = "1")
    @Environment(key = "typewriter.connection.timeout", value = "500")
    default void nest() {
        Person person = new Person("test", 10);

        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);
        dao.update(person);

        dao.findBy(person.getId()).buffer().flatIterable(e -> e).to(p -> {
            p.name = "updated";
            dao.update(p);
        });

        assert dao.findBy(person.getId()).to().exact().name.equals("updated");
    }

    @Test
    @Environment(key = "typewriter.connection.maxPool", value = "1")
    @Environment(key = "typewriter.connection.timeout", value = "500")
    default void triple() {
        Person person = new Person("test", 10);

        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);
        dao.update(person);

        dao.findBy(person.getId()).buffer().to(_ -> {
            dao.findBy(person.getId()).buffer().flatIterable(x -> x).to(p -> {
                p.name = "updated";
                dao.update(p);
            });
        });

        assert dao.findBy(person.getId()).to().exact().name.equals("updated");
    }

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
    }
}