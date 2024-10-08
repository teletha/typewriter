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

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kiss.Extensible;
import kiss.I;
import kiss.Managed;
import kiss.Model;
import kiss.Property;
import kiss.Singleton;
import kiss.WiseBiFunction;
import kiss.Ⅱ;
import typewriter.api.Constraint.ZonedDateTimeConstraint;

@Managed(Singleton.class)
public abstract class RDBCodec<T> implements Extensible {

    /** Built-in codecs. */
    private static final Map<Class, RDBCodec> BULTINS = new HashMap();

    static {
        I.load(RDBCodec.class);

        register(String.class, ResultSet::getString);
        register(int.class, ResultSet::getInt);
        register(long.class, ResultSet::getLong);
        register(float.class, ResultSet::getFloat);
        register(double.class, ResultSet::getDouble);
        register(byte.class, ResultSet::getByte);
        register(short.class, ResultSet::getShort);
        register(boolean.class, ResultSet::getBoolean);
        register(Integer.class, ResultSet::getInt);
        register(Long.class, ResultSet::getLong);
        register(Float.class, ResultSet::getFloat);
        register(Double.class, ResultSet::getDouble);
        register(Byte.class, ResultSet::getByte);
        register(Short.class, ResultSet::getShort);
        register(Boolean.class, ResultSet::getBoolean);
        register(BigDecimal.class, ResultSet::getBigDecimal);
        register(java.sql.Date.class, ResultSet::getDate);
        register(Time.class, ResultSet::getTime);
    }

    /**
     * Registration helper.
     * 
     * @param <T>
     * @param type
     * @param decoder
     */
    private static <T> void register(Class<T> type, WiseBiFunction<ResultSet, String, T> decoder) {
        BULTINS.put(type, new GenericCodec<>(type, (result, name) -> {
            try {
                T value = decoder.apply(result, name);
                if (result.wasNull()) {
                    return null;
                } else {
                    return value;
                }
            } catch (Exception e) {
                return null;
            }
        }));
    }

    /**
     * Find {@link RDBCodec} by type.
     * 
     * @param <T>
     * @param model
     * @return
     */
    public static <T> RDBCodec<T> by(Model<T> model) {
        RDBCodec<T> codec = BULTINS.get(model.type);
        if (codec != null) {
            return codec;
        }

        codec = I.find(RDBCodec.class, model.type);
        if (codec != null) {
            return codec;
        }

        if (model.type == List.class) {
            return new ListCodec(model);
        }

        if (model.type.isEnum()) {
            return new EnumCodec(model.type);
        }

        throw new Error(RDBCodec.class.getSimpleName() + " for " + model.type + " is not found.");
    }

    /**
     * Helper method to decode by property.
     * 
     * @param property A target property.
     * @param result A result set.
     * @return A decoded value.
     * @throws SQLException
     */
    public static Object decode(Property property, ResultSet result) throws SQLException {
        return by(property.model).decode(result, property.name);
    }

    /** The associtated types. */
    final List<Class> types;

    /** The associated names. */
    final List<String> names;

    /**
     * Define codec.
     * 
     * @param type1
     */
    protected RDBCodec(Class type1) {
        this.types = List.of(type1);
        this.names = List.of("");
    }

    /**
     * Define codec.
     * 
     * @param type1
     * @param name1
     * @param type2
     * @param name2
     */
    protected RDBCodec(Class type1, String name1, Class type2, String name2) {
        this.types = List.of(type1, type2);
        this.names = List.of(name1, name2);
    }

    public abstract void encode(Map<String, Object> result, String name, T value);

    public abstract T decode(ResultSet result, String name) throws SQLException;

    /**
     * List up all types with its name.
     * 
     * @param defaultName
     */
    public final List<Ⅱ<String, Class>> info(String defaultName) {
        List<Ⅱ<String, Class>> set = new ArrayList();
        for (int i = 0; i < names.size(); i++) {
            set.add(I.pair(defaultName + names.get(i), types.get(i)));
        }
        return set;
    }

    /**
     * Generic codec.
     */
    static class GenericCodec<T> extends RDBCodec<T> {

        /** The actual date decoder. */
        private final WiseBiFunction<ResultSet, String, T> decoder;

