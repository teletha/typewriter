/*
 * Copyright (C) 2022 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package typewriter.api.model;

import typewriter.api.Deletable;
import typewriter.api.Restorable;
import typewriter.api.Updatable;

public abstract class BackendedModel<M extends BackendedModel<M, B>, B extends Updatable & Deletable & Restorable>
        extends IdentifiableModel {

    /**
     * Restore this model from the backend storage.
     * 
     * @return
     */
    public M restore() {
        backend().restore(this);

        return (M) this;
    }

    /**
     * Save this model to the backend storage.
     * 
     * @return
     */
    public M save() {
        backend().update(this);

        return (M) this;
    }

    /**
     * Delete this model from the backend storage.
     * 
     * @return
     */
    public M delete() {
        backend().delete(this);

        return (M) this;
    }

    /**
     * Initialize DAO.
     * 
     * @return
     */
    protected abstract B backend();
}
