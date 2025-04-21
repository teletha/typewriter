/*
 * Copyright (C) 2025 The TYPEWRITER Development Team
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          https://opensource.org/licenses/MIT
 */
package typewriter.mongo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;
import kiss.Signal;
import typewriter.api.Identifiable;
import typewriter.api.QueryExecutor;
import typewriter.api.Testable;

public class MongoTestBase implements Testable {

    /** The mocked mongodb server. */
    private MongoServer server;

    /** The mongo client for test. */
    private MongoClient client;

    @BeforeEach
    void setup() {
        server = new MongoServer(new MemoryBackend());
        client = MongoClients.create("mongodb:/" + server.bind());
    }

    @AfterEach
    void teardown() {
        client.close();
        server.shutdownNow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <M extends Identifiable, Q extends QueryExecutor<M, Signal<M>, ?, Q>> Q createEmptyDB(Class<M> type, String name) {
        return (Q) new Mongo(type, client, name);
    }

}