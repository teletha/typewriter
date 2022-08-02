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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Sorts;

import kiss.I;
import typewriter.api.Constraint;
import typewriter.api.Constraint.DateConstraint;
import typewriter.api.Constraint.LocalDateConstraint;
import typewriter.api.Constraint.LocalDateTimeConstraint;
import typewriter.api.Constraint.LocalTimeConstraint;
import typewriter.api.Constraint.NumericConstraint;
import typewriter.api.Constraint.StringConstraint;
import typewriter.api.Constraint.TypeConstraint;
import typewriter.api.Constraint.ZonedDateTimeConstraint;
import typewriter.api.Queryable;
import typewriter.api.Specifier.BooleanSpecifier;
import typewriter.api.Specifier.CharSpecifier;
import typewriter.api.Specifier.DateSpecifier;
import typewriter.api.Specifier.LocalDateSpecifier;
import typewriter.api.Specifier.LocalDateTimeSpecifier;
import typewriter.api.Specifier.LocalTimeSpecifier;
import typewriter.api.Specifier.NumericSpecifier;
import typewriter.api.Specifier.StringSpecifier;
import typewriter.api.Specifier.ZonedDateTimeSpecifier;
import typewriter.mongo.MongoConstraint.ForDate;
import typewriter.mongo.MongoConstraint.ForLocalDate;
import typewriter.mongo.MongoConstraint.ForLocalDateTime;
import typewriter.mongo.MongoConstraint.ForLocalTime;
import typewriter.mongo.MongoConstraint.ForNumeric;
import typewriter.mongo.MongoConstraint.ForString;
import typewriter.mongo.MongoConstraint.ForZonedDateTime;
import typewriter.mongo.MongoConstraint.GenericType;

/**
 * {@link Queryable} for mongodb.
 */
public class MongoQuery<M> implements Queryable<M, MongoQuery<M>> {

    /** The all constraint set. */
    protected final List<MongoConstraint<?, ?>> constraints = new ArrayList();

    /** The limit size. */
    private int limit;

    /** The offset position. */
    private int offset;

    /** The sorting property. */
    private String sort;

    /** The sorting order. */
    private boolean ascending;

