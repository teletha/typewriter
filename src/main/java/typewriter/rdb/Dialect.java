/*
 * Copyright (C) 2023 The TYPEWRITER Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package typewriter.rdb;

import java.sql.Connection;
import java.sql.DriverManager;

import kiss.I;
import kiss.Managed;
import kiss.Singleton;
import kiss.model.Model;
import kiss.model.Property;

@Managed(Singleton.class)
public abstract class Dialect {

    /** The kind of RDMS. */
    public final String kind;

    /**
     * Hide constructor.
     */
    protected Dialect() {
        this.kind = getClass().getSimpleName().toLowerCase();
    }

    /**
     * Define the type mapping between Java and SQL.
     * 
     * @param type Java type.
     * @return SQL type.
     */
    public abstract String types(Class type);

    /**
     * Define the user-defined database location.
     * 
     * @param userLocation A user specified location.
     * @return A detected database location.
     */
    public final String configureLocation(String userLocation) {
        if (userLocation == null || userLocation.isBlank()) {
            return I.env("typewriter." + getClass().getSimpleName().toLowerCase(), defaultLocation());
        } else {
            return userLocation;
        }
    }

    /**
     * Define the default database location.
     * 
     * @return
     */
    protected abstract String defaultLocation();

    /**
     * Create new {@link Connection}.
     * 
     * @param url A database URL.
     * @return
     */
    public Connection createConnection(String url) throws Exception {
        return DriverManager.getConnection(url);
    }

    /**
     * Create new database.
     * 
     * @param url
     */
    public void createDatabase(String url) {
    }

    /**
     * Define SQL for creating new table.
     * 
     * @param tableName
     * @param model
     * @return
     */
    public String commandCreateTable(String tableName, Model model) {
        return "CREATE TABLE IF NOT EXISTS " + tableName + " " + defineColumns(model);
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
     * Define SQL for LIMIT and OFFSET.
     * 
     * @param sql
     * @param limit
     * @param offset
     */
    public void commandLimitAndOffset(SQL sql, long limit, long offset) {
        if (0 < limit) sql.write("LIMIT").write(limit);
        if (0 < offset) sql.write("OFFSET").write(offset);
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