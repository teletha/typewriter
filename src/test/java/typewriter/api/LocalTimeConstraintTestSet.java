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
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import kiss.Signal;
import typewriter.api.model.DerivableModel;

public interface LocalTimeConstraintTestSet extends Testable {

    @Test
    default void is() {
        Event model1 = new Event("First", 11, 0);
        Event model2 = new Event("Second", 13, 30);
        Event model3 = new Event("Third", 15, 45);

        QueryExecutor<Event, Signal<Event>, ?, ?> dao = createEmptyDB(Event.class);
        dao.update(model1);
        dao.update(model2);
        dao.update(model3);

        List<Event> founds = dao.findBy(Event::getStart, day -> day.is(11, 0, 0)).toList();
        assert founds.size() == 1;
        assert founds.get(0).equals(model1);
    }

    @Test
    default void isInvalidInput() {
        QueryExecutor<Event, Signal<Event>, ?, ?> dao = createEmptyDB(Event.class);

        assertThrows(DateTimeException.class, () -> dao.findBy(Event::getStart, day -> day.is(-10, 33, 1098)).toList());
    }

    @Test
    default void isNot() {
        Event model1 = new Event("First", 11, 0);
        Event model2 = new Event("Second", 13, 30);
        Event model3 = new Event("Third", 15, 45);

        QueryExecutor<Event, Signal<Event>, ?, ?> dao = createEmptyDB(Event.class);
        dao.update(model1);
        dao.update(model2);
        dao.update(model3);

        List<Event> founds = dao.findBy(Event::getStart, day -> day.isNot(11, 0, 0)).toList();
        assert founds.size() == 2;
        assert founds.get(0).equals(model2);
        assert founds.get(1).equals(model3);
    }

    @Test
    default void isNotInvalidInput() {
        QueryExecutor<Event, Signal<Event>, ?, ?> dao = createEmptyDB(Event.class);

        assertThrows(DateTimeException.class, () -> dao.findBy(Event::getStart, day -> day.isNot(-10, 33, 1098)).toList());
    }

    @Test
    default void isDate() {
        Event model1 = new Event("First", 11, 0);
        Event model2 = new Event("Second", 13, 30);
        Event model3 = new Event("Third", 15, 45);

        QueryExecutor<Event, Signal<Event>, ?, ?> dao = createEmptyDB(Event.class);
        dao.update(model1);
        dao.update(model2);
        dao.update(model3);

        List<Event> founds = dao.findBy(Event::getStart, day -> day.is(time(13, 30))).toList();
        assert founds.size() == 1;
        assert founds.get(0).equals(model2);
    }

    @Test
    default void isNullDate() {
        QueryExecutor<Event, Signal<Event>, ?, ?> dao = createEmptyDB(Event.class);

        assertThrows(NullPointerException.class, () -> dao.findBy(Event::getStart, day -> day.is(null)).toList());
    }

    @Test
    default void isNotDate() {
        Event model1 = new Event("First", 11, 0);
        Event model2 = new Event("Second", 13, 30);
        Event model3 = new Event("Third", 15, 45);

        QueryExecutor<Event, Signal<Event>, ?, ?> dao = createEmptyDB(Event.class);
        dao.update(model1);
        dao.update(model2);
        dao.update(model3);

        List<Event> founds = dao.findBy(Event::getStart, day -> day.isNot(time(13, 30))).toList();
        assert founds.size() == 2;
        assert founds.get(0).equals(model1);
        assert founds.get(1).equals(model3);
    }

    @Test
    default void isNotNullDate() {
        QueryExecutor<Event, Signal<Event>, ?, ?> dao = createEmptyDB(Event.class);

        assertThrows(NullPointerException.class, () -> dao.findBy(Event::getStart, day -> day.isNot(null)).toList());
    }

