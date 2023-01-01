/*
 * Copyright (C) 2023 The TYPEWRITER Development Team
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
        transact((WiseFunction) operation::invoke);
    }

    /**
     * Do your transaction.
     * 
     * @param <R>
     * @param operation Your operation.
     * @return A result of operation.
     */
    <R> R transact(WiseFunction<Self, R> operation);
}