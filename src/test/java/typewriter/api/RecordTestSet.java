/*
 * Copyright (C) 2023 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package typewriter.api;

import org.junit.jupiter.api.Test;

import kiss.Signal;

public interface RecordTestSet extends Testable {

    @Test
    default void writeAndRead() {
        Item item1 = new Item(1, "one");
        Item item2 = new Item(2, "two");
        Item item3 = new Item(3, "three");

        QueryExecutor<Item, Signal<Item>, ?, ?> dao = createEmptyDB(Item.class);
        dao.updateAll(item1, item2, item3);
        assert dao.count() == 3;
        assert dao.findBy(2).to().v.name.equals("two");
    }

    @Test
    default void linq() {
        Item item1 = new Item(1, "one");
        Item item2 = new Item(2, "two");
        Item item3 = new Item(3, "three");

        QueryExecutor<Item, Signal<Item>, ?, ?> dao = createEmptyDB(Item.class);
        dao.updateAll(item1, item2, item3);
        assert dao.findBy(x -> x.id > 1).toList().size() == 2;
        assert dao.findBy(x -> x.name.contains("o")).toList().size() == 2;
        assert dao.findBy(x -> x.name().contains("o")).toList().size() == 2;
    }

    record Item(long id, String name) implements Identifiable {

        @Override
        public long getId() {
            return id;
        }
    }
}