    @Test
    default void isBefore() {
        Event model1 = new Event("First", 11, 0);
        Event model2 = new Event("Second", 13, 30);
        Event model3 = new Event("Third", 15, 45);

        QueryExecutor<Event, Signal<Event>, ?, ?> dao = createEmptyDB(Event.class);
        dao.update(model1);
        dao.update(model2);
        dao.update(model3);

        List<Event> founds = dao.findBy(Event::getStart, day -> day.isBefore(13, 30, 0)).toList();
        assert founds.size() == 1;
        assert founds.get(0).equals(model1);
    }

    @Test
    default void isBeforeInvalidInput() {
        QueryExecutor<Event, Signal<Event>, ?, ?> dao = createEmptyDB(Event.class);

        assertThrows(DateTimeException.class, () -> dao.findBy(Event::getStart, day -> day.isBefore(-10, 33, 1098)).toList());
    }

    @Test
    default void isBeforeDate() {
        Event model1 = new Event("First", 11, 0);
        Event model2 = new Event("Second", 13, 30);
        Event model3 = new Event("Third", 15, 45);

        QueryExecutor<Event, Signal<Event>, ?, ?> dao = createEmptyDB(Event.class);
        dao.update(model1);
        dao.update(model2);
        dao.update(model3);

        List<Event> founds = dao.findBy(Event::getStart, day -> day.isBefore(13, 30, 0)).toList();
        assert founds.size() == 1;
        assert founds.get(0).equals(model1);
    }

    @Test
    default void isBeforeNullDate() {
        QueryExecutor<Event, Signal<Event>, ?, ?> dao = createEmptyDB(Event.class);

        assertThrows(NullPointerException.class, () -> dao.findBy(Event::getStart, day -> day.isBefore(null)).toList());
    }

    @Test
    default void isBeforeOrSame() {
        Event model1 = new Event("First", 11, 0);
        Event model2 = new Event("Second", 13, 30);
        Event model3 = new Event("Third", 15, 45);

        QueryExecutor<Event, Signal<Event>, ?, ?> dao = createEmptyDB(Event.class);
        dao.update(model1);
        dao.update(model2);
        dao.update(model3);

        List<Event> founds = dao.findBy(Event::getStart, day -> day.isBeforeOrSame(13, 30, 0)).toList();
        assert founds.size() == 2;
        assert founds.get(0).equals(model1);
        assert founds.get(1).equals(model2);
    }

    @Test
    default void isBeforeOrSameInvalidInput() {
        QueryExecutor<Event, Signal<Event>, ?, ?> dao = createEmptyDB(Event.class);

        assertThrows(DateTimeException.class, () -> dao.findBy(Event::getStart, day -> day.isBeforeOrSame(-10, 33, 1098)).toList());
    }

    @Test
    default void isBeforeOrSameDate() {
        Event model1 = new Event("First", 11, 0);
        Event model2 = new Event("Second", 13, 30);
        Event model3 = new Event("Third", 15, 45);

        QueryExecutor<Event, Signal<Event>, ?, ?> dao = createEmptyDB(Event.class);
        dao.update(model1);
        dao.update(model2);
        dao.update(model3);

        List<Event> founds = dao.findBy(Event::getStart, day -> day.isBeforeOrSame(time(13, 30))).toList();
        assert founds.size() == 2;
        assert founds.get(0).equals(model1);
        assert founds.get(1).equals(model2);
    }

    @Test
    default void isBeforeOrSameNullDate() {
        QueryExecutor<Event, Signal<Event>, ?, ?> dao = createEmptyDB(Event.class);

        assertThrows(NullPointerException.class, () -> dao.findBy(Event::getStart, day -> day.isBeforeOrSame(null)).toList());
    }

    @Test
    default void isAfter() {
        Event model1 = new Event("First", 11, 0);
        Event model2 = new Event("Second", 13, 30);
        Event model3 = new Event("Third", 15, 45);

        QueryExecutor<Event, Signal<Event>, ?, ?> dao = createEmptyDB(Event.class);
        dao.update(model1);
        dao.update(model2);
        dao.update(model3);

        List<Event> founds = dao.findBy(Event::getStart, day -> day.isAfter(13, 30, 0)).toList();
        assert founds.size() == 1;
        assert founds.get(0).equals(model3);
    }

