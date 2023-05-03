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


import static top.lytree.collections.CollectionUtils.isNotEmpty;

import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import top.lytree.base.Predicate;


/**
 * Provides utility methods and decorators for
 *
 * @author Pride_Yang
 */
public class SetUtils {


    /**
     * 一个不可修改的<b>视图<b>的集合，该集合可能被其他集合支持。
     * <p>
     * 如果被装饰的设置发生变化，这个视图也会发生变化. 此视图的内容可以通过 {@link #copyInto(Set)} and {@link #toSet()} methods.
     *
     * @param <E> 元素类型
     */
    public abstract static class SetView<E> extends AbstractSet<E> {

        /**
         * Copies the contents of this view into the provided set.
         *
         * @param <S> the set type
         * @param set the set for copying the contents
         */
        public <S extends Set<E>> void copyInto(final S set) {
            CollectionUtils.addAll(set, this);
        }

        /**
         * Return an iterator for this view; the returned iterator is not required to be unmodifiable.
         *
         * @return a new iterator for this view
         */
        protected abstract Iterator<E> createIterator();

        @Override
        public Iterator<E> iterator() {
            return createIterator();
        }

        @Override
        public int size() {
            return IteratorUtils.size(iterator());
        }

        /**
         * Returns a new set containing the contents of this view.
         *
         * @return a new set containing all elements of this view
         */
        public Set<E> toSet() {
            final Set<E> set = new HashSet<>(size());
            copyInto(set);
            return set;
        }
    }

    /**
     * Returns an immutable empty set if the argument is {@code null}, or the argument itself otherwise.
     *
     * @param <T> the element type
     * @param set the set, possibly {@code null}
     * @return an empty set if the argument is {@code null}
     */
    public static <T> Set<T> emptyIfNull(final Set<T> set) {
        return set == null ? Collections.<T>emptySet() : set;
    }

    //-----------------------------------------------------------------------

    /**
     * Get a typed empty unmodifiable Set.
     *
     * @param <E> the element type
     * @return an empty Set
     */
    public static <E> Set<E> emptySet() {
        return Collections.<E>emptySet();
    }

    /**
     * Generates a hash code using the algorithm specified in {@link Set#hashCode()}.
     * <p>
     * This method is useful for implementing {@code Set} when you cannot extend AbstractSet. The method takes Collection instances to enable other collection types to use the Set
     * implementation algorithm.
     *
     * @param <T> the element type
     * @param set the set to calculate the hash code for, may be null
     * @return the hash code
     * @see Set#hashCode()
     */
    public static <T> int hashCodeForSet(final Collection<T> set) {
        if (set == null) {
            return 0;
        }

        int hashCode = 0;
        for (final T obj : set) {
            if (obj != null) {
                hashCode += obj.hashCode();
            }
        }
        return hashCode;
    }

    /**
     * Creates a set from the given items. If the passed var-args argument is {@code null}, then the method returns {@code null}.
     *
     * @param <E>   the element type
     * @param items the elements that make up the new set
     * @return a set
     */
    @SafeVarargs
    public static <E> HashSet<E> hashSet(final E... items) {
        if (items == null) {
            return null;
        }
        return new HashSet<>(Arrays.asList(items));
    }

    /**
     * Tests two sets for equality as per the {@code equals()} contract in {@link Set#equals(Object)}.
     * <p>
     * This method is useful for implementing {@code Set} when you cannot extend AbstractSet. The method takes Collection instances to enable other collection types to use the Set
     * implementation algorithm.
     * <p>
     * The relevant text (slightly paraphrased as this is a static method) is:
     * <blockquote>
     * <p>Two sets are considered equal if they have
     * the same size, and every member of the first set is contained in the second. This ensures that the {@code equals} method works properly across different implementations of
     * the {@code Set} interface.</p>
     *
     * <p>
     * This implementation first checks if the two sets are the same object: if so it returns {@code true}.  Then, it checks if the two sets are identical in size; if not, it
     * returns false. If so, it returns {@code a.containsAll((Collection) b)}.</p>
     * </blockquote>
     *
     * @param set1 the first set, may be null
     * @param set2 the second set, may be null
     * @return whether the sets are equal by value comparison
     * @see Set
     */
    public static boolean isEqualSet(final Collection<?> set1, final Collection<?> set2) {
        if (set1 == set2) {
            return true;
        }
        if (set1 == null || set2 == null || set1.size() != set2.size()) {
            return false;
        }

        return set1.containsAll(set2);
    }

