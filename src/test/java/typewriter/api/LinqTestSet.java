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

import org.junit.jupiter.api.Test;

import typewriter.api.model.DerivableModel;
import typewriter.rdb.RDB;

public interface LinqTestSet extends Testable {

    @Test
    default void stringEquals() {
        Person model1 = new Person("A", 10);
        Person model2 = new Person("A", 20);
        Person model3 = new Person("B", 30);
        Person model4 = new Person("B", 40);
        Person model5 = new Person("B", 50);
        Person model6 = new Person("C", 60);

        RDB<Person> dao = createDB(model1, model2, model3, model4, model5, model6);

        assert dao.query().selectAll().fromModelTable().where(m -> m.getName().equals("A")).qurey().toList().size() == 2;
    }

    @Test
    default void stringContains() {
        Person model1 = new Person("A", 10);
        Person model2 = new Person("A", 20);
        Person model3 = new Person("AB", 30);
        Person model4 = new Person("B", 40);
        Person model5 = new Person("BA", 50);
        Person model6 = new Person("C", 60);

        RDB<Person> dao = createDB(model1, model2, model3, model4, model5, model6);

        assert dao.query().selectAll().fromModelTable().where(m -> m.getName().contains("A")).qurey().toList().size() == 4;
    }

    @Test
    default void equal() {
        Person model1 = new Person("A", 10);
        Person model2 = new Person("A", 20);
        Person model3 = new Person("B", 30);
        Person model4 = new Person("B", 40);
        Person model5 = new Person("B", 50);
        Person model6 = new Person("C", 60);

        RDB<Person> dao = createDB(model1, model2, model3, model4, model5, model6);

        assert dao.query().selectAll().fromModelTable().where(m -> m.getAge() == 20).qurey().toList().size() == 1;
    }

    @Test
    default void accessExternalLocalVariable() {
        Person model1 = new Person("A", 10);
        Person model2 = new Person("A", 20);
        Person model3 = new Person("B", 30);
        Person model4 = new Person("B", 40);
        Person model5 = new Person("B", 50);
        Person model6 = new Person("C", 60);

        RDB<Person> dao = createDB(model1, model2, model3, model4, model5, model6);

        int param = 20;
        assert dao.query().selectAll().fromModelTable().where(m -> m.getAge() == param).qurey().toList().size() == 1;
    }

    @Test
    default void notEqual() {
        Person model1 = new Person("A", 10);
        Person model2 = new Person("A", 20);
        Person model3 = new Person("B", 30);
        Person model4 = new Person("B", 40);
        Person model5 = new Person("B", 50);
        Person model6 = new Person("C", 60);

        RDB<Person> dao = createDB(model1, model2, model3, model4, model5, model6);

        assert dao.query().selectAll().fromModelTable().where(m -> m.getAge() != 20).qurey().toList().size() == 5;
    }

    @Test
    default void lessThan() {
        Person model1 = new Person("A", 10);
        Person model2 = new Person("A", 20);
        Person model3 = new Person("B", 30);
        Person model4 = new Person("B", 40);
        Person model5 = new Person("B", 50);
        Person model6 = new Person("C", 60);

        RDB<Person> dao = createDB(model1, model2, model3, model4, model5, model6);

        assert dao.query().selectAll().fromModelTable().where(m -> m.getAge() < 40).qurey().toList().size() == 3;
    }

    @Test
    default void lessThanOrEqual() {
        Person model1 = new Person("A", 10);
        Person model2 = new Person("A", 20);
        Person model3 = new Person("B", 30);
        Person model4 = new Person("B", 40);
        Person model5 = new Person("B", 50);
        Person model6 = new Person("C", 60);

        RDB<Person> dao = createDB(model1, model2, model3, model4, model5, model6);

        assert dao.query().selectAll().fromModelTable().where(m -> m.getAge() <= 40).qurey().toList().size() == 4;
    }

    @Test
    default void greaterThan() {
        Person model1 = new Person("A", 10);
        Person model2 = new Person("A", 20);
        Person model3 = new Person("B", 30);
        Person model4 = new Person("B", 40);
        Person model5 = new Person("B", 50);
        Person model6 = new Person("C", 60);

        RDB<Person> dao = createDB(model1, model2, model3, model4, model5, model6);

        assert dao.query().selectAll().fromModelTable().where(m -> m.getAge() > 40).qurey().toList().size() == 2;
    }

    @Test
    default void greaterThanOrEqual() {
        Person model1 = new Person("A", 10);
        Person model2 = new Person("A", 20);
        Person model3 = new Person("B", 30);
        Person model4 = new Person("B", 40);
        Person model5 = new Person("B", 50);
        Person model6 = new Person("C", 60);

        RDB<Person> dao = createDB(model1, model2, model3, model4, model5, model6);

        assert dao.query().selectAll().fromModelTable().where(m -> m.getAge() >= 40).qurey().toList().size() == 3;
    }

    @Test
    default void or() {
        Person model1 = new Person("A", 10);
        Person model2 = new Person("A", 20);
        Person model3 = new Person("B", 30);
        Person model4 = new Person("B", 40);
        Person model5 = new Person("B", 50);
        Person model6 = new Person("C", 60);

        RDB<Person> dao = createDB(model1, model2, model3, model4, model5, model6);

        assert dao.query().selectAll().fromModelTable().where(m -> m.getAge() <= 20 || 50 <= m.getAge()).qurey().toList().size() == 4;
    }

    @Test
    default void and() {
        Person model1 = new Person("A", 10);
        Person model2 = new Person("A", 20);
        Person model3 = new Person("B", 10);
        Person model4 = new Person("B", 20);
        Person model5 = new Person("B", 30);
        Person model6 = new Person("C", 10);

        RDB<Person> dao = createDB(model1, model2, model3, model4, model5, model6);

        assert dao.query()
                .selectAll()
                .fromModelTable()
                .where(m -> m.getAge() <= 10 && m.getName().equals("A"))
                .qurey()
                .toList()
                .size() == 1;
    }

    /**
     * 
     */
    class Person extends DerivableModel {

        private String name;

        private int age;

        private boolean marked;

        /**
         * @param name
         * @param age
         */
        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        /**
         * Get the name property of this {@link LinqTestSet.Person}.
         * 
         * @return The name property.
         */
        public final String getName() {
            return name;
        }

        /**
         * Set the name property of this {@link LinqTestSet.Person}.
         * 
         * @param name The name value to set.
         */
        public final void setName(String name) {
            this.name = name;
        }

        /**
         * Get the age property of this {@link LinqTestSet.Person}.
         * 
         * @return The age property.
         */
        public final int getAge() {
            return age;
        }

        /**
         * Set the age property of this {@link LinqTestSet.Person}.
         * 
         * @param age The age value to set.
         */
        public final void setAge(int age) {
            this.age = age;
        }

        /**
         * Get the marked property of this {@link LinqTestSet.Person}.
         * 
         * @return The marked property.
         */
        public final boolean isMarked() {
            return marked;
        }

        /**
         * Set the marked property of this {@link LinqTestSet.Person}.
         * 
         * @param marked The marked value to set.
         */
        public final void setMarked(boolean marked) {
            this.marked = marked;
        }
    }
}