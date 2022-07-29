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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
     * Helper to write name of columns.
     * 
     * @param properties
     * @return
     */
    public static CharSequence column(List<Property> properties) {
        StringBuilder builder = new StringBuilder();

        for (Property property : properties) {
            RDBCodec<?> codec = RDBCodec.by(property.model.type);
            for (int j = 0; j < codec.types.size(); j++) {
                builder.append(property.name).append(codec.names.get(j)).append(',');
            }
        }

        return deleteTailComma(builder);
    }

    private static List<Property> properties(Model model, Specifier[] specifiers) {
        return I.signal(specifiers).skipNull().map(s -> model.property(s.propertyName())).toList();
    }

    /**
     * Delete comma character at tail.
     * 
     * @param builder
     */
    private static StringBuilder deleteTailComma(StringBuilder builder) {
        int last = builder.length() - 1;
        if (builder.charAt(last) == ',') {
            builder.deleteCharAt(last);
        }
        return builder;
    }

    /**
     * Helper to write delete columns.
     * 
     * @return
     */
    public static CharSequence SETNULL(Model model, Specifier[] specifiers) {
        StringBuilder builder = new StringBuilder("SET ");

        for (Property property : properties(model, specifiers)) {
            RDBCodec codec = RDBCodec.by(property.model.type);

            for (int i = 0; i < codec.types.size(); i++) {
                builder.append(property.name).append(codec.names.get(i)).append("=NULL,");
            }
        }

        return deleteTailComma(builder);
    }

    /**
     * Helper to write set columns.
     * 
     * @return
     */
    public static CharSequence SET(Model model, Specifier[] specifiers, Object instance) {
        Map<String, Object> result = new HashMap();
        for (Property property : properties(model, specifiers)) {
            RDBCodec codec = RDBCodec.by(property.model.type);
            codec.encode(result, property.name, model.get(instance, property));
        }

        StringBuilder builder = new StringBuilder("SET ");
        for (Entry<String, Object> entry : result.entrySet()) {
            builder.append(entry.getKey()).append('=').append(I.transform(entry.getValue(), String.class)).append(',');
        }

        return deleteTailComma(builder);
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
    public static <V> CharSequence VALUES(Model<V> model, V instance) {
        Map<String, Object> result = new LinkedHashMap();
        for (Property property : model.properties()) {
            RDBCodec codec = RDBCodec.by(property.model.type);
            codec.encode(result, property.name, model.get(instance, property));
        }

        StringBuilder builder = new StringBuilder("VALUES(");
        for (Entry<String, Object> entry : result.entrySet()) {
            builder.append(I.transform(entry.getValue(), String.class)).append(",");
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
