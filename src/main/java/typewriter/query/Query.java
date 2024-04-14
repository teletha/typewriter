/*
 * Copyright (C) 2023 Nameless Production Committee
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
import java.util.stream.Stream;

import typewriter.api.Identifiable;
import typewriter.api.Specifier;

public abstract class Query<M extends Identifiable> {

    private final List<String> select = new ArrayList();

    /**
     * Declare SELECT statement.
     * 
     * @param names
     */
    protected final void SELECT(Specifier<M, ?>... names) {
        SELECT(Stream.of(names).map(Specifier::propertyName).toList());
    }

    /**
     * Declare SELECT statement.
     * 
     * @param names
     */
    protected final void SELECT(String... names) {
        SELECT(List.of(names));
    }

    /**
     * Declare SELECT statement.
     * 
     * @param names
     */
    protected final void SELECT(List<String> names) {
        select.addAll(names);
    }

    /**
     * Test query.
     * 
     * @param query
     * @return
     */
    public boolean is(String query) {
        return query.equals(toString());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        // SELECT
        builder.append("SELECT ").append(select.stream().collect(Collectors.joining(", ")));

        return builder.toString();
    }
}
