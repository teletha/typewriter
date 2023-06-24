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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import kiss.I;
import kiss.Signal;
import kiss.WiseFunction;
import kiss.WiseSupplier;
import kiss.model.Model;
import kiss.model.Property;
import typewriter.api.QueryExecutor;
import typewriter.api.Specifier;
import typewriter.api.model.IdentifiableModel;
import typewriter.h2.H2;
import typewriter.maria.MariaDB;
import typewriter.sqlite.SQLite;

/**
 * Data Access Object for RDBMS.
 */
public class RDB<M extends IdentifiableModel> extends QueryExecutor<M, Signal<M>, RDBQuery<M>, RDB<M>> {

    /** The supported RDBMS. */
    public static final Dialect H2 = I.make(H2.class);

    /** The supported RDBMS. */
    public static final Dialect SQLite = I.make(SQLite.class);

    /** The supported RDBMS. */
    public static final Dialect MariaDB = I.make(MariaDB.class);

    /** The reusable DAO cache. */
    private static final Map<Dialect, Map<Class, RDB>> DAO = Map
            .of(H2, new ConcurrentHashMap(), SQLite, new ConcurrentHashMap(), MariaDB, new ConcurrentHashMap());

    /** The document model. */
    protected final Model<M> model;

    /** The table name. */
    protected final String tableName;

    /** The associated {@link Dialect}. */
    protected final Dialect dialect;

    /** The connection provider. */
    protected final WiseSupplier<Connection> provider;

    /**
     * Data Access Object.
     * 
     * @param type A target model type.
     * @param dialect
     * @param url A user specified backend address.
     */
    public RDB(Class<M> type, Dialect dialect, String url) {
        this(Model.of(type), dialect, ConnectionPool.by(url));

        dialect.createDatabase(url);

        new SQL<>(this).write(dialect.commandCreateTable(tableName, model)).execute();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected RDBQuery<M> createQueryable() {
        return new RDBQuery(dialect);
    }

    /**
     * Data Access Object.
     * 
     * @param model A target model.
     * @param dialect A dialect of RDBMS.
     * @param provider A user specified backend connection.
     * @param createTable Should I create table?
     */
    private RDB(Model<M> model, Dialect dialect, WiseSupplier<Connection> provider) {
        this.model = model;
        this.tableName = '`' + model.type.getName() + '`';
        this.dialect = dialect;
        this.provider = provider;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long count() {
        return new SQL<>(this).write("SELECT count(*) N").from(tableName).qurey().map(result -> result.getLong("N")).to().exact();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <V> Signal<V> distinct(Specifier<M, V> specifier) {
        Property property = model.property(specifier.propertyName());
        return new SQL<>(this).write("SELECT DISTINCT", property.name).from(tableName).qurey().map(result -> (V) decode(property, result));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <C extends Comparable> C min(Specifier<M, C> specifier) {
        Property property = model.property(specifier.propertyName());
        return new SQL<>(this).write("SELECT")
                .func("min", property)
                .as(property.name)
                .from(tableName)
                .qurey()
                .map(result -> (C) decode(property, result))
                .to()
                .exact();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <C extends Comparable> C max(Specifier<M, C> specifier) {
        Property property = model.property(specifier.propertyName());
        return new SQL<>(this).write("SELECT")
                .func("max", property)
                .as(property.name)
                .from(tableName)
                .qurey()
                .map(result -> (C) decode(property, result))
                .to()
                .exact();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <N extends Number> double avg(Specifier<M, N> specifier) {
        Property property = model.property(specifier.propertyName());
        return new SQL<>(this).write("SELECT")
                .func("avg", property)
                .as("N")
                .from(tableName)
                .qurey()
                .map(result -> result.getDouble("N"))
                .to()
                .exact();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <N extends Number> N sum(Specifier<M, N> specifier) {
        Property property = model.property(specifier.propertyName());
        return new SQL<>(this).write("SELECT")
                .func("sum", property)
                .as(property.name)
                .from(tableName)
                .qurey()
                .map(result -> (N) decode(property, result))
                .to()
                .exact();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Signal<M> findBy(RDBQuery<M> query) {
        return new SQL<>(this).write("SELECT *")
                .from(tableName)
                .write(query)
                .qurey()
                .map(result -> decode(model, model.properties(), I.make(model.type), result));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Signal<M> restore(M instance, Specifier<M, ?>... specifiers) {
        if (instance == null) {
            return I.signal();
        }

        List<Property> properties = names(specifiers).map(model::property).or(I.signal(model.properties())).toList();

        return new SQL<>(this).write("SELECT")
                .names(properties)
                .from(tableName)
                .where(instance)
                .qurey()
                .map(result -> decode(model, properties, instance, result));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(M instance, Specifier<M, ?>... specifiers) {
        if (instance == null) {
            return;
        }

        if (specifiers == null || specifiers.length == 0) {
            // delete model
            new SQL<>(this).write("DELETE").from(tableName).where(instance).execute();
        } else {
            // delete properties
            new SQL<>(this).write(dialect.commandUpdate(), tableName)
                    .setNull(names(specifiers).map(model::property).toList())
                    .where(instance)
                    .execute();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(M instance, Specifier<M, ?>... specifiers) {
        if (instance == null) {
            return;
        }

        if (specifiers == null || specifiers.length == 0) {
            // update model
            new SQL<>(this).write(dialect.commandReplace(), tableName).values(instance).execute();
        } else {
            // update properties
            new SQL<>(this).write(dialect.commandUpdate(), tableName)
                    .set(names(specifiers).map(model::property).toList(), instance)
                    .where(instance)
                    .execute();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized <R> R transactWith(WiseFunction<RDB<M>, R> operation) {
        Connection connection = provider.get();
        try {
            connection.setAutoCommit(false);

            R result = operation.apply(new RDB<>(model, dialect, () -> connection));
            connection.commit();
            return result;
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException x) {
                throw I.quiet(x);
            }
            throw I.quiet(e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                throw I.quiet(e);
            }
        }
    }

    /**
     * Decode from {@link ResultSet} to property data.
     * 
     * @param <V>
     * @param property
     * @param result
     * @return
     * @throws SQLException
     */
    private <V> V decode(Property property, ResultSet result) throws SQLException {
        RDBCodec<V> codec = RDBCodec.by(property.model);
        return codec.decode(result, property.name);
    }

    /**
     * Decode from {@link ResultSet} to model data.
     * 
     * @param <V>
     * @param model
     * @param properties
     * @param instance
     * @param result
     * @return
     */
    private <V> V decode(Model model, Collection<Property> properties, V instance, ResultSet result) throws SQLException {
        for (Property property : properties) {
            RDBCodec codec = RDBCodec.by(property.model);
            model.set(instance, property, codec.decode(result, property.name));
        }
        return instance;
    }

    /**
     * Get the collection.
     * 
     * @param <M>
     * @param type The model type.
     * @return
     */
    public static <M extends IdentifiableModel> RDB<M> of(Class<M> type, Dialect dialect) {
        return DAO.get(dialect).computeIfAbsent(type, key -> new RDB(type, dialect, dialect.configureLocation(null)));
    }

    /**
     * Release all system resources related to RDB.
     */
    public static void release() {
        ConnectionPool.release();
        DAO.clear();
    }

    /**
     * Release all system resources related to the specified URL.
     */
    public static void release(String url) {
        if (url != null && url.startsWith("jdbc:")) {
            ConnectionPool.release(url);
        }
    }
}