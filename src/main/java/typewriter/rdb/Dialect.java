/*
 * Copyright (C) 2022 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package typewriter.rdb;

import java.sql.Connection;

import kiss.Managed;
import kiss.Singleton;
import kiss.model.Model;
import kiss.model.Property;

@Managed(Singleton.class)
public abstract class Dialect {

    /**
     * Define the type mapping between Java and SQL.
     * 
     * @param type Java type.
     * @return SQL type.
     */
    public abstract String types(Class type);

    /**
     * Define the default database location.
     * 
     * @return
     */
    public abstract String defaultLocation();

    /**
     * Initialize the new {@link Connection}.
     * 
     * @param connection A new created connection.
     * @throws Exception
     */
    public void initializeConnection(Connection connection) throws Exception {
        // do nothing
    }

    /**
     * Define SQL for creating new table.
     * 
     * @param tableName
     * @param model
     * @return
     */
    public String createTable(String tableName, Model model) {
        return "CREATE TABLE IF NOT EXISTS" + tableName + defineColumns(model);
    }

    /**
     * Define SQL for replacing column.
     * 
     * @return
     */
    public String commandReplace() {
        return "REPLACE INTO";
    }

    /**
     * Define SQL for updating values.
     * 
     * @return
     */
    public String commandUpdate() {
        return "UPDATE";
    }

    /**
     * Helper to write column definitions.
     * 
     * @param model A processing model.
     * @return
     */
    protected CharSequence defineColumns(Model<?> model) {
        StringBuilder builder = new StringBuilder();
        builder.append('(');
        for (Property property : model.properties()) {
            RDBCodec<?> codec = RDBCodec.by(property.model.type);
            for (int i = 0; i < codec.types.size(); i++) {
                Class columnType = codec.types.get(i);
                String columnName = codec.names.get(i);

                String type = types(columnType);
                if (type == null) throw error("SQL type is not found for [", columnType, "]");

                builder.append(property.name).append(columnName).append(' ').append(type).append(',');
            }
        }
        builder.append("PRIMARY KEY(id))");

        return builder;
    }

    /**
     * Build error.
     * 
     * @param messages
     * @return
     */
    private Error error(Object... messages) {
        StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName()).append(" : ");

        for (Object message : messages) {
            builder.append(message);
        }

        return new Error(builder.toString());
    }
}
