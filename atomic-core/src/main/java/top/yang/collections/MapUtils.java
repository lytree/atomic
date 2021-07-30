/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package top.yang.collections;


import javax.xml.crypto.dsig.keyinfo.KeyValue;
import java.io.PrintStream;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import top.yang.tree.Tree;


/**
 * Provides utility methods and decorators for {@link Map} and {@link SortedMap} instances.
 * <p>
 * It contains various type safe methods as well as other useful features like deep copying.
 * </p>
 * <p>
 * It also provides the following decorators:
 * </p>
 * </ul>
 *
 * @since 1.0
 */
@SuppressWarnings("deprecation")
public class MapUtils {

    /**
     * String used to indent the verbose and debug Map prints.
     */
    private static final String INDENT_STRING = "    ";

    /**
     * Applies the {@code getFunction} and returns its result if non-null, if null returns the result of applying the
     * default function.
     *
     * @param <K>             The key type.
     * @param <R>             The result type.
     * @param map             The map to query.
     * @param key             The key into the map.
     * @param getFunction     The get function.
     * @param defaultFunction The function to provide a default value.
     * @return The result of applying a function.
     */
    private static <K, R> R applyDefaultFunction(final Map<? super K, ?> map, final K key,
                                                 final BiFunction<Map<? super K, ?>, K, R> getFunction, final Function<K, R> defaultFunction) {
        return applyDefaultFunction(map, key, getFunction, defaultFunction, null);
    }

    /**
     * Applies the {@code getFunction} and returns its result if non-null, if null returns the result of applying the
     * default function.
     *
     * @param <K>             The key type.
     * @param <R>             The result type.
     * @param map             The map to query.
     * @param key             The key into the map.
     * @param getFunction     The get function.
     * @param defaultFunction The function to provide a default value.
     * @param defaultValue    The default value.
     * @return The result of applying a function.
     */
    private static <K, R> R applyDefaultFunction(final Map<? super K, ?> map, final K key,
                                                 final BiFunction<Map<? super K, ?>, K, R> getFunction, final Function<K, R> defaultFunction,
                                                 final R defaultValue) {
        R value = map != null && getFunction != null ? getFunction.apply(map, key) : null;
        if (value == null) {
            value = defaultFunction != null ? defaultFunction.apply(key) : null;
        }
        return value != null ? value : defaultValue;
    }

    /**
     * Applies the {@code getFunction} and returns its result if non-null, if null returns the {@code defaultValue}.
     *
     * @param <K>          The key type.
     * @param <R>          The result type.
     * @param map          The map to query.
     * @param key          The key into the map.
     * @param getFunction  The get function.
     * @param defaultValue The default value.
     * @return The result of applying a function.
     */
    private static <K, R> R applyDefaultValue(final Map<? super K, ?> map, final K key,
                                              final BiFunction<Map<? super K, ?>, K, R> getFunction, final R defaultValue) {
        final R value = map != null && getFunction != null ? getFunction.apply(map, key) : null;
        return value == null ? defaultValue : value;
    }


    /**
     * Returns an immutable empty map if the argument is {@code null}, or the argument itself otherwise.
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param map the map, possibly {@code null}
     * @return an empty map if the argument is {@code null}
     */
    public static <K, V> Map<K, V> emptyIfNull(final Map<K, V> map) {
        return map == null ? Collections.<K, V>emptyMap() : map;
    }

    /**
     * Gets a Boolean from a Map in a null-safe manner.
     * <p>
     * If the value is a {@code Boolean} it is returned directly. If the value is a {@code String} and it
     * equals 'true' ignoring case then {@code true} is returned, otherwise {@code false}. If the value is a
     * {@code Number} an integer zero value returns {@code false} and non-zero returns {@code true}.
     * Otherwise, {@code null} is returned.
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @return the value in the Map as a Boolean, {@code null} if null map input
     */
    public static <K> Boolean getBoolean(final Map<? super K, ?> map, final K key) {
        if (map != null) {
            final Object answer = map.get(key);
            if (answer != null) {
                if (answer instanceof Boolean) {
                    return (Boolean) answer;
                }
                if (answer instanceof String) {
                    return Boolean.valueOf((String) answer);
                }
                if (answer instanceof Number) {
                    final Number n = (Number) answer;
                    return n.intValue() != 0 ? Boolean.TRUE : Boolean.FALSE;
                }
            }
        }
        return null;
    }

    /**
     * Looks up the given key in the given map, converting the result into a boolean, using the default value if the
     * conversion fails.
     *
     * @param <K>          the key type
     * @param map          the map whose value to look up
     * @param key          the key of the value to look up in that map
     * @param defaultValue what to return if the value is null or if the conversion fails
     * @return the value in the map as a boolean, or defaultValue if the original value is null, the map is null or the
     * boolean conversion fails
     */
    public static <K> Boolean getBoolean(final Map<? super K, ?> map, final K key, final Boolean defaultValue) {
        return applyDefaultValue(map, key, MapUtils::getBoolean, defaultValue);
    }

    /**
     * Looks up the given key in the given map, converting the result into a boolean, using the defaultFunction to
     * produce the default value if the conversion fails.
     *
     * @param <K>             the key type
     * @param map             the map whose value to look up
     * @param key             the key of the value to look up in that map
     * @param defaultFunction what to produce the default value if the value is null or if the conversion fails
     * @return the value in the map as a boolean, or defaultValue produced by the defaultFunction if the original value
     * is null, the map is null or the boolean conversion fails
     * @since 4.5
     */
    public static <K> Boolean getBoolean(final Map<? super K, ?> map, final K key,
                                         final Function<K, Boolean> defaultFunction) {
        return applyDefaultFunction(map, key, MapUtils::getBoolean, defaultFunction);
    }

    // Type safe primitive getters
    // -------------------------------------------------------------------------

