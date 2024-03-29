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
package top.lytree.collections;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

import top.lytree.Filter;
import top.lytree.pattern.Matcher;


public class CollectionUtils {

    /**
     * The index value when an element is not found in a collection or array: {@code -1}.
     */
    public static final int INDEX_NOT_FOUND = -1;

    /**
     * Default prefix used while converting an Iterator to its String representation.
     */
    public static final String DEFAULT_TOSTRING_PREFIX = "[";

    /**
     * Default suffix used while converting an Iterator to its String representation.
     */
    public static final String DEFAULT_TOSTRING_SUFFIX = "]";

    /**
     * A String for Colon  (":").
     */
    public static final String COLON = ":";
    /**
     * A String for Comma (",").
     */
    public static final String COMMA = ",";


    /**
     * An empty unmodifiable collection. The JDK provides empty Set and List implementations which could be used for this purpose. However they could be cast to Set or List which
     * might be undesirable. This implementation only implements Collection.
     */
    @SuppressWarnings("rawtypes") // we deliberately use the raw type here
    public static final Collection EMPTY_COLLECTION = Collections.emptyList();

    /**
     * Returns the immutable EMPTY_COLLECTION with generic type safety.
     *
     * @param <T> the element type
     * @return immutable empty collection
     * @see #EMPTY_COLLECTION
     */
    @SuppressWarnings("unchecked") // OK, empty collection is compatible with any type
    public static <T> Collection<T> emptyCollection() {
        return EMPTY_COLLECTION;
    }

    /**
     * Returns an immutable empty collection if the argument is {@code null}, or the argument itself otherwise.
     *
     * @param <T>        the element type
     * @param collection the collection, possibly {@code null}
     * @return an empty collection if the argument is {@code null}
     */
    public static <T> Collection<T> emptyIfNull(final Collection<T> collection) {
        return collection == null ? CollectionUtils.<T>emptyCollection() : collection;
    }

    /**
     * Returns {@code true} iff all elements of {@code coll2} are also contained in {@code coll1}. The cardinality of values in {@code coll2} is not taken into account, which is
     * the same behavior as {@link Collection#containsAll(Collection)}.
     * <p>
     */
    public static boolean containsAll(final Collection<?> coll1, final Collection<?> coll2) {
        return org.apache.commons.collections4.CollectionUtils.containsAll(coll1, coll2);
    }

    /**
     * Returns {@code true} iff at least one element is in both collections.
     */
    public static <T> boolean containsAny(final Collection<?> coll1, @SuppressWarnings("unchecked") final T... coll2) {
        return org.apache.commons.collections4.CollectionUtils.containsAny(coll1, coll2);
    }

    /**
     * Returns {@code true} iff at least one element is in both collections.
     * <p>
     */
    public static boolean containsAny(final Collection<?> coll1, final Collection<?> coll2) {
        return org.apache.commons.collections4.CollectionUtils.containsAll(coll1, coll2);
    }

    /**
     * Returns a {@link Map} mapping each unique element in the given {@link Collection} to an {@link Integer} representing the number of occurrences of that element in the {@link
     * Collection}.
     * <p>
     * Only those elements present in the collection will appear as keys in the map.
     * </p>
     *
     * @param <O>  the type of object in the returned {@link Map}. This is a super type of &lt;I&gt;.
     * @param coll the collection to get the cardinality map for, must not be null
     * @return the populated cardinality map
     * @throws NullPointerException if coll is null
     */
    public static <O> Map<O, Integer> getCardinalityMap(final Iterable<? extends O> coll) {
        return org.apache.commons.collections4.CollectionUtils.getCardinalityMap(coll);
    }

    /**
     * Returns {@code true} iff <i>a</i> is a sub-collection of <i>b</i>, that is, iff the cardinality of <i>e</i> in <i>a</i> is less than or equal to the cardinality of <i>e</i>
     * in <i>b</i>, for each element <i>e</i> in <i>a</i>.
     *
     * @param a the first (sub?) collection, must not be null
     * @param b the second (super?) collection, must not be null
     * @return {@code true} iff <i>a</i> is a sub-collection of <i>b</i>
     * @throws NullPointerException if either collection is null
     * @see #isProperSubCollection
     * @see Collection#containsAll
     */
    public static boolean isSubCollection(final Collection<?> a, final Collection<?> b) {
        return org.apache.commons.collections4.CollectionUtils.containsAll(a, b);
    }

