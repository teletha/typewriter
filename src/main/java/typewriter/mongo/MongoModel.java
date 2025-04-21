/*
 * Copyright (C) 2025 The TYPEWRITER Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package typewriter.mongo;

import typewriter.api.model.BackendedModel;

public abstract class MongoModel<Self extends MongoModel<Self>> extends BackendedModel<Self, Mongo<Self>> {

    /**
     * {@inheritDoc}
     */
    @Override
    protected Mongo backend() {
        return Mongo.of(getClass());
    }
}