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

public class AVGOption {

    protected boolean distinct;

    /**
     * Enable DISTINCT option.
     * 
     * @return
     */
    public AVGOption distinct() {
        distinct = true;
        return this;
    }
}
