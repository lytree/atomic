package top.yang.collections;

import org.apache.commons.collections4.*;
import org.apache.commons.collections4.map.*;

import java.io.PrintStream;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;

public class MapUtil {
    private MapUtil() {
    }

    /**
     * Gets a String from a Map in a null-safe manner.
     * <p>
     * The String is obtained via <code>toString</code>.
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @return the value in the Map as a String, <code>null</code> if null map input
     */
    public static <K> String getString(final Map<? super K, ?> map, final K key) {
        return MapUtils.getString(map, key);
    }

    /**
     * Gets from a Map in a null-safe manner.
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param map the map to use
     * @param key the key to look up
     * @return the value in the Map, <code>null</code> if null map input
     */
    public static <K, V> V getObject(final Map<? super K, V> map, final K key) {
        return MapUtils.getObject(map, key);
    }

    /**
     * Gets a Boolean from a Map in a null-safe manner.
     * <p>
     * If the value is a <code>Boolean</code> it is returned directly.
     * If the value is a <code>String</code> and it equals 'true' ignoring case
     * then <code>true</code> is returned, otherwise <code>false</code>.
     * If the value is a <code>Number</code> an integer zero value returns
     * <code>false</code> and non-zero returns <code>true</code>.
     * Otherwise, <code>null</code> is returned.
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @return the value in the Map as a Boolean, <code>null</code> if null map input
     */
    public static <K> Boolean getBoolean(final Map<? super K, ?> map, final K key) {
        return MapUtils.getBoolean(map, key);
    }

    /**
     * Gets a Number from a Map in a null-safe manner.
     * <p>
     * If the value is a <code>Number</code> it is returned directly.
     * If the value is a <code>String</code> it is converted using
     * {@link NumberFormat#parse(String)} on the system default formatter
     * returning <code>null</code> if the conversion fails.
     * Otherwise, <code>null</code> is returned.
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @return the value in the Map as a Number, <code>null</code> if null map input
     */
    public static <K> Number getNumber(final Map<? super K, ?> map, final K key) {
        return MapUtils.getNumber(map, key);
    }

    /**
     * Gets a Byte from a Map in a null-safe manner.
     * <p>
     * The Byte is obtained from the results of {@link #getNumber(Map, Object)}.
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @return the value in the Map as a Byte, <code>null</code> if null map input
     */
    public static <K> Byte getByte(final Map<? super K, ?> map, final K key) {
        return MapUtils.getByte(map, key);
    }

    /**
     * Gets a Short from a Map in a null-safe manner.
     * <p>
     * The Short is obtained from the results of {@link #getNumber(Map, Object)}.
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @return the value in the Map as a Short, <code>null</code> if null map input
     */
    public static <K> Short getShort(final Map<? super K, ?> map, final K key) {
        return MapUtils.getShort(map, key);
    }

    /**
     * Gets a Integer from a Map in a null-safe manner.
     * <p>
     * The Integer is obtained from the results of {@link #getNumber(Map, Object)}.
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @return the value in the Map as a Integer, <code>null</code> if null map input
     */
    public static <K> Integer getInteger(final Map<? super K, ?> map, final K key) {
        return MapUtils.getInteger(map, key);
    }

    /**
     * Gets a Long from a Map in a null-safe manner.
     * <p>
     * The Long is obtained from the results of {@link #getNumber(Map, Object)}.
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @return the value in the Map as a Long, <code>null</code> if null map input
     */
    public static <K> Long getLong(final Map<? super K, ?> map, final K key) {
        return MapUtils.getLong(map, key);
    }

    /**
     * Gets a Float from a Map in a null-safe manner.
     * <p>
     * The Float is obtained from the results of {@link #getNumber(Map, Object)}.
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @return the value in the Map as a Float, <code>null</code> if null map input
     */
    public static <K> Float getFloat(final Map<? super K, ?> map, final K key) {
        return MapUtils.getFloat(map, key);
    }

    /**
     * Gets a Double from a Map in a null-safe manner.
     * <p>
     * The Double is obtained from the results of {@link #getNumber(Map, Object)}.
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @return the value in the Map as a Double, <code>null</code> if null map input
     */
    public static <K> Double getDouble(final Map<? super K, ?> map, final K key) {
        return MapUtils.getDouble(map, key);
    }