    /**
     * Hide constructor.
     */
    MongoQuery() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MongoQuery<M> findBy(Constraint constraint) {
        if (constraint != null) {
            constraints.add((MongoConstraint<?, ?>) constraint);
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <N extends Number> MongoQuery<M> findBy(NumericSpecifier<M, N> specifier, UnaryOperator<NumericConstraint<N>> constraint) {
        return findBy(constraint.apply(new ForNumeric(specifier)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MongoQuery<M> findBy(CharSpecifier<M> specifier, UnaryOperator<TypeConstraint<Character>> constraint) {
        return findBy(constraint.apply(new GenericType(specifier)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MongoQuery<M> findBy(BooleanSpecifier<M> specifier, UnaryOperator<TypeConstraint<Boolean>> constraint) {
        return findBy(constraint.apply(new GenericType(specifier)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MongoQuery<M> findBy(StringSpecifier<M> specifier, UnaryOperator<StringConstraint> constraint) {
        return findBy(constraint.apply(new ForString(specifier)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MongoQuery<M> findBy(DateSpecifier<M> specifier, UnaryOperator<DateConstraint> constraint) {
        return findBy(constraint.apply(new ForDate(specifier)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MongoQuery<M> findBy(LocalDateSpecifier<M> specifier, UnaryOperator<LocalDateConstraint> constraint) {
        return findBy(constraint.apply(new ForLocalDate(specifier)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MongoQuery<M> findBy(LocalTimeSpecifier<M> specifier, UnaryOperator<LocalTimeConstraint> constraint) {
        return findBy(constraint.apply(new ForLocalTime(specifier)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MongoQuery<M> findBy(LocalDateTimeSpecifier<M> specifier, UnaryOperator<LocalDateTimeConstraint> constraint) {
        return findBy(constraint.apply(new ForLocalDateTime(specifier)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MongoQuery<M> findBy(ZonedDateTimeSpecifier<M> specifier, UnaryOperator<ZonedDateTimeConstraint> constraint) {
        return findBy(constraint.apply(new ForZonedDateTime(specifier)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MongoQuery<M> limit(long size) {
        if (0 < size) {
            this.limit = (int) size;
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MongoQuery<M> offset(long position) {
        if (0 < position) {
            this.offset = (int) position;
        }
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <N extends Number> MongoQuery<M> sortBy(NumericSpecifier<M, N> specifier, boolean ascending) {
        this.sort = specifier.propertyName();
        this.ascending = ascending;

        return this;
    }

    /**
     * Build query.
     * 
     * @param collection
     * @return
     */
    FindIterable<Document> buildQuery(MongoCollection collection) {
        FindIterable finder = collection.find();
        finder = finder.filter(new AndFilter(I.signal(constraints).flatIterable(c -> c.filters).toList()));
        if (0 < limit) finder = finder.limit(limit);
        if (0 < offset) finder = finder.skip(offset);
        if (sort != null && !sort.isBlank()) finder = finder.sort(ascending ? Sorts.ascending(sort) : Sorts.descending(sort));

        return finder;
    }

    /**
     * 
     */
    private static class AndFilter implements Bson {

        /** The combination. */
        private final Iterable<Bson> filters;

        /**
         * Hide constructor.
         * 
         * @param filters
         */
        private AndFilter(Iterable<Bson> filters) {
            this.filters = filters;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public <TDocument> BsonDocument toBsonDocument(Class<TDocument> documentClass, CodecRegistry codecRegistry) {
            BsonDocument andRenderable = new BsonDocument();

            for (Bson filter : filters) {
                for (Map.Entry<String, BsonValue> element : filter.toBsonDocument(documentClass, codecRegistry).entrySet()) {
                    addClause(andRenderable, element);
                }
            }
            return andRenderable;
        }

        private void addClause(final BsonDocument document, final Map.Entry<String, BsonValue> clause) {
            String key = clause.getKey();

            if (key.equals("$and")) {
                for (BsonValue value : clause.getValue().asArray()) {
                    for (Map.Entry<String, BsonValue> element : value.asDocument().entrySet()) {
                        addClause(document, element);
                    }
                }
            } else if (document.size() == 1 && document.keySet().iterator().next().equals("$and")) {
                document.get("$and").asArray().add(new BsonDocument(key, clause.getValue()));
            } else if (document.containsKey(key)) {
                if (!clause.getKey().equals("$expr") && document.get(key).isDocument() && clause.getValue().isDocument()) {
                    BsonDocument existingClauseValue = document.get(key).asDocument();
                    BsonDocument clauseValue = clause.getValue().asDocument();
                    if (keysIntersect(clauseValue, existingClauseValue)) {
                        promoteRenderableToDollarForm(document, clause);
                    } else {
                        existingClauseValue.putAll(clauseValue);
                    }
                } else {
                    promoteRenderableToDollarForm(document, clause);
                }
            } else {
                document.append(key, clause.getValue());
            }
        }

        private boolean keysIntersect(BsonDocument first, BsonDocument second) {
            for (String name : first.keySet()) {
                if (second.containsKey(name)) {
                    return true;
                }
            }
            return false;
        }

        private void promoteRenderableToDollarForm(BsonDocument document, Map.Entry<String, BsonValue> clause) {
            BsonArray clauses = new BsonArray();
            for (Map.Entry<String, BsonValue> queryElement : document.entrySet()) {
                clauses.add(new BsonDocument(queryElement.getKey(), queryElement.getValue()));
            }
            clauses.add(new BsonDocument(clause.getKey(), clause.getValue()));
            document.clear();
            document.put("$and", clauses);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean equals(final Object o) {
            return o instanceof AndFilter and ? filters.equals(and.filters) : false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return filters.hashCode();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return "And Filter{" + "filters=" + filters + '}';
        }
    }
}