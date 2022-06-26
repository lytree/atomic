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

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import top.yang.Filter;
import top.yang.collections.iterators.ArrayIterator;
import top.yang.collections.iterators.ArrayListIterator;
import top.yang.collections.iterators.EmptyIterator;
import top.yang.collections.iterators.EmptyListIterator;
import top.yang.collections.iterators.EmptyMapIterator;
import top.yang.collections.iterators.EnumerationIterator;
import top.yang.collections.iterators.IteratorEnumeration;
import top.yang.collections.iterators.IteratorIterable;
import top.yang.collections.iterators.ListIteratorWrapper;
import top.yang.collections.iterators.MapIterator;
import top.yang.collections.iterators.ObjectArrayIterator;
import top.yang.collections.iterators.ObjectArrayListIterator;
import top.yang.collections.iterators.ResettableIterator;
import top.yang.collections.iterators.ResettableListIterator;
import top.yang.collections.iterators.SingletonIterator;
import top.yang.collections.iterators.SingletonListIterator;


/**
 * Provides static utility methods and decorators for {@link Iterator} instances. The implementations are provided in the iterators subpackage.
 *
 *
 */
public class IteratorUtils {
    // validation is done in this class in certain cases because the
    // public classes allow invalid states

    /**
     * An iterator over no elements.
     */
    @SuppressWarnings("rawtypes")
    public static final ResettableIterator EMPTY_ITERATOR = EmptyIterator.RESETTABLE_INSTANCE;

    /**
     * A list iterator over no elements.
     */
    @SuppressWarnings("rawtypes")
    public static final ResettableListIterator EMPTY_LIST_ITERATOR = EmptyListIterator.RESETTABLE_INSTANCE;


    /**
     * A map iterator over no elements.
     */
    @SuppressWarnings("rawtypes")
    public static final MapIterator EMPTY_MAP_ITERATOR = EmptyMapIterator.INSTANCE;

    /**
     * Default delimiter used to delimit elements while converting an Iterator to its String representation.
     */
    private static final String DEFAULT_TOSTRING_DELIMITER = ", ";

    /**
     * Don't allow instances.
     */
    private IteratorUtils() {
    }

    // Empty
    //-----------------------------------------------------------------------

    /**
     * Gets an empty iterator.
     * <p>
     * This iterator is a valid iterator object that will iterate over nothing.
     *
     * @param <E> the element type
     * @return an iterator over nothing
     */
    public static <E> ResettableIterator<E> emptyIterator() {
        return EmptyIterator.<E>resettableEmptyIterator();
    }

    /**
     * Gets an empty list iterator.
     * <p>
     * This iterator is a valid list iterator object that will iterate over nothing.
     *
     * @param <E> the element type
     * @return a list iterator over nothing
     */
    public static <E> ResettableListIterator<E> emptyListIterator() {
        return EmptyListIterator.<E>resettableEmptyListIterator();
    }


    /**
     * Gets an empty map iterator.
     * <p>
     * This iterator is a valid map iterator object that will iterate over nothing.
     *
     * @param <K> the key type
     * @param <V> the value type
     * @return a map iterator over nothing
     */
    public static <K, V> MapIterator<K, V> emptyMapIterator() {
        return EmptyMapIterator.<K, V>emptyMapIterator();
    }

    // Singleton
    //-----------------------------------------------------------------------

    /**
     * Gets a singleton iterator.
     * <p>
     * This iterator is a valid iterator object that will iterate over the specified object.
     *
     * @param <E>    the element type
     * @param object the single object over which to iterate
     * @return a singleton iterator over the object
     */
    public static <E> ResettableIterator<E> singletonIterator(final E object) {
        return new SingletonIterator<>(object);
    }

    /**
     * Gets a singleton list iterator.
     * <p>
     * This iterator is a valid list iterator object that will iterate over the specified object.
     *
     * @param <E>    the element type
     * @param object the single object over which to iterate
     * @return a singleton list iterator over the object
     */
    public static <E> ListIterator<E> singletonListIterator(final E object) {
        return new SingletonListIterator<>(object);
    }

    // Arrays
    //-----------------------------------------------------------------------

