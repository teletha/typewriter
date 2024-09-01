/*
 * Copyright (C) 2024 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package typewriter.api;

import static typewriter.api.QueryDSL.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import antibug.powerassert.PowerAssertOff;
import typewriter.api.model.DerivableModel;
import typewriter.rdb.RDB;

@PowerAssertOff
public interface FetchTestSet extends Testable {

    @Test
    default void select() {
        RDB<Item> dao = createRDB(items(1, 2, 3, 4, 5));

        assert dao.fetchList(item -> {
            Select("*");
            From(dao);
        }).size() == 5;
    }

    @Test
    default void where() {
        RDB<Item> dao = createRDB(items(1, 2, 3, 4, 5));

        assert dao.fetchList(item -> {
            Select("*");
            From(dao);
            Where(item.num <= 3);
            // OrderBy(item.num, DESC);
        }).size() == 3;
    }

    @Test
    default void orderBy() {
        RDB<Item> dao = createRDB(items(3, 1, 2));

        System.out.println(dao.findAll().toList());

        List<Item> list = dao.fetchList(item -> {
            Select("*");
            From(dao);
            OrderBy(item.num, DESC);
        });
        System.out.println(list);
        assert list.get(0).num == 1;
        assert list.get(1).num == 2;
        assert list.get(2).num == 3;
    }

    class Item extends DerivableModel {
        public int num;

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return "Item[" + num + "]";
        }
    }

    /**
     * Create items.
     * 
     * @param values
     * @return
     */
    private Item[] items(int... values) {
        Item[] items = new Item[values.length];
        for (int i = 0; i < items.length; i++) {
            Item item = new Item();
            item.num = values[i];
            items[i] = item;
        }
        return items;
    }
}
