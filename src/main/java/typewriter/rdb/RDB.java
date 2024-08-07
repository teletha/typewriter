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
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.UnaryOperator;

import kiss.I;
import kiss.Managed;
import kiss.Model;
import kiss.Property;
import kiss.Signal;
import kiss.WiseFunction;
import kiss.WiseSupplier;
import typewriter.api.Identifiable;
import typewriter.api.QueryExecutor;
import typewriter.api.Specifier;
import typewriter.api.model.IdentifiableModel;
import typewriter.duck.DuckDB;
import typewriter.duck.DuckModel;
import typewriter.h2.H2;
import typewriter.h2.H2Model;
import typewriter.maria.MariaDB;
import typewriter.maria.MariaModel;
import typewriter.query.AVGOption;
import typewriter.sqlite.SQLite;
import typewriter.sqlite.SQLiteModel;

/**
 * Data Access Object for RDBMS.
 */
public class RDB<M extends Identifiable> extends QueryExecutor<M, Signal<M>, RDBQuery<M>, RDB<M>> {

    /** The supported RDBMS. */
    public static final Dialect H2 = I.make(H2.class);

    /** The supported RDBMS. */
    public static final Dialect SQLite = I.make(SQLite.class);

    /** The supported RDBMS. */
    public static final Dialect MariaDB = I.make(MariaDB.class);

    /** The supported RDBMS. */
    public static final Dialect DuckDB = I.make(DuckDB.class);

    /** The reusable DAO cache. */
    private static final Map<Dialect, Map<String, RDB>> DAO = Map
            .of(H2, new ConcurrentHashMap(), SQLite, new ConcurrentHashMap(), MariaDB, new ConcurrentHashMap(), DuckDB, new ConcurrentHashMap());

    /** The document model. */
    protected final Model<M> model;

    /** The model name. */
    protected final String name;

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
    public RDB(Class<M> type, String name, Dialect dialect, String url) {
        this(Model.of(type), name, dialect, ConnectionPool.by(url));

        dialect.createDatabase(url);

        new SQL<>(this).write(dialect.commandCreateTable(tableName, model)).execute();

        // collect table metadata
        Map<String, String> rows = new HashMap();
        try (Connection connection = provider.get()) {
            DatabaseMetaData meta = connection.getMetaData();
            ResultSet columns = meta.getColumns(null, null, tableName.replaceAll(dialect.quote(), ""), null);

            while (columns.next()) {
                rows.put(columns.getString("COLUMN_NAME"), columns.getString("TYPE_NAME"));
            }
        } catch (SQLException e) {
            throw I.quiet(e);
        }

        // validate property
        I.signal(model.properties())
                .flatIterable(x -> RDBCodec.by((Model<?>) x.model).info(x.name))
                .take(x -> rows.get(x.ⅰ) == null)
                .to(x -> {
                    new SQL<>(this).write(dialect.commandAddRow(tableName, x.ⅰ, x.ⅱ)).execute();
                });
    }

