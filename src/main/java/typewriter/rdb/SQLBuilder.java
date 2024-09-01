/*
 * Copyright (C) 2024 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package typewriter.rdb;

import java.util.ArrayList;
import java.util.List;

import kiss.Variable;
import kiss.Ⅱ;
import reincarnation.coder.Join;
import typewriter.api.QueryDSL.OrderType;

class SQLBuilder {

    /** SELECT */
    private final List<String> selects = new ArrayList();

    /** FROM */
    private final Variable<RDB> from = Variable.empty();

    /** WHERE */
    private final Variable<String> where = Variable.empty();

    /** ORDER BY */
    private final List<Ⅱ<String, OrderType>> orders = new ArrayList();

    /**
     * Register SELECT item.
     * 
     * @param name
     */
    void select(String name) {
        if (name != null) {
            selects.add(name);
        }
    }

    /**
     * Register FROM table.
     * 
     * @param table
     */
    void from(RDB table) {
        if (table != null) {
            from.set(table);
        }
    }

    /**
     * Register WHERE condition.
     * 
     * @param condition
     */
    void where(String condition) {
        if (condition != null) {
            where.set(condition);
        }
    }

    String build() {
        StringBuilder builder = new StringBuilder();
        builder.append(Join.of(selects).separator(", ").prefix("SELECT ").ignoreEmpty());
        builder.append(Join.of(from).prefix("FROM ").ignoreEmpty());

        return builder.toString();
    }
}
