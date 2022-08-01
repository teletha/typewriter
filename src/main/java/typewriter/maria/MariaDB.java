/*
 * Copyright (C) 2022 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package typewriter.maria;

import java.util.HashMap;
import java.util.Map;

import typewriter.rdb.Dialect;

public class MariaDB extends Dialect {

    /** The JAVA-SQL type mapping. */
    private static final Map<Class, String> TYPES = new HashMap();

    static {
        TYPES.put(int.class, "integer");
        TYPES.put(long.class, "bigint");
        TYPES.put(float.class, "float");
        TYPES.put(double.class, "double");
        TYPES.put(short.class, "tinyint");
        TYPES.put(byte.class, "smallint");
        TYPES.put(boolean.class, "boolean");
        TYPES.put(String.class, "varchar(10)");
    }

    /**
     * Hide constructor.
     */
    private MariaDB() {
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
    protected String defaultLocation() {
        return "jdbc:mariadb:mem:temporary";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void commandLimitAndOffset(StringBuilder builder, long limit, long offset) {
        if (0 < limit) builder.append(" LIMIT ").append(limit);
        if (0 < offset) {
            if (limit <= 0) builder.append(" LIMIT 18446744073709551615");
            builder.append(" OFFSET ").append(offset);
        }
    }
}
