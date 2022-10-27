package top.lytree.collections;


import java.util.AbstractMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Consumer;
import top.lytree.base.Assert;
import top.lytree.bean.ObjectUtils;
import top.lytree.collections.MapDifference.ValueDifference;
import top.lytree.collections.comparator.Ordering;
import top.lytree.math.Ints;

public final class Maps {

    private Maps() {
    }

    static <K, V> Iterator<K> keyIterator(
            Iterator<Entry<K, V>> entryIterator) {
        return new TransformedIterator<Entry<K, V>, K>(entryIterator) {
            @Override
            K transform(Entry<K, V> entry) {
                return entry.getKey();
            }
        };
    }

    static <K, V> Iterator<V> valueIterator(
            Iterator<Entry<K, V>> entryIterator) {
        return new TransformedIterator<Entry<K, V>, V>(entryIterator) {
            @Override
            V transform(Entry<K, V> entry) {
                return entry.getValue();
            }
        };
    }

    /**
     * Delegates to {@link Map#get}. Returns {@code null} on {@code ClassCastException} and {@code NullPointerException}.
     */

    static <V> V safeGet(Map<?, V> map, Object key) {
        Assert.isNull(map);
        try {
            return map.get(key);
        } catch (ClassCastException | NullPointerException e) {
            return null;
        }
    }

    static class KeySet<K, V>
            extends Sets.ImprovedAbstractSet<K> {

        final Map<K, V> map;

        KeySet(Map<K, V> map) {
            this.map = Assert.notNull(map);
        }

        Map<K, V> map() {
            return map;
        }

        @Override
        public Iterator<K> iterator() {
            return keyIterator(map().entrySet().iterator());
        }

        @Override
        public void forEach(Consumer<? super K> action) {
            Assert.notNull(action);
            // avoids entry allocation for those maps that allocate entries on iteration
            map.forEach((k, v) -> action.accept(k));
        }

        @Override
        public int size() {
            return map().size();
        }

        @Override
        public boolean isEmpty() {
            return map().isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return map().containsKey(o);
        }

        @Override
        public boolean remove(Object o) {
            if (contains(o)) {
                map().remove(o);
                return true;
            }
            return false;
        }

        @Override
        public void clear() {
            map().clear();
        }
    }

    abstract static class EntrySet<K, V>
            extends Sets.ImprovedAbstractSet<Entry<K, V>> {

        abstract Map<K, V> map();

        @Override
        public int size() {
            return map().size();
        }

        @Override
        public void clear() {
            map().clear();
        }

        @Override
        public boolean contains(Object o) {
            if (o instanceof Entry) {
                Entry<?, ?> entry = (Entry<?, ?>) o;
                Object key = entry.getKey();
                V value = Maps.safeGet(map(), key);
                return Objects.equals(value, entry.getValue()) && (value != null || map().containsKey(key));
            }
            return false;
        }

        @Override
        public boolean isEmpty() {
            return map().isEmpty();
        }

        @Override
        public boolean remove(Object o) {
            /*
             * `o instanceof Entry` is guaranteed by `contains`, but we check it here to satisfy our
             * nullness checker.
             */
            if (contains(o) && o instanceof Entry) {
                Entry<?, ?> entry = (Entry<?, ?>) o;
                return map().keySet().remove(entry.getKey());
            }
            return false;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            try {
                return super.removeAll(Assert.notNull(c));
            } catch (UnsupportedOperationException e) {
                // if the iterators don't support remove
                return Sets.removeAllImpl(this, c.iterator());
            }
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            try {
                return super.retainAll(Assert.notNull(c));
            } catch (UnsupportedOperationException e) {
                // if the iterators don't support remove
                Set<Object> keys = Sets.newHashSetWithExpectedSize(c.size());
                for (Object o : c) {
                    /*
                     * `o instanceof Entry` is guaranteed by `contains`, but we check it here to satisfy our
                     * nullness checker.
                     */
                    if (contains(o) && o instanceof Entry) {
                        Entry<?, ?> entry = (Entry<?, ?>) o;
                        keys.add(entry.getKey());
                    }
                }
                return map().keySet().retainAll(keys);
            }
        }
    }

