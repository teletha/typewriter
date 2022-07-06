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

import org.junit.jupiter.api.Test;

class DeletableTest extends MongoTestSupport {

    @Test
    void deleteModel() {
        Person model = new Person("one", 10);

        Mongo<Person> mongo = createEmptyDB(Person.class);
        mongo.update(model);
        assert mongo.count() == 1;

        mongo.delete(model);
        assert mongo.count() == 0;
    }

    @Test
    void deleteModelByID() {
        Person model = new Person("one", 10);

        Mongo<Person> mongo = createEmptyDB(Person.class);
        mongo.update(model);
        assert mongo.count() == 1;

        mongo.delete(model.id);
        assert mongo.count() == 0;
    }

    @Test
    void deleteSpecifedProperty() {
        Person model = new Person("one", 10);

        Mongo<Person> mongo = createEmptyDB(Person.class);
        mongo.update(model);
        mongo.delete(model, Person::getAge);

        Person found = mongo.findBy(model.id).waitForTerminate().to().exact();
        assert found.age == 0;
        assert found.name.equals("one");
    }

    @Test
    void deleteSpecifedPropertyByID() {
        Person model = new Person("one", 10);

        Mongo<Person> mongo = createEmptyDB(Person.class);
        mongo.update(model);
        mongo.delete(model.id, Person::getAge);

        Person found = mongo.findBy(model.id).waitForTerminate().to().exact();
        assert found.age == 0;
        assert found.name.equals("one");
    }

    @Test
    void deleteSpecifedProperties() {
        Person model = new Person("one", 10);

        Mongo<Person> mongo = createEmptyDB(Person.class);
        mongo.update(model);
        mongo.delete(model, Person::getAge, Person::getName);

        Person found = mongo.findBy(model.id).waitForTerminate().to().exact();
        assert found.age == 0;
        assert found.name.isEmpty();
    }

    @Test
    void deleteSpecifedPropertiesByID() {
        Person model = new Person("one", 10);

        Mongo<Person> mongo = createEmptyDB(Person.class);
        mongo.update(model);
        mongo.delete(model.id, Person::getAge, Person::getName);

        Person found = mongo.findBy(model.id).waitForTerminate().to().exact();
        assert found.age == 0;
        assert found.name.isEmpty();
    }

    /**
     * 
     */
    private static class Person extends DerivableModel {
        public String name;

        public int age;

        /**
         * @param name
         * @param age
         */
        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        /**
         * Get the name property of this {@link DeletableTest.Person}.
         * 
         * @return The name property.
         */
        public String getName() {
            return name;
        }

        /**
         * Get the age property of this {@link DeletableTest.Person}.
         * 
         * @return The age property.
         */
        public int getAge() {
            return age;
        }
    }
}