    /**
     * Returns a new hash set that matches elements based on {@code ==} not {@code equals()}.
     * <p>
     * <strong>This set will violate the detail of various Set contracts.</strong>
     * As a general rule, don't compare this set to other sets. In particular, you can't use decorators like on it, which silently assume that these contracts are fulfilled.
     * <p>
     * <strong>Note that the returned set is not synchronized and is not thread-safe.</strong>
     * If you wish to use this set from multiple threads concurrently, you must use appropriate synchronization. The simplest approach is to wrap this map using {@link
     * Collections#synchronizedSet(Set)}. This class may throw exceptions when accessed by concurrent threads without synchronization.
     *
     * @param <E> the element type
     * @return a new identity hash set
     */
    public static <E> Set<E> newIdentityHashSet() {
        return Collections.newSetFromMap(new IdentityHashMap<E, Boolean>());
    }

    /**
     * Returns a unmodifiable <b>view</b> containing the difference of the given {@link Set}s, denoted by {@code a \ b} (or {@code a - b}).
     * <p>
     * The returned view contains all elements of {@code a} that are not a member of {@code b}.
     *
     * @param <E>  the generic type that is able to represent the types contained in both input sets.
     * @param setA the set to subtract from, must not be null
     * @param setB the set to subtract, must not be null
     * @return a view of the relative complement of  of the two sets
     * 
     */
    public static <E> SetView<E> difference(final Set<? extends E> setA, final Set<? extends E> setB) {
        Objects.requireNonNull(setA, "setA");
        Objects.requireNonNull(setB, "setB");

        final Predicate<E> notContainedInB = object -> !setB.contains(object);

        return new SetView<E>() {
            @Override
            public boolean contains(final Object o) {
                return setA.contains(o) && !setB.contains(o);
            }

            @Override
            public Iterator<E> createIterator() {
                return IteratorUtils.filteredIterator(setA.iterator(), notContainedInB);
            }
        };
    }

    /**
     * Returns a unmodifiable <b>view</b> of the union of the given {@link Set}s.
     * <p>
     * The returned view contains all elements of {@code a} and {@code b}.
     *
     * @param <E>  the generic type that is able to represent the types contained in both input sets.
     * @param setA the first set, must not be null
     * @param setB the second set, must not be null
     * @return a view of the union of the two set
     * @throws NullPointerException if either input set is null
     * 
     */
    public static <E> SetView<E> union(final Set<? extends E> setA, final Set<? extends E> setB) {
        Objects.requireNonNull(setA, "setA");
        Objects.requireNonNull(setB, "setB");

        final SetView<E> bMinusA = difference(setB, setA);

        return new SetView<E>() {
            @Override
            public boolean contains(final Object o) {
                return setA.contains(o) || setB.contains(o);
            }

            @Override
            public Iterator<E> createIterator() {
                return IteratorUtils.chainedIterator(setA.iterator(), bMinusA.iterator());
            }

            @Override
            public boolean isEmpty() {
                return setA.isEmpty() && setB.isEmpty();
            }

            @Override
            public int size() {
                return setA.size() + bMinusA.size();
            }
        };
    }

    public static <E> String join(Set<E> set, String separator, boolean isIgnoreNull, String... otherParams) {
        StringBuilder strBuilder = new StringBuilder();
        boolean isFirst = true;
        if (isNotEmpty(set)) {
            for (E entry : set) {
                if (!isIgnoreNull || entry != null) {
                    if (isFirst) {
                        isFirst = false;
                    } else {
                        strBuilder.append(separator);
                    }
                }
            }
        }
        // 补充其它字符串到末尾，默认无分隔符
        if (ArrayUtils.isNotEmpty(otherParams)) {
            for (String otherParam : otherParams) {
                strBuilder.append(otherParam);
            }
        }
        return strBuilder.toString();
    }

    /**
     * Don't allow instances.
     */
    private SetUtils() {
    }
}