    /**
     * Gets a boolean from a Map in a null-safe manner.
     * <p>
     * If the value is a {@code Boolean} its value is returned. If the value is a {@code String} and it equals
     * 'true' ignoring case then {@code true} is returned, otherwise {@code false}. If the value is a
     * {@code Number} an integer zero value returns {@code false} and non-zero returns {@code true}.
     * Otherwise, {@code false} is returned.
     * </p>
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @return the value in the Map as a Boolean, {@code false} if null map input
     */
    public static <K> boolean getBooleanValue(final Map<? super K, ?> map, final K key) {
        return Boolean.TRUE.equals(getBoolean(map, key));
    }

    // Type safe primitive getters with default values
    // -------------------------------------------------------------------------

    /**
     * Gets a boolean from a Map in a null-safe manner, using the default value if the conversion fails.
     * <p>
     * If the value is a {@code Boolean} its value is returned. If the value is a {@code String} and it equals
     * 'true' ignoring case then {@code true} is returned, otherwise {@code false}. If the value is a
     * {@code Number} an integer zero value returns {@code false} and non-zero returns {@code true}.
     * Otherwise, {@code defaultValue} is returned.
     * </p>
     *
     * @param <K>          the key type
     * @param map          the map to use
     * @param key          the key to look up
     * @param defaultValue return if the value is null or if the conversion fails
     * @return the value in the Map as a Boolean, {@code defaultValue} if null map input
     */
    public static <K> boolean getBooleanValue(final Map<? super K, ?> map, final K key, final boolean defaultValue) {
        return applyDefaultValue(map, key, MapUtils::getBoolean, defaultValue).booleanValue();
    }

    /**
     * Gets a boolean from a Map in a null-safe manner, using the default value produced by the defaultFunction if the
     * conversion fails.
     * <p>
     * If the value is a {@code Boolean} its value is returned. If the value is a {@code String} and it equals
     * 'true' ignoring case then {@code true} is returned, otherwise {@code false}. If the value is a
     * {@code Number} an integer zero value returns {@code false} and non-zero returns {@code true}.
     * Otherwise, defaultValue produced by the {@code defaultFunction} is returned.
     * </p>
     *
     * @param <K>             the key type
     * @param map             the map to use
     * @param key             the key to look up
     * @param defaultFunction produce the default value to return if the value is null or if the conversion fails
     * @return the value in the Map as a Boolean, default value produced by the {@code defaultFunction} if null map
     * input
     * @since 4.5
     */
    public static <K> boolean getBooleanValue(final Map<? super K, ?> map, final K key,
                                              final Function<K, Boolean> defaultFunction) {
        return applyDefaultFunction(map, key, MapUtils::getBoolean, defaultFunction, false).booleanValue();
    }

    /**
     * Gets a Byte from a Map in a null-safe manner.
     * <p>
     * The Byte is obtained from the results of {@link #getNumber(Map, Object)}.
     * </p>
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @return the value in the Map as a Byte, {@code null} if null map input
     */
    public static <K> Byte getByte(final Map<? super K, ?> map, final K key) {
        final Number answer = getNumber(map, key);
        if (answer == null) {
            return null;
        }
        if (answer instanceof Byte) {
            return (Byte) answer;
        }
        return Byte.valueOf(answer.byteValue());
    }

    /**
     * Looks up the given key in the given map, converting the result into a byte, using the default value if the
     * conversion fails.
     *
     * @param <K>          the key type
     * @param map          the map whose value to look up
     * @param key          the key of the value to look up in that map
     * @param defaultValue what to return if the value is null or if the conversion fails
     * @return the value in the map as a number, or defaultValue if the original value is null, the map is null or the
     * number conversion fails
     */
    public static <K> Byte getByte(final Map<? super K, ?> map, final K key, final Byte defaultValue) {
        return applyDefaultValue(map, key, MapUtils::getByte, defaultValue);
    }

    /**
     * Looks up the given key in the given map, converting the result into a byte, using the defaultFunction to produce
     * the default value if the conversion fails.
     *
     * @param <K>             the key type
     * @param map             the map whose value to look up
     * @param key             the key of the value to look up in that map
     * @param defaultFunction what to produce the default value if the value is null or if the conversion fails
     * @return the value in the map as a number, or defaultValue produced by the defaultFunction if the original value
     * is null, the map is null or the number conversion fails
     * @since 4.5
     */
    public static <K> Byte getByte(final Map<? super K, ?> map, final K key, final Function<K, Byte> defaultFunction) {
        return applyDefaultFunction(map, key, MapUtils::getByte, defaultFunction);
    }

    /**
     * Gets a byte from a Map in a null-safe manner.
     * <p>
     * The byte is obtained from the results of {@link #getNumber(Map, Object)}.
     * </p>
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @return the value in the Map as a byte, {@code 0} if null map input
     */
    public static <K> byte getByteValue(final Map<? super K, ?> map, final K key) {
        return applyDefaultValue(map, key, MapUtils::getByte, 0).byteValue();
    }

    /**
     * Gets a byte from a Map in a null-safe manner, using the default value if the conversion fails.
     * <p>
     * The byte is obtained from the results of {@link #getNumber(Map, Object)}.
     * </p>
     *
     * @param <K>          the key type
     * @param map          the map to use
     * @param key          the key to look up
     * @param defaultValue return if the value is null or if the conversion fails
     * @return the value in the Map as a byte, {@code defaultValue} if null map input
     */
    public static <K> byte getByteValue(final Map<? super K, ?> map, final K key, final byte defaultValue) {
        return applyDefaultValue(map, key, MapUtils::getByte, defaultValue).byteValue();
    }

    /**
     * Gets a byte from a Map in a null-safe manner, using the default value produced by the defaultFunction if the
     * conversion fails.
     * <p>
     * The byte is obtained from the results of {@link #getNumber(Map, Object)}.
     * </p>
     *
     * @param <K>             the key type
     * @param map             the map to use
     * @param key             the key to look up
     * @param defaultFunction produce the default value to return if the value is null or if the conversion fails
     * @return the value in the Map as a byte, default value produced by the {@code defaultFunction} if null map
     * input
     * @since 4.5
     */
    public static <K> byte getByteValue(final Map<? super K, ?> map, final K key,
                                        final Function<K, Byte> defaultFunction) {
        return applyDefaultFunction(map, key, MapUtils::getByte, defaultFunction, (byte) 0).byteValue();
    }

