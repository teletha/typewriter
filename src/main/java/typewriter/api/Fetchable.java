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

import java.util.List;

import kiss.Signal;
import kiss.Variable;

/**
 * RDB specific API.
 */
public interface Fetchable<M extends Identifiable> {

    /**
     * Fetch single data by your query.
     * 
     * @param query
     * @return
     */
    default Variable<M> fetchOne(QueryDSL<M> query) {
        return fetch(query).first().to();
    }

    /**
     * Fetch data as {@link List} by your query.
     * 
     * @param query
     * @return
     */
    default List<M> fetchList(QueryDSL<M> query) {
        return fetch(query).toList();
    }

    /**
     * Fetch data by your query.
     * 
     * @param query
     * @return
     */
    Signal<M> fetch(QueryDSL<M> query);
}