    /**
     * Data Access Object.
     * 
     * @param model A target model.
     * @param dialect A dialect of RDBMS.
     * @param provider A user specified backend connection.
     * @param createTable Should I create table?
     */
    private RDB(Model<M> model, String name, Dialect dialect, WiseSupplier<Connection> provider) {
        Managed managed = model.type.getAnnotation(Managed.class);
        if (managed != null && !managed.name().isEmpty()) {
            name = managed.name();
        }

        this.model = model;
        this.name = name;
        this.tableName = dialect.quote() + name + dialect.quote();
        this.dialect = dialect;
        this.provider = provider;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected RDBQuery<M> createQueryable() {
        return new RDBQuery(dialect);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long count() {
        return new SQL<>(this).select("count(*)").from(tableName).qurey().map(result -> result.getLong(1)).to().exact();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <V> Signal<V> distinct(Specifier<M, V> specifier) {
        Property property = model.property(specifier.propertyName(dialect));
        return new SQL<>(this).write("SELECT DISTINCT", property.name).from(tableName).qurey().map(result -> (V) decode(property, result));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <C extends Comparable> C min(Specifier<M, C> specifier) {
        Property property = model.property(specifier.propertyName(dialect));
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
        Property property = model.property(specifier.propertyName(dialect));
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
    public <N extends Number> Signal<Double> avg(Specifier<M, N> specifier, UnaryOperator<AVGOption<M>> option) {
        return new SQL<>(this).write("SELECT").avg(specifier, option).as("N").from(tableName).qurey().map(result -> result.getDouble("N"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <N extends Number> N sum(Specifier<M, N> specifier) {
        Property property = model.property(specifier.propertyName(dialect));
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
                .qurey(result -> decode(model, model.properties(), I.make(model.type), result));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Signal<M> restore(M instance, Specifier<M, ?>... specifiers) {
        if (instance == null) {
            return I.signal();
        }

        List<Property> properties = names(dialect, specifiers).map(model::property).or(I.signal(model.properties())).toList();

        return new SQL<>(this).write("SELECT")
                .names(properties)
                .from(tableName)
                .where(instance)
                .qurey(result -> decode(model, properties, instance, result));
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
                    .setNull(names(dialect, specifiers).map(model::property).toList())
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
            new SQL<>(this).write(dialect.commandReplace(), tableName)
                    .write("(")
                    .names(model.properties())
                    .write(")")
                    .values(instance)
                    .execute();
        } else {
            // update properties
            new SQL<>(this).write(dialect.commandUpdate(), tableName)
                    .set(names(dialect, specifiers).map(model::property).toList(), instance)
                    .where(instance)
                    .execute();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateAll(Iterable<M> models) {
        new SQL<>(this).write(dialect.commandReplace(), tableName).write("(").names(model.properties()).write(")").values(models).execute();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized <R> R transactWith(WiseFunction<RDB<M>, R> operation) {
        Connection connection = provider.get();
        try {
            connection.setAutoCommit(false);

            R result = operation.apply(new RDB<>(model, name, dialect, () -> connection));
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
     * Execute raw query.
     * 
     * @return
     */
    public SQL<M> query() {
        return new SQL<>(this);
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
        try {
            RDBCodec<V> codec = RDBCodec.by(property.model);
            return codec.decode(result, property.name);
        } catch (Throwable e) {
            return null;
        }
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
            try {
                RDBCodec codec = RDBCodec.by(property.model);
                model.set(instance, property, codec.decode(result, property.name));
            } catch (Throwable e) {
                // ignore
            }
        }
        return instance;
    }

    /**
     * Get the collection using {@link Dialect} found in the environmental information.
     * 
     * @param <M>
     * @param type The model type.
     * @return
     */
    public static <M extends IdentifiableModel> RDB<M> of(Class<M> type, Object... qualifiers) {
        return of(type, null, qualifiers);
    }

    /**
     * Get the collection.
     * 
     * @param <M>
     * @param type The model type.
     * @return
     */
    public static <M extends IdentifiableModel> RDB<M> of(Class<M> type, Dialect dialect, Object... qualifiers) {
        if (dialect == null) {
            if (SQLiteModel.class.isAssignableFrom(type)) {
                dialect = SQLite;
            } else if (H2Model.class.isAssignableFrom(type)) {
                dialect = H2;
            } else if (MariaModel.class.isAssignableFrom(type)) {
                dialect = MariaDB;
            } else if (DuckModel.class.isAssignableFrom(type)) {
                dialect = DuckDB;
            } else if (I.env("typewriter.sqlite") != null) {
                dialect = SQLite;
            } else if (I.env("typewriter.h2") != null) {
                dialect = H2;
            } else if (I.env("typewriter.mariadb") != null) {
                dialect = MariaDB;
            } else if (I.env("typewriter.duckdb") != null) {
                dialect = DuckDB;
            } else if (has("org.sqlite.JDBC")) {
                dialect = SQLite;
            } else if (has("org.h2.Driver")) {
                dialect = H2;
            } else if (has("ch.vorburger.mariadb4j.DB")) {
                dialect = MariaDB;
            } else if (has("org.duckdb.DuckDBDriver")) {
                dialect = DuckDB;
            } else {

            }
        }

        Dialect detected = dialect;
        StringJoiner joiner = new StringJoiner("_");
        joiner.add(type.getName());
        for (Object qualifer : qualifiers) {
            joiner.add(String.valueOf(qualifer));
        }
        String name = joiner.toString();

        return DAO.get(detected).computeIfAbsent(name, key -> {
            return new RDB(type, name, detected, detected.configureLocation(null));
        });
    }

    /**
     * Detect DB by class.
     * 
     * @param fqcn
     * @return
     */
    private static boolean has(String fqcn) {
        try {
            ClassLoader.getSystemClassLoader().loadClass(fqcn);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * Get the collection by {@link Signal}.
     * 
     * @param <M>
     * @param type The model type.
     * @return
     */
    public static <M extends IdentifiableModel> Signal<RDB<M>> by(Class<M> type, Object... quealifers) {
        return by(type, null, quealifers);
    }

    /**
     * Get the collection by {@link Signal}.
     * 
     * @param <M>
     * @param type The model type.
     * @return
     */
    public static <M extends IdentifiableModel> Signal<RDB<M>> by(Class<M> type, Dialect dialect, Object... qualifiers) {
        return new Signal<>((observer, disposer) -> {
            observer.accept(of(type, dialect, qualifiers));
            observer.complete();
            return disposer;
        });
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