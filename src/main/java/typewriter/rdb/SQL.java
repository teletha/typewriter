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
import kiss.Ⅱ;
import kiss.model.Property;
import typewriter.api.Specifier;
import typewriter.api.model.IdentifiableModel;

public class SQL<M extends IdentifiableModel> {

    /** The database layer. */
    private final RDB<M> rdb;

    /** The statement expresison. */
    private final StringBuilder text = new StringBuilder();

    /** The variable list. */
    private final List<WiseBiConsumer<PreparedStatement, Integer>> variables = new ArrayList();

    /**
     * Hide constructor.
     */
    SQL(RDB<M> rdb) {
        this.rdb = rdb;
    }

    /**
     * Write statement.
     * 
     * @param statement
     * @return
     */
    public SQL<M> write(CharSequence statement) {
        text.append(' ').append(statement);
        return this;
    }

    /**
     * Write statement.
     * 
     * @param statement1
     * @param statement2
     * @return
     */
    public SQL<M> write(CharSequence statement1, CharSequence statement2) {
        return write(statement1).write(statement2);
    }

    /**
     * Write statement.
     * 
     * @param value
     * @return
     */
    public SQL<M> write(int value) {
        text.append(' ').append(value);
        return this;
    }

    /**
     * Write statement.
     * 
     * @param value
     * @return
     */
    public SQL<M> write(long value) {
        text.append(' ').append(value);
        return this;
    }

    /**
     * Write statement by {@link RDBQuery}.
     * 
     * @param query
     */
    public SQL<M> write(RDBQuery<M> query) {
        int count = 0;
        for (RDBConstraint<?, ?> constraint : query.constraints) {
            for (String e : constraint.expression) {
                text.append(count++ == 0 ? " WHERE " : " AND ").append(e);
            }
        }

        rdb.dialect.commandLimitAndOffset(this, query.limit, query.offset);

        if (query.sorts != null) {
            count = 0;
            for (Ⅱ<Specifier, Boolean> sort : query.sorts) {
                Property property = rdb.model.property(sort.ⅰ.propertyName());
                RDBCodec<?> codec = RDBCodec.by(property.model.type);
                for (String name : codec.names) {
                    text.append(count++ == 0 ? " ORDER BY " : ",").append(property.name.concat(name)).append(sort.ⅱ ? " ASC" : " DESC");
                }
            }
        }
        return this;
    }

    /**
     * Register variable.
     * 
     * @param variable
     * @return
     */
    public SQL<M> bind(Object variable) {
        variables.add((p, index) -> p.setObject(index, variable));
        return this;
    }

    /**
     * Write column names.
     * 
     * @param properties
     * @return
     */
    public SQL<M> names(List<Property> properties) {
        int count = 0;
        for (Property property : properties) {
            RDBCodec<?> codec = RDBCodec.by(property.model.type);
            for (int i = 0; i < codec.types.size(); i++) {
                text.append(count++ == 0 ? ' ' : ',').append(property.name).append(codec.names.get(i));
            }
        }
        return this;
    }

    /**
     * Write VALUES statement.
     * 
     * @param instance
     * @return
     */
    public SQL<M> values(M instance) {
        Map<String, Object> result = new LinkedHashMap();
        for (Property property : rdb.model.properties()) {
            RDBCodec codec = RDBCodec.by(property.model.type);
            codec.encode(result, property.name, rdb.model.get(instance, property));
        }

        int count = 0;
        for (Entry<String, Object> entry : result.entrySet()) {
            text.append(count++ == 0 ? " VALUES(?" : ",?");
            variables.add((p, index) -> p.setObject(index, entry.getValue()));
        }
        text.append(')');
        return this;
    }

    /**
     * Write SET properties.
     * 
     * @param properties
     * @param instance
     */
    public SQL<M> set(List<Property> properties, M instance) {
        Map<String, Object> result = new HashMap();
        for (Property property : properties) {
            RDBCodec codec = RDBCodec.by(property.model.type);
            codec.encode(result, property.name, rdb.model.get(instance, property));
        }

        int count = 0;
        for (Entry<String, Object> entry : result.entrySet()) {
            text.append(count++ == 0 ? " SET " : ",").append(entry.getKey()).append("=?");
            variables.add((p, index) -> p.setObject(index, entry.getValue()));
        }
        return this;
    }

    /**
     * Write SET properties to null.
     * 
     * @param properties
     */
    public SQL<M> setNull(List<Property> properties) {
        int count = 0;
        for (Property property : properties) {
            RDBCodec codec = RDBCodec.by(property.model.type);

            for (int i = 0; i < codec.types.size(); i++) {
                text.append(count++ == 0 ? " SET " : ",").append(property.name).append(codec.names.get(i)).append("=NULL");
            }
        }
        return this;
    }

    /**
     * Write function statement.
     * 
     * @param name A function name.
     * @param property A target property.
     * @return Chainable API.
     */
    public SQL<M> func(String name, Property property) {
        text.append(' ').append(name).append('(').append(property.name).append(')');
        return this;
    }

    /**
     * Write AS statement.
     * 
     * @param alias An alias.
     */
    public SQL<M> as(String alias) {
        text.append(" AS ").append(alias);
        return this;
    }

    /**
     * Write FROM statement.
     * 
     * @param table A name of table.
     */
    public SQL<M> from(String table) {
        text.append(" FROM ").append(table);
        return this;
    }

    /**
     * Write WHERE statement.
     * 
     * @param instance
     */
    public SQL<M> where(M instance) {
        text.append(" WHERE id=").append(instance.getId());
        return this;
    }

    /**
     * Execute query.
     */
    void execute() {
        int index = 1;
        try (Connection connection = rdb.provider.get()) {
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
     * Execute query.
     */
    Signal<ResultSet> qurey() {
        if (text.isEmpty()) {
            return I.signal();
        }

        return new Signal<>((observer, disposer) -> {
            int index = 1;
            try (Connection connection = rdb.provider.get()) {
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