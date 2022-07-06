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

public interface Updatable<M> {

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
}
