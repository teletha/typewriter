/*
 * Copyright (C) 2023 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package typewriter.query;

import org.junit.jupiter.api.Test;

import typewriter.api.model.IdentifiableModel;

public class QueryTest {

    @Test
    void selectName() {
        Query query = new Query() {
            {
                SELECT("test");
            }
        };

        assert query.is("SELECT test");
    }

    @Test
    void selectNames() {
        Query query = new Query() {
            {
                SELECT("first", "second");
            }
        };

        assert query.is("SELECT first, second");
    }

    @Test
    void selectSpecifier() {
        Query query = new Query<Person>() {
            {
                SELECT(Person::getName);
            }
        };

        assert query.is("SELECT name");
    }

    @Test
    void selectSpecifiers() {
        Query query = new Query() {
            {
                SELECT("first", "second");
            }
        };

        assert query.is("SELECT first, second");
    }

    /**
     * 
     */
    static class Person extends IdentifiableModel {

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
         * Get the name property of this {@link Person}.
         * 
         * @return The name property.
         */
        public String getName() {
            return name;
        }

        /**
         * Get the age property of this {@link Person}.
         * 
         * @return The age property.
         */
        public int getAge() {
            return age;
        }
    }
}