    /**
     * Gets a Double from a Map in a null-safe manner.
     * <p>
     * The Double is obtained from the results of {@link #getNumber(Map, Object)}.
     * </p>
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @return the value in the Map as a Double, {@code null} if null map input
     */
    public static <K> Double getDouble(final Map<? super K, ?> map, final K key) {
        final Number answer = getNumber(map, key);
        if (answer == null) {
            return null;
        }
        if (answer instanceof Double) {
            return (Double) answer;
        }
        return Double.valueOf(answer.doubleValue());
    }

    /**
     * Looks up the given key in the given map, converting the result into a double, using the default value if the
     * conversion fails.
     *
     * @param <K>          the key type
     * @param map          the map whose value to look up
     * @param key          the key of the value to look up in that map
     * @param defaultValue what to return if the value is null or if the conversion fails
     * @return the value in the map as a number, or defaultValue if the original value is null, the map is null or the
     * number conversion fails
     */
    public static <K> Double getDouble(final Map<? super K, ?> map, final K key, final Double defaultValue) {
        return applyDefaultValue(map, key, MapUtils::getDouble, defaultValue);
    }

    /**
     * Looks up the given key in the given map, converting the result into a double, using the defaultFunction to
     * produce the default value if the conversion fails.
     *
     * @param <K>             the key type
     * @param map             the map whose value to look up
     * @param key             the key of the value to look up in that map
     * @param defaultFunction what to produce the default value if the value is null or if the conversion fails
     * @return the value in the map as a number, or defaultValue produced by the defaultFunction if the original value
     * is null, the map is null or the number conversion fails
     * @since 4.5
     */
    public static <K> Double getDouble(final Map<? super K, ?> map, final K key,
                                       final Function<K, Double> defaultFunction) {
        return applyDefaultFunction(map, key, MapUtils::getDouble, defaultFunction);
    }

    /**
     * Gets a double from a Map in a null-safe manner.
     * <p>
     * The double is obtained from the results of {@link #getNumber(Map, Object)}.
     * </p>
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @return the value in the Map as a double, {@code 0.0} if null map input
     */
    public static <K> double getDoubleValue(final Map<? super K, ?> map, final K key) {
        return applyDefaultValue(map, key, MapUtils::getDouble, 0d).doubleValue();
    }

    /**
     * Gets a double from a Map in a null-safe manner, using the default value if the conversion fails.
     * <p>
     * The double is obtained from the results of {@link #getNumber(Map, Object)}.
     * </p>
     *
     * @param <K>          the key type
     * @param map          the map to use
     * @param key          the key to look up
     * @param defaultValue return if the value is null or if the conversion fails
     * @return the value in the Map as a double, {@code defaultValue} if null map input
     */
    public static <K> double getDoubleValue(final Map<? super K, ?> map, final K key, final double defaultValue) {
        return applyDefaultValue(map, key, MapUtils::getDouble, defaultValue).doubleValue();
    }

    /**
     * Gets a double from a Map in a null-safe manner, using the default value produced by the defaultFunction if the
     * conversion fails.
     * <p>
     * The double is obtained from the results of {@link #getNumber(Map, Object)}.
     * </p>
     *
     * @param <K>             the key type
     * @param map             the map to use
     * @param key             the key to look up
     * @param defaultFunction produce the default value to return if the value is null or if the conversion fails
     * @return the value in the Map as a double, default value produced by the {@code defaultFunction} if null map
     * input
     * @since 4.5
     */
    public static <K> double getDoubleValue(final Map<? super K, ?> map, final K key,
                                            final Function<K, Double> defaultFunction) {
        return applyDefaultFunction(map, key, MapUtils::getDouble, defaultFunction, 0d).doubleValue();
    }

    /**
     * Gets a Float from a Map in a null-safe manner.
     * <p>
     * The Float is obtained from the results of {@link #getNumber(Map, Object)}.
     * </p>
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @return the value in the Map as a Float, {@code null} if null map input
     */
    public static <K> Float getFloat(final Map<? super K, ?> map, final K key) {
        final Number answer = getNumber(map, key);
        if (answer == null) {
            return null;
        }
        if (answer instanceof Float) {
            return (Float) answer;
        }
        return Float.valueOf(answer.floatValue());
    }

    /**
     * Looks up the given key in the given map, converting the result into a float, using the default value if the
     * conversion fails.
     *
     * @param <K>          the key type
     * @param map          the map whose value to look up
     * @param key          the key of the value to look up in that map
     * @param defaultValue what to return if the value is null or if the conversion fails
     * @return the value in the map as a number, or defaultValue if the original value is null, the map is null or the
     * number conversion fails
     */
    public static <K> Float getFloat(final Map<? super K, ?> map, final K key, final Float defaultValue) {
        return applyDefaultValue(map, key, MapUtils::getFloat, defaultValue);
    }

    /**
     * Looks up the given key in the given map, converting the result into a float, using the defaultFunction to produce
     * the default value if the conversion fails.
     *
     * @param <K>             the key type
     * @param map             the map whose value to look up
     * @param key             the key of the value to look up in that map
     * @param defaultFunction what to produce the default value if the value is null or if the conversion fails
     * @return the value in the map as a number, or defaultValue produced by the defaultFunction if the original value
     * is null, the map is null or the number conversion fails
     * @since 4.5
     */
    public static <K> Float getFloat(final Map<? super K, ?> map, final K key,
                                     final Function<K, Float> defaultFunction) {
        return applyDefaultFunction(map, key, MapUtils::getFloat, defaultFunction);
    }

    /**
     * Gets a float from a Map in a null-safe manner.
     * <p>
     * The float is obtained from the results of {@link #getNumber(Map, Object)}.
     * </p>
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @return the value in the Map as a float, {@code 0.0F} if null map input
     */
    public static <K> float getFloatValue(final Map<? super K, ?> map, final K key) {
        return applyDefaultValue(map, key, MapUtils::getFloat, 0f).floatValue();
    }

