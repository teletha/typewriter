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

import typewriter.api.model.IdentifiableModel;

public interface Restorable<M extends IdentifiableModel> {

    /**
     * Restore the specified model.
     * 
     * @param model A target model.
     */
    default void restore(M model) {
        restore(model, (Specifier[]) null);
    }

    /**
     * Restore the property of the given model.
     * 
     * @param model A target model.
     * @param specifier A property {@link Specifier}.
     */
    default void restore(M model, Specifier<M, ?> specifier) {
        restore(model, new Specifier[] {specifier});
    }

    /**
     * Restore the properties of the given model.
     * 
     * @param model A target model.
     * @param specifiers A list of property {@link Specifier}.
     */
    void restore(M model, Specifier<M, ?>... specifiers);
}
