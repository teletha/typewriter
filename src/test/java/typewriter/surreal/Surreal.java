/*
 * Copyright (C) 2025 The TYPEWRITER Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package typewriter.surreal;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.surrealdb.driver.SyncSurrealDriver;

import kiss.Signal;
import typewriter.api.Identifiable;
import typewriter.api.QueryExecutor;
import typewriter.mongo.Mongo;

public class Surreal<M extends Identifiable> extends QueryExecutor<M, Signal<M>, SurrealQuery<M>, Surreal<M>> {

    /** The reusabel {@link Mongo} cache. */
    private static final Map<Class, Surreal> Cache = new ConcurrentHashMap();

    private SyncSurrealDriver driver;

    Surreal(Class<M> type) {
        SurrealManager.with().establish().waitForTerminate().to(connection -> {
            driver = new SyncSurrealDriver(connection);
            driver.use("test", "ns");
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Signal<M> findBy(SurrealQuery<M> query) {
        System.out.println(query);
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(M model) {
        driver.create("ok", model);
        System.out.println("Save " + model);
    }

    /**
     * Get the collection.
     * 
     * @param <M>
     * @param model The model type.
     * @return
     */
    public static <M extends Identifiable> Surreal<M> of(Class<M> model) {
        return Cache.computeIfAbsent(model, key -> new Surreal(key));
    }
}