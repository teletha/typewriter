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

public interface Accumulable<M> {

    /**
     * Count the number of registered models.
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

    /**
     * Returns the lowest expression value for each group.
     * 
     * @param specifier A {@link Specifier} of the target property.
     * @return Calculated result.
     */
    <C extends Comparable> C min(Specifier<M, C> specifier);

    /**
     * Returns the highest expression value for each group.
     * 
     * @param specifier A {@link Specifier} of the target property.
     * @return Calculated result.
     */
    <C extends Comparable> C max(Specifier<M, C> specifier);

    /**
     * Returns an average of numerical values. Ignores non-numeric values.
     * 
     * @param specifier A {@link Specifier} of the target property.
     * @return Calculated result.
     */
    <N extends Number> double avg(Specifier<M, N> specifier);

    /**
     * Returns a sum of numerical values. Ignores non-numeric values.
     * 
     * @param specifier A {@link Specifier} of the target property.
     * @return Calculated result.
     */
    <N extends Number> N sum(Specifier<M, N> specifier);
}