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

public abstract class LazyUpdatable<M extends Identifiable> implements Updatable<M> {

    private int max = 256;

    private long delay = 1000 * 60 * 3;

    private List<M> models = new ArrayList(max);

    private Disposable stop;

    /**
     * Congifure the delay time of bulk update.
     * 
     * @param mills
     */
    public void setDelay(long mills) {
        if (0 < mills) {
            delay = mills;
        }
    }

    /**
     * Congifure the maximum size of bulk update.
     * 
     * @param size
     */
    public void setMax(int size) {
        if (0 < size) {
            max = size;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void updateLazy(M model) {
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
    public void commit() {
        List<M> items = models;
        models = new ArrayList(max);
        updateAll(items);

        if (stop != null) {
            stop.dispose();
            stop = null;
        }
    }
}
