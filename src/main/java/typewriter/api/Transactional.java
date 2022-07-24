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

import kiss.WiseRunnable;
import kiss.WiseSupplier;

public interface Transactional {

    /**
     * Do your transaction.
     * 
     * @param operation Your operation.
     */
    default void transact(WiseRunnable operation) {
        transact((WiseSupplier) operation::invoke);
    }

    /**
     * Do your transaction.
     * 
     * @param <R>
     * @param operation Your operation.
     * @return A result of operation.
     */
    <R> R transact(WiseSupplier<R> operation);
}
