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

import kiss.WiseConsumer;
import kiss.WiseFunction;

public interface Transactional<Self extends Transactional<Self>> {

    /**
     * Do your transaction.
     * 
     * @param operation Your operation.
     */
    default void transact(WiseConsumer<Self> operation) {
        transactWith((WiseFunction) operation::invoke);
    }

    /**
     * Do your transaction.
     * 
     * @param <R>
     * @param operation Your operation.
     * @return A result of operation.
     */
    <R> R transactWith(WiseFunction<Self, R> operation);
}