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


import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Predicate;


/**
 * Provides utility methods and decorators for
 *
 * @author Pride_Yang
 */
public class SetUtils {

    /**
     * An unmodifiable <b>view</b> of a set that may be backed by other sets.
     * <p>
     * If the decorated sets change, this view will change as well. The contents
     * of this view can be transferred to another instance via the {@link #copyInto(Set)}
     * and {@link #toSet()} methods.
     *
     * @param <E> the element type
     * @since 4.1
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
         * Return an iterator for this view; the returned iterator is
         * not required to be unmodifiable.
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
     * Returns an immutable empty set if the argument is {@code null},
     * or the argument itself otherwise.
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
     * Generates a hash code using the algorithm specified in
     * {@link Set#hashCode()}.
     * <p>
     * This method is useful for implementing {@code Set} when you cannot
     * extend AbstractSet. The method takes Collection instances to enable other
     * collection types to use the Set implementation algorithm.
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
     * Creates a set from the given items. If the passed var-args argument is {@code
     * null}, then the method returns {@code null}.
     *
     * @param <E>   the element type
     * @param items the elements that make up the new set
     * @return a set
     * @since 4.3
     */
    public static <E> HashSet<E> hashSet(final E... items) {
        if (items == null) {
            return null;
        }
        return new HashSet<>(Arrays.asList(items));
    }

    /**
     * Tests two sets for equality as per the {@code equals()} contract
     * in {@link Set#equals(Object)}.
     * <p>
     * This method is useful for implementing {@code Set} when you cannot
     * extend AbstractSet. The method takes Collection instances to enable other
     * collection types to use the Set implementation algorithm.
     * <p>
     * The relevant text (slightly paraphrased as this is a static method) is:
     * <blockquote>
     * <p>Two sets are considered equal if they have
     * the same size, and every member of the first set is contained in
     * the second. This ensures that the {@code equals} method works
     * properly across different implementations of the {@code Set}
     * interface.</p>
     *
     * <p>
     * This implementation first checks if the two sets are the same object:
     * if so it returns {@code true}.  Then, it checks if the two sets are
     * identical in size; if not, it returns false. If so, it returns
     * {@code a.containsAll((Collection) b)}.</p>
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
     * Returns a new hash set that matches elements based on {@code ==} not
     * {@code equals()}.
     * <p>
     * <strong>This set will violate the detail of various Set contracts.</strong>
     * As a general rule, don't compare this set to other sets. In particular, you can't
     * use decorators like on it, which silently assume that these
     * contracts are fulfilled.
     * <p>
     * <strong>Note that the returned set is not synchronized and is not thread-safe.</strong>
     * If you wish to use this set from multiple threads concurrently, you must use
     * appropriate synchronization. The simplest approach is to wrap this map
     * using {@link Collections#synchronizedSet(Set)}. This class may throw
     * exceptions when accessed by concurrent threads without synchronization.
     *
     * @param <E> the element type
     * @return a new identity hash set
     * @since 4.1
     */
    public static <E> Set<E> newIdentityHashSet() {
        return Collections.newSetFromMap(new IdentityHashMap<E, Boolean>());
    }

    /**
     * Don't allow instances.
     */
    private SetUtils() {
    }
}
