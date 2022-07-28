/*
 * Copyright (C) 2022 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package typewriter.jdbc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import kiss.I;
import kiss.model.Model;
import kiss.model.Property;
import typewriter.api.Specifier;
import typewriter.api.model.IdentifiableModel;

/**
 * Help to writing Common SQL statement.
 */
public class SQLTemplate {

    /**
     * Helper to write table definition
     * 
     * @param model
     * @param mapper
     * @return
     */
    public static CharSequence tableDefinition(Model<?> model, Function<Class, String> mapper) {
        StringBuilder builder = new StringBuilder();
        builder.append('(');
        for (Property property : model.properties()) {
            JDBCTypeCodec<?> codec = JDBCTypeCodec.by(property.model.type);
            for (int i = 0; i < codec.types.size(); i++) {
                Class columnType = codec.types.get(i);
                String columnName = codec.names.get(i);

                builder.append(property.name).append(columnName).append(' ').append(mapper.apply(columnType)).append(',');
            }
        }
        builder.append("PRIMARY KEY(id))");

        return builder;
    }

    /**
     * Helper to write name of columns.
     * 
     * @param properties
     * @return
     */
    public static CharSequence column(List<Property> properties) {
        StringBuilder builder = new StringBuilder();

        for (Property property : properties) {
            JDBCTypeCodec<?> codec = JDBCTypeCodec.by(property.model.type);
            for (int j = 0; j < codec.types.size(); j++) {
                builder.append(property.name).append(codec.names.get(j)).append(',');
            }
        }

        // remove tail comma
        int last = builder.length() - 1;
        if (builder.charAt(last) == ',') {
            builder.deleteCharAt(last);
        }
        return builder;
    }

    /**
     * Helper to write set columns.
     * 
     * @return
     */
    public static CharSequence SET(Model model, Specifier[] specifiers, Function<Property, String> process) {
        return SET(I.signal(specifiers).skipNull().map(s -> model.property(s.propertyName())).toList(), process);
    }

    /**
     * Helper to write set columns.
     * 
     * @param properties
     * @return
     */
    public static CharSequence SET(List<Property> properties, Function<Property, String> process) {
        StringBuilder builder = new StringBuilder("SET ");

        for (Property property : properties) {
            JDBCTypeCodec codec = JDBCTypeCodec.by(property.model.type);

            for (int i = 0; i < codec.types.size(); i++) {
                builder.append(property.name).append(codec.names.get(i)).append('=').append(process.apply(property)).append(',');
            }
        }

        // remove tail comma
        int last = builder.length() - 1;
        if (builder.charAt(last) == ',') {
            builder.deleteCharAt(last);
        }
        return builder;
    }

    /**
     * Helper to write set columns.
     * 
     * @return
     */
    public static CharSequence SET2(Model model, Specifier[] specifiers, Object instance) {
        return SET2(model, I.signal(specifiers).skipNull().map(s -> model.property(s.propertyName())).toList(), instance);
    }

    /**
     * Helper to write set columns.
     * 
     * @param properties
     * @return
     */
    public static CharSequence SET2(Model model, List<Property> properties, Object instance) {
        StringBuilder builder = new StringBuilder("SET ");

        for (Property property : properties) {
            Map<String, Object> result = new HashMap();
            JDBCTypeCodec codec = JDBCTypeCodec.by(property.model.type);
            codec.encode(result, property.name, model.get(instance, property));

            for (Entry<String, Object> entry : result.entrySet()) {
                builder.append(entry.getKey()).append('=').append(I.transform(entry.getValue(), String.class)).append(',');
            }
        }

        // remove tail comma
        int last = builder.length() - 1;
        if (builder.charAt(last) == ',') {
            builder.deleteCharAt(last);
        }
        return builder;
    }

    /**
     * Helper to write WHERE statement.
     * 
     * @param model
     * @return
     */
    public static CharSequence WHERE(IdentifiableModel model) {
        StringBuilder builder = new StringBuilder();
        builder.append("WHERE id=").append(model.getId());
        return builder;
    }

    /**
     * Helper to write VALUES statement.
     * 
     * @param model
     * @return
     */
    public static CharSequence VALUES(Model model, Object instance) {
        StringBuilder builder = new StringBuilder("VALUES(");

        List<Property> properties = model.properties();
        for (int i = 0; i < properties.size(); i++) {
            Property property = properties.get(i);
            Map<String, Object> result = JDBCTypeCodec.encode(property, model.get(instance, property));

            for (Entry<String, Object> entry : result.entrySet()) {
                builder.append(I.transform(entry.getValue(), String.class)).append(",");
            }
        }

        // remove tail comma
        int last = builder.length() - 1;
        if (builder.charAt(last) == ',') {
            builder.deleteCharAt(last);
        }

        builder.append(")");

        return builder;
    }
}
