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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.model.changestream.FullDocument;

import kiss.I;
import kiss.Signal;
import kiss.model.Model;
import kiss.model.Property;
import typewriter.api.Deletable;
import typewriter.api.Operatable;
import typewriter.api.QueryExecutor;
import typewriter.api.Restorable;
import typewriter.api.Specifier;
import typewriter.api.Updatable;
import typewriter.api.model.IdentifiableModel;

public class Mongo<M extends IdentifiableModel> extends QueryExecutor<M, Signal<M>, MongoQuery<M>>
        implements Operatable<M>, Updatable<M>, Deletable<M>, Restorable<M> {

    /** The primary key. */
    private static final String PrimaryKey = "_id";

    /** The local identical key. */
    private static final String IdenticalKey = "id";

    /** The decoder manager. */
    private static final Map<Class, BiFunction<Document, String, ?>> decoders = new HashMap();

    /** The default decorder. */
    private static final BiFunction<Document, String, ?> defaultDecoder = Document::get;

    // register built-in decoders
    static {
        decoders.put(LocalDate.class, (doc, key) -> {
            return LocalDate.ofInstant(doc.getDate(key).toInstant(), ZoneId.systemDefault());
        });
        decoders.put(LocalTime.class, (doc, key) -> {
            return LocalTime.ofInstant(doc.getDate(key).toInstant(), ZoneId.systemDefault());
        });
        decoders.put(LocalDateTime.class, (doc, key) -> {
            return LocalDateTime.ofInstant(doc.getDate(key).toInstant(), ZoneId.systemDefault());
        });
    }

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
        this(model, null);
    }

    /**
     * @param model
     * @param client
     */
    Mongo(Class<M> model, MongoClient client) {
        this.model = Model.of(model);
        this.db = Objects.requireNonNullElse(client, Client).getDatabase("master");
        this.collection = db.getCollection(model.getName().replace('$', '#'));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long count() {
        return collection.estimatedDocumentCount();
    }

    /**
     * Find model by id.
     * 
     * @param id
     * @return
     */
    public Signal<M> findBy(long id) {
        return findBy(Filters.eq(PrimaryKey, id));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Signal<M> findBy(MongoQuery<M> query) {
        return findBy(query.build());
    }

    /**
     * Find by your query.
     * 
     * @param query
     * @return
     */
    private Signal<M> findBy(Bson query) {
        return new Signal<>((observer, disposer) -> {
            try {
                FindIterable<Document> founds = collection.find(query);
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
    public Signal<M> restore(M model, Specifier<M, ?>... specifiers) {
        return new Signal<M>((observer, disposer) -> {
            try {
                Document doc = collection.find(identify(model)).first();
                if (doc != null) {
                    observer.accept(decode(doc, model));
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
    public void delete(M model, Specifier<M, ?>... specifiers) {
        if (model == null) {
            return;
        }

        if (specifiers == null || specifiers.length == 0) {
            // delete model
            collection.deleteOne(identify(model));
        } else {
            // delete properties
            List<Bson> operations = new ArrayList();
            for (Specifier<M, ?> specifier : specifiers) {
                if (specifier != null) {
                    operations.add(Updates.unset(specifier.propertyName()));
                }
            }
            collection.updateOne(identify(model), Updates.combine(operations));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(M model, Specifier<M, ?>... specifiers) {
        if (model == null) {
            return;
        }

        if (specifiers == null || specifiers.length == 0) {
            // update model
            collection.replaceOne(identify(model), encode(model), new ReplaceOptions().upsert(true));
        } else {
            // update properties
            Model m = Model.of(model);
            List<Bson> operations = new ArrayList();
            for (Specifier<M, ?> specifier : specifiers) {
                if (specifier != null) {
                    String name = specifier.propertyName();
                    Property property = m.property(name);

                    operations.add(Updates.set(name, m.get(model, property)));
                }
            }
            collection.updateOne(identify(model), Updates.combine(operations), new UpdateOptions().upsert(true));
        }
    }

    /**
     * Watch the property modification.
     * 
     * @return
     */
    public Signal<M> watch() {
        return new Signal<M>((observer, disposer) -> {
            try {
                MongoCursor<ChangeStreamDocument<Document>> iterator = collection.watch()
                        .fullDocument(FullDocument.UPDATE_LOOKUP)
                        .iterator();

                while (iterator.hasNext() && !disposer.isDisposed()) {
                    observer.accept(decode(iterator.next().getFullDocument()));
                }
                observer.complete();
            } catch (Throwable e) {
                observer.error(e);
            }
            return disposer;
        }).subscribeOn(I::schedule);
    }

    /**
     * Create identical filter.
     * 
     * @param model
     * @return
     */
    private Bson identify(M model) {
        return Filters.eq(PrimaryKey, model.getId());
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
        return decode(doc, I.make(model.type));
    }

    /**
     * Decode from {@link Document}.
     * 
     * @param doc
     * @return
     */
    private M decode(Document doc, M object) {
        for (Entry<String, Object> entry : doc.entrySet()) {
            String key = entry.getKey();
            String localKey = key;
            if (localKey.equals(PrimaryKey)) {
                localKey = IdenticalKey;
            }

            Property property = model.property(localKey);
            Class type = property.model.type;
            Object value = decoders.getOrDefault(type, defaultDecoder).apply(doc, key);

            model.set(object, property, value);
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