    /**
     * Gets a float from a Map in a null-safe manner, using the default value if the conversion fails.
     * <p>
     * The float is obtained from the results of {@link #getNumber(Map, Object)}.
     * </p>
     *
     * @param <K>          the key type
     * @param map          the map to use
     * @param key          the key to look up
     * @param defaultValue return if the value is null or if the conversion fails
     * @return the value in the Map as a float, {@code defaultValue} if null map input
     */
    public static <K> float getFloatValue(final Map<? super K, ?> map, final K key, final float defaultValue) {
        return applyDefaultValue(map, key, MapUtils::getFloat, defaultValue).floatValue();
    }

    /**
     * Gets a float from a Map in a null-safe manner, using the default value produced by the defaultFunction if the
     * conversion fails.
     * <p>
     * The float is obtained from the results of {@link #getNumber(Map, Object)}.
     * </p>
     *
     * @param <K>             the key type
     * @param map             the map to use
     * @param key             the key to look up
     * @param defaultFunction produce the default value to return if the value is null or if the conversion fails
     * @return the value in the Map as a float, default value produced by the {@code defaultFunction} if null map
     * input
     * @since 4.5
     */
    public static <K> float getFloatValue(final Map<? super K, ?> map, final K key,
                                          final Function<K, Float> defaultFunction) {
        return applyDefaultFunction(map, key, MapUtils::getFloat, defaultFunction, 0f).floatValue();
    }

    /**
     * Gets a Integer from a Map in a null-safe manner.
     * <p>
     * The Integer is obtained from the results of {@link #getNumber(Map, Object)}.
     * </p>
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @return the value in the Map as a Integer, {@code null} if null map input
     */
    public static <K> Integer getInteger(final Map<? super K, ?> map, final K key) {
        final Number answer = getNumber(map, key);
        if (answer == null) {
            return null;
        }
        if (answer instanceof Integer) {
            return (Integer) answer;
        }
        return Integer.valueOf(answer.intValue());
    }

    /**
     * Looks up the given key in the given map, converting the result into an integer, using the defaultFunction to
     * produce the default value if the conversion fails.
     *
     * @param <K>             the key type
     * @param map             the map whose value to look up
     * @param key             the key of the value to look up in that map
     * @param defaultFunction what to produce the default value if the value is null or if the conversion fails
     * @return the value in the map as a number, or defaultValue produced by the defaultFunction if the original value
     * is null, the map is null or the number conversion fails
     * @since 4.5
     */
    public static <K> Integer getInteger(final Map<? super K, ?> map, final K key,
                                         final Function<K, Integer> defaultFunction) {
        return applyDefaultFunction(map, key, MapUtils::getInteger, defaultFunction);
    }

    /**
     * Looks up the given key in the given map, converting the result into an integer, using the default value if the
     * conversion fails.
     *
     * @param <K>          the key type
     * @param map          the map whose value to look up
     * @param key          the key of the value to look up in that map
     * @param defaultValue what to return if the value is null or if the conversion fails
     * @return the value in the map as a number, or defaultValue if the original value is null, the map is null or the
     * number conversion fails
     */
    public static <K> Integer getInteger(final Map<? super K, ?> map, final K key, final Integer defaultValue) {
        return applyDefaultValue(map, key, MapUtils::getInteger, defaultValue);
    }

    /**
     * Gets an int from a Map in a null-safe manner.
     * <p>
     * The int is obtained from the results of {@link #getNumber(Map, Object)}.
     * </p>
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @return the value in the Map as an int, {@code 0} if null map input
     */
    public static <K> int getIntValue(final Map<? super K, ?> map, final K key) {
        return applyDefaultValue(map, key, MapUtils::getInteger, 0).intValue();
    }

    /**
     * Gets an int from a Map in a null-safe manner, using the default value produced by the defaultFunction if the
     * conversion fails.
     * <p>
     * The int is obtained from the results of {@link #getNumber(Map, Object)}.
     * </p>
     *
     * @param <K>             the key type
     * @param map             the map to use
     * @param key             the key to look up
     * @param defaultFunction produce the default value to return if the value is null or if the conversion fails
     * @return the value in the Map as an int, default value produced by the {@code defaultFunction} if null map
     * input
     * @since 4.5
     */
    public static <K> int getIntValue(final Map<? super K, ?> map, final K key,
                                      final Function<K, Integer> defaultFunction) {
        return applyDefaultFunction(map, key, MapUtils::getInteger, defaultFunction, 0).byteValue();
    }

    /**
     * Gets an int from a Map in a null-safe manner, using the default value if the conversion fails.
     * <p>
     * The int is obtained from the results of {@link #getNumber(Map, Object)}.
     * </p>
     *
     * @param <K>          the key type
     * @param map          the map to use
     * @param key          the key to look up
     * @param defaultValue return if the value is null or if the conversion fails
     * @return the value in the Map as an int, {@code defaultValue} if null map input
     */
    public static <K> int getIntValue(final Map<? super K, ?> map, final K key, final int defaultValue) {
        return applyDefaultValue(map, key, MapUtils::getInteger, defaultValue).intValue();
    }

    /**
     * Gets a Long from a Map in a null-safe manner.
     * <p>
     * The Long is obtained from the results of {@link #getNumber(Map, Object)}.
     * </p>
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @return the value in the Map as a Long, {@code null} if null map input
     */
    public static <K> Long getLong(final Map<? super K, ?> map, final K key) {
        final Number answer = getNumber(map, key);
        if (answer == null) {
            return null;
        }
        if (answer instanceof Long) {
            return (Long) answer;
        }
        return Long.valueOf(answer.longValue());
    }