        /**
         * Hide constructor.
         * 
         * @param decoder
         */
        GenericCodec(Class type, WiseBiFunction<ResultSet, String, T> decoder) {
            super(type);
            this.decoder = decoder;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void encode(Map<String, Object> result, String name, T value) {
            result.put(name, value);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public T decode(ResultSet result, String name) throws SQLException {
            return decoder.apply(result, name);
        }
    }

    /**
     * Built-in codec.
     */
    static class DateCodec extends RDBCodec<Date> {

        /**
         * 
         */
        private DateCodec() {
            super(long.class);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void encode(Map<String, Object> result, String name, Date value) {
            result.put(name, value == null ? null : value.getTime());
        }

        /**
         * {@inheritDoc}
         * 
         * @throws SQLException
         */
        @Override
        public Date decode(ResultSet result, String name) throws SQLException {
            long value = result.getLong(name);
            if (result.wasNull()) {
                return null;
            } else {
                return new Date(value);
            }
        }
    }

    /**
     * Built-in codec.
     */
    static class LocalDateCodec extends RDBCodec<LocalDate> {

        /**
         * 
         */
        private LocalDateCodec() {
            super(long.class);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void encode(Map<String, Object> result, String name, LocalDate value) {
            result.put(name, value == null ? null : value.toEpochDay());
        }

        /**
         * {@inheritDoc}
         * 
         * @throws SQLException
         */
        @Override
        public LocalDate decode(ResultSet result, String name) throws SQLException {
            long value = result.getLong(name);
            return result.wasNull() ? null : LocalDate.ofEpochDay(value);
        }
    }

    /**
     * Built-in codec.
     */
    static class LocalTimeCodec extends RDBCodec<LocalTime> {

        /**
         * 
         */
        private LocalTimeCodec() {
            super(long.class);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void encode(Map<String, Object> result, String name, LocalTime value) {
            result.put(name, value == null ? null : value.toNanoOfDay());
        }

        /**
         * {@inheritDoc}
         * 
         * @throws SQLException
         */
        @Override
        public LocalTime decode(ResultSet result, String name) throws SQLException {
            long value = result.getLong(name);
            return result.wasNull() ? null : LocalTime.ofNanoOfDay(value);
        }
    }

    /**
     * Built-in codec.
     */
    static class LocalDateTimeCodec extends RDBCodec<LocalDateTime> {

        /**
         * 
         */
        LocalDateTimeCodec() {
            super(long.class);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void encode(Map<String, Object> result, String name, LocalDateTime value) {
            result.put(name, value == null ? null : value.toInstant(ZoneOffset.UTC).toEpochMilli());
        }

        /**
         * {@inheritDoc}
         * 
         * @throws SQLException
         */
        @Override
        public LocalDateTime decode(ResultSet result, String name) throws SQLException {
            long value = result.getLong(name);
            if (result.wasNull()) {
                return null;
            } else {
                Instant instant = Instant.ofEpochMilli(value);
                return instant.atOffset(ZoneOffset.UTC).toLocalDateTime();
            }
        }
    }

    /**
     * Built-in codec.
     */
    static class OffsetDateTimeCodec extends RDBCodec<OffsetDateTime> {

        static final String POSTFIX = "_date";

        OffsetDateTimeCodec() {
            super(long.class, POSTFIX, int.class, "_offset");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void encode(Map<String, Object> result, String name, OffsetDateTime value) {
            result.put(name + POSTFIX, value == null ? null : value.toInstant().toEpochMilli());
            result.put(name + "_offset", value == null ? null : value.getOffset().getTotalSeconds());
        }

        /**
         * {@inheritDoc}
         * 
         * @throws SQLException
         */
        @Override
        public OffsetDateTime decode(ResultSet result, String name) throws SQLException {
            long value = result.getLong(name + POSTFIX);
            if (result.wasNull()) {
                return null;
            } else {
                Instant date = Instant.ofEpochMilli(value);
                ZoneOffset offset = ZoneOffset.ofTotalSeconds(result.getInt(name + "_offset"));
                return OffsetDateTime.ofInstant(date, offset);
            }
        }
    }

    /**
     * Built-in codec.
     */
    static class ZonedDateTimeCodec extends RDBCodec<ZonedDateTime> {

        static final String POSTFIX = "_date";

        ZonedDateTimeCodec() {
            super(long.class, POSTFIX, String.class, "_zone");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void encode(Map<String, Object> result, String name, ZonedDateTime value) {
            result.put(name + POSTFIX, value == null ? null
                    : value.withZoneSameInstant(ZonedDateTimeConstraint.UTC).toInstant().toEpochMilli());
            result.put(name + "_zone", value == null ? null : value.getZone().getId());
        }

        /**
         * {@inheritDoc}
         * 
         * @throws SQLException
         */
        @Override
        public ZonedDateTime decode(ResultSet result, String name) throws SQLException {
            long value = result.getLong(name + POSTFIX);
            if (result.wasNull()) {
                return null;
            } else {
                Instant date = Instant.ofEpochMilli(value);
                ZoneId zone = ZoneId.of(result.getString(name + "_zone"));
                return ZonedDateTime.ofInstant(date, zone);
            }
        }
    }

    /**
     * Built-in codec.
     */
    static class ListCodec<T> extends RDBCodec<List<T>> {

        /** The specialized list model. */
        private final Model<List<T>> model;

        private ListCodec(Model<List<T>> model) {
            super(List.class);

            this.model = model;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void encode(Map<String, Object> result, String name, List<T> value) {
            StringBuilder buffer = new StringBuilder();
            I.write(model, value, buffer);
            result.put(name, buffer.toString());
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public List<T> decode(ResultSet result, String name) throws SQLException {
            return I.json(result.getString(name)).as(model);
        }
    }

    /**
     * Built-in codec.
     */
    static class EnumCodec<T> extends RDBCodec<T> {

        private final Class<T> type;

        private EnumCodec(Class<T> type) {
            super(String.class);

            this.type = type;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void encode(Map<String, Object> result, String name, T value) {
            result.put(name, I.transform(value, String.class));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public T decode(ResultSet result, String name) throws SQLException {
            return I.transform(result.getString(name), type);
        }
    }
}