    /**
     * Returns {@code true} iff <i>a</i> is a <i>proper</i> sub-collection of <i>b</i>, that is, iff the cardinality of <i>e</i> in <i>a</i> is less than or equal to the
     * cardinality of <i>e</i> in <i>b</i>, for each element <i>e</i> in <i>a</i>, and there is at least one element <i>f</i> such that the cardinality of <i>f</i> in <i>b</i> is
     * strictly greater than the cardinality of <i>f</i> in <i>a</i>.
     * <p>
     * The implementation assumes
     * </p>
     * <ul>
     *    <li>{@code a.size()} and {@code b.size()} represent the
     *    total cardinality of <i>a</i> and <i>b</i>, resp. </li>
     *    <li>{@code a.size() &lt; Integer.MAXVALUE}</li>
     * </ul>
     *
     * @param a the first (sub?) collection, must not be null
     * @param b the second (super?) collection, must not be null
     * @return {@code true} iff <i>a</i> is a <i>proper</i> sub-collection of <i>b</i>
     * @throws NullPointerException if either collection is null
     * @see #isSubCollection
     * @see Collection#containsAll
     */
    public static boolean isProperSubCollection(final Collection<?> a, final Collection<?> b) {
        Objects.requireNonNull(a, "a");
        Objects.requireNonNull(b, "b");
        return a.size() < b.size() && CollectionUtils.isSubCollection(a, b);
    }

    /**
     * Returns {@code true} iff the given {@link Collection}s contain exactly the same elements with exactly the same cardinalities.
     * <p>
     * That is, iff the cardinality of <i>e</i> in <i>a</i> is equal to the cardinality of <i>e</i> in <i>b</i>, for each element <i>e</i> in <i>a</i> or <i>b</i>.
     * </p>
     *
     * @param a the first collection, must not be null
     * @param b the second collection, must not be null
     * @return {@code true} iff the collections contain the same elements with the same cardinalities.
     * @throws NullPointerException if either collection is null
     */
    public static boolean isEqualCollection(final Collection<?> a, final Collection<?> b) {
        return org.apache.commons.collections4.CollectionUtils.isEqualCollection(a, b);
    }

    /**
     * Returns the hash code of the input collection using the hash method of an equator.
     *
     * <p>
     * Returns 0 if the input collection is {@code null}.
     * </p>
     *
     * @param <E>        the element type
     * @param collection the input collection
     * @param equator    the equator used for generate hashCode
     * @return the hash code of the input collection using the hash method of an equator
     * @throws NullPointerException if the equator is {@code null}
     */
    public static <E> int hashCode(final Collection<? extends E> collection,
                                   final Equator<? super E> equator) {
        Objects.requireNonNull(equator, "equator");
        if (null == collection) {
            return 0;
        }
        int hashCode = 1;
        for (final E e : collection) {
            hashCode = 31 * hashCode + equator.hash(e);
        }
        return hashCode;
    }

    //-----------------------------------------------------------------------

    /**
     * Adds an element to the collection unless the element is null.
     *
     * @param <T>        the type of object the {@link Collection} contains
     * @param collection the collection to add to, must not be null
     * @param object     the object to add, if null it will not be added
     * @return true if the collection changed
     * @throws NullPointerException if the collection is null
     */
    public static <T> boolean addIgnoreNull(final Collection<T> collection, final T object) {
        Objects.requireNonNull(collection, "collection");
        return object != null && collection.add(object);
    }

    /**
     * Adds all elements in the {@link Iterable} to the given collection. If the {@link Iterable} is a {@link Collection} then it is cast and will be added using {@link
     * Collection#addAll(Collection)} instead of iterating.
     *
     * @param <C>        the type of object the {@link Collection} contains
     * @param collection the collection to add to, must not be null
     * @param iterable   the iterable of elements to add, must not be null
     * @return a boolean indicating whether the collection has changed or not.
     * @throws NullPointerException if the collection or iterable is null
     */
    public static <C> boolean addAll(final Collection<C> collection, final Iterable<? extends C> iterable) {
        Objects.requireNonNull(collection, "collection");
        Objects.requireNonNull(iterable, "iterable");
        if (iterable instanceof Collection<?>) {
            return collection.addAll((Collection<? extends C>) iterable);
        }
        return addAll(collection, iterable.iterator());
    }

