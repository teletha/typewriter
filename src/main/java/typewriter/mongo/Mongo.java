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

import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.bson.Document;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.model.changestream.FullDocument;

import kiss.I;
import kiss.Signal;
import kiss.model.Model;
import kiss.model.Property;
import typewriter.IdentifiableModel;
import typewriter.api.QueryExecutor;

public class Mongo<M extends IdentifiableModel> extends QueryExecutor<M, Signal<M>, MongoQuery<M>> {

    /** The primary key. */
    private static final String PrimaryKey = "_id";

    /** The local identical key. */
    private static final String IdenticalKey = "id";

    /** The reusabel {@link Mongo} cache. */
    private static final Map<Class, Mongo> Cache = new ConcurrentHashMap();

    /** The singleton. */
    private static final MongoClient Client = MongoClients.create(I.env("mongodb", "mongodb://localhost:27017"));

    /** The document model. */
    private final Model<M> model;

    /** The associated database. */
    private final MongoDatabase db;

    /** The associated collection. */
    private final MongoCollection<Document> collection;

    /**
     * @param model
     */
    private Mongo(Class<M> model) {
        this(null, model);
    }

    /**
     * @param client
     * @param model
     */
    Mongo(MongoClient client, Class<M> model) {
        this.model = Model.of(model);
        this.db = Objects.requireNonNullElse(client, Client).getDatabase("master");
        this.collection = db.getCollection(model.getName().replace('$', '#'));
    }

    /**
     * Find model by id.
     * 
     * @param id
     * @return
     */
    public Signal<M> findBy(int id) {
        return new Signal<>((observer, disposer) -> {
            try {
                FindIterable<Document> founds = collection.find(Filters.eq(PrimaryKey, id));
                for (Document found : founds) {
                    if (!disposer.isDisposed()) {
                        observer.accept(decode(found));
                    }
                }
                observer.complete();
            } catch (Throwable e) {
                observer.error(e);
            }
            return disposer;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Signal<M> findBy(MongoQuery<M> query) {
        return new Signal<>((observer, disposer) -> {
            try {
                FindIterable<Document> founds = collection.find(query.build());
                for (Document found : founds) {
                    if (!disposer.isDisposed()) {
                        observer.accept(decode(found));
                    }
                }
                observer.complete();
            } catch (Throwable e) {
                observer.error(e);
            }
            return disposer;
        });
    }

    public Signal<M> watch() {
        return new Signal<>((observer, disposer) -> {
            MongoCursor<ChangeStreamDocument<Document>> iterator = collection.watch().fullDocument(FullDocument.UPDATE_LOOKUP).iterator();
            while (iterator.hasNext() && !disposer.isDisposed()) {
                ChangeStreamDocument<Document> next = iterator.next();
                observer.accept(decode(next.getFullDocument()));
            }
            observer.complete();
            return disposer;
        });
    }

    /**
     * Delete model.
     * 
     * @param model
     */
    public void delete(M model) {
        if (model == null) {
            return;
        }

        collection.deleteOne(Filters.eq(PrimaryKey, model.id));
    }

    /**
     * Update model.
     * 
     * @param model
     */
    public void update(M model) {
        if (model == null) {
            return;
        }

        ReplaceOptions o = new ReplaceOptions().upsert(true);

        collection.replaceOne(Filters.eq(PrimaryKey, model.id), encode(model), o);
    }

    /**
     * Encode to {@link Document}.
     * 
     * @param object
     * @return
     */
    private Document encode(M object) {
        Document doc = new Document();

        model.walk(object, (m, p, v) -> {
            String name = p.name;
            if (name.equals(IdenticalKey)) {
                name = PrimaryKey;
            }
            doc.put(name, v);
        });

        return doc;
    }

    /**
     * Decode from {@link Document}.
     * 
     * @param doc
     * @return
     */
    private M decode(Document doc) {
        M object = I.make(model.type);

        for (Entry<String, Object> entry : doc.entrySet()) {
            String key = entry.getKey();
            String localKey = key;
            if (localKey.equals(PrimaryKey)) {
                localKey = IdenticalKey;
            }

            Property property = model.property(localKey);
            model.set(object, property, doc.get(key));
        }

        return object;
    }

    /**
     * Get the collection.
     * 
     * @param <M>
     * @param model The model type.
     * @return
     */
    public static <M extends IdentifiableModel> Mongo<M> of(Class<M> model) {
        return Cache.computeIfAbsent(model, key -> new Mongo(key));
    }

}
