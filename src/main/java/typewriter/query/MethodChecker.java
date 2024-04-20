/*
 * Copyright (C) 2024 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package typewriter.query;

import java.util.List;

import ch.epfl.labos.iu.orm.queryll2.path.PathAnalysisMethodChecker;
import ch.epfl.labos.iu.orm.queryll2.symbolic.BasicSymbolicInterpreter.OperationSideEffect;
import ch.epfl.labos.iu.orm.queryll2.symbolic.MethodSignature;
import ch.epfl.labos.iu.orm.queryll2.symbolic.TypedValue;

public class MethodChecker implements PathAnalysisMethodChecker {

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isFluentChaining(MethodSignature m) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isPutFieldAllowed() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OperationSideEffect isStaticMethodSafe(MethodSignature m) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OperationSideEffect isMethodSafe(MethodSignature m, TypedValue base, List<TypedValue> args) {
        return null;
    }
}
