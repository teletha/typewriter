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

public class AVGOption {

    public boolean distinct;

    public int from;

    public int to;

    /**
     * @param option
     */
    public AVGOption(UnaryOperator<AVGOption> option) {
        if (option != null) option.apply(this);
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
     * Configure window range.
     * 
     * @return
     */
    public AVGOption range(int from, int to) {
        this.from = from;
        this.to = to;
        return this;
    }
}