    /**
     * Gets an iterator over an object array.
     *
     * @param <E>   the element type
     * @param array the array over which to iterate
     * @return an iterator over the array
     * @throws NullPointerException if array is null
     */
    public static <E> ResettableIterator<E> arrayIterator(final E... array) {
        return new ObjectArrayIterator<>(array);
    }

    /**
     * Gets an iterator over an object or primitive array.
     * <p>
     * This method will handle primitive arrays as well as object arrays. The primitives will be wrapped in the appropriate wrapper class.
     *
     * @param <E>   the element type
     * @param array the array over which to iterate
     * @return an iterator over the array
     * @throws IllegalArgumentException if the array is not an array
     * @throws NullPointerException     if array is null
     */
    public static <E> ResettableIterator<E> arrayIterator(final Object array) {
        return new ArrayIterator<>(array);
    }

    /**
     * Gets an iterator over the end part of an object array.
     *
     * @param <E>   the element type
     * @param array the array over which to iterate
     * @param start the index to start iterating at
     * @return an iterator over part of the array
     * @throws IndexOutOfBoundsException if start is less than zero or greater than the length of the array
     * @throws NullPointerException      if array is null
     */
    public static <E> ResettableIterator<E> arrayIterator(final E[] array, final int start) {
        return new ObjectArrayIterator<>(array, start);
    }

    /**
     * Gets an iterator over the end part of an object or primitive array.
     * <p>
     * This method will handle primitive arrays as well as object arrays. The primitives will be wrapped in the appropriate wrapper class.
     *
     * @param <E>   the element type
     * @param array the array over which to iterate
     * @param start the index to start iterating at
     * @return an iterator over part of the array
     * @throws IllegalArgumentException  if the array is not an array
     * @throws IndexOutOfBoundsException if start is less than zero or greater than the length of the array
     * @throws NullPointerException      if array is null
     */
    public static <E> ResettableIterator<E> arrayIterator(final Object array, final int start) {
        return new ArrayIterator<>(array, start);
    }

    /**
     * Gets an iterator over part of an object array.
     *
     * @param <E>   the element type
     * @param array the array over which to iterate
     * @param start the index to start iterating at
     * @param end   the index to finish iterating at
     * @return an iterator over part of the array
     * @throws IndexOutOfBoundsException if array bounds are invalid
     * @throws IllegalArgumentException  if end is before start
     * @throws NullPointerException      if array is null
     */
    public static <E> ResettableIterator<E> arrayIterator(final E[] array, final int start, final int end) {
        return new ObjectArrayIterator<>(array, start, end);
    }

    /**
     * Gets an iterator over part of an object or primitive array.
     * <p>
     * This method will handle primitive arrays as well as object arrays. The primitives will be wrapped in the appropriate wrapper class.
     *
     * @param <E>   the element type
     * @param array the array over which to iterate
     * @param start the index to start iterating at
     * @param end   the index to finish iterating at
     * @return an iterator over part of the array
     * @throws IllegalArgumentException  if the array is not an array or end is before start
     * @throws IndexOutOfBoundsException if array bounds are invalid
     * @throws NullPointerException      if array is null
     */
    public static <E> ResettableIterator<E> arrayIterator(final Object array, final int start, final int end) {
        return new ArrayIterator<>(array, start, end);
    }

    //-----------------------------------------------------------------------

    /**
     * Gets a list iterator over an object array.
     *
     * @param <E>   the element type
     * @param array the array over which to iterate
     * @return a list iterator over the array
     * @throws NullPointerException if array is null
     */
    public static <E> ResettableListIterator<E> arrayListIterator(final E... array) {
        return new ObjectArrayListIterator<>(array);
    }

    /**
     * Gets a list iterator over an object or primitive array.
     * <p>
     * This method will handle primitive arrays as well as object arrays. The primitives will be wrapped in the appropriate wrapper class.
     *
     * @param <E>   the element type
     * @param array the array over which to iterate
     * @return a list iterator over the array
     * @throws IllegalArgumentException if the array is not an array
     * @throws NullPointerException     if array is null
     */
    public static <E> ResettableListIterator<E> arrayListIterator(final Object array) {
        return new ArrayListIterator<>(array);
    }