    /**
     * Looks up the given key in the given map, converting the result into a Long, using the defaultFunction to produce
     * the default value if the conversion fails.
     *
     * @param <K>             the key type
     * @param map             the map whose value to look up
     * @param key             the key of the value to look up in that map
     * @param defaultFunction what to produce the default value if the value is null or if the conversion fails
     * @return the value in the map as a number, or defaultValue produced by the defaultFunction if the original value
     * is null, the map is null or the number conversion fails
     * @since 4.5
     */
    public static <K> Long getLong(final Map<? super K, ?> map, final K key, final Function<K, Long> defaultFunction) {
        return applyDefaultFunction(map, key, MapUtils::getLong, defaultFunction);
    }

    /**
     * Looks up the given key in the given map, converting the result into a long, using the default value if the
     * conversion fails.
     *
     * @param <K>          the key type
     * @param map          the map whose value to look up
     * @param key          the key of the value to look up in that map
     * @param defaultValue what to return if the value is null or if the conversion fails
     * @return the value in the map as a number, or defaultValue if the original value is null, the map is null or the
     * number conversion fails
     */
    public static <K> Long getLong(final Map<? super K, ?> map, final K key, final Long defaultValue) {
        return applyDefaultValue(map, key, MapUtils::getLong, defaultValue);
    }

    /**
     * Gets a long from a Map in a null-safe manner.
     * <p>
     * The long is obtained from the results of {@link #getNumber(Map, Object)}.
     * </p>
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @return the value in the Map as a long, {@code 0L} if null map input
     */
    public static <K> long getLongValue(final Map<? super K, ?> map, final K key) {
        return applyDefaultValue(map, key, MapUtils::getLong, 0L).longValue();
    }

    /**
     * Gets a long from a Map in a null-safe manner, using the default value produced by the defaultFunction if the
     * conversion fails.
     * <p>
     * The long is obtained from the results of {@link #getNumber(Map, Object)}.
     * </p>
     *
     * @param <K>             the key type
     * @param map             the map to use
     * @param key             the key to look up
     * @param defaultFunction produce the default value to return if the value is null or if the conversion fails
     * @return the value in the Map as a long, default value produced by the {@code defaultFunction} if null map
     * input
     * @since 4.5
     */
    public static <K> long getLongValue(final Map<? super K, ?> map, final K key,
                                        final Function<K, Long> defaultFunction) {
        return applyDefaultFunction(map, key, MapUtils::getLong, defaultFunction, 0L).byteValue();
    }

    /**
     * Gets a long from a Map in a null-safe manner, using the default value if the conversion fails.
     * <p>
     * The long is obtained from the results of {@link #getNumber(Map, Object)}.
     * </p>
     *
     * @param <K>          the key type
     * @param map          the map to use
     * @param key          the key to look up
     * @param defaultValue return if the value is null or if the conversion fails
     * @return the value in the Map as a long, {@code defaultValue} if null map input
     */
    public static <K> long getLongValue(final Map<? super K, ?> map, final K key, final long defaultValue) {
        return applyDefaultValue(map, key, MapUtils::getLong, defaultValue).longValue();
    }

    /**
     * Gets a Map from a Map in a null-safe manner.
     * <p>
     * If the value returned from the specified map is not a Map then {@code null} is returned.
     * </p>
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @return the value in the Map as a Map, {@code null} if null map input
     */
    public static <K> Map<?, ?> getMap(final Map<? super K, ?> map, final K key) {
        if (map != null) {
            final Object answer = map.get(key);
            if (answer instanceof Map) {
                return (Map<?, ?>) answer;
            }
        }
        return null;
    }

    /**
     * Looks up the given key in the given map, converting the result into a map, using the defaultFunction to produce
     * the default value if the conversion fails.
     *
     * @param <K>             the key type
     * @param map             the map whose value to look up
     * @param key             the key of the value to look up in that map
     * @param defaultFunction what to produce the default value if the value is null or if the conversion fails
     * @return the value in the map as a number, or defaultValue produced by the defaultFunction if the original value
     * is null, the map is null or the map conversion fails
     * @since 4.5
     */
    public static <K> Map<?, ?> getMap(final Map<? super K, ?> map, final K key,
                                       final Function<K, Map<?, ?>> defaultFunction) {
        return applyDefaultFunction(map, key, MapUtils::getMap, defaultFunction);
    }

    /**
     * Looks up the given key in the given map, converting the result into a map, using the default value if the
     * conversion fails.
     *
     * @param <K>          the key type
     * @param map          the map whose value to look up
     * @param key          the key of the value to look up in that map
     * @param defaultValue what to return if the value is null or if the conversion fails
     * @return the value in the map as a number, or defaultValue if the original value is null, the map is null or the
     * map conversion fails
     */
    public static <K> Map<?, ?> getMap(final Map<? super K, ?> map, final K key, final Map<?, ?> defaultValue) {
        return applyDefaultValue(map, key, MapUtils::getMap, defaultValue);
    }

    /**
     * Gets a Number from a Map in a null-safe manner.
     * <p>
     * If the value is a {@code Number} it is returned directly. If the value is a {@code String} it is
     * converted using {@link NumberFormat#parse(String)} on the system default formatter returning {@code null} if
     * the conversion fails. Otherwise, {@code null} is returned.
     * </p>
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @return the value in the Map as a Number, {@code null} if null map input
     */
    public static <K> Number getNumber(final Map<? super K, ?> map, final K key) {
        if (map != null) {
            final Object answer = map.get(key);
            if (answer != null) {
                if (answer instanceof Number) {
                    return (Number) answer;
                }
                if (answer instanceof String) {
                    try {
                        final String text = (String) answer;
                        return NumberFormat.getInstance().parse(text);
                    } catch (final ParseException e) { // NOPMD
                        // failure means null is returned
                    }
                }
            }
        }
        return null;
    }

    /**
     * Looks up the given key in the given map, converting the result into a number, using the defaultFunction to
     * produce the default value if the conversion fails.
     *
     * @param <K>             the key type
     * @param map             the map whose value to look up
     * @param key             the key of the value to look up in that map
     * @param defaultFunction what to produce the default value if the value is null or if the conversion fails
     * @return the value in the map as a number, or defaultValue produced by the defaultFunction if the original value
     * is null, the map is null or the number conversion fails
     * @since 4.5
     */
    public static <K> Number getNumber(final Map<? super K, ?> map, final K key,
                                       final Function<K, Number> defaultFunction) {
        return applyDefaultFunction(map, key, MapUtils::getNumber, defaultFunction);
    }

