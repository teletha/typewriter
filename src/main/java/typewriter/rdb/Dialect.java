/*
 * Copyright (C) 2024 The TYPEWRITER Development Team
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
import java.util.Properties;

import kiss.I;
import kiss.Managed;
import kiss.Model;
import kiss.Property;
import kiss.Singleton;
import typewriter.api.Constraint.ListConstraint;
import typewriter.api.Identifiable;
import typewriter.api.Specifier.ListSpecifier;
import typewriter.rdb.RDBConstraint.ForList;

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
     * Define table name quotation character.
     * 
     * @return
     */
    public String quote() {
        return "`";
    }

    /**
     * Normalize the given column name.
     * 
     * @param name
     */
    public String normalizeColumnName(String name) {
        return name;
    }

    /**
     * Create new {@link Connection}.
     * 
     * @param url A database URL.
     * @param properties JDBC options
     * @return
     */
    public Connection createConnection(String url, Properties properties) throws Exception {
        return DriverManager.getConnection(url, properties);
    }

    /**
     * Create new database.
     * 
     * @param url
     */
    public void createDatabase(String url) {
    }

    /**
     * @param specifier
     * @return
     */
    public <M, N> ListConstraint<N> createListConstraint(ListSpecifier<M, N> specifier) {
        return new ForList(specifier, this);
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
     * Define SQL for adding new column.
     * 
     * @param tableName
     * @param columnName
     * @param type
     * @return
     */
    public String commandAddRow(String tableName, String columnName, Class type) {
        return "ALTER TABLE " + tableName + " ADD COLUMN " + columnName + " " + types(type);
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

    public <M extends Identifiable> SQL commandUpsert(SQL<M> sql, Iterable<M> models) {
        return sql.write(commandReplace(), sql.tableName).write("(").names(sql.model.properties()).write(")").values(models);
    }

    public <M extends Identifiable> SQL commandUpsert(SQL<M> sql, Iterable<M> models, Iterable<Property> properties) {
        return sql.write(commandReplace(), sql.tableName).write("(").names(properties).write(")").values(models, properties);
    }

    /**
     * Define SQL for LIMIT and OFFSET.
     * 
     * @param sql
     * @param limit
     * @param offset
     */
    public void commandLimitAndOffset(SQL sql, long limit, long offset) {
        sql.limit(limit).offset(offset);
    }

    /**
     * @param propertyName
     * @param regex
     * @return
     */
    public String commandRegex(String propertyName, String regex) {
        return propertyName + " REGEXP '" + regex + "'";
    }

    /**
     * Define function for length of list.
     * 
     * @return
     */
    public abstract String commnadListLength();

    /**
     * Define function for length of list.
     * 
     * @return
     */
    public abstract String commnadListContains(String propertyName, Object value);

    public String commandOnConflict() {
        return "ON CONFLICT (id) DO UPDATE";
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
            RDBCodec<?> codec = RDBCodec.by(property.model);
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