    /**
     * Gets a list iterator over the end part of an object array.
     *
     * @param <E>   the element type
     * @param array the array over which to iterate
     * @param start the index to start iterating at
     * @return a list iterator over part of the array
     * @throws IndexOutOfBoundsException if start is less than zero
     * @throws NullPointerException      if array is null
     */
    public static <E> ResettableListIterator<E> arrayListIterator(final E[] array, final int start) {
        return new ObjectArrayListIterator<>(array, start);
    }

    /**
     * Gets a list iterator over the end part of an object or primitive array.
     * <p>
     * This method will handle primitive arrays as well as object arrays. The primitives will be wrapped in the appropriate wrapper class.
     *
     * @param <E>   the element type
     * @param array the array over which to iterate
     * @param start the index to start iterating at
     * @return a list iterator over part of the array
     * @throws IllegalArgumentException  if the array is not an array
     * @throws IndexOutOfBoundsException if start is less than zero
     * @throws NullPointerException      if array is null
     */
    public static <E> ResettableListIterator<E> arrayListIterator(final Object array, final int start) {
        return new ArrayListIterator<>(array, start);
    }

    /**
     * Gets a list iterator over part of an object array.
     *
     * @param <E>   the element type
     * @param array the array over which to iterate
     * @param start the index to start iterating at
     * @param end   the index to finish iterating at
     * @return a list iterator over part of the array
     * @throws IndexOutOfBoundsException if array bounds are invalid
     * @throws IllegalArgumentException  if end is before start
     * @throws NullPointerException      if array is null
     */
    public static <E> ResettableListIterator<E> arrayListIterator(final E[] array, final int start, final int end) {
        return new ObjectArrayListIterator<>(array, start, end);
    }

    /**
     * Gets a list iterator over part of an object or primitive array.
     * <p>
     * This method will handle primitive arrays as well as object arrays. The primitives will be wrapped in the appropriate wrapper class.
     *
     * @param <E>   the element type
     * @param array the array over which to iterate
     * @param start the index to start iterating at
     * @param end   the index to finish iterating at
     * @return a list iterator over part of the array
     * @throws IllegalArgumentException  if the array is not an array or end is before start
     * @throws IndexOutOfBoundsException if array bounds are invalid
     * @throws NullPointerException      if array is null
     */
    public static <E> ResettableListIterator<E> arrayListIterator(final Object array, final int start, final int end) {
        return new ArrayListIterator<>(array, start, end);
    }
    // Views
    //-----------------------------------------------------------------------

    /**
     * Gets an iterator that provides an iterator view of the given enumeration.
     *
     * @param <E>         the element type
     * @param enumeration the enumeration to use, may not be null
     * @return a new iterator
     * @throws NullPointerException if enumeration is null
     */
    public static <E> Iterator<E> asIterator(final Enumeration<? extends E> enumeration) {
        return new EnumerationIterator<>(Objects.requireNonNull(enumeration, "enumeration"));
    }

    /**
     * Gets an iterator that provides an iterator view of the given enumeration that will remove elements from the specified collection.
     *
     * @param <E>              the element type
     * @param enumeration      the enumeration to use, may not be null
     * @param removeCollection the collection to remove elements from, may not be null
     * @return a new iterator
     * @throws NullPointerException if enumeration or removeCollection is null
     */
    public static <E> Iterator<E> asIterator(final Enumeration<? extends E> enumeration,
            final Collection<? super E> removeCollection) {
        return new EnumerationIterator<>(Objects.requireNonNull(enumeration, "enumeration"),
                Objects.requireNonNull(removeCollection, "removeCollection"));
    }

    /**
     * Gets an enumeration that wraps an iterator.
     *
     * @param <E>      the element type
     * @param iterator the iterator to use, may not be null
     * @return a new enumeration
     * @throws NullPointerException if iterator is null
     */
    public static <E> Enumeration<E> asEnumeration(final Iterator<? extends E> iterator) {
        return new IteratorEnumeration<>(Objects.requireNonNull(iterator, "iterator"));
    }

    /**
     * Gets an {@link Iterable} that wraps an iterator.  The returned {@link Iterable} can be used for a single iteration.
     *
     * @param <E>      the element type
     * @param iterator the iterator to use, may not be null
     * @return a new, single use {@link Iterable}
     * @throws NullPointerException if iterator is null
     */
    public static <E> Iterable<E> asIterable(final Iterator<? extends E> iterator) {
        Objects.requireNonNull(iterator, "iterator");
        return new IteratorIterable<>(iterator, false);
    }