    /**
     * Looks up the given key in the given map, converting the result into a number, using the default value if the
     * conversion fails.
     *
     * @param <K>          the key type
     * @param map          the map whose value to look up
     * @param key          the key of the value to look up in that map
     * @param defaultValue what to return if the value is null or if the conversion fails
     * @return the value in the map as a number, or defaultValue if the original value is null, the map is null or the
     * number conversion fails
     */
    public static <K> Number getNumber(final Map<? super K, ?> map, final K key, final Number defaultValue) {
        return applyDefaultValue(map, key, MapUtils::getNumber, defaultValue);
    }

    // -------------------------------------------------------------------------

    /**
     * Gets from a Map in a null-safe manner.
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param map the map to use
     * @param key the key to look up
     * @return the value in the Map, {@code null} if null map input
     */
    public static <K, V> V getObject(final Map<? super K, V> map, final K key) {
        if (map != null) {
            return map.get(key);
        }
        return null;
    }

    // -------------------------------------------------------------------------

    /**
     * Looks up the given key in the given map, converting null into the given default value.
     *
     * @param <K>          the key type
     * @param <V>          the value type
     * @param map          the map whose value to look up
     * @param key          the key of the value to look up in that map
     * @param defaultValue what to return if the value is null
     * @return the value in the map, or defaultValue if the original value is null or the map is null
     */
    public static <K, V> V getObject(final Map<K, V> map, final K key, final V defaultValue) {
        if (map != null) {
            final V answer = map.get(key);
            if (answer != null) {
                return answer;
            }
        }
        return defaultValue;
    }

    /**
     * Gets a Short from a Map in a null-safe manner.
     * <p>
     * The Short is obtained from the results of {@link #getNumber(Map, Object)}.
     * </p>
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @return the value in the Map as a Short, {@code null} if null map input
     */
    public static <K> Short getShort(final Map<? super K, ?> map, final K key) {
        final Number answer = getNumber(map, key);
        if (answer == null) {
            return null;
        }
        if (answer instanceof Short) {
            return (Short) answer;
        }
        return Short.valueOf(answer.shortValue());
    }

    /**
     * Looks up the given key in the given map, converting the result into a short, using the defaultFunction to produce
     * the default value if the conversion fails.
     *
     * @param <K>             the key type
     * @param map             the map whose value to look up
     * @param key             the key of the value to look up in that map
     * @param defaultFunction what to produce the default value if the value is null or if the conversion fails
     * @return the value in the map as a number, or defaultValue produced by the defaultFunction if the original value
     * is null, the map is null or the number conversion fails
     * @since 4.5
     */
    public static <K> Short getShort(final Map<? super K, ?> map, final K key,
                                     final Function<K, Short> defaultFunction) {
        return applyDefaultFunction(map, key, MapUtils::getShort, defaultFunction);
    }

    /**
     * Looks up the given key in the given map, converting the result into a short, using the default value if the
     * conversion fails.
     *
     * @param <K>          the key type
     * @param map          the map whose value to look up
     * @param key          the key of the value to look up in that map
     * @param defaultValue what to return if the value is null or if the conversion fails
     * @return the value in the map as a number, or defaultValue if the original value is null, the map is null or the
     * number conversion fails
     */
    public static <K> Short getShort(final Map<? super K, ?> map, final K key, final Short defaultValue) {
        return applyDefaultValue(map, key, MapUtils::getShort, defaultValue);
    }

    /**
     * Gets a short from a Map in a null-safe manner.
     * <p>
     * The short is obtained from the results of {@link #getNumber(Map, Object)}.
     * </p>
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @return the value in the Map as a short, {@code 0} if null map input
     */
    public static <K> short getShortValue(final Map<? super K, ?> map, final K key) {
        return applyDefaultValue(map, key, MapUtils::getShort, 0).shortValue();
    }

    /**
     * Gets a short from a Map in a null-safe manner, using the default value produced by the defaultFunction if the
     * conversion fails.
     * <p>
     * The short is obtained from the results of {@link #getNumber(Map, Object)}.
     * </p>
     *
     * @param <K>             the key type
     * @param map             the map to use
     * @param key             the key to look up
     * @param defaultFunction produce the default value to return if the value is null or if the conversion fails
     * @return the value in the Map as a short, default value produced by the {@code defaultFunction} if null map
     * input
     * @since 4.5
     */
    public static <K> short getShortValue(final Map<? super K, ?> map, final K key,
                                          final Function<K, Short> defaultFunction) {
        return applyDefaultFunction(map, key, MapUtils::getShort, defaultFunction, (short) 0).shortValue();
    }

    /**
     * Gets a short from a Map in a null-safe manner, using the default value if the conversion fails.
     * <p>
     * The short is obtained from the results of {@link #getNumber(Map, Object)}.
     * </p>
     *
     * @param <K>          the key type
     * @param map          the map to use
     * @param key          the key to look up
     * @param defaultValue return if the value is null or if the conversion fails
     * @return the value in the Map as a short, {@code defaultValue} if null map input
     */
    public static <K> short getShortValue(final Map<? super K, ?> map, final K key, final short defaultValue) {
        return applyDefaultValue(map, key, MapUtils::getShort, defaultValue).shortValue();
    }

    /**
     * Gets a String from a Map in a null-safe manner.
     * <p>
     * The String is obtained via {@code toString}.
     * </p>
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @return the value in the Map as a String, {@code null} if null map input
     */
    public static <K> String getString(final Map<? super K, ?> map, final K key) {
        if (map != null) {
            final Object answer = map.get(key);
            if (answer != null) {
                return answer.toString();
            }
        }
        return null;
    }

