/*
 * Copyright (C) 2022 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package typewriter.api;

import static org.junit.jupiter.api.Assertions.*;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import kiss.Signal;

public interface LocalDateTimeConstraintTestSet extends Testable {

    @Test
    default void is() {
        Person model1 = new Person("Ema", 2011, 6, 23);
        Person model2 = new Person("Diana", 2004, 11, 25);
        Person model3 = new Person("Sera", 1995, 2, 1);

        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);
        dao.update(model1);
        dao.update(model2);
        dao.update(model3);

        List<Person> founds = dao.findBy(Person::getBirthday, day -> day.is(2004, 11, 25)).toList();
        assert founds.size() == 1;
        assert founds.get(0).equals(model2);
    }

    @Test
    default void isInvalidInput() {
        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);

        assertThrows(DateTimeException.class, () -> dao.findBy(Person::getBirthday, day -> day.is(-10, 33, 1098)).toList());
    }

    @Test
    default void isNot() {
        Person model1 = new Person("Ema", 2011, 6, 23);
        Person model2 = new Person("Diana", 2004, 11, 25);
        Person model3 = new Person("Sera", 1995, 2, 1);

        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);
        dao.update(model1);
        dao.update(model2);
        dao.update(model3);

        List<Person> founds = dao.findBy(Person::getBirthday, day -> day.isNot(2004, 11, 25)).toList();
        assert founds.size() == 2;
        assert founds.get(0).equals(model1);
        assert founds.get(1).equals(model3);
    }

    @Test
    default void isNotInvalidInput() {
        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);

        assertThrows(DateTimeException.class, () -> dao.findBy(Person::getBirthday, day -> day.isNot(-10, 33, 1098)).toList());
    }

    @Test
    default void isDate() {
        Person model1 = new Person("Ema", 2011, 6, 23);
        Person model2 = new Person("Diana", 2004, 11, 25);
        Person model3 = new Person("Sera", 1995, 2, 1);

        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);
        dao.update(model1);
        dao.update(model2);
        dao.update(model3);

        List<Person> founds = dao.findBy(Person::getBirthday, day -> day.is(date(2004, 11, 25))).toList();
        assert founds.size() == 1;
        assert founds.get(0).equals(model2);
    }

    @Test
    default void isNullDate() {
        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);

        assertThrows(NullPointerException.class, () -> dao.findBy(Person::getBirthday, day -> day.is(null)).toList());
    }

    @Test
    default void isNotDate() {
        Person model1 = new Person("Ema", 2011, 6, 23);
        Person model2 = new Person("Diana", 2004, 11, 25);
        Person model3 = new Person("Sera", 1995, 2, 1);

        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);
        dao.update(model1);
        dao.update(model2);
        dao.update(model3);

        List<Person> founds = dao.findBy(Person::getBirthday, day -> day.isNot(date(2004, 11, 25))).toList();
        assert founds.size() == 2;
        assert founds.get(0).equals(model1);
        assert founds.get(1).equals(model3);
    }

    @Test
    default void isNotNullDate() {
        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);

        assertThrows(NullPointerException.class, () -> dao.findBy(Person::getBirthday, day -> day.isNot(null)).toList());
    }

    @Test
    default void isBefore() {
        Person model1 = new Person("Ema", 2011, 6, 23);
        Person model2 = new Person("Diana", 2004, 11, 25);
        Person model3 = new Person("Sera", 1995, 2, 1);

        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);
        dao.update(model1);
        dao.update(model2);
        dao.update(model3);

        List<Person> founds = dao.findBy(Person::getBirthday, day -> day.isBefore(2004, 11, 25)).toList();
        assert founds.size() == 1;
        assert founds.get(0).equals(model3);
    }

    @Test
    default void isBeforeInvalidInput() {
        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);

        assertThrows(DateTimeException.class, () -> dao.findBy(Person::getBirthday, day -> day.isBefore(-10, 33, 1098)).toList());
    }

    @Test
    default void isBeforeDate() {
        Person model1 = new Person("Ema", 2011, 6, 23);
        Person model2 = new Person("Diana", 2004, 11, 25);
        Person model3 = new Person("Sera", 1995, 2, 1);

        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);
        dao.update(model1);
        dao.update(model2);
        dao.update(model3);

        List<Person> founds = dao.findBy(Person::getBirthday, day -> day.isBefore(date(2004, 11, 25))).toList();
        assert founds.size() == 1;
        assert founds.get(0).equals(model3);
    }

    @Test
    default void isBeforeNullDate() {
        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);

        assertThrows(NullPointerException.class, () -> dao.findBy(Person::getBirthday, day -> day.isBefore(null)).toList());
    }

    @Test
    default void isBeforeOrSame() {
        Person model1 = new Person("Ema", 2011, 6, 23);
        Person model2 = new Person("Diana", 2004, 11, 25);
        Person model3 = new Person("Sera", 1995, 2, 1);

        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);
        dao.update(model1);
        dao.update(model2);
        dao.update(model3);

        List<Person> founds = dao.findBy(Person::getBirthday, day -> day.isBeforeOrSame(2004, 11, 25)).toList();
        assert founds.size() == 2;
        assert founds.get(0).equals(model2);
        assert founds.get(1).equals(model3);
    }

    @Test
    default void isBeforeOrSameInvalidInput() {
        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);

        assertThrows(DateTimeException.class, () -> dao.findBy(Person::getBirthday, day -> day.isBeforeOrSame(-10, 33, 1098)).toList());
    }

    @Test
    default void isBeforeOrSameDate() {
        Person model1 = new Person("Ema", 2011, 6, 23);
        Person model2 = new Person("Diana", 2004, 11, 25);
        Person model3 = new Person("Sera", 1995, 2, 1);

        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);
        dao.update(model1);
        dao.update(model2);
        dao.update(model3);

        List<Person> founds = dao.findBy(Person::getBirthday, day -> day.isBeforeOrSame(date(2004, 11, 25))).toList();
        assert founds.size() == 2;
        assert founds.get(0).equals(model2);
        assert founds.get(1).equals(model3);
    }

    @Test
    default void isBeforeOrSameNullDate() {
        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);

        assertThrows(NullPointerException.class, () -> dao.findBy(Person::getBirthday, day -> day.isBeforeOrSame(null)).toList());
    }

    @Test
    default void isAfter() {
        Person model1 = new Person("Ema", 2011, 6, 23);
        Person model2 = new Person("Diana", 2004, 11, 25);
        Person model3 = new Person("Sera", 1995, 2, 1);

        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);
        dao.update(model1);
        dao.update(model2);
        dao.update(model3);

        List<Person> founds = dao.findBy(Person::getBirthday, day -> day.isAfter(2004, 11, 25)).toList();
        assert founds.size() == 1;
        assert founds.get(0).equals(model1);
    }

    @Test
    default void isAfterInvalidInput() {
        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);

        assertThrows(DateTimeException.class, () -> dao.findBy(Person::getBirthday, day -> day.isAfter(-10, 33, 1098)).toList());
    }

    @Test
    default void isAfterDate() {
        Person model1 = new Person("Ema", 2011, 6, 23);
        Person model2 = new Person("Diana", 2004, 11, 25);
        Person model3 = new Person("Sera", 1995, 2, 1);

        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);
        dao.update(model1);
        dao.update(model2);
        dao.update(model3);

        List<Person> founds = dao.findBy(Person::getBirthday, day -> day.isAfter(date(2004, 11, 25))).toList();
        assert founds.size() == 1;
        assert founds.get(0).equals(model1);
    }

    @Test
    default void isAfterNullDate() {
        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);

        assertThrows(NullPointerException.class, () -> dao.findBy(Person::getBirthday, day -> day.isAfter(null)).toList());
    }

    @Test
    default void isAfterOrSame() {
        Person model1 = new Person("Ema", 2011, 6, 23);
        Person model2 = new Person("Diana", 2004, 11, 25);
        Person model3 = new Person("Sera", 1995, 2, 1);

        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);
        dao.update(model1);
        dao.update(model2);
        dao.update(model3);

        List<Person> founds = dao.findBy(Person::getBirthday, day -> day.isAfterOrSame(2004, 11, 25)).toList();
        assert founds.size() == 2;
        assert founds.get(0).equals(model1);
        assert founds.get(1).equals(model2);
    }

    @Test
    default void isAfterOrSameInvalidInput() {
        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);

        assertThrows(DateTimeException.class, () -> dao.findBy(Person::getBirthday, day -> day.isAfterOrSame(-10, 33, 1098)).toList());
    }

    @Test
    default void isAfterOrSameDate() {
        Person model1 = new Person("Ema", 2011, 6, 23);
        Person model2 = new Person("Diana", 2004, 11, 25);
        Person model3 = new Person("Sera", 1995, 2, 1);

        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);
        dao.update(model1);
        dao.update(model2);
        dao.update(model3);

        List<Person> founds = dao.findBy(Person::getBirthday, day -> day.isAfterOrSame(date(2004, 11, 25))).toList();
        assert founds.size() == 2;
        assert founds.get(0).equals(model1);
        assert founds.get(1).equals(model2);
    }

    @Test
    default void isAfterOrSameNullDate() {
        QueryExecutor<Person, Signal<Person>, ?, ?> dao = createEmptyDB(Person.class);

        assertThrows(NullPointerException.class, () -> dao.findBy(Person::getBirthday, day -> day.isAfterOrSame(null)).toList());
    }

    /**
     * Date converter.
     * 
     * @param year
     * @param month
     * @param day
     * @return
     */
    private LocalDateTime date(int year, int month, int day) {
        return LocalDateTime.of(year, month, day, 0, 0, 0, 0);
    }

    /**
     * 
     */
    class Person extends DerivableModel {

        public String name;

        public LocalDateTime birthday;

        /**
         * Create empty model.
         */
        private Person() {
        }

        /**
         */
        private Person(String name, int year, int month, int day) {
            this.name = name;
            this.birthday = LocalDateTime.of(year, month, day, 0, 0, 0, 0);
        }

        /**
         * Get the name property of this {@link LocalDateTimeConstraintTestSet.Person}.
         * 
         * @return The name property.
         */
        public String getName() {
            return name;
        }

        /**
         * Get the birthday property of this {@link LocalDateTimeConstraintTestSet.Person}.
         * 
         * @return The birthday property.
         */
        public LocalDateTime getBirthday() {
            return birthday;
        }
    }
}
