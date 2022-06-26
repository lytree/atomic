package top.yang.collections;


import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import top.yang.Filter;

public class IterableUtils {

    /**
     * Returns the number of elements contained in the given iterator.
     * <p>
     * A {@code null} or empty iterator returns {@code 0}.
     *
     * @param iterable the iterable to check, may be null
     * @return the number of elements contained in the iterable
     */
    public static int size(final Iterable<?> iterable) {
        if (iterable == null) {
            return 0;
        }
        if (iterable instanceof Collection<?>) {
            return ((Collection<?>) iterable).size();
        }
        return IteratorUtils.size(emptyIteratorIfNull(iterable));
    }

    /**
     * Returns an empty iterator if the argument is {@code null}, or {@code iterable.iterator()} otherwise.
     *
     * @param <E>      the element type
     * @param iterable the iterable, possibly {@code null}
     * @return an empty iterator if the argument is {@code null}
     */
    private static <E> Iterator<E> emptyIteratorIfNull(final Iterable<E> iterable) {
        return iterable != null ? iterable.iterator() : IteratorUtils.<E>emptyIterator();
    }

    /**
     * Returns the {@code index}-th value in the {@code iterable}'s {@link Iterator}, throwing {@code IndexOutOfBoundsException} if there is no such element.
     * <p>
     * If the {@link Iterable} is a {@link List}, then it will use {@link List#get(int)}.
     *
     * @param <T>      the type of object in the {@link Iterable}.
     * @param iterable the {@link Iterable} to get a value from, may be null
     * @param index    the index to get
     * @return the object at the specified index
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    public static <T> T get(final Iterable<T> iterable, final int index) {
        CollectionUtils.checkIndexBounds(index);
        if (iterable instanceof List<?>) {
            return ((List<T>) iterable).get(index);
        }
        return IteratorUtils.get(emptyIteratorIfNull(iterable), index);
    }

    /**
     * Shortcut for {@code get(iterator, 0)}.
     * <p>
     * Returns the {@code first} value in the {@code iterable}'s {@link Iterator}, throwing {@code IndexOutOfBoundsException} if there is no such element.
     * </p>
     * <p>
     * If the {@link Iterable} is a {@link List}, then it will use {@link List#get(int)}.
     * </p>
     *
     * @param <T>      the type of object in the {@link Iterable}.
     * @param iterable the {@link Iterable} to get a value from, may be null
     * @return the first object
     * @throws IndexOutOfBoundsException if the request  is invalid
     *
     */
    public static <T> T first(final Iterable<T> iterable) {
        return get(iterable, 0);
    }

    /**
     * Answers true if the provided iterable is empty.
     * <p>
     * A {@code null} iterable returns true.
     *
     * @param iterable the {@link Iterable to use}, may be null
     * @return true if the iterable is null or empty, false otherwise
     */
    public static boolean isEmpty(final Iterable<?> iterable) {
        if (iterable instanceof Collection<?>) {
            return ((Collection<?>) iterable).isEmpty();
        }
        return IteratorUtils.isEmpty(emptyIteratorIfNull(iterable));
    }

}
