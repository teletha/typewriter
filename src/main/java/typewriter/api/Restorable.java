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

import kiss.Signal;

public interface Restorable<M extends Identifiable> {

    /**
     * Restore the specified model.
     * 
     * @param model A target model.
     * @return The stream for restored model.
     */
    default Signal<M> restore(M model) {
        return restore(model, (Specifier[]) null);
    }

    /**
     * Restore the property of the given model.
     * 
     * @param model A target model.
     * @param specifier A property {@link Specifier}.
     * @return The stream for restored model.
     */
    default Signal<M> restore(M model, Specifier<M, ?> specifier) {
        return restore(model, new Specifier[] {specifier});
    }

    /**
     * Restore the properties of the given model.
     * 
     * @param model A target model.
     * @param specifiers A list of property {@link Specifier}.
     * @return The stream for restored model.
     */
    Signal<M> restore(M model, Specifier<M, ?>... specifiers);
}