    /**
     * Gets a Map from a Map in a null-safe manner.
     * <p>
     * If the value returned from the specified map is not a Map then
     * <code>null</code> is returned.
     *
     * @param <K> the key type
     * @param map the map to use
     * @param key the key to look up
     * @return the value in the Map as a Map, <code>null</code> if null map input
     */
    public static <K> Map<?, ?> getMap(final Map<? super K, ?> map, final K key) {
        return MapUtils.getMap(map, key);
    }

    // Conversion methods
    //-------------------------------------------------------------------------

    /**
     * Gets a new Properties object initialised with the values from a Map.
     * A null input will return an empty properties object.
     * <p>
     * A Properties object may only store non-null keys and values, thus if
     * the provided map contains either a key or value which is {@code null},
     * a {@link NullPointerException} will be thrown.
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param map the map to convert to a Properties object
     * @return the properties object
     * @throws NullPointerException if a key or value in the provided map is {@code null}
     */
    public static <K, V> Properties toProperties(final Map<K, V> map) {
        return MapUtils.toProperties(map);
    }

    /**
     * Creates a new HashMap using data copied from a ResourceBundle.
     *
     * @param resourceBundle the resource bundle to convert, may not be null
     * @return the hashmap containing the data
     * @throws NullPointerException if the bundle is null
     */
    public static Map<String, Object> toMap(final ResourceBundle resourceBundle) {
        return MapUtils.toMap(resourceBundle);
    }

    // Printing methods
    //-------------------------------------------------------------------------

    /**
     * Prints the given map with nice line breaks.
     * <p>
     * This method prints a nicely formatted String describing the Map.
     * Each map entry will be printed with key and value.
     * When the value is a Map, recursive behaviour occurs.
     * <p>
     * This method is NOT thread-safe in any special way. You must manually
     * synchronize on either this class or the stream as required.
     *
     * @param out   the stream to print to, must not be null
     * @param label The label to be used, may be <code>null</code>.
     *              If <code>null</code>, the label is not output.
     *              It typically represents the name of the property in a bean or similar.
     * @param map   The map to print, may be <code>null</code>.
     *              If <code>null</code>, the text 'null' is output.
     * @throws NullPointerException if the stream is <code>null</code>
     */
    public static void verbosePrint(final PrintStream out, final Object label, final Map<?, ?> map) {
        MapUtils.verbosePrint(out, label, map);
    }

    /**
     * Prints the given map with nice line breaks.
     * <p>
     * This method prints a nicely formatted String describing the Map.
     * Each map entry will be printed with key, value and value classname.
     * When the value is a Map, recursive behaviour occurs.
     * <p>
     * This method is NOT thread-safe in any special way. You must manually
     * synchronize on either this class or the stream as required.
     *
     * @param out   the stream to print to, must not be null
     * @param label The label to be used, may be <code>null</code>.
     *              If <code>null</code>, the label is not output.
     *              It typically represents the name of the property in a bean or similar.
     * @param map   The map to print, may be <code>null</code>.
     *              If <code>null</code>, the text 'null' is output.
     * @throws NullPointerException if the stream is <code>null</code>
     */
    public static void debugPrint(final PrintStream out, final Object label, final Map<?, ?> map) {
        MapUtils.debugPrint(out, label, map);
    }


    // Misc
    //-----------------------------------------------------------------------

    /**
     * Inverts the supplied map returning a new HashMap such that the keys of
     * the input are swapped with the values.
     * <p>
     * This operation assumes that the inverse mapping is well defined.
     * If the input map had multiple entries with the same value mapped to
     * different keys, the returned map will map one of those keys to the
     * value, but the exact key which will be mapped is undefined.
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param map the map to invert, may not be null
     * @return a new HashMap containing the inverted data
     * @throws NullPointerException if the map is null
     */
    public static <K, V> Map<V, K> invertMap(final Map<K, V> map) {
        return MapUtils.invertMap(map);
    }

    //-----------------------------------------------------------------------

