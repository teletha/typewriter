/*
 * Copyright (C) 2022 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package typewriter.mongo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

import de.bwaldvogel.mongo.MongoServer;
import de.bwaldvogel.mongo.backend.memory.MemoryBackend;

public class MongoTestSupport {

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
     * Create empty database.
     * 
     * @param <M>
     * @param type
     * @return
     */
    protected <M extends DerivableModel> Mongo<M> createEmptyDB(Class<M> type) {
        return new Mongo<>(type, client);
    }
}
