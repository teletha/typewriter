/*
 * Copyright (C) 2024 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package typewriter.api;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import kiss.Disposable;
import kiss.I;

public class LazyBulkUpdater<M extends Identifiable> {

    private final int max;

    private final long delay;

    private final Updatable<M> updatable;

    private List<M> models;

    private Disposable stop;

    public LazyBulkUpdater(int max, long delay, Updatable<M> updatable) {
        this.max = max;
        this.delay = delay;
        this.updatable = updatable;
        this.models = new ArrayList(max);
    }

    /**
     * Register updating model.
     * 
     * @param model
     */
    public synchronized void update(M model) {
        if (model != null) {
            models.add(model);

            if (max <= models.size()) {
                commit();
            } else if (stop == null) {
                stop = I.schedule(delay, TimeUnit.MILLISECONDS).to(this::commit);
            }
        }
    }

    /**
     * Force to update.
     */
    private void commit() {
        List<M> items = models;
        models = new ArrayList(max);
        updatable.updateAll(items);

        if (stop != null) {
            stop.dispose();
            stop = null;
        }
    }
}
