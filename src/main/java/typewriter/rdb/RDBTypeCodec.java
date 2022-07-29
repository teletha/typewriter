/*
 * Copyright (C) 2022 Nameless Production Committee
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *          http://opensource.org/licenses/mit-license.php
 */
package typewriter.rdb;

import static typewriter.api.Constraint.ZonedDateTimeConstraint.*;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kiss.Extensible;
import kiss.I;
import kiss.Managed;
import kiss.Singleton;
import kiss.WiseBiFunction;
import kiss.model.Property;

@Managed(Singleton.class)
public abstract class RDBTypeCodec<T> implements Extensible {

    /** Built-in codecs. */
    private static final Map<Class, RDBTypeCodec> BULTINS = new HashMap();

    static {
        I.load(RDBTypeCodec.class);

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
        // register(String.class, ResultSet::getString);
        register(BigDecimal.class, ResultSet::getBigDecimal);
        // register(Date.class, ResultSet::getDate);
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
        BULTINS.put(type, new GenericCodec(type, decoder));
    }

    /**
     * Find {@link RDBTypeCodec} by type.
     * 
     * @param <T>
     * @param type
     * @return
     */
    public static <T> RDBTypeCodec<T> by(Class<T> type) {
        RDBTypeCodec<T> codec = BULTINS.get(type);
        if (codec != null) {
            return codec;
        }

        codec = I.find(RDBTypeCodec.class, type);
        if (codec != null) {
            return codec;
        }
        throw new Error(RDBTypeCodec.class.getSimpleName() + " for " + type.getName() + " is not found.");
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
        return by(property.model.type).decode(result, property.name);
    }

    /** The associtated types. */
    final List<Class> types;

    /** The associated names. */
    final List<String> names;

    protected RDBTypeCodec(Class type1) {
        this.types = List.of(type1);
        this.names = List.of("");
    }

    protected RDBTypeCodec(Class type1, String name1, Class type2, String name2) {
        this.types = List.of(type1, type2);
        this.names = List.of(name1, name2);
    }

    public abstract void encode(Map<String, Object> result, String name, T value);

    public abstract T decode(ResultSet result, String name) throws SQLException;

    /**
     * Generic codec.
     */
    static class GenericCodec<T> extends RDBTypeCodec<T> {

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
    static class StringCodec extends RDBTypeCodec<String> {

        /**
         * 
         */
        private StringCodec() {
            super(String.class);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void encode(Map<String, Object> result, String name, String value) {
            result.put(name, "'" + value + "'");
        }

        /**
         * {@inheritDoc}
         * 
         * @throws SQLException
         */
        @Override
        public String decode(ResultSet result, String name) throws SQLException {
            return result.getString(name);
        }
    }

    /**
     * Built-in codec.
     */
    static class DateCodec extends RDBTypeCodec<Date> {

        /**
         * 
         */
        private DateCodec() {
            super(Date.class);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void encode(Map<String, Object> result, String name, Date value) {
            result.put(name, value.getTime());
        }

        /**
         * {@inheritDoc}
         * 
         * @throws SQLException
         */
        @Override
        public Date decode(ResultSet result, String name) throws SQLException {
            return new Date(result.getLong(name));
        }
    }

    /**
     * Built-in codec.
     */
    static class LocalDateCodec extends RDBTypeCodec<LocalDate> {

        /**
         * 
         */
        private LocalDateCodec() {
            super(LocalDate.class);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void encode(Map<String, Object> result, String name, LocalDate value) {
            result.put(name, value.toEpochDay());
        }

        /**
         * {@inheritDoc}
         * 
         * @throws SQLException
         */
        @Override
        public LocalDate decode(ResultSet result, String name) throws SQLException {
            return LocalDate.ofEpochDay(result.getLong(name));
        }
    }

    /**
     * Built-in codec.
     */
    static class LocalTimeCodec extends RDBTypeCodec<LocalTime> {

        /**
         * 
         */
        private LocalTimeCodec() {
            super(LocalTime.class);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void encode(Map<String, Object> result, String name, LocalTime value) {
            result.put(name, value.toNanoOfDay());
        }

        /**
         * {@inheritDoc}
         * 
         * @throws SQLException
         */
        @Override
        public LocalTime decode(ResultSet result, String name) throws SQLException {
            return LocalTime.ofNanoOfDay(result.getLong(name));
        }
    }

    /**
     * Built-in codec.
     */
    static class LocalDateTimeCodec extends RDBTypeCodec<LocalDateTime> {

        /**
         * 
         */
        LocalDateTimeCodec() {
            super(LocalDateTime.class);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void encode(Map<String, Object> result, String name, LocalDateTime value) {
            result.put(name, value.toInstant(ZoneOffset.UTC).toEpochMilli());
        }

        /**
         * {@inheritDoc}
         * 
         * @throws SQLException
         */
        @Override
        public LocalDateTime decode(ResultSet result, String name) throws SQLException {
            Instant instant = Instant.ofEpochMilli(result.getLong(name));
            return instant.atOffset(ZoneOffset.UTC).toLocalDateTime();
        }
    }

    /**
     * Built-in codec.
     */
    static class ZonedDateTimeCodec extends RDBTypeCodec<ZonedDateTime> {

        ZonedDateTimeCodec() {
            super(long.class, "DATE", String.class, "ZONE");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void encode(Map<String, Object> result, String name, ZonedDateTime value) {
            result.put(name + "DATE", value.withZoneSameInstant(UTC).toInstant().toEpochMilli());
            result.put(name + "ZONE", "'" + value.getZone().getId() + "'");
        }

        /**
         * {@inheritDoc}
         * 
         * @throws SQLException
         */
        @Override
        public ZonedDateTime decode(ResultSet result, String name) throws SQLException {
            Instant date = Instant.ofEpochMilli(result.getLong(name + "DATE"));
            ZoneId zone = ZoneId.of(result.getString(name + "ZONE"));
            return ZonedDateTime.ofInstant(date, zone);
        }
    }
}