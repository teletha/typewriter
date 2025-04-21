/*
 * Copyright (C) 2025 The TYPEWRITER Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package typewriter.api;

public interface Deletable<M extends Identifiable> {

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
    void delete(M model, Specifier<M, ?>... specifiers);
}