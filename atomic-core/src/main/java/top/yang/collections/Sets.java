/*
 * Copyright (C) 2007 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.yang.collections;


import static top.yang.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Consumer;
import java.util.stream.Collector;
import java.util.stream.Stream;


/**
 * Static utility methods pertaining to {@link Set} instances. Also see this class's counterparts {@link Lists}, {@link Maps} and {@link Queues}.
 *
 * <p>See the Guava User Guide article on <a href=
 * "https://github.com/google/guava/wiki/CollectionUtilitiesExplained#sets">{@code Sets}</a>.
 *
 * @author Kevin Bourrillion
 * @author Jared Levy
 * @author Chris Povirk
 * @since 2.0
 */

public final class Sets {

    private Sets() {
    }

    /**
     * Returns a new hash set using the smallest initial table size that can hold {@code expectedSize} elements without resizing. Note that this is not what {@link
     * HashSet#HashSet(int)} does, but it is what most users want and expect it to do.
     *
     * <p>This behavior can't be broadly guaranteed, but has been tested with OpenJDK 1.7 and 1.8.
     *
     * @param expectedSize the number of elements you expect to add to the returned set
     * @return a new, empty hash set with enough capacity to hold {@code expectedSize} elements without resizing
     * @throws IllegalArgumentException if {@code expectedSize} is negative
     */
    public static <E> HashSet<E> newHashSetWithExpectedSize(
            int expectedSize) {
        return new HashSet<E>(Maps.capacity(expectedSize));
    }

    /**
     * {@link AbstractSet} substitute without the potentially-quadratic {@code removeAll} implementation.
     */
    abstract static class ImprovedAbstractSet<E extends Object> extends AbstractSet<E> {

        @Override
        public boolean removeAll(Collection<?> c) {
            return removeAllImpl(this, c);
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            return super.retainAll(checkNotNull(c)); // GWT compatibility
        }
    }

    /**
     * Remove each element in an iterable from a set.
     */
    static boolean removeAllImpl(Set<?> set, Iterator<?> iterator) {
        boolean changed = false;
        while (iterator.hasNext()) {
            changed |= set.remove(iterator.next());
        }
        return changed;
    }

    static boolean removeAllImpl(Set<?> set, Collection<?> collection) {
        checkNotNull(collection); // for GWT
        if (collection instanceof Multiset) {
            collection = ((Multiset<?>) collection).elementSet();
        }
        /*
         * AbstractSet.removeAll(List) has quadratic behavior if the list size
         * is just more than the set's size.  We augment the test by
         * assuming that sets have fast contains() performance, and other
         * collections don't.  See
         * http://code.google.com/p/guava-libraries/issues/detail?id=1013
         */
        if (collection instanceof Set && collection.size() > set.size()) {
            return Iterators.removeAll(set.iterator(), collection);
        } else {
            return removeAllImpl(set, collection.iterator());
        }
    }


}
