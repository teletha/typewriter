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

import java.util.List;

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
        updateAll(List.of(models));
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
        updateAll(models.toList());
    }
}