    /**
     * Protects against adding null values to a map.
     * <p>
     * This method checks the value being added to the map, and if it is null
     * it is replaced by an empty string.
     * <p>
     * This could be useful if the map does not accept null values, or for
     * receiving data from a source that may provide null or empty string
     * which should be held in the same way in the map.
     * <p>
     * Keys are not validated.
     * Note that this method can be used to circumvent the map's
     * value type at runtime.
     *
     * @param <K>   the key type
     * @param map   the map to add to, may not be null
     * @param key   the key
     * @param value the value, null converted to ""
     * @throws NullPointerException if the map is null
     */
    public static <K> void safeAddToMap(final Map<? super K, Object> map, final K key, final Object value)
            throws NullPointerException {
        MapUtils.safeAddToMap(map, key, value);
    }

    //-----------------------------------------------------------------------

    /**
     * Puts all the keys and values from the specified array into the map.
     * <p>
     * This method is an alternative to the {@link java.util.Map#putAll(java.util.Map)}
     * method and constructors. It allows you to build a map from an object array
     * of various possible styles.
     * <p>
     * If the first entry in the object array implements {@link java.util.Map.Entry}
     * or {@link KeyValue} then the key and value are added from that object.
     * If the first entry in the object array is an object array itself, then
     * it is assumed that index 0 in the sub-array is the key and index 1 is the value.
     * Otherwise, the array is treated as keys and values in alternate indices.
     * <p>
     * For example, to create a color map:
     * <pre>
     * Map colorMap = MapUtils.putAll(new HashMap(), new String[][] {
     *     {"RED", "#FF0000"},
     *     {"GREEN", "#00FF00"},
     *     {"BLUE", "#0000FF"}
     * });
     * </pre>
     * or:
     * <pre>
     * Map colorMap = MapUtils.putAll(new HashMap(), new String[] {
     *     "RED", "#FF0000",
     *     "GREEN", "#00FF00",
     *     "BLUE", "#0000FF"
     * });
     * </pre>
     * or:
     * <pre>
     * Map colorMap = MapUtils.putAll(new HashMap(), new Map.Entry[] {
     *     new DefaultMapEntry("RED", "#FF0000"),
     *     new DefaultMapEntry("GREEN", "#00FF00"),
     *     new DefaultMapEntry("BLUE", "#0000FF")
     * });
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
        return MapUtils.putAll(map, array);
    }

    //-----------------------------------------------------------------------

    /**
     * Returns an immutable empty map if the argument is <code>null</code>,
     * or the argument itself otherwise.
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param map the map, possibly <code>null</code>
     * @return an empty map if the argument is <code>null</code>
     */
    public static <K, V> Map<K, V> emptyIfNull(final Map<K, V> map) {
        return MapUtils.emptyIfNull(map);
    }

    /**
     * Null-safe check if the specified map is empty.
     * <p>
     * Null returns true.
     *
     * @param map the map to check, may be null
     * @return true if empty or null
     * @since 3.2
     */
    public static boolean isEmpty(final Map<?, ?> map) {
        return MapUtils.isEmpty(map);
    }

    /**
     * Null-safe check if the specified map is not empty.
     * <p>
     * Null returns false.
     *
     * @param map the map to check, may be null
     * @return true if non-null and non-empty
     * @since 3.2
     */
    public static boolean isNotEmpty(final Map<?, ?> map) {
        return !MapUtils.isEmpty(map);
    }

    // Map decorators
    //-----------------------------------------------------------------------

    /**
     * Returns a synchronized map backed by the given map.
     * <p>
     * You must manually synchronize on the returned buffer's iterator to
     * avoid non-deterministic behavior:
     *
     * <pre>
     * Map m = MapUtils.synchronizedMap(myMap);
     * Set s = m.keySet();  // outside synchronized block
     * synchronized (m) {  // synchronized on MAP!
     *     Iterator i = s.iterator();
     *     while (i.hasNext()) {
     *         process (i.next());
     *     }
     * }
     * </pre>
     * <p>
     * This method uses the implementation in {@link java.util.Collections Collections}.
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param map the map to synchronize, must not be null
     * @return a synchronized map backed by the given map
     */
    public static <K, V> Map<K, V> synchronizedMap(final Map<K, V> map) {
        return Collections.synchronizedMap(map);
    }

