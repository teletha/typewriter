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

import java.util.function.UnaryOperator;

import typewriter.api.Specifier;
import typewriter.rdb.Dialect;

public class AVGOption<M> {

    public boolean distinct;

    public int from;

    public int to;

    public String orderBy;

    private final Dialect dialect;

    /**
     * @param option
     */
    public AVGOption(UnaryOperator<AVGOption> option, Dialect dialect) {
        if (option != null) option.apply(this);
        this.dialect = dialect;
    }

    /**
     * Configure DISTINCT option.
     * 
     * @return
     */
    public AVGOption distinct() {
        distinct = true;
        return this;
    }

    /**
     * Configure window frame.
     * 
     * @return
     */
    public AVGOption frame(int from, int to) {
        this.from = from;
        this.to = to;
        return this;
    }

    /**
     * Configure ORDER BY option.
     * 
     * @param specifier
     * @return
     */
    public AVGOption orderBy(Specifier<M, ?> specifier) {
        this.orderBy = specifier.propertyName(dialect);
        return this;
    }
}
