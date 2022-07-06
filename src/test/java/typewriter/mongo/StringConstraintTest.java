/*
 * Copyright (C) 2022 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package typewriter.mongo;

import java.util.List;

import org.junit.jupiter.api.Test;

class StringConstraintTest extends MongoTestSupport {

    @Test
    void is() {
        Person model1 = new Person("one");
        Person model2 = new Person("two");
        Person model3 = new Person("three");

        Mongo<Person> mongo = createEmptyDB(Person.class);
        mongo.update(model1);
        mongo.update(model2);
        mongo.update(model3);

        List<Person> founds = mongo.findBy(Person::getName, c -> c.is("one")).waitForTerminate().toList();
        assert founds.size() == 1;
        assert founds.get(0).equals(model1);
    }

    @Test
    void isNot() {
        Person model1 = new Person("one");
        Person model2 = new Person("two");
        Person model3 = new Person("three");

        Mongo<Person> mongo = createEmptyDB(Person.class);
        mongo.update(model1);
        mongo.update(model2);
        mongo.update(model3);

        List<Person> founds = mongo.findBy(Person::getName, c -> c.isNot("one")).waitForTerminate().toList();
        assert founds.size() == 2;
        assert founds.get(0).equals(model2);
        assert founds.get(1).equals(model3);
    }

    @Test
    void lessThan() {
        Person model1 = new Person("one");
        Person model2 = new Person("two");
        Person model3 = new Person("three");

        Mongo<Person> mongo = createEmptyDB(Person.class);
        mongo.update(model1);
        mongo.update(model2);
        mongo.update(model3);

        List<Person> founds = mongo.findBy(Person::getName, c -> c.lessThan(4)).waitForTerminate().toList();
        assert founds.size() == 2;
        assert founds.get(0).equals(model1);
        assert founds.get(1).equals(model2);
    }

    @Test
    void lessThanOrEqual() {
        Person model1 = new Person("one");
        Person model2 = new Person("two");
        Person model3 = new Person("three");

        Mongo<Person> mongo = createEmptyDB(Person.class);
        mongo.update(model1);
        mongo.update(model2);
        mongo.update(model3);

        List<Person> founds = mongo.findBy(Person::getName, c -> c.lessThanOrEqual(3)).waitForTerminate().toList();
        assert founds.size() == 2;
        assert founds.get(0).equals(model1);
        assert founds.get(1).equals(model2);
    }

    @Test
    void greaterThan() {
        Person model1 = new Person("one");
        Person model2 = new Person("two");
        Person model3 = new Person("three");

        Mongo<Person> mongo = createEmptyDB(Person.class);
        mongo.update(model1);
        mongo.update(model2);
        mongo.update(model3);

        List<Person> founds = mongo.findBy(Person::getName, c -> c.greaterThan(3)).waitForTerminate().toList();
        assert founds.size() == 1;
        assert founds.get(0).equals(model3);
    }

    @Test
    void greaterThanOrEqual() {
        Person model1 = new Person("one");
        Person model2 = new Person("two");
        Person model3 = new Person("three");

        Mongo<Person> mongo = createEmptyDB(Person.class);
        mongo.update(model1);
        mongo.update(model2);
        mongo.update(model3);

        List<Person> founds = mongo.findBy(Person::getName, c -> c.greaterThanOrEqual(4)).waitForTerminate().toList();
        assert founds.size() == 1;
        assert founds.get(0).equals(model3);
    }

    /**
     * 
     */
    private static class Person extends DerivableModel {

        public String name;

        /**
         * @param name
         */
        private Person(String name) {
            this.name = name;
        }

        /**
         * Get the name property of this {@link StringConstraintTest.Person}.
         * 
         * @return The name property.
         */
        public String getName() {
            return name;
        }
    }
}