    /**
     * Adds all elements in the iteration to the given collection.
     *
     * @param <C>        the type of object the {@link Collection} contains
     * @param collection the collection to add to, must not be null
     * @param iterator   the iterator of elements to add, must not be null
     * @return a boolean indicating whether the collection has changed or not.
     * @throws NullPointerException if the collection or iterator is null
     */
    public static <C> boolean addAll(final Collection<C> collection, final Iterator<? extends C> iterator) {
        Objects.requireNonNull(collection, "collection");
        Objects.requireNonNull(iterator, "iterator");
        boolean changed = false;
        while (iterator.hasNext()) {
            changed |= collection.add(iterator.next());
        }
        return changed;
    }

    /**
     * Adds all elements in the enumeration to the given collection.
     *
     * @param <C>         the type of object the {@link Collection} contains
     * @param collection  the collection to add to, must not be null
     * @param enumeration the enumeration of elements to add, must not be null
     * @return {@code true} if the collections was changed, {@code false} otherwise
     * @throws NullPointerException if the collection or enumeration is null
     */
    public static <C> boolean addAll(final Collection<C> collection, final Enumeration<? extends C> enumeration) {
        Objects.requireNonNull(collection, "collection");
        Objects.requireNonNull(enumeration, "enumeration");
        boolean changed = false;
        while (enumeration.hasMoreElements()) {
            changed |= collection.add(enumeration.nextElement());
        }
        return changed;
    }

    /**
     * Adds all elements in the array to the given collection.
     *
     * @param <C>        the type of object the {@link Collection} contains
     * @param collection the collection to add to, must not be null
     * @param elements   the array of elements to add, must not be null
     * @return {@code true} if the collection was changed, {@code false} otherwise
     * @throws NullPointerException if the collection or elements is null
     */
    public static <C> boolean addAll(final Collection<C> collection, final C... elements) {
        Objects.requireNonNull(collection, "collection");
        Objects.requireNonNull(elements, "elements");
        boolean changed = false;
        for (final C element : elements) {
            changed |= collection.add(element);
        }
        return changed;
    }