    /**
     * Looks up the given key in the given map, converting the result into a string, using the defaultFunction to
     * produce the default value if the conversion fails.
     *
     * @param <K>             the key type
     * @param map             the map whose value to look up
     * @param key             the key of the value to look up in that map
     * @param defaultFunction what to produce the default value if the value is null or if the conversion fails
     * @return the value in the map as a string, or defaultValue produced by the defaultFunction if the original value
     * is null, the map is null or the string conversion fails
     * @since 4.5
     */
    public static <K> String getString(final Map<? super K, ?> map, final K key,
                                       final Function<K, String> defaultFunction) {
        return applyDefaultFunction(map, key, MapUtils::getString, defaultFunction);
    }

    /**
     * Looks up the given key in the given map, converting the result into a string, using the default value if the
     * conversion fails.
     *
     * @param <K>          the key type
     * @param map          the map whose value to look up
     * @param key          the key of the value to look up in that map
     * @param defaultValue what to return if the value is null or if the conversion fails
     * @return the value in the map as a string, or defaultValue if the original value is null, the map is null or the
     * string conversion fails
     */
    public static <K> String getString(final Map<? super K, ?> map, final K key, final String defaultValue) {
        return applyDefaultValue(map, key, MapUtils::getString, defaultValue);
    }

    // Misc
    // -----------------------------------------------------------------------

    /**
     * Inverts the supplied map returning a new HashMap such that the keys of the input are swapped with the values.
     * <p>
     * This operation assumes that the inverse mapping is well defined. If the input map had multiple entries with the
     * same value mapped to different keys, the returned map will map one of those keys to the value, but the exact key
     * which will be mapped is undefined.
     * </p>
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param map the map to invert, must not be null
     * @return a new HashMap containing the inverted data
     * @throws NullPointerException if the map is null
     */
    public static <K, V> Map<V, K> invertMap(final Map<K, V> map) {
        Objects.requireNonNull(map, "map");
        final Map<V, K> out = new HashMap<>(map.size());
        for (final Entry<K, V> entry : map.entrySet()) {
            out.put(entry.getValue(), entry.getKey());
        }
        return out;
    }

    /**
     * Null-safe check if the specified map is empty.
     * <p>
     * Null returns true.
     * </p>
     *
     * @param map the map to check, may be null
     * @return true if empty or null
     * @since 3.2
     */
    public static boolean isEmpty(final Map<?, ?> map) {
        
        return map == null || map.isEmpty();
    }

    /**
     * Null-safe check if the specified map is not empty.
     * <p>
     * Null returns false.
     * </p>
     *
     * @param map the map to check, may be null
     * @return true if non-null and non-empty
     * @since 3.2
     */
    public static boolean isNotEmpty(final Map<?, ?> map) {
        return !MapUtils.isEmpty(map);
    }

    /**
     * Writes indentation to the given stream.
     *
     * @param out    the stream to indent
     * @param indent the index of the indentation
     */
    private static void printIndent(final PrintStream out, final int indent) {
        for (int i = 0; i < indent; i++) {
            out.print(INDENT_STRING);
        }
    }

    // -----------------------------------------------------------------------

    /**
     * Puts all the keys and values from the specified array into the map.
     * <p>
     * This method is an alternative to the {@link Map#putAll(Map)} method and constructors. It
     * allows you to build a map from an object array of various possible styles.
     * </p>
     * <p>
     * If the first entry in the object array implements {@link Entry} or {@link KeyValue} then the key
     * and value are added from that object. If the first entry in the object array is an object array itself, then it
     * is assumed that index 0 in the sub-array is the key and index 1 is the value. Otherwise, the array is treated as
     * keys and values in alternate indices.
     * </p>
     * <p>
     * For example, to create a color map:
     * </p>
     *
     * <pre>
     * Map colorMap = MapUtils.putAll(new HashMap(),
     *         new String[][] { { "RED", "#FF0000" }, { "GREEN", "#00FF00" }, { "BLUE", "#0000FF" } });
     * </pre>
     *
     * <p>
     * or:
     * </p>
     *
     * <pre>
     * Map colorMap = MapUtils.putAll(new HashMap(),
     *         new String[] { "RED", "#FF0000", "GREEN", "#00FF00", "BLUE", "#0000FF" });
     * </pre>
     *
     * <p>
     * or:
     * </p>
     *
     * <pre>
     * Map colorMap = MapUtils.putAll(new HashMap(), new Map.Entry[] { new DefaultMapEntry("RED", "#FF0000"),
     *         new DefaultMapEntry("GREEN", "#00FF00"), new DefaultMapEntry("BLUE", "#0000FF") });
     * </pre>
     *
     * @param <K>   the key type
     * @param <V>   the value type
     * @param map   the map to populate, must not be null
     * @param array an array to populate from, null ignored
     * @return the input map
     * @throws NullPointerException     if map is null
     * @throws IllegalArgumentException if sub-array or entry matching used and an entry is invalid
     * @throws ClassCastException       if the array contents is mixed
     * @since 3.2
     */
    @SuppressWarnings("unchecked") // As per Javadoc throws CCE for invalid array contents
    public static <K, V> Map<K, V> putAll(final Map<K, V> map, final Object[] array) {
        Objects.requireNonNull(map, "map");
        if (array == null || array.length == 0) {
            return map;
        }
        final Object obj = array[0];
        if (obj instanceof Map.Entry) {
            for (final Object element : array) {
                // cast ok here, type is checked above
                final Entry<K, V> entry = (Entry<K, V>) element;
                map.put(entry.getKey(), entry.getValue());
            }
        } else if (obj instanceof Object[]) {
            for (int i = 0; i < array.length; i++) {
                final Object[] sub = (Object[]) array[i];
                if (sub == null || sub.length < 2) {
                    throw new IllegalArgumentException("Invalid array element: " + i);
                }
                // these casts can fail if array has incorrect types
                map.put((K) sub[0], (V) sub[1]);
            }
        } else {
            for (int i = 0; i < array.length - 1; ) {
                // these casts can fail if array has incorrect types
                map.put((K) array[i++], (V) array[i++]);
            }
        }
        return map;
    }