    abstract static class IteratorBasedAbstractMap<
            K, V>
            extends AbstractMap<K, V> {

        @Override
        public abstract int size();

        abstract Iterator<Entry<K, V>> entryIterator();

        Spliterator<Entry<K, V>> entrySpliterator() {
            return Spliterators.spliterator(
                    entryIterator(), size(), Spliterator.SIZED | Spliterator.DISTINCT);
        }

        @Override
        public Set<Entry<K, V>> entrySet() {
            return new EntrySet<K, V>() {
                @Override
                Map<K, V> map() {
                    return IteratorBasedAbstractMap.this;
                }

                @Override
                public Iterator<Entry<K, V>> iterator() {
                    return entryIterator();
                }

                @Override
                public Spliterator<Entry<K, V>> spliterator() {
                    return entrySpliterator();
                }

                @Override
                public void forEach(Consumer<? super Entry<K, V>> action) {
                    forEachEntry(action);
                }
            };
        }

        void forEachEntry(Consumer<? super Entry<K, V>> action) {
            entryIterator().forEachRemaining(action);
        }

        @Override
        public void clear() {
            Iterators.clear(entryIterator());
        }
    }

    private static <K, V> Map<K, V> unmodifiableMap(
            Map<K, ? extends V> map) {
        if (map instanceof SortedMap) {
            return Collections.unmodifiableSortedMap((SortedMap<K, ? extends V>) map);
        } else {
            return Collections.unmodifiableMap(map);
        }
    }

    static class MapDifferenceImpl<K, V>
            implements MapDifference<K, V> {

        final Map<K, V> onlyOnLeft;
        final Map<K, V> onlyOnRight;
        final Map<K, V> onBoth;
        final Map<K, ValueDifference<V>> differences;

        MapDifferenceImpl(
                Map<K, V> onlyOnLeft,
                Map<K, V> onlyOnRight,
                Map<K, V> onBoth,
                Map<K, ValueDifference<V>> differences) {
            this.onlyOnLeft = unmodifiableMap(onlyOnLeft);
            this.onlyOnRight = unmodifiableMap(onlyOnRight);
            this.onBoth = unmodifiableMap(onBoth);
            this.differences = unmodifiableMap(differences);
        }

        @Override
        public boolean areEqual() {
            return onlyOnLeft.isEmpty() && onlyOnRight.isEmpty() && differences.isEmpty();
        }

        @Override
        public Map<K, V> entriesOnlyOnLeft() {
            return onlyOnLeft;
        }

        @Override
        public Map<K, V> entriesOnlyOnRight() {
            return onlyOnRight;
        }

        @Override
        public Map<K, V> entriesInCommon() {
            return onBoth;
        }

        @Override
        public Map<K, ValueDifference<V>> entriesDiffering() {
            return differences;
        }

        @Override
        public boolean equals(Object object) {
            if (object == this) {
                return true;
            }
            if (object instanceof MapDifference) {
                MapDifference<?, ?> other = (MapDifference<?, ?>) object;
                return entriesOnlyOnLeft().equals(other.entriesOnlyOnLeft())
                        && entriesOnlyOnRight().equals(other.entriesOnlyOnRight())
                        && entriesInCommon().equals(other.entriesInCommon())
                        && entriesDiffering().equals(other.entriesDiffering());
            }
            return false;
        }

        @Override
        public int hashCode() {
            return ObjectUtils.hashCode(
                    entriesOnlyOnLeft(), entriesOnlyOnRight(), entriesInCommon(), entriesDiffering());
        }

        @Override
        public String toString() {
            if (areEqual()) {
                return "equal";
            }

            StringBuilder result = new StringBuilder("not equal");
            if (!onlyOnLeft.isEmpty()) {
                result.append(": only on left=").append(onlyOnLeft);
            }
            if (!onlyOnRight.isEmpty()) {
                result.append(": only on right=").append(onlyOnRight);
            }
            if (!differences.isEmpty()) {
                result.append(": value differences=").append(differences);
            }
            return result.toString();
        }
    }