    /**
     * Returns an unmodifiable map backed by the given map.
     * <p>
     * This method uses the implementation in the decorators subpackage.
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param map the map to make unmodifiable, must not be null
     * @return an unmodifiable map backed by the given map
     * @throws NullPointerException if the map is null
     */
    public static <K, V> Map<K, V> unmodifiableMap(final Map<? extends K, ? extends V> map) {
        return UnmodifiableMap.unmodifiableMap(map);
    }

    /**
     * Returns a predicated (validating) map backed by the given map.
     * <p>
     * Only objects that pass the tests in the given predicates can be added to the map.
     * Trying to add an invalid object results in an IllegalArgumentException.
     * Keys must pass the key predicate, values must pass the value predicate.
     * It is important not to use the original map after invoking this method,
     * as it is a backdoor for adding invalid objects.
     *
     * @param <K>       the key type
     * @param <V>       the value type
     * @param map       the map to predicate, must not be null
     * @param keyPred   the predicate for keys, null means no check
     * @param valuePred the predicate for values, null means no check
     * @return a predicated map backed by the given map
     * @throws NullPointerException if the Map is null
     */
    public static <K, V> IterableMap<K, V> predicatedMap(final Map<K, V> map, final Predicate<? super K> keyPred,
                                                         final Predicate<? super V> valuePred) {
        return MapUtils.predicatedMap(map, keyPred, valuePred);
    }

    /**
     * Returns a transformed map backed by the given map.
     * <p>
     * This method returns a new map (decorating the specified map) that
     * will transform any new entries added to it.
     * Existing entries in the specified map will not be transformed.
     * If you want that behaviour, see {@link TransformedMap#transformedMap}.
     * <p>
     * Each object is passed through the transformers as it is added to the
     * Map. It is important not to use the original map after invoking this
     * method, as it is a backdoor for adding untransformed objects.
     * <p>
     * If there are any elements already in the map being decorated, they
     * are NOT transformed.
     *
     * @param <K>              the key type
     * @param <V>              the value type
     * @param map              the map to transform, must not be null, typically empty
     * @param keyTransformer   the transformer for the map keys, null means no transformation
     * @param valueTransformer the transformer for the map values, null means no transformation
     * @return a transformed map backed by the given map
     * @throws NullPointerException if the Map is null
     */
    public static <K, V> IterableMap<K, V> transformedMap(final Map<K, V> map,
                                                          final Transformer<? super K, ? extends K> keyTransformer,
                                                          final Transformer<? super V, ? extends V> valueTransformer) {
        return MapUtils.transformedMap(map, keyTransformer, valueTransformer);
    }

    /**
     * Returns a fixed-sized map backed by the given map.
     * Elements may not be added or removed from the returned map, but
     * existing elements can be changed (for instance, via the
     * {@link Map#put(Object, Object)} method).
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param map the map whose size to fix, must not be null
     * @return a fixed-size map backed by that map
     * @throws NullPointerException if the Map is null
     */
    public static <K, V> IterableMap<K, V> fixedSizeMap(final Map<K, V> map) {
        return MapUtils.fixedSizeMap(map);
    }

    /**
     * Returns a "lazy" map whose values will be created on demand.
     * <p>
     * When the key passed to the returned map's {@link Map#get(Object)}
     * method is not present in the map, then the factory will be used
     * to create a new object and that object will become the value
     * associated with that key.
     * <p>
     * For instance:
     * <pre>
     * Factory factory = new Factory() {
     *     public Object create() {
     *         return new Date();
     *     }
     * }
     * Map lazyMap = MapUtils.lazyMap(new HashMap(), factory);
     * Object obj = lazyMap.get("test");
     * </pre>
     * <p>
     * After the above code is executed, <code>obj</code> will contain
     * a new <code>Date</code> instance.  Furthermore, that <code>Date</code>
     * instance is the value for the <code>"test"</code> key in the map.
     *
     * @param <K>     the key type
     * @param <V>     the value type
     * @param map     the map to make lazy, must not be null
     * @param factory the factory for creating new objects, must not be null
     * @return a lazy map backed by the given map
     * @throws NullPointerException if the Map or Factory is null
     */
    public static <K, V> IterableMap<K, V> lazyMap(final Map<K, V> map, final Factory<? extends V> factory) {
        return MapUtils.lazyMap(map, factory);
    }

