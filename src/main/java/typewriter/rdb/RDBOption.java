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

import java.util.function.UnaryOperator;

public class RDBOption {

    /** The target dialect. */
    Dialect dialect;

    /** The table name. */
    UnaryOperator<String> renamer = UnaryOperator.identity();

    /** The database location. */
    String location;

    /**
     * Configure the database dialect.
     * 
     * @param dialect
     * @return
     */
    public RDBOption dialect(Dialect dialect) {
        if (dialect != null) {
            this.dialect = dialect;
        }
        return this;
    }

    /**
     * Configure the table name.
     * 
     * @param name
     * @return
     */
    public RDBOption name(UnaryOperator<String> name) {
        if (name != null) {
            this.renamer = name;
        }
        return this;
    }

    /**
     * Configure the database location.
     * 
     * @param location
     * @return
     */
    public RDBOption location(String location) {
        if (location != null) {
            this.location = location;
        }
        return this;
    }
}
