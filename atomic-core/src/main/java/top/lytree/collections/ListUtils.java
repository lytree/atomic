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


import static top.lytree.collections.Lists.computeArrayListCapacity;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.RandomAccess;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

import top.lytree.collections.Lists.RandomAccessReverseList;
import top.lytree.collections.Lists.ReverseList;


/**
 * 为{@link List}实例提供实用方法和装饰器。
 */
public class ListUtils {

    /**
     * Don't allow instances.
     */
    private ListUtils() {
    }

    //-----------------------------------------------------------------------

    /**
     * 如果参数是{@code null}，返回一个不可变的空列表，否则返回参数本身。
     *
     * @param <T>  the element type
     * @param list the list, possibly {@code null}
     *
     * @return an empty list if the argument is {@code null}
     */
    public static <T> List<T> emptyIfNull(final List<T> list) {
        return list == null ? Collections.<T>emptyList() : list;
    }

    /**
     * 返回传入的列表，如果列表是{@code null}，则返回{@code defaultList}的值。
     *
     * @param <T>         元素类型
     * @param list        列表,可能为{@code null}
     * @param defaultList the returned values if list is {@code null}
     *
     * @return an empty list if the argument is {@code null}
     */
    public static <T> List<T> defaultIfNull(final List<T> list, final List<T> defaultList) {
        return list == null ? defaultList : list;
    }

    /**
     * 返回一个新列表，其中包含两个给定列表中包含的所有元素。
     *
     * @param <E>   the element type
     * @param list1 the first list
     * @param list2 the second list
     *
     * @return the intersection of those two lists
     *
     * @throws NullPointerException if either list is null
     */
    public static <E> List<E> intersection(final List<? extends E> list1, final List<? extends E> list2) {
        final List<E> result = new ArrayList<>();

        List<? extends E> smaller = list1;
        List<? extends E> larger = list2;
        if (list1.size() > list2.size()) {
            smaller = list2;
            larger = list1;
        }

        final HashSet<E> hashSet = new HashSet<>(smaller);

        for (final E e : larger) {
            if (hashSet.contains(e)) {
                result.add(e);
                hashSet.remove(e);
            }
        }
        return result;
    }

    /**
     * 返回一个新列表，其中包含附加到第一个列表的第二个列表。{@link List#addAll(Collection)}操作用于将两个给定的列表追加到一个新列表中.
     *
     * @param <E>   the element type
     * @param list1 the first list
     * @param list2 the second list
     *
     * @return a new list containing the union of those lists
     *
     * @throws NullPointerException if either list is null
     */
    public static <E> List<E> union(final List<? extends E> list1, final List<? extends E> list2) {
        final ArrayList<E> result = new ArrayList<>(list1.size() + list2.size());
        result.addAll(list1);
        result.addAll(list2);
        return result;
    }


    /**
     * Tests two lists for value-equality as per the equality contract in {@link List#equals(Object)}.
     * <p>
     * This method is useful for implementing {@code List} when you cannot extend AbstractList. The method takes Collection instances to enable other collection types to use the
     * List implementation algorithm.
     * <p>
     * The relevant text (slightly paraphrased as this is a static method) is:
     * <blockquote>
     * Compares the two list objects for equality.  Returns {@code true} if and only if both lists have the same size, and all corresponding pairs of elements in the two lists are
     * <i>equal</i>.  (Two elements {@code e1} and {@code e2} are <i>equal</i> if <code>(e1==null ? e2==null : e1.equals(e2))</code>.)  In other words, two lists are defined to be
     * equal if they contain the same elements in the same order.  This definition ensures that the equals method works properly across different implementations of the {@code
     * List} interface.
     * </blockquote>
     *
     * <b>Note:</b> The behavior of this method is undefined if the lists are
     * modified during the equals comparison.
     *
     * @param list1 the first list, may be null
     * @param list2 the second list, may be null
     *
     * @return whether the lists are equal by value comparison
     *
     * @see List
     */
    public static boolean isEqualList(final Collection<?> list1, final Collection<?> list2) {
        if (list1 == list2) {
            return true;
        }
        if (list1 == null || list2 == null || list1.size() != list2.size()) {
            return false;
        }

        final Iterator<?> it1 = list1.iterator();
        final Iterator<?> it2 = list2.iterator();
        Object obj1 = null;
        Object obj2 = null;

        while (it1.hasNext() && it2.hasNext()) {
            obj1 = it1.next();
            obj2 = it2.next();

            if (!(obj1 == null ? obj2 == null : obj1.equals(obj2))) {
                return false;
            }
        }

        return !(it1.hasNext() || it2.hasNext());
    }