    /**
     * Returns a "lazy" map whose values will be created on demand.
     * <p>
     * When the key passed to the returned map's {@link Map#get(Object)}
     * method is not present in the map, then the factory will be used
     * to create a new object and that object will become the value
     * associated with that key. The factory is a {@link Transformer}
     * that will be passed the key which it must transform into the value.
     * <p>
     * For instance:
     * <pre>
     * Transformer factory = new Transformer() {
     *     public Object transform(Object mapKey) {
     *         return new File(mapKey);
     *     }
     * }
     * Map lazyMap = MapUtils.lazyMap(new HashMap(), factory);
     * Object obj = lazyMap.get("C:/dev");
     * </pre>
     * <p>
     * After the above code is executed, <code>obj</code> will contain
     * a new <code>File</code> instance for the C drive dev directory.
     * Furthermore, that <code>File</code> instance is the value for the
     * <code>"C:/dev"</code> key in the map.
     * <p>
     * If a lazy map is wrapped by a synchronized map, the result is a simple
     * synchronized cache. When an object is not is the cache, the cache itself
     * calls back to the factory Transformer to populate itself, all within the
     * same synchronized block.
     *
     * @param <K>                the key type
     * @param <V>                the value type
     * @param map                the map to make lazy, must not be null
     * @param transformerFactory the factory for creating new objects, must not be null
     * @return a lazy map backed by the given map
     * @throws NullPointerException if the Map or Transformer is null
     */
    public static <K, V> IterableMap<K, V> lazyMap(final Map<K, V> map,
                                                   final Transformer<? super K, ? extends V> transformerFactory) {
        return LazyMap.lazyMap(map, transformerFactory);
    }

    /**
     * Returns a map that maintains the order of keys that are added
     * backed by the given map.
     * <p>
     * If a key is added twice, the order is determined by the first add.
     * The order is observed through the keySet, values and entrySet.
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param map the map to order, must not be null
     * @return an ordered map backed by the given map
     * @throws NullPointerException if the Map is null
     */
    public static <K, V> OrderedMap<K, V> orderedMap(final Map<K, V> map) {
        return MapUtils.orderedMap(map);
    }

    // SortedMap decorators
    //-----------------------------------------------------------------------

    /**
     * Returns a synchronized sorted map backed by the given sorted map.
     * <p>
     * You must manually synchronize on the returned buffer's iterator to
     * avoid non-deterministic behavior:
     *
     * <pre>
     * Map m = MapUtils.synchronizedSortedMap(myMap);
     * Set s = m.keySet();  // outside synchronized block
     * synchronized (m) {  // synchronized on MAP!
     *     Iterator i = s.iterator();
     *     while (i.hasNext()) {
     *         process (i.next());
     *     }
     * }
     * </pre>
     * <p>
     * This method uses the implementation in {@link java.util.Collections Collections}.
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
     * Returns an unmodifiable sorted map backed by the given sorted map.
     * <p>
     * This method uses the implementation in the decorators subpackage.
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param map the sorted map to make unmodifiable, must not be null
     * @return an unmodifiable map backed by the given map
     * @throws NullPointerException if the map is null
     */
    public static <K, V> SortedMap<K, V> unmodifiableSortedMap(final SortedMap<K, ? extends V> map) {
        return UnmodifiableSortedMap.unmodifiableSortedMap(map);
    }

    /**
     * Returns a predicated (validating) sorted map backed by the given map.
     * <p>
     * Only objects that pass the tests in the given predicates can be added to the map.
     * Trying to add an invalid object results in an IllegalArgumentException.
     * Keys must pass the key predicate, values must pass the value predicate.
     * It is important not to use the original map after invoking this method,
     * as it is a backdoor for adding invalid objects.
     *
     * @param <K>       the key type
     * @param <V>       the value type
     * @param map       the map to predicate, must not be null
     * @param keyPred   the predicate for keys, null means no check
     * @param valuePred the predicate for values, null means no check
     * @return a predicated map backed by the given map
     * @throws NullPointerException if the SortedMap is null
     */
    public static <K, V> SortedMap<K, V> predicatedSortedMap(final SortedMap<K, V> map,
                                                             final Predicate<? super K> keyPred, final Predicate<? super V> valuePred) {
        return MapUtils.predicatedSortedMap(map, keyPred, valuePred);
    }