    /**
     * Gets a list iterator based on a simple iterator.
     * <p>
     * As the wrapped Iterator is traversed, a LinkedList of its values is cached, permitting all required operations of ListIterator.
     *
     * @param <E>      the element type
     * @param iterator the iterator to use, may not be null
     * @return a new iterator
     * @throws NullPointerException if iterator parameter is null
     */
    public static <E> ListIterator<E> toListIterator(final Iterator<? extends E> iterator) {
        Objects.requireNonNull(iterator, "iterator");
        return new ListIteratorWrapper<>(iterator);
    }

    /**
     * Gets an array based on an iterator.
     * <p>
     * As the wrapped Iterator is traversed, an ArrayList of its values is created. At the end, this is converted to an array.
     *
     * @param iterator the iterator to use, not null
     * @return an array of the iterator contents
     * @throws NullPointerException if iterator parameter is null
     */
    public static Object[] toArray(final Iterator<?> iterator) {
        Objects.requireNonNull(iterator, "iterator");
        final List<?> list = toList(iterator, 100);
        return list.toArray();
    }

    /**
     * Gets an array based on an iterator.
     * <p>
     * As the wrapped Iterator is traversed, an ArrayList of its values is created. At the end, this is converted to an array.
     *
     * @param <E>        the element type
     * @param iterator   the iterator to use, not null
     * @param arrayClass the class of array to create
     * @return an array of the iterator contents
     * @throws NullPointerException if iterator parameter or arrayClass is null
     * @throws ArrayStoreException  if the arrayClass is invalid
     */
    public static <E> E[] toArray(final Iterator<? extends E> iterator, final Class<E> arrayClass) {
        Objects.requireNonNull(iterator, "iterator");
        Objects.requireNonNull(arrayClass, "arrayClass");
        final List<E> list = toList(iterator, 100);
        @SuppressWarnings("unchecked") final E[] array = (E[]) Array.newInstance(arrayClass, list.size());
        return list.toArray(array);
    }

    /**
     * Gets a list based on an iterator.
     * <p>
     * As the wrapped Iterator is traversed, an ArrayList of its values is created. At the end, the list is returned.
     *
     * @param <E>      the element type
     * @param iterator the iterator to use, not null
     * @return a list of the iterator contents
     * @throws NullPointerException if iterator parameter is null
     */
    public static <E> List<E> toList(final Iterator<? extends E> iterator) {
        return toList(iterator, 10);
    }

    /**
     * Gets a list based on an iterator.
     * <p>
     * As the wrapped Iterator is traversed, an ArrayList of its values is created. At the end, the list is returned.
     *
     * @param <E>           the element type
     * @param iterator      the iterator to use, not null
     * @param estimatedSize the initial size of the ArrayList
     * @return a list of the iterator contents
     * @throws NullPointerException     if iterator parameter is null
     * @throws IllegalArgumentException if the size is less than 1
     */
    public static <E> List<E> toList(final Iterator<? extends E> iterator, final int estimatedSize) {
        Objects.requireNonNull(iterator, "iterator");
        if (estimatedSize < 1) {
            throw new IllegalArgumentException("Estimated size must be greater than 0");
        }
        final List<E> list = new ArrayList<>(estimatedSize);
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        return list;
    }

