/*
 * Copyright (C) 2022 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package typewriter;

public abstract class IdentifiableModel {

    public long id;

    /**
     * Get the id property of this {@link IdentifiableModel}.
     * 
     * @return The id property.
     */
    public long id() {
        return id;
    }

    /**
     * Set the id property of this {@link IdentifiableModel}.
     * 
     * @param id The id value to set.
     */
    public void setId(long id) {
        this.id = id;
    }
}