    /**
     * Returns a transformed sorted map backed by the given map.
     * <p>
     * This method returns a new sorted map (decorating the specified map) that
     * will transform any new entries added to it.
     * Existing entries in the specified map will not be transformed.
     * If you want that behaviour, see {@link TransformedSortedMap#transformedSortedMap}.
     * <p>
     * Each object is passed through the transformers as it is added to the
     * Map. It is important not to use the original map after invoking this
     * method, as it is a backdoor for adding untransformed objects.
     * <p>
     * If there are any elements already in the map being decorated, they
     * are NOT transformed.
     *
     * @param <K>              the key type
     * @param <V>              the value type
     * @param map              the map to transform, must not be null, typically empty
     * @param keyTransformer   the transformer for the map keys, null means no transformation
     * @param valueTransformer the transformer for the map values, null means no transformation
     * @return a transformed map backed by the given map
     * @throws NullPointerException if the SortedMap is null
     */
    public static <K, V> SortedMap<K, V> transformedSortedMap(final SortedMap<K, V> map,
                                                              final Transformer<? super K, ? extends K> keyTransformer,
                                                              final Transformer<? super V, ? extends V> valueTransformer) {
        return MapUtils.transformedSortedMap(map, keyTransformer, valueTransformer);
    }

    /**
     * Returns a fixed-sized sorted map backed by the given sorted map.
     * Elements may not be added or removed from the returned map, but
     * existing elements can be changed (for instance, via the
     * {@link Map#put(Object, Object)} method).
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param map the map whose size to fix, must not be null
     * @return a fixed-size map backed by that map
     * @throws NullPointerException if the SortedMap is null
     */
    public static <K, V> SortedMap<K, V> fixedSizeSortedMap(final SortedMap<K, V> map) {
        return MapUtils.fixedSizeSortedMap(map);
    }

    /**
     * Returns a "lazy" sorted map whose values will be created on demand.
     * <p>
     * When the key passed to the returned map's {@link Map#get(Object)}
     * method is not present in the map, then the factory will be used
     * to create a new object and that object will become the value
     * associated with that key.
     * <p>
     * For instance:
     *
     * <pre>
     * Factory factory = new Factory() {
     *     public Object create() {
     *         return new Date();
     *     }
     * }
     * SortedMap lazy = MapUtils.lazySortedMap(new TreeMap(), factory);
     * Object obj = lazy.get("test");
     * </pre>
     * <p>
     * After the above code is executed, <code>obj</code> will contain
     * a new <code>Date</code> instance.  Furthermore, that <code>Date</code>
     * instance is the value for the <code>"test"</code> key.
     *
     * @param <K>     the key type
     * @param <V>     the value type
     * @param map     the map to make lazy, must not be null
     * @param factory the factory for creating new objects, must not be null
     * @return a lazy map backed by the given map
     * @throws NullPointerException if the SortedMap or Factory is null
     */
    public static <K, V> SortedMap<K, V> lazySortedMap(final SortedMap<K, V> map, final Factory<? extends V> factory) {
        return MapUtils.lazySortedMap(map, factory);
    }

    /**
     * Returns a "lazy" sorted map whose values will be created on demand.
     * <p>
     * When the key passed to the returned map's {@link Map#get(Object)}
     * method is not present in the map, then the factory will be used
     * to create a new object and that object will become the value
     * associated with that key. The factory is a {@link Transformer}
     * that will be passed the key which it must transform into the value.
     * <p>
     * For instance:
     * <pre>
     * Transformer factory = new Transformer() {
     *     public Object transform(Object mapKey) {
     *         return new File(mapKey);
     *     }
     * }
     * SortedMap lazy = MapUtils.lazySortedMap(new TreeMap(), factory);
     * Object obj = lazy.get("C:/dev");
     * </pre>
     * <p>
     * After the above code is executed, <code>obj</code> will contain
     * a new <code>File</code> instance for the C drive dev directory.
     * Furthermore, that <code>File</code> instance is the value for the
     * <code>"C:/dev"</code> key in the map.
     * <p>
     * If a lazy map is wrapped by a synchronized map, the result is a simple
     * synchronized cache. When an object is not is the cache, the cache itself
     * calls back to the factory Transformer to populate itself, all within the
     * same synchronized block.
     *
     * @param <K>                the key type
     * @param <V>                the value type
     * @param map                the map to make lazy, must not be null
     * @param transformerFactory the factory for creating new objects, must not be null
     * @return a lazy map backed by the given map
     * @throws NullPointerException if the Map or Transformer is null
     */
    public static <K, V> SortedMap<K, V> lazySortedMap(final SortedMap<K, V> map,
                                                       final Transformer<? super K, ? extends V> transformerFactory) {
        return MapUtils.lazySortedMap(map, transformerFactory);
    }