    /**
     * Generates a hash code using the algorithm specified in {@link List#hashCode()}.
     * <p>
     * This method is useful for implementing {@code List} when you cannot extend AbstractList. The method takes Collection instances to enable other collection types to use the
     * List implementation algorithm.
     *
     * @param list the list to generate the hashCode for, may be null
     *
     * @return the hash code
     *
     * @see List#hashCode()
     */
    public static int hashCodeForList(final Collection<?> list) {
        if (list == null) {
            return 0;
        }
        int hashCode = 1;
        final Iterator<?> it = list.iterator();

        while (it.hasNext()) {
            final Object obj = it.next();
            hashCode = 31 * hashCode + (obj == null ? 0 : obj.hashCode());
        }
        return hashCode;
    }

    //-----------------------------------------------------------------------

    /**
     * Returns a List containing all the elements in {@code collection} that are also in {@code retain}. The cardinality of an element {@code e} in the returned list is the same as
     * the cardinality of {@code e} in {@code collection} unless {@code retain} does not contain {@code e}, in which case the cardinality is zero. This method is useful if you do
     * not wish to modify the collection {@code c} and thus cannot call {@code collection.retainAll(retain);}.
     * <p>
     * This implementation iterates over {@code collection}, checking each element in turn to see if it's contained in {@code retain}. If it's contained, it's added to the returned
     * list. As a consequence, it is advised to use a collection type for {@code retain} that provides a fast (e.g. O(1)) implementation of {@link Collection#contains(Object)}.
     *
     * @param <E>        the element type
     * @param collection the collection whose contents are the target of the #retailAll operation
     * @param retain     the collection containing the elements to be retained in the returned collection
     *
     * @return a {@code List} containing all the elements of {@code c} that occur at least once in {@code retain}.
     *
     * @throws NullPointerException if either parameter is null
     */
    public static <E> List<E> retainAll(final Collection<E> collection, final Collection<?> retain) {
        final List<E> list = new ArrayList<>(Math.min(collection.size(), retain.size()));

        for (final E obj : collection) {
            if (retain.contains(obj)) {
                list.add(obj);
            }
        }
        return list;
    }

    /**
     * Removes the elements in {@code remove} from {@code collection}. That is, this method returns a list containing all the elements in {@code collection} that are not in {@code
     * remove}. The cardinality of an element {@code e} in the returned collection is the same as the cardinality of {@code e} in {@code collection} unless {@code remove} contains
     * {@code e}, in which case the cardinality is zero. This method is useful if you do not wish to modify {@code collection} and thus cannot call {@code
     * collection.removeAll(remove);}.
     * <p>
     * This implementation iterates over {@code collection}, checking each element in turn to see if it's contained in {@code remove}. If it's not contained, it's added to the
     * returned list. As a consequence, it is advised to use a collection type for {@code remove} that provides a fast (e.g. O(1)) implementation of {@link
     * Collection#contains(Object)}.
     *
     * @param <E>        the element type
     * @param collection the collection from which items are removed (in the returned collection)
     * @param remove     the items to be removed from the returned {@code collection}
     *
     * @return a {@code List} containing all the elements of {@code c} except any elements that also occur in {@code remove}.
     *
     * @throws NullPointerException if either parameter is null
     */
    public static <E> List<E> removeAll(final Collection<E> collection, final Collection<?> remove) {
        final List<E> list = new ArrayList<>();
        for (final E obj : collection) {
            if (!remove.contains(obj)) {
                list.add(obj);
            }
        }
        return list;
    }

