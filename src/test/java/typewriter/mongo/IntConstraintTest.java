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

class IntConstraintTest extends MongoTestSupport {

    @Test
    void is() {
        Person model1 = new Person(10);
        Person model2 = new Person(20);
        Person model3 = new Person(30);

        Mongo<Person> mongo = createEmptyDB(Person.class);
        mongo.update(model1);
        mongo.update(model2);
        mongo.update(model3);

        List<Person> founds = mongo.findBy(Person::getAge, c -> c.is(10)).waitForTerminate().toList();
        assert founds.size() == 1;
        assert founds.get(0).equals(model1);
    }

    @Test
    void isNot() {
        Person model1 = new Person(10);
        Person model2 = new Person(20);
        Person model3 = new Person(30);

        Mongo<Person> mongo = createEmptyDB(Person.class);
        mongo.update(model1);
        mongo.update(model2);
        mongo.update(model3);

        List<Person> founds = mongo.findBy(Person::getAge, c -> c.isNot(10)).waitForTerminate().toList();
        assert founds.size() == 2;
        assert founds.get(0).equals(model2);
        assert founds.get(1).equals(model3);
    }

    @Test
    void lessThan() {
        Person model1 = new Person(10);
        Person model2 = new Person(20);
        Person model3 = new Person(30);

        Mongo<Person> mongo = createEmptyDB(Person.class);
        mongo.update(model1);
        mongo.update(model2);
        mongo.update(model3);

        List<Person> founds = mongo.findBy(Person::getAge, c -> c.lessThan(20)).waitForTerminate().toList();
        assert founds.size() == 1;
        assert founds.get(0).equals(model1);
    }

    @Test
    void lessThanOrEqual() {
        Person model1 = new Person(10);
        Person model2 = new Person(20);
        Person model3 = new Person(30);

        Mongo<Person> mongo = createEmptyDB(Person.class);
        mongo.update(model1);
        mongo.update(model2);
        mongo.update(model3);

        List<Person> founds = mongo.findBy(Person::getAge, c -> c.lessThanOrEqual(20)).waitForTerminate().toList();
        assert founds.size() == 2;
        assert founds.get(0).equals(model1);
        assert founds.get(1).equals(model2);
    }

    @Test
    void greaterThan() {
        Person model1 = new Person(10);
        Person model2 = new Person(20);
        Person model3 = new Person(30);

        Mongo<Person> mongo = createEmptyDB(Person.class);
        mongo.update(model1);
        mongo.update(model2);
        mongo.update(model3);

        List<Person> founds = mongo.findBy(Person::getAge, c -> c.greaterThan(20)).waitForTerminate().toList();
        assert founds.size() == 1;
        assert founds.get(0).equals(model3);
    }

    @Test
    void greaterThanOrEqual() {
        Person model1 = new Person(10);
        Person model2 = new Person(20);
        Person model3 = new Person(30);

        Mongo<Person> mongo = createEmptyDB(Person.class);
        mongo.update(model1);
        mongo.update(model2);
        mongo.update(model3);

        List<Person> founds = mongo.findBy(Person::getAge, c -> c.greaterThanOrEqual(20)).waitForTerminate().toList();
        assert founds.size() == 2;
        assert founds.get(0).equals(model2);
        assert founds.get(1).equals(model3);
    }

    /**
     * 
     */
    private static class Person extends DerivableModel {

        public int age;

        /**
         */
        private Person(int age) {
            this.age = age;
        }

        /**
         * Get the age property of this {@link IntConstraintTest.Person}.
         * 
         * @return The age property.
         */
        public int getAge() {
            return age;
        }
    }
}
