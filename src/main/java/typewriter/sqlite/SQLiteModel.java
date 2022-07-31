/*
 * Copyright (C) 2022 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package typewriter.sqlite;

import typewriter.api.model.BackendedModel;
import typewriter.rdb.RDB;

public abstract class SQLiteModel<Self extends SQLiteModel<Self>> extends BackendedModel<Self, RDB<Self>> {

    /**
     * {@inheritDoc}
     */
    @Override
    protected RDB<Self> backend() {
        return RDB.of((Class<Self>) getClass());
    }
}
