/*
 * Copyright (C) 2024 The TYPEWRITER Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package typewriter.api;

import kiss.I;
import kiss.Signal;

public interface Updatable<M extends Identifiable> {

    /**
     * Update the specified model.
     * 
     * @param model A target model.
     */
    default void update(M model) {
        update(model, (Specifier[]) null);
    }

    /**
     * Update the property of the given model.
     * 
     * @param model A target model.
     * @param specifier A property {@link Specifier}.
     */
    default void update(M model, Specifier<M, ?> specifier) {
        update(model, new Specifier[] {specifier});
    }

    /**
     * Update the properties of the given model.
     * 
     * @param model A target model.
     * @param specifiers A list of property {@link Specifier}.
     */
    void update(M model, Specifier<M, ?>... specifiers);

    /**
     * Update the properties of all given models.
     * 
     * @param models
     */
    default void updateAll(M... models) {
        updateAll(I.list(models));
    }

    /**
     * Update the properties of all given models.
     * 
     * @param models
     */
    default void updateAll(Iterable<M> models) {
        models.forEach(this::update);
    }

    /**
     * Update the properties of all given models.
     * 
     * @param models
     */
    default void updateAll(Signal<M> models) {
        models.to(this::update);
    }

    /**
     * Update the specified model lazily.
     * 
     * @param model A target model.
     */
    void updateLazy(M model);

    /**
     * Update the properties of all given models.
     * 
     * @param models
     */
    default void updateAllLazy(M... models) {
        updateAllLazy(I.list(models));
    }

    /**
     * Update the properties of all given models.
     * 
     * @param models
     */
    default void updateAllLazy(Iterable<M> models) {
        models.forEach(this::updateLazy);
    }

    /**
     * Update the properties of all given models.
     * 
     * @param models
     */
    default void updateAllLazy(Signal<M> models) {
        models.to(this::updateLazy);
    }
}