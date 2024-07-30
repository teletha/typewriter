/*
 * Copyright (C) 2024 The TYPEWRITER Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package typewriter.maria;

import typewriter.api.model.BackendedModel;
import typewriter.rdb.RDB;

public abstract class MariaModel<Self extends MariaModel<Self>> extends BackendedModel<Self, RDB<Self>> {

    /**
     * {@inheritDoc}
     */
    @Override
    protected RDB<Self> backend() {
        return RDB.of((Class<Self>) getClass(), o -> o.dialect(RDB.MariaDB));
    }
}