    /**
     * Populates a Map using the supplied <code>Transformer</code> to transform the elements
     * into keys, using the unaltered element as the value in the <code>Map</code>.
     *
     * @param <K>            the key type
     * @param <V>            the value type
     * @param map            the <code>Map</code> to populate.
     * @param elements       the <code>Iterable</code> containing the input values for the map.
     * @param keyTransformer the <code>Transformer</code> used to transform the element into a key value
     * @throws NullPointerException if the map, elements or transformer are null
     */
    public static <K, V> void populateMap(final Map<K, V> map, final Iterable<? extends V> elements,
                                          final Transformer<V, K> keyTransformer) {
        MapUtils.populateMap(map, elements, keyTransformer, TransformerUtils.<V>nopTransformer());
    }

    /**
     * Populates a Map using the supplied <code>Transformer</code>s to transform the elements
     * into keys and values.
     *
     * @param <K>              the key type
     * @param <V>              the value type
     * @param <E>              the type of object contained in the {@link Iterable}
     * @param map              the <code>Map</code> to populate.
     * @param elements         the <code>Iterable</code> containing the input values for the map.
     * @param keyTransformer   the <code>Transformer</code> used to transform the element into a key value
     * @param valueTransformer the <code>Transformer</code> used to transform the element into a value
     * @throws NullPointerException if the map, elements or transformers are null
     */
    public static <K, V, E> void populateMap(final Map<K, V> map, final Iterable<? extends E> elements,
                                             final Transformer<E, K> keyTransformer,
                                             final Transformer<E, V> valueTransformer) {
        MapUtils.populateMap(map, elements, keyTransformer, valueTransformer);
    }


    /**
     * Get the specified {@link Map} as an {@link IterableMap}.
     *
     * @param <K> the key type
     * @param <V> the value type
     * @param map to wrap if necessary.
     * @return IterableMap&lt;K, V&gt;
     * @throws NullPointerException if map is null
     * @since 4.0
     */
    public static <K, V> IterableMap<K, V> iterableMap(final Map<K, V> map) {
        return MapUtils.iterableMap(map);
    }

    /**
     * Get the specified {@link SortedMap} as an {@link IterableSortedMap}.
     *
     * @param <K>       the key type
     * @param <V>       the value type
     * @param sortedMap to wrap if necessary
     * @return {@link IterableSortedMap}&lt;K, V&gt;
     * @throws NullPointerException if sortedMap is null
     * @since 4.0
     */
    public static <K, V> IterableSortedMap<K, V> iterableSortedMap(final SortedMap<K, V> sortedMap) {
        return MapUtils.iterableSortedMap(sortedMap);
    }

    /**
     * Gets the given map size or 0 if the map is null
     *
     * @param map a Map or null
     * @return the given map size or 0 if the map is null
     */
    public static int size(final Map<?, ?> map) {
        return MapUtils.size(map);
    }

    /**
     * 合并多个map
     *
     * @param maps
     * @param <K>
     * @param <V>
     * @return
     * @throws Exception
     */
    @SafeVarargs
    public static <K, V> Map mergeMaps(Map<K, V>... maps) throws IllegalAccessException, InstantiationException {
        Class clazz = maps[0].getClass(); // 获取传入map的类型
        Map map = null;
        map = (Map) clazz.newInstance();
        for (int i = 0, len = maps.length; i < len; i++) {
            map.putAll(maps[i]);
        }
        return map;
    }
}