    @Test
    default void isAfterInvalidInput() {
        QueryExecutor<Event, Signal<Event>, ?, ?> dao = createEmptyDB(Event.class);

        assertThrows(DateTimeException.class, () -> dao.findBy(Event::getStart, day -> day.isAfter(-10, 33, 1098)).toList());
    }

    @Test
    default void isAfterDate() {
        Event model1 = new Event("First", 11, 0);
        Event model2 = new Event("Second", 13, 30);
        Event model3 = new Event("Third", 15, 45);

        QueryExecutor<Event, Signal<Event>, ?, ?> dao = createEmptyDB(Event.class);
        dao.update(model1);
        dao.update(model2);
        dao.update(model3);

        List<Event> founds = dao.findBy(Event::getStart, day -> day.isAfter(time(13, 30))).toList();
        assert founds.size() == 1;
        assert founds.get(0).equals(model3);
    }

    @Test
    default void isAfterNullDate() {
        QueryExecutor<Event, Signal<Event>, ?, ?> dao = createEmptyDB(Event.class);

        assertThrows(NullPointerException.class, () -> dao.findBy(Event::getStart, day -> day.isAfter(null)).toList());
    }

    @Test
    default void isAfterOrSame() {
        Event model1 = new Event("First", 11, 0);
        Event model2 = new Event("Second", 13, 30);
        Event model3 = new Event("Third", 15, 45);

        QueryExecutor<Event, Signal<Event>, ?, ?> dao = createEmptyDB(Event.class);
        dao.update(model1);
        dao.update(model2);
        dao.update(model3);

        List<Event> founds = dao.findBy(Event::getStart, day -> day.isAfterOrSame(13, 30, 0)).toList();
        assert founds.size() == 2;
        assert founds.get(0).equals(model2);
        assert founds.get(1).equals(model3);
    }

    @Test
    default void isAfterOrSameInvalidInput() {
        QueryExecutor<Event, Signal<Event>, ?, ?> dao = createEmptyDB(Event.class);

        assertThrows(DateTimeException.class, () -> dao.findBy(Event::getStart, day -> day.isAfterOrSame(-10, 33, 1098)).toList());
    }

    @Test
    default void isAfterOrSameDate() {
        Event model1 = new Event("First", 11, 0);
        Event model2 = new Event("Second", 13, 30);
        Event model3 = new Event("Third", 15, 45);

        QueryExecutor<Event, Signal<Event>, ?, ?> dao = createEmptyDB(Event.class);
        dao.update(model1);
        dao.update(model2);
        dao.update(model3);

        List<Event> founds = dao.findBy(Event::getStart, day -> day.isAfterOrSame(time(13, 30))).toList();
        assert founds.size() == 2;
        assert founds.get(0).equals(model2);
        assert founds.get(1).equals(model3);
    }

    @Test
    default void isAfterOrSameNullDate() {
        QueryExecutor<Event, Signal<Event>, ?, ?> dao = createEmptyDB(Event.class);

        assertThrows(NullPointerException.class, () -> dao.findBy(Event::getStart, day -> day.isAfterOrSame(null)).toList());
    }

    /**
     * Time converter.
     * 
     * @param hour
     * @param minute
     * @return
     */
    private LocalTime time(int hour, int minute) {
        return LocalTime.of(hour, minute, 0);
    }

    /**
     * 
     */
    class Event extends DerivableModel {

        public String name;

        public LocalTime start;

        /**
         * Create empty model.
         */
        private Event() {
        }

        /**
         */
        private Event(String name, int hour, int minute) {
            this.name = name;
            this.start = LocalTime.of(hour, minute, 0);
        }

        /**
         * Get the name property of this {@link LocalTimeConstraintTestSet.Event}.
         * 
         * @return The name property.
         */
        public String getName() {
            return name;
        }

        /**
         * Get the start property of this {@link LocalTimeConstraintTestSet.Event}.
         * 
         * @return The start property.
         */
        public LocalTime getStart() {
            return start;
        }
    }
}
