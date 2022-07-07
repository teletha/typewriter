/*
 * Copyright (C) 2022 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package typewriter;

import typewriter.mongo.Mongo;

public class QueryableModel<M extends QueryableModel<M>> extends IdentifiableModel {

    /** Cache of DAO */
    private Mongo<M> mongo;

    /**
     * Save this model.
     * 
     * @return
     */
    public M save() {
        mongo().update((M) this);

        return (M) this;
    }

    /**
     * Initialize DAO.
     * 
     * @return
     */
    private synchronized Mongo<M> mongo() {
        if (mongo == null) {
            mongo = Mongo.of((Class<M>) getClass());
        }
        return mongo;
    }
}