    /**
     * Gets a suitable Iterator for the given object.
     * <p>
     * This method can handle objects as follows
     * <ul>
     * <li>null - empty iterator
     * <li>Iterator - returned directly
     * <li>Enumeration - wrapped
     * <li>Collection - iterator from collection returned
     * <li>Map - values iterator returned
     * <li>Dictionary - values (elements) enumeration returned as iterator
     * <li>array - iterator over array returned
     * <li>object with iterator() public method accessed by reflection
     * <li>object - singleton iterator
     * <li>NodeList - iterator over the list
     * <li>Node - iterator over the child nodes
     * </ul>
     *
     * @param obj the object to convert to an iterator
     * @return a suitable iterator, never null
     */
    public static Iterator<?> getIterator(final Object obj) {
        if (obj == null) {
            return emptyIterator();
        }
        if (obj instanceof Iterator) {
            return (Iterator<?>) obj;
        }
        if (obj instanceof Iterable) {
            return ((Iterable<?>) obj).iterator();
        }
        if (obj instanceof Object[]) {
            return new ObjectArrayIterator<>((Object[]) obj);
        }
        if (obj instanceof Enumeration) {
            return new EnumerationIterator<>((Enumeration<?>) obj);
        }
        if (obj instanceof Map) {
            return ((Map<?, ?>) obj).values().iterator();
        }
        if (obj instanceof Dictionary) {
            return new EnumerationIterator<>(((Dictionary<?, ?>) obj).elements());
        }
        if (obj.getClass().isArray()) {
            return new ArrayIterator<>(obj);
        }
        try {
            final Method method = obj.getClass().getMethod("iterator", (Class[]) null);
            if (Iterator.class.isAssignableFrom(method.getReturnType())) {
                final Iterator<?> it = (Iterator<?>) method.invoke(obj, (Object[]) null);
                if (it != null) {
                    return it;
                }
            }
        } catch (final RuntimeException | NoSuchMethodException | IllegalAccessException |
                InvocationTargetException e) { // NOPMD
            // ignore
        }
        return singletonIterator(obj);
    }

    /**
     * Checks if the given iterator is empty.
     * <p>
     * A {@code null} or empty iterator returns true.
     *
     * @param iterator the {@link Iterator} to use, may be null
     * @return true if the iterator is exhausted or null, false otherwise
     *
     */
    public static boolean isEmpty(final Iterator<?> iterator) {
        return iterator == null || !iterator.hasNext();
    }

    /**
     * Returns the {@code index}-th value in {@link Iterator}, throwing {@code IndexOutOfBoundsException} if there is no such element.
     * <p>
     * The Iterator is advanced to {@code index} (or to the end, if {@code index} exceeds the number of entries) as a side effect of this method.
     *
     * @param <E>      the type of object in the {@link Iterator}
     * @param iterator the iterator to get a value from
     * @param index    the index to get
     * @return the object at the specified index
     * @throws IndexOutOfBoundsException if the index is invalid
     *
     */
    public static <E> E get(final Iterator<E> iterator, final int index) {
        int i = index;
        CollectionUtils.checkIndexBounds(i);
        while (iterator.hasNext()) {
            i--;
            if (i == -1) {
                return iterator.next();
            }
            iterator.next();
        }
        throw new IndexOutOfBoundsException("Entry does not exist: " + i);
    }

    /**
     * Shortcut for {@code get(iterator, 0)}.
     * <p>
     * Returns the {@code first} value in {@link Iterator}, throwing {@code IndexOutOfBoundsException} if there is no such element.
     * </p>
     * <p>
     * The Iterator is advanced to {@code 0} (or to the end, if {@code 0} exceeds the number of entries) as a side effect of this method.
     * </p>
     *
     * @param <E>      the type of object in the {@link Iterator}
     * @param iterator the iterator to get a value from
     * @return the first object
     * @throws IndexOutOfBoundsException if the request is invalid
     *
     */
    public static <E> E first(final Iterator<E> iterator) {
        return get(iterator, 0);
    }

    /**
     * Returns the number of elements contained in the given iterator.
     * <p>
     * A {@code null} or empty iterator returns {@code 0}.
     *
     * @param iterator the iterator to check, may be null
     * @return the number of elements contained in the iterator
     *
     */
    public static int size(final Iterator<?> iterator) {
        int size = 0;
        if (iterator != null) {
            while (iterator.hasNext()) {
                iterator.next();
                size++;
            }
        }
        return size;
    }


    /**
     * 获取集合的第一个非空元素
     *
     * @param <T>      集合元素类型
     * @param iterable {@link Iterable}
     * @return 第一个元素
     *
     */
    public static <T> T getFirstNoneNull(Iterable<T> iterable) {
        if (null == iterable) {
            return null;
        }
        return getFirstNoneNull(iterable.iterator());
    }

    /**
     * 获取集合的第一个非空元素
     *
     * @param <T>      集合元素类型
     * @param iterator {@link Iterator}
     * @return 第一个非空元素，null表示未找到
     *
     */
    public static <T> T getFirstNoneNull(Iterator<T> iterator) {
        if (null != iterator) {
            while (iterator.hasNext()) {
                final T next = iterator.next();
                if (!Objects.isNull(next)) {
                    return next;
                }
            }
        }
        return null;
    }

}
