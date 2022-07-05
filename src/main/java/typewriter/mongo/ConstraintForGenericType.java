/*
 * Copyright (C) 2022 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package typewriter.mongo;

import typewriter.api.Constraint;
import typewriter.api.Specifier;
import typewriter.api.Constraint.TypeConstraint;

/**
 * The specialized {@link Constraint}.
 */
class ConstraintForGenericType<T> extends MongoConstraint<T, TypeConstraint<T>> implements TypeConstraint<T> {
    ConstraintForGenericType(Specifier specifier) {
        super(specifier);
    }
}