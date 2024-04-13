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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import kiss.I;
import kiss.Property;
import kiss.Signal;
import kiss.WiseFunction;
import kiss.Ⅱ;
import typewriter.api.Identifiable;
import typewriter.api.Specifier;

public class SQL<M extends Identifiable> {

    /** The database layer. */
    private final RDB<M> rdb;

    /** The statement expresison. */
    private final StringBuilder text = new StringBuilder();

    /** The variable list. */
    private final List variables = new ArrayList();

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
                RDBCodec<?> codec = RDBCodec.by(property.model);
                for (String name : codec.names) {
                    text.append(count++ == 0 ? " ORDER BY " : ",").append(property.name.concat(name)).append(sort.ⅱ ? " ASC" : " DESC");
                }
            }
        }

        return this;
    }

    /**
     * Write column names.
     * 
     * @param properties
     * @return
     */
    public SQL<M> names(Iterable<Property> properties) {
        int count = 0;
        Iterator<Property> iterator = properties.iterator();
        while (iterator.hasNext()) {
            Property property = iterator.next();
            RDBCodec<?> codec = RDBCodec.by(property.model);
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
        return values(List.of(instance));
    }

    /**
     * Write VALUES statement.
     * 
     * @param instances
     * @return
     */
    public SQL<M> values(Iterable<M> instances) {
        // prepare RDB codecs
        List<Ⅱ<Property, RDBCodec>> codecs = new ArrayList();
        for (Property property : rdb.model.properties()) {
            codecs.add(I.pair(property, RDBCodec.by(property.model)));
        }

        Mapper mapper = new Mapper();
        text.append("VALUES");
        for (M instance : instances) {
            text.append('(');
            for (Ⅱ<Property, RDBCodec> codec : codecs) {
                codec.ⅱ.encode(mapper, codec.ⅰ.name, rdb.model.get(instance, codec.ⅰ));
            }
            text.deleteCharAt(text.length() - 1).append("),");
        }
        text.deleteCharAt(text.length() - 1);

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
            RDBCodec codec = RDBCodec.by(property.model);
            codec.encode(result, property.name, rdb.model.get(instance, property));
        }

        int count = 0;
        for (Entry<String, Object> entry : result.entrySet()) {
            text.append(count++ == 0 ? " SET " : ",").append(entry.getKey()).append("=?");
            variables.add(entry.getValue());
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
            RDBCodec codec = RDBCodec.by(property.model);

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
     */
    public SQL<M> fromCurrentTable() {
        return from(rdb.tableName);
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
     * Write ORDER BY statement.
     * 
     * @param specifier
     * @return
     */
    public SQL<M> orderBy(Specifier<M, ?> specifier) {
        return orderBy(specifier, true);
    }

    /**
     * Write ORDER BY statement.
     * 
     * @param specifier
     * @return
     */
    public SQL<M> orderBy(Specifier<M, ?> specifier, boolean ascending) {
        return orderBy(specifier, ascending, null, true);
    }

    /**
     * Write ORDER BY statement.
     * 
     * @return
     */
    public SQL<M> orderBy(Specifier<M, ?> specifier1, boolean ascending1, Specifier<M, ?> specifier2, boolean ascending2) {
        return orderBy(specifier1, ascending1, specifier2, ascending2, null, true);
    }

    /**
     * Write ORDER BY statement.
     * 
     * @return
     */
    public SQL<M> orderBy(Specifier<M, ?> specifier1, boolean ascending1, Specifier<M, ?> specifier2, boolean ascending2, Specifier<M, ?> specifier3, boolean ascending3) {
        text.append(" ORDER BY ").append(specifier1.propertyName()).append(ascending1 ? " ASC" : " DESC");
        if (specifier2 != null) text.append(", ").append(specifier2.propertyName()).append(ascending2 ? " ASC" : " DESC");
        if (specifier3 != null) text.append(", ").append(specifier3.propertyName()).append(ascending3 ? " ASC" : " DESC");

        return this;
    }

    /**
     * Write SELECT statement.
     * 
     * @param specifiers
     * @return
     */
    public SQL<M> select(Specifier<M, ?>... specifiers) {
        text.append(" SELECT ").append(Stream.of(specifiers).map(Specifier::propertyName).collect(Collectors.joining(", ")));
        return this;
    }

    public SQL<M> avg(Specifier<M, ?> specifier, int windowSize) {
        text.append(", avg(")
                .append(specifier.propertyName())
                .append(") OVER (order by id")
                .append(" rows between ")
                .append(windowSize)
                .append(" preceding and current row)");
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
    public void execute() {
        int index = 1;
        try (Connection connection = rdb.provider.get()) {
            PreparedStatement prepared = connection.prepareStatement(text.toString());
            for (Object variable : variables) {
                prepared.setObject(index++, variable);
            }
            prepared.execute();

            log(null);
        } catch (SQLException e) {
            log(e);
            throw I.quiet(e);
        }
    }

    /**
     * Execute query.
     */
    public Signal<ResultSet> qurey() {
        return qurey(x -> x);
    }

    /**
     * Execute query.
     */
    public <R> Signal<R> qurey(WiseFunction<ResultSet, R> process) {
        if (text.isEmpty()) {
            return I.signal();
        }

        return new Signal<ResultSet>((observer, disposer) -> {
            int index = 1;
            try (Connection connection = rdb.provider.get()) {
                PreparedStatement prepared = connection.prepareStatement(text.toString());
                for (Object variable : variables) {
                    prepared.setObject(index++, variable);
                }
                ResultSet result = prepared.executeQuery();
                while (!disposer.isDisposed() && !result.isClosed() && result.next()) {
                    observer.accept(result);
                }
                observer.complete();
                log(null);
            } catch (SQLException e) {
                observer.error(new SQLException(text.toString(), e));
            }
            return disposer;
        }).map(process).effectOnError(this::log);
    }

    /**
     * Write execution log in detail.
     * 
     * @param e
     */
    private void log(Throwable e) {
        if (e == null) {
            I.debug(message(e));
        } else {
            I.error(message(e));
            I.error(e);
        }
    }

    /**
     * Create log message lazily.
     * 
     * @param e
     * @return
     */
    private Supplier message(Throwable e) {
        return () -> {
            StringBuilder builder = new StringBuilder("Typewriter ");
            builder.append(e == null ? "executes" : "throws ".concat(e.getMessage()));
            builder.append(" Model: ").append(rdb.model.type.getCanonicalName());
            builder.append("\tTable: ").append(rdb.tableName);
            builder.append("\tDialect: ").append(rdb.dialect.kind);
            builder.append(" \tSQL: ").append(text.toString());

            return builder.toString();
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return text.toString();
    }

    /**
     * Transparent variable mapper.
     */
    private class Mapper implements Map {

        /**
         * {@inheritDoc}
         */
        @Override
        public int size() {
            throw new Error();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isEmpty() {
            throw new Error();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean containsKey(Object key) {
            throw new Error();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean containsValue(Object value) {
            throw new Error();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Object get(Object key) {
            throw new Error();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Object put(Object key, Object value) {
            text.append("?,");
            variables.add(value);
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Object remove(Object key) {
            throw new Error();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void putAll(Map m) {
            throw new Error();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void clear() {
            throw new Error();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Set keySet() {
            throw new Error();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Collection values() {
            throw new Error();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Set entrySet() {
            throw new Error();
        }
    }
}