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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;

import kiss.Signal;
import typewriter.api.model.DerivableModel;

public interface SortTestSet extends Testable {

    @Test
    default void sort() {
        Person model3 = new Person("one", 10);
        Person model5 = new Person("two", 20);
        Person model1 = new Person("three", 30);
        Person model4 = new Person("four", 40);
        Person model2 = new Person("five", 50);

        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);
        dao.update(model1);
        dao.update(model2);
        dao.update(model3);
        dao.update(model4);
        dao.update(model5);

        List<Person> found = dao.query(o -> o.sortBy(Person::getAge, true)).toList();
        assert found.size() == 5;
        assert found.get(0).equals(model3);
        assert found.get(1).equals(model5);
        assert found.get(2).equals(model1);
        assert found.get(3).equals(model4);
        assert found.get(4).equals(model2);

        found = dao.query(o -> o.sortBy(Person::getAge, false)).toList();
        assert found.size() == 5;
        assert found.get(0).equals(model2);
        assert found.get(1).equals(model4);
        assert found.get(2).equals(model1);
        assert found.get(3).equals(model5);
        assert found.get(4).equals(model3);
    }

    @Test
    default void sortByText() {
        Person model1 = new Person("one", 10);
        Person model2 = new Person("two", 20);
        Person model3 = new Person("three", 30);
        Person model4 = new Person("four", 40);
        Person model5 = new Person("five", 50);

        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);
        dao.update(model1);
        dao.update(model2);
        dao.update(model3);
        dao.update(model4);
        dao.update(model5);

        List<Person> found = dao.query(o -> o.sortBy(Person::getName, true)).toList();
        assert found.size() == 5;
        assert found.get(0).equals(model5);
        assert found.get(1).equals(model4);
        assert found.get(2).equals(model1);
        assert found.get(3).equals(model3);
        assert found.get(4).equals(model2);

        found = dao.query(o -> o.sortBy(Person::getName, false)).toList();
        assert found.size() == 5;
        assert found.get(0).equals(model2);
        assert found.get(1).equals(model3);
        assert found.get(2).equals(model1);
        assert found.get(3).equals(model4);
        assert found.get(4).equals(model5);
    }

    @Test
    default void sortByDate() {
        Event model1 = new Event("one", 2022, 1, 18);
        Event model2 = new Event("two", 2022, 2, 1);
        Event model3 = new Event("three", 2022, 2, 21);
        Event model4 = new Event("four", 2023, 4, 9);
        Event model5 = new Event("five", 2023, 5, 3);

        QueryExecutor<Event, Signal<Event>, ?, ?> dao = createEmptyDB(Event.class);
        dao.update(model3);
        dao.update(model5);
        dao.update(model1);
        dao.update(model4);
        dao.update(model2);

        List<Event> found = dao.query(o -> o.sortBy(Event::getDate, true)).toList();
        assert found.size() == 5;
        assert found.get(0).equals(model1);
        assert found.get(1).equals(model2);
        assert found.get(2).equals(model3);
        assert found.get(3).equals(model4);
        assert found.get(4).equals(model5);

        found = dao.query(o -> o.sortBy(Event::getDate, false)).toList();
        assert found.size() == 5;
        assert found.get(0).equals(model5);
        assert found.get(1).equals(model4);
        assert found.get(2).equals(model3);
        assert found.get(3).equals(model2);
        assert found.get(4).equals(model1);
    }

    @Test
    default void sortByLocalDate() {
        Event model1 = new Event("one", 2022, 1, 18);
        Event model2 = new Event("two", 2022, 2, 1);
        Event model3 = new Event("three", 2022, 2, 21);
        Event model4 = new Event("four", 2023, 4, 9);
        Event model5 = new Event("five", 2023, 5, 3);

        QueryExecutor<Event, Signal<Event>, ?, ?> dao = createEmptyDB(Event.class);
        dao.update(model3);
        dao.update(model5);
        dao.update(model1);
        dao.update(model4);
        dao.update(model2);

        List<Event> found = dao.query(o -> o.sortBy(Event::getLocalDate, true)).toList();
        assert found.size() == 5;
        assert found.get(0).equals(model1);
        assert found.get(1).equals(model2);
        assert found.get(2).equals(model3);
        assert found.get(3).equals(model4);
        assert found.get(4).equals(model5);

        found = dao.query(o -> o.sortBy(Event::getLocalDate, false)).toList();
        assert found.size() == 5;
        assert found.get(0).equals(model5);
        assert found.get(1).equals(model4);
        assert found.get(2).equals(model3);
        assert found.get(3).equals(model2);
        assert found.get(4).equals(model1);
    }

    @Test
    default void sortByLocalDateTime() {
        Event model1 = new Event("one", 2022, 1, 18);
        Event model2 = new Event("two", 2022, 2, 1);
        Event model3 = new Event("three", 2022, 2, 21);
        Event model4 = new Event("four", 2023, 4, 9);
        Event model5 = new Event("five", 2023, 5, 3);

        QueryExecutor<Event, Signal<Event>, ?, ?> dao = createEmptyDB(Event.class);
        dao.update(model3);
        dao.update(model5);
        dao.update(model1);
        dao.update(model4);
        dao.update(model2);

        List<Event> found = dao.query(o -> o.sortBy(Event::getLocalDateTime, true)).toList();
        assert found.size() == 5;
        assert found.get(0).equals(model1);
        assert found.get(1).equals(model2);
        assert found.get(2).equals(model3);
        assert found.get(3).equals(model4);
        assert found.get(4).equals(model5);

        found = dao.query(o -> o.sortBy(Event::getLocalDateTime, false)).toList();
        assert found.size() == 5;
        assert found.get(0).equals(model5);
        assert found.get(1).equals(model4);
        assert found.get(2).equals(model3);
        assert found.get(3).equals(model2);
        assert found.get(4).equals(model1);
    }

    @Test
    default void sortByOffsetDateTime() {
        Event model1 = new Event("one", 2022, 1, 18);
        Event model2 = new Event("two", 2022, 2, 1);
        Event model3 = new Event("three", 2022, 2, 21);
        Event model4 = new Event("four", 2023, 4, 9);
        Event model5 = new Event("five", 2023, 5, 3);

        QueryExecutor<Event, Signal<Event>, ?, ?> dao = createEmptyDB(Event.class);
        dao.update(model3);
        dao.update(model5);
        dao.update(model1);
        dao.update(model4);
        dao.update(model2);

        List<Event> found = dao.query(o -> o.sortBy(Event::getOffset, true)).toList();
        assert found.size() == 5;
        assert found.get(0).equals(model1);
        assert found.get(1).equals(model2);
        assert found.get(2).equals(model3);
        assert found.get(3).equals(model4);
        assert found.get(4).equals(model5);

        found = dao.query(o -> o.sortBy(Event::getOffset, false)).toList();
        assert found.size() == 5;
        assert found.get(0).equals(model5);
        assert found.get(1).equals(model4);
        assert found.get(2).equals(model3);
        assert found.get(3).equals(model2);
        assert found.get(4).equals(model1);
    }

    @Test
    default void sortByMultiTypes() {
        Person model1 = new Person("A", 3);
        Person model2 = new Person("B", 1);
        Person model3 = new Person("B", 3);
        Person model4 = new Person("C", 2);
        Person model5 = new Person("C", 1);

        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);
        dao.update(model3);
        dao.update(model2);
        dao.update(model5);
        dao.update(model4);
        dao.update(model1);

        List<Person> found = dao.query(o -> o.sortBy(Person::getName, true).sortBy(Person::getAge, true)).toList();
        assert found.size() == 5;
        assert found.get(0).equals(model1);
        assert found.get(1).equals(model2);
        assert found.get(2).equals(model3);
        assert found.get(3).equals(model5);
        assert found.get(4).equals(model4);

        found = dao.query(o -> o.sortBy(Person::getAge, false).sortBy(Person::getName, true)).toList();
        assert found.size() == 5;
        assert found.get(0).equals(model1);
        assert found.get(1).equals(model3);
        assert found.get(2).equals(model4);
        assert found.get(3).equals(model2);
        assert found.get(4).equals(model5);
    }

    @Test
    default void withFilter() {
        Person model3 = new Person("one", 10);
        Person model5 = new Person("two", 20);
        Person model1 = new Person("three", 30);
        Person model4 = new Person("four", 40);
        Person model2 = new Person("five", 50);

        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);
        dao.update(model1);
        dao.update(model2);
        dao.update(model3);
        dao.update(model4);
        dao.update(model5);

        List<Person> found = dao.query(o -> o.findBy(p -> p.getName().contains("o")).sortBy(Person::getAge, true)).toList();
        assert found.size() == 3;
        assert found.get(0).equals(model3);
        assert found.get(1).equals(model5);
        assert found.get(2).equals(model4);

        found = dao.query(o -> o.findBy(p -> p.getName().contains("o")).sortBy(Person::getAge, false)).toList();
        assert found.size() == 3;
        assert found.get(0).equals(model4);
        assert found.get(1).equals(model5);
        assert found.get(2).equals(model3);
    }

    /**
     * 
     */
    class Person extends DerivableModel {

        private String name;

        private int age;

        private boolean marked;

        /**
         * Create empty model.
         */
        private Person() {
        }

        /**
         * @param name
         * @param age
         */
        private Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        /**
         * Get the name property of this {@link QueryExecutorTestSet.Person}.
         * 
         * @return The name property.
         */
        public final String getName() {
            return name;
        }

        /**
         * Set the name property of this {@link QueryExecutorTestSet.Person}.
         * 
         * @param name The name value to set.
         */
        public final void setName(String name) {
            this.name = name;
        }

        /**
         * Get the age property of this {@link QueryExecutorTestSet.Person}.
         * 
         * @return The age property.
         */
        public final int getAge() {
            return age;
        }

        /**
         * Set the age property of this {@link QueryExecutorTestSet.Person}.
         * 
         * @param age The age value to set.
         */
        public final void setAge(int age) {
            this.age = age;
        }

        /**
         * Get the marked property of this {@link QueryExecutorTestSet.Person}.
         * 
         * @return The marked property.
         */
        public final boolean isMarked() {
            return marked;
        }

        /**
         * Set the marked property of this {@link QueryExecutorTestSet.Person}.
         * 
         * @param marked The marked value to set.
         */
        public final void setMarked(boolean marked) {
            this.marked = marked;
        }
    }

    /**
     * 
     */
    class Event extends DerivableModel {
        private String name;

        private Date date;

        private LocalDate localDate;

        private LocalDateTime localDateTime;

        private OffsetDateTime offset;

        /**
         * Create empty model.
         */
        private Event() {
        }

        /**
         * @param name
         * @param age
         */
        private Event(String name, int year, int month, int day) {
            this.name = name;
            this.localDate = LocalDate.of(year, month, day);
            this.localDateTime = localDate.atStartOfDay();
            this.date = Date.from(localDate.atStartOfDay().toInstant(ZoneOffset.UTC));
            this.offset = localDate.atTime(OffsetTime.MIN);
        }

        /**
         * Get the name property of this {@link SortTestSet.Event}.
         * 
         * @return The name property.
         */
        public String getName() {
            return name;
        }

        /**
         * Set the name property of this {@link SortTestSet.Event}.
         * 
         * @param name The name value to set.
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * Get the date property of this {@link SortTestSet.Event}.
         * 
         * @return The date property.
         */
        public Date getDate() {
            return date;
        }

        /**
         * Set the date property of this {@link SortTestSet.Event}.
         * 
         * @param date The date value to set.
         */
        public void setDate(Date date) {
            this.date = date;
        }

        /**
         * Get the localDate property of this {@link SortTestSet.Event}.
         * 
         * @return The localDate property.
         */
        public LocalDate getLocalDate() {
            return localDate;
        }

        /**
         * Set the localDate property of this {@link SortTestSet.Event}.
         * 
         * @param localDate The localDate value to set.
         */
        public void setLocalDate(LocalDate localDate) {
            this.localDate = localDate;
        }

        /**
         * Get the localDateTime property of this {@link SortTestSet.Event}.
         * 
         * @return The localDateTime property.
         */
        public final LocalDateTime getLocalDateTime() {
            return localDateTime;
        }

        /**
         * Set the localDateTime property of this {@link SortTestSet.Event}.
         * 
         * @param localDateTime The localDateTime value to set.
         */
        public final void setLocalDateTime(LocalDateTime localDateTime) {
            this.localDateTime = localDateTime;
        }

        /**
         * Get the offset property of this {@link SortTestSet.Event}.
         * 
         * @return The offset property.
         */
        public final OffsetDateTime getOffset() {
            return offset;
        }

        /**
         * Set the offset property of this {@link SortTestSet.Event}.
         * 
         * @param offset The offset value to set.
         */
        public final void setOffset(OffsetDateTime offset) {
            this.offset = offset;
        }
    }
}