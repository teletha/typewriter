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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import kiss.I;
import kiss.Signal;
import kiss.WiseBiConsumer;
import kiss.WiseSupplier;
import kiss.model.Model;
import kiss.model.Property;
import typewriter.api.model.IdentifiableModel;

public class SQL {

    /** The statement expresison. */
    private final StringBuilder text = new StringBuilder();

    /** The variable list. */
    private final List<WiseBiConsumer<PreparedStatement, Integer>> variables = new ArrayList();

    /**
     * Define your SQL.
     * 
     * @return
     */
    public static SQL define() {
        return new SQL();
    }

    /**
     * Hide constructor.
     */
    private SQL() {
    }

    /**
     * Write statement.
     * 
     * @param text
     * @return
     */
    public SQL write(CharSequence text) {
        this.text.append(' ').append(text);
        return this;
    }

    /**
     * Write statement.
     * 
     * @param text1
     * @return
     */
    public SQL write(CharSequence text1, CharSequence text2) {
        return write(text1).write(text2);
    }

    /**
     * Write statement.
     * 
     * @param value
     * @return
     */
    public SQL write(long value) {
        this.text.append(' ').append(value);
        return this;
    }

    /**
     * @param query
     */
    public <M extends IdentifiableModel> SQL write(RDBQuery<M> query, Model<M> model, Dialect dialect) {
        query.build(this, model, dialect);
        return this;
    }

    /**
     * Register variable.
     * 
     * @param variable
     * @return
     */
    public SQL bind(Object variable) {
        variables.add((p, index) -> p.setObject(index, variable));
        return this;
    }

    /**
     * Write VALUES statement.
     * 
     * @param model
     * @param instance
     * @return
     */
    public <M> SQL values(Model<M> model, M instance) {
        Map<String, Object> result = new LinkedHashMap();
        for (Property property : model.properties()) {
            RDBCodec codec = RDBCodec.by(property.model.type);
            codec.encode(result, property.name, model.get(instance, property));
        }

        int count = 0;
        text.append(" VALUES(");
        for (Entry<String, Object> entry : result.entrySet()) {
            if (count++ != 0) text.append(',');
            text.append('?');
            variables.add((p, index) -> p.setObject(index, entry.getValue()));
        }
        text.append(')');
        return this;
    }

    /**
     * Write WHERE statement.
     * 
     * @param instance
     */
    public <M extends IdentifiableModel> SQL where(M instance) {
        text.append(" WHERE id=").append(instance.getId());
        return this;
    }

    /**
     * @param model
     * @param properties
     * @param instance
     */
    public <M extends IdentifiableModel> SQL set(Model<M> model, List<Property> properties, M instance) {
        Map<String, Object> result = new HashMap();
        for (Property property : properties) {
            RDBCodec codec = RDBCodec.by(property.model.type);
            codec.encode(result, property.name, model.get(instance, property));
        }

        int count = 0;
        text.append(" SET ");
        for (Entry<String, Object> entry : result.entrySet()) {
            if (count++ != 0) text.append(',');
            text.append(entry.getKey()).append("=?");
            variables.add((p, index) -> p.setObject(index, entry.getValue()));
        }

        return this;
    }

    /**
     * @param properties
     */
    public <M extends IdentifiableModel> SQL setNull(List<Property> properties) {
        text.append(" SET ");

        int count = 0;
        for (Property property : properties) {
            RDBCodec codec = RDBCodec.by(property.model.type);

            for (int i = 0; i < codec.types.size(); i++) {
                if (count++ != 0) text.append(",");
                text.append(property.name).append(codec.names.get(i)).append("=NULL");
            }
        }

        return this;
    }

    /**
     * @param provider
     */
    public void execute(WiseSupplier<Connection> provider) {
        int index = 1;
        try (Connection connection = provider.get()) {
            PreparedStatement prepared = connection.prepareStatement(text.toString());
            for (WiseBiConsumer<PreparedStatement, Integer> variable : variables) {
                variable.accept(prepared, index++);
            }
            prepared.execute();
        } catch (SQLException e) {
            throw I.quiet(e);
        }
    }

    /**
     * @param provider
     * @return
     */
    public Signal<ResultSet> qurey(WiseSupplier<Connection> provider) {
        if (text.isEmpty()) {
            return I.signal();
        }

        return new Signal<>((observer, disposer) -> {
            int index = 1;
            try (Connection connection = provider.get()) {
                PreparedStatement prepared = connection.prepareStatement(text.toString());
                for (WiseBiConsumer<PreparedStatement, Integer> variable : variables) {
                    variable.accept(prepared, index++);
                }
                ResultSet result = prepared.executeQuery();
                while (!disposer.isDisposed() && result.next()) {
                    observer.accept(result);
                }
                observer.complete();
            } catch (SQLException e) {
                observer.error(new SQLException(text.toString(), e));
            }
            return disposer;
        });
    }
}
