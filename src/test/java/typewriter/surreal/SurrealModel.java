/*
 * Copyright (C) 2025 The TYPEWRITER Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package typewriter.surreal;

import typewriter.api.model.BackendedModel;

public abstract class SurrealModel<Self extends SurrealModel<Self>> extends BackendedModel<Self, Surreal<Self>> {

    /**
     * {@inheritDoc}
     */
    @Override
    protected Surreal backend() {
        return Surreal.of(getClass());
    }
}