    /**
     * 通过func自定义一个规则，此规则将原集合中的元素转换成新的元素，生成新的列表返回<br> 例如提供的是一个Bean列表，通过Function接口实现获取某个字段值，返回这个字段值组成的新列表
     *
     * @param <T>        集合元素类型
     * @param <R>        返回集合元素类型
     * @param collection 原集合
     * @param func       编辑函数
     * @param ignoreNull 是否忽略空值，这里的空值包括函数处理前和处理后的null值
     *
     * @return 抽取后的新列表
     */
    public static <T, R> List<R> map(Iterable<T> collection, Function<? super T, ? extends R> func, boolean ignoreNull) {
        final List<R> fieldValueList = new ArrayList<>();
        if (null == collection) {
            return fieldValueList;
        }

        R value;
        for (T t : collection) {
            if (null == t && ignoreNull) {
                continue;
            }
            value = func.apply(t);
            if (null == value && ignoreNull) {
                continue;
            }
            fieldValueList.add(value);
        }
        return fieldValueList;
    }

    /**
     * A simple wrapper to use a CharSequence as List.
     */
    private static final class CharSequenceAsList extends AbstractList<Character> {

        private final CharSequence sequence;

        CharSequenceAsList(final CharSequence sequence) {
            this.sequence = sequence;
        }

        @Override
        public Character get(final int index) {
            return Character.valueOf(sequence.charAt(index));
        }

        @Override
        public int size() {
            return sequence.length();
        }
    }

    //-----------------------------------------------------------------------

    /**
     * Returns consecutive {@link List#subList(int, int) sublists} of a list, each of the same size (the final list may be smaller). For example, partitioning a list containing
     * {@code [a, b, c, d, e]} with a partition size of 3 yields {@code [[a, b, c], [d, e]]} -- an outer list containing two inner lists of three and two elements, all in the
     * original order.
     * <p>
     * The outer list is unmodifiable, but reflects the latest state of the source list. The inner lists are sublist views of the original list, produced on demand using {@link
     * List#subList(int, int)}, and are subject to all the usual caveats about modification as explained in that API.
     * <p>
     * Adapted from http://code.google.com/p/guava-libraries/
     *
     * @param <T>  the element type
     * @param list the list to return consecutive sublists of
     * @param size the desired size of each sublist (the last may be smaller)
     *
     * @return a list of consecutive sublists
     *
     * @throws NullPointerException     if list is null
     * @throws IllegalArgumentException if size is not strictly positive
     */
    public static <T> List<List<T>> partition(final List<T> list, final int size) {
        Objects.requireNonNull(list, "list");
        if (size <= 0) {
            throw new IllegalArgumentException("Size must be greater than 0");
        }
        return new Partition<>(list, size);
    }

    /**
     * Provides a partition view on a {@link List}.
     */
    private static class Partition<T> extends AbstractList<List<T>> {

        private final List<T> list;
        private final int size;

        private Partition(final List<T> list, final int size) {
            this.list = list;
            this.size = size;
        }

        @Override
        public List<T> get(final int index) {
            final int listSize = size();
            if (index < 0) {
                throw new IndexOutOfBoundsException("Index " + index + " must not be negative");
            }
            if (index >= listSize) {
                throw new IndexOutOfBoundsException("Index " + index + " must be less than size " +
                        listSize);
            }
            final int start = index * size;
            final int end = Math.min(start + size, list.size());
            return list.subList(start, end);
        }

        @Override
        public int size() {
            return (int) Math.ceil((double) list.size() / (double) size);
        }

        @Override
        public boolean isEmpty() {
            return list.isEmpty();
        }
    }

    /**
     * 新建一个ArrayList
     *
     * @param <T>    集合元素类型
     * @param values 数组
     *
     * @return ArrayList对象
     */
    @SafeVarargs
    public static <T> ArrayList<T> toList(T... values) {
        return (ArrayList<T>) newArrayList(false, values);
    }

    /**
     * 数组转为一个不可变List<br> 类似于Java9中的List.of
     *
     * @param ts  对象
     * @param <T> 对象类型
     *
     * @return 不可修改List
     */
    @SafeVarargs
    public static <T> List<T> of(T... ts) {
        if (ArrayUtils.isEmpty(ts)) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(toList(ts));
    }