    /**
     * Protects against adding null values to a map.
     * <p>
     * This method checks the value being added to the map, and if it is null it is replaced by an empty string.
     * </p>
     * <p>
     * This could be useful if the map does not accept null values, or for receiving data from a source that may provide
     * null or empty string which should be held in the same way in the map.
     * </p>
     * <p>
     * Keys are not validated. Note that this method can be used to circumvent the map's value type at runtime.
     * </p>
     *
     * @param <K>   the key type
     * @param map   the map to add to, must not be null
     * @param key   the key
     * @param value the value, null converted to ""
     * @throws NullPointerException if the map is null
     */
    public static <K> void safeAddToMap(final Map<? super K, Object> map, final K key, final Object value)
            throws NullPointerException {
        Objects.requireNonNull(map, "map");
        map.put(key, value == null ? "" : value);
    }

    /**
     * Gets the given map size or 0 if the map is null
     *
     * @param map a Map or null
     * @return the given map size or 0 if the map is null
     */
    public static int size(final Map<?, ?> map) {
        return map == null ? 0 : map.size();
    }

    // -----------------------------------------------------------------------

    /**
     * Returns a synchronized map backed by the given map.
     * <p>
     * You must manually synchronize on the returned buffer's iterator to avoid non-deterministic behavior:
     * </p>
     *
     * <pre>
     * Map m = MapUtils.synchronizedMap(myMap);
     * Set s = m.keySet(); // outside synchronized block
     * synchronized (m) { // synchronized on MAP!
     *     Iterator i = s.iterator();
     *     while (i.hasNext()) {
     *         process(i.next());
     *     }
     * }
     * </pre>
     *
     * <p>
     * This method uses the implementation in {@link Collections Collections}.
     * </p>
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param map the map to synchronize, must not be null
     * @return a synchronized map backed by the given map
     */
    public static <K, V> Map<K, V> synchronizedMap(final Map<K, V> map) {
        return Collections.synchronizedMap(map);
    }

    // -----------------------------------------------------------------------

    /**
     * Returns a synchronized sorted map backed by the given sorted map.
     * <p>
     * You must manually synchronize on the returned buffer's iterator to avoid non-deterministic behavior:
     * </p>
     *
     * <pre>
     * Map m = MapUtils.synchronizedSortedMap(myMap);
     * Set s = m.keySet(); // outside synchronized block
     * synchronized (m) { // synchronized on MAP!
     *     Iterator i = s.iterator();
     *     while (i.hasNext()) {
     *         process(i.next());
     *     }
     * }
     * </pre>
     *
     * <p>
     * This method uses the implementation in {@link Collections Collections}.
     * </p>
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param map the map to synchronize, must not be null
     * @return a synchronized map backed by the given map
     * @throws NullPointerException if the map is null
     */
    public static <K, V> SortedMap<K, V> synchronizedSortedMap(final SortedMap<K, V> map) {
        return Collections.synchronizedSortedMap(map);
    }

    /**
     * Creates a new HashMap using data copied from a ResourceBundle.
     *
     * @param resourceBundle the resource bundle to convert, must not be null
     * @return the HashMap containing the data
     * @throws NullPointerException if the bundle is null
     */
    public static Map<String, Object> toMap(final ResourceBundle resourceBundle) {
        Objects.requireNonNull(resourceBundle, "resourceBundle");
        final Enumeration<String> enumeration = resourceBundle.getKeys();
        final Map<String, Object> map = new HashMap<>();

        while (enumeration.hasMoreElements()) {
            final String key = enumeration.nextElement();
            final Object value = resourceBundle.getObject(key);
            map.put(key, value);
        }

        return map;
    }

    // -------------------------------------------------------------------------

    /**
     * Gets a new Properties object initialized with the values from a Map. A null input will return an empty properties
     * object.
     * <p>
     * A Properties object may only store non-null keys and values, thus if the provided map contains either a key or
     * value which is {@code null}, a {@link NullPointerException} will be thrown.
     * </p>
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param map the map to convert to a Properties object
     * @return the properties object
     * @throws NullPointerException if a key or value in the provided map is {@code null}
     */
    public static <K, V> Properties toProperties(final Map<K, V> map) {
        final Properties answer = new Properties();
        if (map != null) {
            for (final Entry<K, V> entry2 : map.entrySet()) {
                final Entry<?, ?> entry = entry2;
                final Object key = entry.getKey();
                final Object value = entry.getValue();
                answer.put(key, value);
            }
        }
        return answer;
    }
    /**
     * 合并多个map 重复key 保存最新值
     *
     * @param maps
     * @param <K>
     * @param <V>
     * @return Map
     */
    @SafeVarargs
    public static <K, V> Map<K, V> mergeMapNewValue(Map<K, V>... maps) {
        return Arrays.stream(maps).flatMap(m -> m.entrySet().stream().filter(e -> e.getValue() != null))
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue, (a, b) -> b));
    }

    /**
     * 合并多个map 重复key 保存初始值
     *
     * @param maps
     * @param <K>
     * @param <V>
     * @return
     */
    @SafeVarargs
    public static <K, V> Map<K, V> mergeMapOldValue(Map<K, V>... maps) {
        return Arrays.stream(maps).flatMap(m -> m.entrySet().stream().filter(e -> e.getValue() != null))
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue, (a, b) -> a));
    }
    /**
     * 按照值排序，可选是否倒序
     *
     * @param map 需要对值排序的map
     * @param <K> 键类型
     * @param <V> 值类型
     * @param isDesc 是否倒序
     * @return 排序后新的Map
     * @since 5.5.8
     */
    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map, boolean isDesc) {
        Map<K, V> result = new LinkedHashMap<>();
        Comparator<Entry<K, V>> entryComparator = Entry.comparingByValue();
        if(isDesc){
            entryComparator = entryComparator.reversed();
        }
        map.entrySet().stream().sorted(entryComparator).forEachOrdered(e -> result.put(e.getKey(), e.getValue()));
        return result;
    }
    /**
     * Don't allow instances.
     */
    private MapUtils() {
    }


}
