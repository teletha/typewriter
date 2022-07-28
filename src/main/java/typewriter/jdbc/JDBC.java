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

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import kiss.Signal;
import kiss.WiseFunction;
import kiss.model.Model;
import typewriter.api.QueryExecutor;
import typewriter.api.Queryable;
import typewriter.api.model.IdentifiableModel;

public abstract class JDBC<M extends IdentifiableModel, Q extends Queryable<M, Q>> extends QueryExecutor<M, Signal<M>, Q> {

    /** The connection pool. */
    protected static final Map<String, Connection> CONNECTION_POOL = new ConcurrentHashMap();

    /** The document model. */
    protected final Model<M> model;

    /** The table name. */
    protected final String tableName;

    /** The reusable DB connection. */
    protected final Connection connection;

    /**
     * @param type
     * @param url
     */
    public JDBC(Class<M> type, String url) {
        this.model = Model.of(type);
        this.tableName = '"' + type.getName() + '"';
        this.connection = CONNECTION_POOL.computeIfAbsent(url, (WiseFunction<String, Connection>) DriverManager::getConnection);
    }

}
