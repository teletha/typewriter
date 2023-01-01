/*
 * Copyright (C) 2023 The TYPEWRITER Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package typewriter.api.model;

import kiss.I;
import kiss.Signal;
import typewriter.api.Deletable;
import typewriter.api.Restorable;
import typewriter.api.Updatable;

public abstract class BackendedModel<M extends BackendedModel<M, DAO>, DAO extends Updatable<M> & Deletable<M> & Restorable<M>>
        extends IdentifiableModel {

    /**
     * Restore this model from the backend storage.
     * 
     * @return
     */
    public M restore() {
        restoring().to(I.NoOP);
        return (M) this;
    }

    /**
     * Restore this model from the backend storage.
     * 
     * @return
     */
    public Signal<M> restoring() {
        return backend().restore((M) this).or((M) this);
    }

    /**
     * Save this model to the backend storage.
     * 
     * @return
     */
    public M save() {
        backend().update((M) this);

        return (M) this;
    }

    /**
     * Delete this model from the backend storage.
     * 
     * @return
     */
    public M delete() {
        backend().delete((M) this);

        return (M) this;
    }

    /**
     * Initialize DAO.
     * 
     * @return
     */
    protected abstract DAO backend();
}