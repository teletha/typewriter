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

public interface StringStoreTestSet extends Testable {

    @Test
    default void alphabetic() {
        QueryExecutor<Description, Signal<Description>, ?, ?> dao = createEmptyDB(Description.class);
        dao.update(new Description("AaBb"));

        Description person = dao.limit(1).waitForTerminate().to().v;
        assert person.text.equals("AaBb");
    }

    @Test
    default void numeric() {
        QueryExecutor<Description, Signal<Description>, ?, ?> dao = createEmptyDB(Description.class);
        dao.update(new Description("1245"));

        Description person = dao.limit(1).waitForTerminate().to().v;
        assert person.text.equals("1245");
    }

    @Test
    default void alphanumeric() {
        QueryExecutor<Description, Signal<Description>, ?, ?> dao = createEmptyDB(Description.class);
        dao.update(new Description("abc1"));

        Description person = dao.limit(1).waitForTerminate().to().v;
        assert person.text.equals("abc1");
    }

    @Test
    default void japanese() {
        QueryExecutor<Description, Signal<Description>, ?, ?> dao = createEmptyDB(Description.class);
        dao.update(new Description("日本語テストだ！"));

        Description person = dao.limit(1).waitForTerminate().to().v;
        assert person.text.equals("日本語テストだ！");
    }

    @Test
    default void emoji() {
        QueryExecutor<Description, Signal<Description>, ?, ?> dao = createEmptyDB(Description.class);
        dao.update(new Description("😊📵🍎"));

        Description person = dao.limit(1).waitForTerminate().to().v;
        assert person.text.equals("😊📵🍎");
    }

    @Test
    default void whitespace() {
        QueryExecutor<Description, Signal<Description>, ?, ?> dao = createEmptyDB(Description.class);
        dao.update(new Description(" "));

        Description person = dao.limit(1).waitForTerminate().to().v;
        assert person.text.equals(" ");
    }

    @Test
    default void whitespaceHead() {
        QueryExecutor<Description, Signal<Description>, ?, ?> dao = createEmptyDB(Description.class);
        dao.update(new Description(" head-space"));

        Description person = dao.limit(1).waitForTerminate().to().v;
        assert person.text.equals(" head-space");
    }

    @Test
    default void whitespaceTail() {
        QueryExecutor<Description, Signal<Description>, ?, ?> dao = createEmptyDB(Description.class);
        dao.update(new Description("tail-space "));

        Description person = dao.limit(1).waitForTerminate().to().v;
        assert person.text.equals("tail-space ");
    }

    @Test
    default void lineFeed() {
        QueryExecutor<Description, Signal<Description>, ?, ?> dao = createEmptyDB(Description.class);
        dao.update(new Description("\n"));

        Description person = dao.limit(1).waitForTerminate().to().v;
        assert person.text.equals("\n");
    }

    @Test
    default void carriageReturn() {
        QueryExecutor<Description, Signal<Description>, ?, ?> dao = createEmptyDB(Description.class);
        dao.update(new Description("\r"));

        Description person = dao.limit(1).waitForTerminate().to().v;
        assert person.text.equals("\r");
    }

    @Test
    default void tab() {
        QueryExecutor<Description, Signal<Description>, ?, ?> dao = createEmptyDB(Description.class);
        dao.update(new Description("\t"));

        Description person = dao.limit(1).waitForTerminate().to().v;
        assert person.text.equals("\t");
    }

    @Test
    default void quote() {
        QueryExecutor<Description, Signal<Description>, ?, ?> dao = createEmptyDB(Description.class);
        dao.update(new Description("'"));

        Description person = dao.limit(1).waitForTerminate().to().v;
        assert person.text.equals("'");
    }

    @Test
    default void doubleQuote() {
        QueryExecutor<Description, Signal<Description>, ?, ?> dao = createEmptyDB(Description.class);
        dao.update(new Description("\""));

        Description person = dao.limit(1).waitForTerminate().to().v;
        assert person.text.equals("\"");
    }

    @Test
    default void percentage() {
        QueryExecutor<Description, Signal<Description>, ?, ?> dao = createEmptyDB(Description.class);
        dao.update(new Description("%"));

        Description person = dao.limit(1).waitForTerminate().to().v;
        assert person.text.equals("%");
    }

    @Test
    default void underscore() {
        QueryExecutor<Description, Signal<Description>, ?, ?> dao = createEmptyDB(Description.class);
        dao.update(new Description("_"));

        Description person = dao.limit(1).waitForTerminate().to().v;
        assert person.text.equals("_");
    }

    @Test
    default void escape() {
        QueryExecutor<Description, Signal<Description>, ?, ?> dao = createEmptyDB(Description.class);
        dao.update(new Description("\\"));
    
        Description person = dao.limit(1).waitForTerminate().to().v;
        assert person.text.equals("\\");
    }

    /**
     * 
     */
    class Description extends DerivableModel {

        private String text;

        /**
         * Create empty model.
         */
        private Description() {
        }

        /**
         * @param text
         */
        private Description(String text) {
            this.text = text;
        }

        /**
         * Get the text property of this {@link StringStoreTestSet.Description}.
         * 
         * @return The text property.
         */
        public final String getText() {
            return text;
        }

        /**
         * Set the text property of this {@link StringStoreTestSet.Description}.
         * 
         * @param text The text value to set.
         */
        public final void setText(String text) {
            this.text = text;
        }
    }
}