/*
 * Copyright (C) 2024 The TYPEWRITER Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package typewriter.api.model;

public abstract class IdentifiableModel {

    private long id;

    /**
     * Get the id property of this {@link IdentifiableModel}.
     * 
     * @return The id property.
     */
    public long getId() {
        return id;
    }

    /**
     * Set the id property of this {@link IdentifiableModel}.
     * 
     * @param id The id value to set.
     */
    protected void setId(long id) {
        this.id = id;
    }
}