    static class ValueDifferenceImpl<V>
            implements MapDifference.ValueDifference<V> {


        private final V left;

        private final V right;

        static <V> ValueDifference<V> create(
                V left, V right) {
            return new ValueDifferenceImpl<V>(left, right);
        }

        private ValueDifferenceImpl(V left, V right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public V leftValue() {
            return left;
        }

        @Override
        public V rightValue() {
            return right;
        }

        @Override
        public boolean equals(Object object) {
            if (object instanceof MapDifference.ValueDifference) {
                MapDifference.ValueDifference<?> that = (MapDifference.ValueDifference<?>) object;
                return ObjectUtils.equal(this.left, that.leftValue())
                        && ObjectUtils.equal(this.right, that.rightValue());
            }
            return false;
        }

        @Override
        public int hashCode() {
            return ObjectUtils.hashCode(left, right);
        }

        @Override
        public String toString() {
            return "(" + left + ", " + right + ")";
        }
    }

    static class SortedMapDifferenceImpl<K, V>
            extends MapDifferenceImpl<K, V> implements SortedMapDifference<K, V> {

        SortedMapDifferenceImpl(
                SortedMap<K, V> onlyOnLeft,
                SortedMap<K, V> onlyOnRight,
                SortedMap<K, V> onBoth,
                SortedMap<K, ValueDifference<V>> differences) {
            super(onlyOnLeft, onlyOnRight, onBoth, differences);
        }

        @Override
        public SortedMap<K, ValueDifference<V>> entriesDiffering() {
            return (SortedMap<K, ValueDifference<V>>) super.entriesDiffering();
        }

        @Override
        public SortedMap<K, V> entriesInCommon() {
            return (SortedMap<K, V>) super.entriesInCommon();
        }

        @Override
        public SortedMap<K, V> entriesOnlyOnLeft() {
            return (SortedMap<K, V>) super.entriesOnlyOnLeft();
        }

        @Override
        public SortedMap<K, V> entriesOnlyOnRight() {
            return (SortedMap<K, V>) super.entriesOnlyOnRight();
        }
    }

    static <E> Comparator<? super E> orNaturalOrder(
            Comparator<? super E> comparator) {
        if (comparator != null) { // can't use ? : because of javac bug 5080917
            return comparator;
        }
        return (Comparator<E>) Ordering.natural();
    }

    /**
     * Returns a capacity that is sufficient to keep the map from being resized as long as it grows no larger than expectedSize and the load factor is ≥ its default (0.75).
     */
    static int capacity(int expectedSize) {
        if (expectedSize < 3) {
            // todo checkNonnegative(expectedSize, "expectedSize");
            return expectedSize + 1;
        }
        if (expectedSize < Ints.MAX_POWER_OF_TWO) {
            // This seems to be consistent across JDKs. The capacity argument to HashMap and LinkedHashMap
            // ends up being used to compute a "threshold" size, beyond which the internal table
            // will be resized. That threshold is ceilingPowerOfTwo(capacity*loadFactor), where
            // loadFactor is 0.75 by default. So with the calculation here we ensure that the
            // threshold is equal to ceilingPowerOfTwo(expectedSize). There is a separate code
            // path when the first operation on the new map is putAll(otherMap). There, prior to
            // https://github.com/openjdk/jdk/commit/3e393047e12147a81e2899784b943923fc34da8e, a bug
            // meant that sometimes a too-large threshold is calculated. However, this new threshold is
            // independent of the initial capacity, except that it won't be lower than the threshold
            // computed from that capacity. Because the internal table is only allocated on the first
            // write, we won't see copying because of the new threshold. So it is always OK to use the
            // calculation here.
            return (int) Math.ceil(expectedSize / 0.75);
        }
        return Integer.MAX_VALUE; // any large value
    }

    static <K> K keyOrNull(Entry<K, ?> entry) {
        return (entry == null) ? null : entry.getKey();
    }


    static <V> V valueOrNull(Entry<?, V> entry) {
        return (entry == null) ? null : entry.getValue();
    }

    /**
     * Delegates to {@link Map#containsKey}. Returns {@code false} on {@code ClassCastException} and {@code NullPointerException}.
     */
    static boolean safeContainsKey(Map<?, ?> map, Object key) {
        Assert.notNull(map);
        try {
            return map.containsKey(key);
        } catch (ClassCastException | NullPointerException e) {
            return false;
        }
    }

    /**
     * Delegates to {@link Map#remove}. Returns {@code null} on {@code ClassCastException} and {@code NullPointerException}.
     */

    static <V> V safeRemove(Map<?, V> map, Object key) {
        Assert.notNull(map);
        try {
            return map.remove(key);
        } catch (ClassCastException | NullPointerException e) {
            return null;
        }
    }

    /**
     * An admittedly inefficient implementation of {@link Map#containsKey}.
     */
    static boolean containsKeyImpl(Map<?, ?> map, Object key) {
        return IteratorUtils.contains(keyIterator(map.entrySet().iterator()), key);
    }

    /**
     * An implementation of {@link Map#containsValue}.
     */
    static boolean containsValueImpl(Map<?, ?> map, Object value) {
        return IteratorUtils.contains(valueIterator(map.entrySet().iterator()), value);
    }


    /**
     * An implementation of {@link Map#putAll}.
     */
    static <K, V> void putAllImpl(
            Map<K, V> self, Map<? extends K, ? extends V> map) {
        for (Entry<? extends K, ? extends V> entry : map.entrySet()) {
            self.put(entry.getKey(), entry.getValue());
        }
    }
}
