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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import kiss.I;
import kiss.Signal;
import typewriter.api.model.IdentifiableModel;

public interface Testable {

    /**
     * Create the {@link QueryExecutor} for test.
     * 
     * @param <M>
     * @param type
     * @return
     */
    <M extends IdentifiableModel, Q extends QueryExecutor<M, Signal<M>, ?, Q>> Q createEmptyDB(Class<M> type);

    /**
     * Create a temporary file.
     * 
     * @return
     */
    static Path createTemporaryFile() {
        try {
            Path file = Files.createTempFile("typewriter", "db");
            file.toFile().deleteOnExit();
            return file;
        } catch (IOException e) {
            throw I.quiet(e);
        }
    }

    /**
     * Create a temporary file.
     * 
     * @return
     */
    static Path createTemporaryDir() {
        try {
            Path file = Files.createTempDirectory("typewriter");
            file.toFile().deleteOnExit();
            return file;
        } catch (IOException e) {
            throw I.quiet(e);
        }
    }
}
