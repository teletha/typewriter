/*
 * Copyright (C) 2024 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package typewriter.query;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import typewriter.api.Identifiable;
import typewriter.api.Specifier;
import typewriter.rdb.Dialect;

public class QueryWriter<M extends Identifiable> {

    private final Dialect dialect;

    private final List<String> selects = new ArrayList();

    /**
     * @param dialect
     */
    public QueryWriter(Dialect dialect) {
        this.dialect = dialect;
    }

    /**
     * Select column by name.
     * 
     * @param column
     * @return
     */
    public QueryWriter<M> select(String column) {
        return selectAs(null, column);
    }

    /**
     * Select column by name with alias.
     * 
     * @param alias An alias of column.
     * @param column A column name.
     * @return
     */
    public QueryWriter<M> selectAs(String alias, String column) {
        if (alias == null || alias.isEmpty()) {
            selects.add(column);
        } else {
            selects.add(column + " AS " + alias);
        }
        return this;
    }

    /**
     * Select column by {@link Specifier}.
     * 
     * @param specifier A column specifier.
     * @return
     */
    public QueryWriter<M> select(Specifier<M, ?> specifier) {
        return select(specifier.propertyName(dialect));
    }

    /**
     * Select column by {@link Specifier} with alias.
     * 
     * @param alias An alias of column.
     * @param specifier A column specifier.
     * @return
     */
    public QueryWriter<M> selectAs(String alias, Specifier<M, ?> specifier) {
        return selectAs(alias, specifier.propertyName(dialect));
    }

    /**
     * @param dialect
     */
    public String build(Dialect dialect) {
        StringBuilder builder = new StringBuilder();

        builder.append(selects.stream().collect(Collectors.joining(", ", "SELECT ", "")));

        return builder.toString();
    }
}
