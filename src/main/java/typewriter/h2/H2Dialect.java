/*
 * Copyright (C) 2022 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package typewriter.h2;

import java.util.HashMap;
import java.util.Map;

import typewriter.rdb.Dialect;

public class H2Dialect extends Dialect {

    /** Singleton */
    public static final Dialect SINGLETON = new H2Dialect();

    /** The JAVA-SQL type mapping. */
    private static final Map<Class, String> TYPES = new HashMap();

    static {
        TYPES.put(int.class, "int");
        TYPES.put(long.class, "bigint");
        TYPES.put(float.class, "real");
        TYPES.put(double.class, "double");
        TYPES.put(short.class, "tinyint");
        TYPES.put(byte.class, "smallint");
        TYPES.put(boolean.class, "boolean");
        TYPES.put(String.class, "varchar");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String types(Class type) {
        return TYPES.get(type);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String defaultLocation() {
        return "jdbc:h2:mem:temporary";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String commandReplace() {
        return "MERGE INTO";
    }
}