    /**
     * 新建一个CopyOnWriteArrayList
     *
     * @param <T>        集合元素类型
     * @param collection 集合
     *
     * @return {@link CopyOnWriteArrayList}
     */
    public static <T> CopyOnWriteArrayList<T> toCopyOnWriteArrayList(Collection<T> collection) {
        return (null == collection) ? (new CopyOnWriteArrayList<>()) : (new CopyOnWriteArrayList<>(collection));
    }

    /**
     * 新建一个空List
     *
     * @param <T> 集合元素类型
     *
     * @return List对象
     */
    public static <T> List<T> newArrayList() {
        return new ArrayList<>();
    }

    /**
     * 新建一个List
     *
     * @param <T>    集合元素类型
     * @param values 数组
     *
     * @return List对象
     */
    @SafeVarargs
    public static <T> List<T> newArrayList(T... values) {
        if (ArrayUtils.isEmpty(values)) {
            return newArrayList();
        }
        int capacity = computeArrayListCapacity(values.length);
        final List<T> arrayList = new ArrayList<>(capacity);
        Collections.addAll(arrayList, values);
        return arrayList;
    }

    /**
     * 新建一个List
     *
     * @param <T>        集合元素类型
     * @param collection 集合
     *
     * @return List对象
     */
    public static <T> List<T> newArrayList(Collection<T> collection) {
        if (null == collection) {
            return newArrayList();
        }
        return new ArrayList<>(collection);
    }

    /**
     * 新建一个List<br> 提供的参数为null时返回空{@link ArrayList}
     *
     * @param <T>      集合元素类型
     * @param iterable {@link Iterable}
     *
     * @return List对象
     */
    public static <T> List<T> newArrayList(Iterable<T> iterable) {
        if (null == iterable) {
            return newArrayList();
        }
        return newArrayList(iterable.iterator());
    }

    /**
     * 新建一个ArrayList<br> 提供的参数为null时返回空{@link ArrayList}
     *
     * @param <T>  集合元素类型
     * @param iter {@link Iterator}
     *
     * @return ArrayList对象
     */
    public static <T> List<T> newArrayList(Iterator<T> iter) {
        final List<T> list = newArrayList();
        if (null != iter) {
            while (iter.hasNext()) {
                list.add(iter.next());
            }
        }
        return list;
    }

    /**
     * 新建一个List<br> 提供的参数为null时返回空{@link ArrayList}
     *
     * @param <T>        集合元素类型
     * @param enumration {@link Enumeration}
     *
     * @return ArrayList对象
     */
    public static <T> List<T> newArrayList(Enumeration<T> enumration) {
        final List<T> list = newArrayList();
        if (null != enumration) {
            while (enumration.hasMoreElements()) {
                list.add(enumration.nextElement());
            }
        }
        return list;
    }

    /**
     * 针对List排序，排序会修改原List
     *
     * @param <T>  元素类型
     * @param list 被排序的List
     * @param c    {@link Comparator}
     *
     * @return 原list
     *
     * @see Collections#sort(List, Comparator)
     */
    public static <T> List<T> sort(List<T> list, Comparator<? super T> c) {
        if (CollectionUtils.isEmpty(list)) {
            return list;
        }
        list.sort(c);
        return list;
    }

    public static <E> LinkedList<E> newLinkedList() {
        return new LinkedList<>();
    }

    public static <E> LinkedList<E> newLinkedList(
            Iterable<? extends E> elements) {
        LinkedList<E> list = newLinkedList();
        IterableUtils.addAll(list, elements);
        return list;
    }

    /**
     * Returns a reversed view of the specified list. For example, {@code Lists.reverse(Arrays.asList(1, 2, 3))} returns a list containing {@code 3, 2, 1}. The returned list is
     * backed by this list, so changes in the returned list are reflected in this list, and vice-versa. The returned list supports all of the optional list operations supported by
     * this list.
     *
     * <p>The returned list is random-access if the specified list is random access.
     */
    public static <T> List<T> reverse(List<T> list) {
        if (list instanceof ReverseList) {
            return ((ReverseList<T>) list).getForwardList();
        } else if (list instanceof RandomAccess) {
            return new RandomAccessReverseList<>(list);
        } else {
            return new ReverseList<>(list);
        }
    }


}