    /**
     * Ensures an index is not negative.
     *
     * @param index the index to check.
     * @throws IndexOutOfBoundsException if the index is negative.
     */
    static void checkIndexBounds(final int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("Index cannot be negative: " + index);
        }
    }

    /**
     * Returns the {@code index}-th value in {@code object}, throwing {@code IndexOutOfBoundsException} if there is no such element or {@code IllegalArgumentException} if {@code
     * object} is not an instance of one of the supported types.
     * <p>
     * The supported types, and associated semantics are:
     * </p>
     * <ul>
     * <li> Map -- the value returned is the {@code Map.Entry} in position
     *      {@code index} in the map's {@code entrySet} iterator,
     *      if there is such an entry.</li>
     * <li> List -- this method is equivalent to the list's get method.</li>
     * <li> Array -- the {@code index}-th array entry is returned,
     *      if there is such an entry; otherwise an {@code IndexOutOfBoundsException}
     *      is thrown.</li>
     * <li> Collection -- the value returned is the {@code index}-th object
     *      returned by the collection's default iterator, if there is such an element.</li>
     * <li> Iterator or Enumeration -- the value returned is the
     *      {@code index}-th object in the Iterator/Enumeration, if there
     *      is such an element.  The Iterator/Enumeration is advanced to
     *      {@code index} (or to the end, if {@code index} exceeds the
     *      number of entries) as a side effect of this method.</li>
     * </ul>
     *
     * @param object the object to get a value from
     * @param index  the index to get
     * @return the object at the specified index
     * @throws IndexOutOfBoundsException if the index is invalid
     * @throws IllegalArgumentException  if the object type is invalid
     */
    public static Object get(final Object object, final int index) {
        final int i = index;
        if (i < 0) {
            throw new IndexOutOfBoundsException("Index cannot be negative: " + i);
        }
        if (object instanceof Map<?, ?>) {
            final Map<?, ?> map = (Map<?, ?>) object;
            final Iterator<?> iterator = map.entrySet().iterator();
            return IteratorUtils.get(iterator, i);
        }
        if (object instanceof Object[]) {
            return ((Object[]) object)[i];
        }
        if (object instanceof Iterator<?>) {
            final Iterator<?> it = (Iterator<?>) object;
            return IteratorUtils.get(it, i);
        }
        if (object instanceof Iterable<?>) {
            final Iterable<?> iterable = (Iterable<?>) object;
            return IterableUtils.get(iterable, i);
        }
        if (object instanceof Enumeration<?>) {
            final Enumeration<?> it = (Enumeration<?>) object;
            return EnumerationUtils.get(it, i);
        }
        if (object == null) {
            throw new IllegalArgumentException("Unsupported object type: null");
        }
        try {
            return Array.get(object, i);
        } catch (final IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unsupported object type: " + object.getClass().getName());
        }
    }

    /**
     * Returns the {@code index}-th {@code Map.Entry} in the {@code map}'s {@code entrySet}, throwing {@code IndexOutOfBoundsException} if there is no such element.
     *
     * @param <K>   the key type in the {@link Map}
     * @param <V>   the value type in the {@link Map}
     * @param map   the object to get a value from
     * @param index the index to get
     * @return the object at the specified index
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    public static <K, V> Map.Entry<K, V> get(final Map<K, V> map, final int index) {
        Objects.requireNonNull(map, "map");
        checkIndexBounds(index);
        return (Map.Entry<K, V>) get(map.entrySet(), index);
    }

    /**
     * Gets the size of the collection/iterator specified.
     * <p>
     * This method can handles objects as follows
     * </p>
     * <ul>
     * <li>Collection - the collection size
     * <li>Map - the map size
     * <li>Array - the array size
     * <li>Iterator - the number of elements remaining in the iterator
     * <li>Enumeration - the number of elements remaining in the enumeration
     * </ul>
     *
     * @param object the object to get the size of, may be null
     * @return the size of the specified collection or 0 if the object was null
     * @throws IllegalArgumentException thrown if object is not recognized
     */
    public static int size(final Object object) {
        if (object == null) {
            return 0;
        }
        int total = 0;
        if (object instanceof Map<?, ?>) {
            total = ((Map<?, ?>) object).size();
        } else if (object instanceof Collection<?>) {
            total = ((Collection<?>) object).size();
        } else if (object instanceof Iterable<?>) {
            total = IterableUtils.size((Iterable<?>) object);
        } else if (object instanceof Object[]) {
            total = ((Object[]) object).length;
        } else if (object instanceof Iterator<?>) {
            total = IteratorUtils.size((Iterator<?>) object);
        } else if (object instanceof Enumeration<?>) {
            final Enumeration<?> it = (Enumeration<?>) object;
            while (it.hasMoreElements()) {
                total++;
                it.nextElement();
            }
        } else {
            try {
                total = Array.getLength(object);
            } catch (final IllegalArgumentException ex) {
                throw new IllegalArgumentException("Unsupported object type: " + object.getClass().getName());
            }
        }
        return total;
    }

    /**
     * Checks if the specified collection/array/iterator is empty.
     * <p>
     * This method can handles objects as follows
     * </p>
     * <ul>
     * <li>Collection - via collection isEmpty
     * <li>Map - via map isEmpty
     * <li>Array - using array size
     * <li>Iterator - via hasNext
     * <li>Enumeration - via hasMoreElements
     * </ul>
     * <p>
     * Note: This method is named to avoid clashing with
     * {@link #isEmpty(Collection)}.
     * </p>
     *
     * @param object the object to get the size of, may be null
     * @return true if empty or null
     * @throws IllegalArgumentException thrown if object is not recognized
     */
    public static boolean sizeIsEmpty(final Object object) {
        if (object == null) {
            return true;
        }
        if (object instanceof Collection<?>) {
            return ((Collection<?>) object).isEmpty();
        }
        if (object instanceof Iterable<?>) {
            return IterableUtils.isEmpty((Iterable<?>) object);
        }
        if (object instanceof Map<?, ?>) {
            return ((Map<?, ?>) object).isEmpty();
        }
        if (object instanceof Object[]) {
            return ((Object[]) object).length == 0;
        }
        if (object instanceof Iterator<?>) {
            return ((Iterator<?>) object).hasNext() == false;
        }
        if (object instanceof Enumeration<?>) {
            return ((Enumeration<?>) object).hasMoreElements() == false;
        }
        try {
            return Array.getLength(object) == 0;
        } catch (final IllegalArgumentException ex) {
            throw new IllegalArgumentException("Unsupported object type: " + object.getClass().getName());
        }
    }

    //-----------------------------------------------------------------------

    /**
     * Null-safe check if the specified collection is empty.
     * <p>
     * Null returns true.
     * </p>
     *
     * @param coll the collection to check, may be null
     * @return true if empty or null
     */
    public static boolean isEmpty(Collection<?> coll) {
        return coll == null || coll.isEmpty();
    }

    /**
     * Return {@code true} if the supplied Map is {@code null} or empty. Otherwise, return {@code false}.
     *
     * @param map the Map to check
     * @return whether the given Map is empty
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return (map == null || map.isEmpty());
    }

    /**
     * Null-safe check if the specified collection is not empty.
     * <p>
     * Null returns false.
     * </p>
     *
     * @param coll the collection to check, may be null
     * @return true if non-null and non-empty
     */
    public static boolean isNotEmpty(final Collection<?> coll) {
        return !isEmpty(coll);
    }

    /**
     * 如果给定集合为空，返回默认集合
     *
     * @param <T>               集合类型
     * @param <E>               集合元素类型
     * @param collection        集合
     * @param defaultCollection 默认数组
     * @return 非空（empty）的原集合或默认集合
     */
    public static <T extends Collection<E>, E> T defaultIfEmpty(T collection, T defaultCollection) {
        return isEmpty(collection) ? defaultCollection : collection;
    }

    /**
     * 如果给定集合为空，返回默认集合
     *
     * @param <T>        集合类型
     * @param <E>        集合元素类型
     * @param collection 集合
     * @param supplier   默认值懒加载函数
     * @return 非空（empty）的原集合或默认集合
     */
    public static <T extends Collection<E>, E> T defaultIfEmpty(T collection, Supplier<? extends T> supplier) {
        return isEmpty(collection) ? supplier.get() : collection;
    }

    //-----------------------------------------------------------------------

    /**
     * Reverses the order of the given array.
     *
     * @param array the array to reverse
     */
    public static void reverseArray(final Object[] array) {
        Objects.requireNonNull(array, "array");
        int i = 0;
        int j = array.length - 1;
        Object tmp;

        while (j > i) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
            i++;
        }
    }

    //-----------------------------------------------------------------------

    /**
     * Returns a collection containing all the elements in {@code collection} that are also in {@code retain}. The cardinality of an element {@code e} in the returned collection is
     * the same as the cardinality of {@code e} in {@code collection} unless {@code retain} does not contain {@code e}, in which case the cardinality is zero. This method is useful
     * if you do not wish to modify the collection {@code c} and thus cannot call {@code c.retainAll(retain);}.
     * <p>
     * This implementation iterates over {@code collection}, checking each element in turn to see if it's contained in {@code retain}. If it's contained, it's added to the returned
     * list. As a consequence, it is advised to use a collection type for {@code retain} that provides a fast (e.g. O(1)) implementation of {@link Collection#contains(Object)}.
     * </p>
     *
     * @param <C>        the type of object the {@link Collection} contains
     * @param collection the collection whose contents are the target of the #retailAll operation
     * @param retain     the collection containing the elements to be retained in the returned collection
     * @return a {@code Collection} containing all the elements of {@code collection} that occur at least once in {@code retain}.
     * @throws NullPointerException if either parameter is null
     */
    public static <C> Collection<C> retainAll(final Collection<C> collection, final Collection<?> retain) {
        Objects.requireNonNull(collection, "collection");
        Objects.requireNonNull(retain, "retain");
        return ListUtils.retainAll(collection, retain);
    }

    /**
     * Removes elements whose index are between startIndex, inclusive and endIndex, exclusive in the collection and returns them. This method modifies the input collections.
     *
     * @param <E>        the type of object the {@link Collection} contains
     * @param input      the collection will be operated, must not be null
     * @param startIndex the start index (inclusive) to remove element, must not be less than 0
     * @param endIndex   the end index (exclusive) to remove, must not be less than startIndex
     * @return collection of elements that removed from the input collection
     * @throws NullPointerException if input is null
     */
    public static <E> Collection<E> removeRange(final Collection<E> input, final int startIndex, final int endIndex) {
        Objects.requireNonNull(input, "input");
        if (endIndex < startIndex) {
            throw new IllegalArgumentException("The end index can't be less than the start index.");
        }
        if (input.size() < endIndex) {
            throw new IndexOutOfBoundsException("The end index can't be greater than the size of collection.");
        }
        return CollectionUtils.removeCount(input, startIndex, endIndex - startIndex);
    }

    /**
     * Removes the specified number of elements from the start index in the collection and returns them. This method modifies the input collections.
     *
     * @param <E>        the type of object the {@link Collection} contains
     * @param input      the collection will be operated, can't be null
     * @param startIndex the start index (inclusive) to remove element, can't be less than 0
     * @param count      the specified number to remove, can't be less than 1
     * @return collection of elements that removed from the input collection
     * @throws NullPointerException if input is null
     */
    public static <E> Collection<E> removeCount(final Collection<E> input, int startIndex, int count) {
        Objects.requireNonNull(input, "input");
        if (startIndex < 0) {
            throw new IndexOutOfBoundsException("The start index can't be less than 0.");
        }
        if (count < 0) {
            throw new IndexOutOfBoundsException("The count can't be less than 0.");
        }
        if (input.size() < startIndex + count) {
            throw new IndexOutOfBoundsException(
                    "The sum of start index and count can't be greater than the size of collection.");
        }

        final Collection<E> result = new ArrayList<>(count);
        final Iterator<E> iterator = input.iterator();
        while (count > 0) {
            if (startIndex > 0) {
                startIndex = startIndex - 1;
                iterator.next();
                continue;
            }
            count = count - 1;
            result.add(iterator.next());
            iterator.remove();
        }
        return result;
    }

    /**
     * Removes the elements in {@code remove} from {@code collection}. That is, this method returns a collection containing all the elements in {@code c} that are not in {@code
     * remove}. The cardinality of an element {@code e} in the returned collection is the same as the cardinality of {@code e} in {@code collection} unless {@code remove} contains
     * {@code e}, in which case the cardinality is zero. This method is useful if you do not wish to modify the collection {@code c} and thus cannot call {@code
     * collection.removeAll(remove);}.
     * <p>
     * This implementation iterates over {@code collection}, checking each element in turn to see if it's contained in {@code remove}. If it's not contained, it's added to the
     * returned list. As a consequence, it is advised to use a collection type for {@code remove} that provides a fast (e.g. O(1)) implementation of {@link
     * Collection#contains(Object)}.
     * </p>
     *
     * @param <E>        the type of object the {@link Collection} contains
     * @param collection the collection from which items are removed (in the returned collection)
     * @param remove     the items to be removed from the returned {@code collection}
     * @return a {@code Collection} containing all the elements of {@code collection} except any elements that also occur in {@code remove}.
     * @throws NullPointerException if either parameter is null
     */
    public static <E> Collection<E> removeAll(final Collection<E> collection, final Collection<?> remove) {
        return ListUtils.removeAll(collection, remove);
    }

    /**
     * 去除指定元素，此方法直接修改原集合
     *
     * @param <T>        集合类型
     * @param <E>        集合元素类型
     * @param collection 集合
     * @param filter     过滤器
     * @return 处理后的集合
     */
    public static <T extends Collection<E>, E> T filter(T collection, final Filter<E> filter) {
        return filter(collection, filter);
    }

    /**
     * 过滤集合，此方法在原集合上直接修改<br> 通过实现Filter接口，完成元素的过滤，这个Filter实现可以实现以下功能：
     *
     * <pre>
     * 1、过滤出需要的对象，{@link Filter#accept(Object)}方法返回false的对象将被使用{@link Iterator#remove()}方法移除
     * </pre>
     *
     * @param <T>    集合类型
     * @param <E>    集合元素类型
     * @param iter   集合
     * @param filter 过滤器接口
     * @return 编辑后的集合
     */
    public static <T extends Iterable<E>, E> T filter(T iter, Filter<E> filter) {
        if (null == iter) {
            return null;
        }

        filter(iter.iterator(), filter);

        return iter;
    }

    /**
     * 过滤集合，此方法在原集合上直接修改<br> 通过实现Filter接口，完成元素的过滤，这个Filter实现可以实现以下功能：
     *
     * <pre>
     * 1、过滤出需要的对象，{@link Filter#accept(Object)}方法返回false的对象将被使用{@link Iterator#remove()}方法移除
     * </pre>
     *
     * @param <E>    集合元素类型
     * @param iter   集合
     * @param filter 过滤器接口，删除{@link Filter#accept(Object)}为{@code false}的元素
     * @return 编辑后的集合
     */
    public static <E> Iterator<E> filter(Iterator<E> iter, Filter<E> filter) {
        if (null == iter || null == filter) {
            return iter;
        }

        while (iter.hasNext()) {
            if (!filter.accept(iter.next())) {
                iter.remove();
            }
        }
        return iter;
    }

    /**
     * 集合中匹配规则的数量
     *
     * @param <T>      集合元素类型
     * @param iterable {@link Iterable}
     * @param matcher  匹配器，为空则全部匹配
     * @return 匹配数量
     */
    public static <T> int count(Iterable<T> iterable, Matcher<T> matcher) {
        int count = 0;
        if (null != iterable) {
            for (T t : iterable) {
                if (null == matcher || matcher.match(t)) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * 获取匹配规则定义中匹配到元素的第一个位置<br> 此方法对于某些无序集合的位置信息，以转换为数组后的位置为准。
     *
     * @param <T>        元素类型
     * @param collection 集合
     * @param matcher    匹配器，为空则全部匹配
     * @return 第一个位置
     */
    public static <T> int indexOf(Collection<T> collection, Matcher<T> matcher) {
        if (isNotEmpty(collection)) {
            int index = 0;
            for (T t : collection) {
                if (null == matcher || matcher.match(t)) {
                    return index;
                }
                index++;
            }
        }
        return -1;
    }

    /**
     * 获取匹配规则定义中匹配到元素的最后位置<br> 此方法对于某些无序集合的位置信息，以转换为数组后的位置为准。
     *
     * @param <T>        元素类型
     * @param collection 集合
     * @param matcher    匹配器，为空则全部匹配
     * @return 最后一个位置
     */
    public static <T> int lastIndexOf(Collection<T> collection, Matcher<T> matcher) {
        int matchIndex = -1;
        if (isNotEmpty(collection)) {
            int index = collection.size();
            for (T t : collection) {
                if (null == matcher || matcher.match(t)) {
                    matchIndex = index;
                }
                index--;
            }
        }
        return matchIndex;
    }

    /**
     * 获取匹配规则定义中匹配到元素的所有位置<br> 此方法对于某些无序集合的位置信息，以转换为数组后的位置为准。
     *
     * @param <T>        元素类型
     * @param collection 集合
     * @param matcher    匹配器，为空则全部匹配
     * @return 位置数组
     */
    public static <T> List<Integer> indexOfAll(Collection<T> collection, Matcher<T> matcher) {
        final List<Integer> indexList = new ArrayList<>();
        if (null != collection) {
            int index = 0;
            for (T t : collection) {
                if (null == matcher || matcher.match(t)) {
                    indexList.add(index);
                }
                index++;
            }
        }
        return indexList;
    }

    /**
     * Extract the lone element of the specified Collection.
     *
     * @param <E>        collection type
     * @param collection to read
     * @return sole member of collection
     * @throws NullPointerException     if collection is null
     * @throws IllegalArgumentException if collection is empty or contains more than one element
     */
    public static <E> E extractSingleton(final Collection<E> collection) {
        Objects.requireNonNull(collection, "collection");
        if (collection.size() != 1) {
            throw new IllegalArgumentException("Can extract singleton only when collection size == 1");
        }
        return collection.iterator().next();
    }

    // region

    /**
     * 去重集合
     *
     * @param <T>        集合元素类型
     * @param collection 集合
     * @return {@link ArrayList}
     */
    public static <T> ArrayList<T> distinct(final Collection<T> collection) {
        if (isEmpty(collection)) {
            return new ArrayList<>();
        } else if (collection instanceof Set) {
            return new ArrayList<>(collection);
        } else {
            return new ArrayList<>(new LinkedHashSet<>(collection));
        }
    }

    /**
     * 根据函数生成的KEY去重集合，如根据Bean的某个或者某些字段完成去重。<br>
     * 去重可选是保留最先加入的值还是后加入的值
     *
     * @param <T>             集合元素类型
     * @param <K>             唯一键类型
     * @param collection      集合
     * @param uniqueGenerator 唯一键生成器
     * @param override        是否覆盖模式，如果为{@code true}，加入的新值会覆盖相同key的旧值，否则会忽略新加值
     * @return {@link ArrayList}
     * @since 5.8.0
     */
    public static <T, K> List<T> distinct(final Collection<T> collection, final Function<T, K> uniqueGenerator, final boolean override) {
        if (isEmpty(collection)) {
            return new ArrayList<>();
        }

        final UniqueKeySet<K, T> set = new UniqueKeySet<>(true, uniqueGenerator);
        if (override) {
            set.addAll(collection);
        } else {
            set.addAllIfAbsent(collection);
        }
        return new ArrayList<>(set);
    }

    // endregion
}
