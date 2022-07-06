/*
 * Copyright (C) 2022 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package typewriter.api;

import typewriter.IdentifiableModel;

public interface Deletable<M extends IdentifiableModel> {

    /**
     * Delete the specified model.
     * 
     * @param model A target model.
     */
    default void delete(M model) {
        delete(model, (Specifier[]) null);
    }

    /**
     * Delete the property of the given model.
     * 
     * @param model A target model.
     * @param specifier A property {@link Specifier}.
     */
    default void delete(M model, Specifier<M, ?> specifier) {
        delete(model, new Specifier[] {specifier});
    }

    /**
     * Delete the properties of the given model.
     * 
     * @param model A target model.
     * @param specifiers A list of property {@link Specifier}.
     */
    default void delete(M model, Specifier<M, ?>... specifiers) {
        if (model != null) {
            delete(model.id, specifiers);
        }
    }

    /**
     * Delete the specified model.
     * 
     * @param id An id of target model.
     */
    default void delete(long id) {
        delete(id, (Specifier[]) null);
    }

    /**
     * Delete the property of the given model.
     * 
     * @param id An id of target model.
     * @param specifier A property {@link Specifier}.
     */
    default void delete(long id, Specifier<M, ?> specifier) {
        delete(id, new Specifier[] {specifier});
    }

    /**
     * Delete the properties of the given model.
     * 
     * @param id An id of target model.
     * @param specifiers A list of property {@link Specifier}.
     */
    void delete(long id, Specifier<M, ?>... specifiers);
}
