/*
 * Copyright (C) 2023 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package typewriter.api;

public interface Metadatable {

    /**
     * Get the last accessed date on the data source.
     * 
     * @return
     */
    long lastAccessed();

    /**
     * Get the last modified date on the data source.
     * 
     * @return
     */
    long lastModified();

    /**
     * Get the snapshot id of the data source.
     * 
     * @return
     */
    long stamp();
}
