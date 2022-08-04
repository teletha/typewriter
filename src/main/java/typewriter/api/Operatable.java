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

import kiss.Signal;

public interface Operatable<M> {

    /**
     * Count registered models.
     * 
     * @return
     */
    long count();

    /**
     * Distinct values by the specified property.
     * 
     * @param <V>
     * @param specifier
     * @return
     */
    <V> Signal<V> distinct(Specifier<M, V> specifier);
}
