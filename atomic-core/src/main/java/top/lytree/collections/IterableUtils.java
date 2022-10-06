package top.lytree.collections;


import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class IterableUtils {

    /**
     * 返回给定迭代器中包含的元素个数。
     * <p>
     * A {@code null} or empty iterator returns {@code 0}.
     *
     * @param iterable 要检查的可迭代对象可以为空
     * @return 迭代对象中包含的元素数量
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
     * 如果参数为{@code null}，则返回空迭代器，否则返回{@code iterable.iterator()}。
     *
     * @param <E>      the element type
     * @param iterable the iterable, possibly {@code null}
     * @return an empty iterator if the argument is {@code null}
     */
    private static <E> Iterator<E> emptyIteratorIfNull(final Iterable<E> iterable) {
        return iterable != null ? iterable.iterator() : IteratorUtils.<E>emptyIterator();
    }

    /**
     * 在{@code iterable}的{@link Iterator}中返回{@code index}的值，如果没有该元素则抛出{@code IndexOutOfBoundsException}。如果{@link Iterable}是一个{@link List}，那么它将使用{@link Listget(int)}。
     *
     * @param <T>      {@link Iterable}中的对象类型。
     * @param iterable 从{@link Iterable}获取一个值，可以为空
     * @param index    要获取的索引
     * @return 指定索引处的对象
     * @throws IndexOutOfBoundsException 如果索引无效
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
     * 在{@code iterable}的{@link Iterator}中返回{@code first}的值，如果没有该元素则抛出{@code IndexOutOfBoundsException}。
     * </p>
     * <p>
     * 如果{@link Iterable}是一个{@link List}，那么它将使用{@link List#get(int)}。
     * </p>
     *
     * @param <T>      {@link Iterable}中的对象类型。
     * @param iterable 从{@link Iterable}获取一个值，可以为空
     * @return 第一个对象
     * @throws IndexOutOfBoundsException 如果请求无效
     */
    public static <T> T first(final Iterable<T> iterable) {
        return get(iterable, 0);
    }

    /**
     * 如果提供的可迭代对象为空，则返回true。
     * <p>
     * {@code null}可迭代对象返回true。
     *
     * @param iterable the {@link Iterable to use}, 可以是 null
     * @return 如果可迭代对象为null或空，则为True，否则为false
     */
    public static boolean isEmpty(final Iterable<?> iterable) {
        if (iterable instanceof Collection<?>) {
            return ((Collection<?>) iterable).isEmpty();
        }
        return IteratorUtils.isEmpty(emptyIteratorIfNull(iterable));
    }

    public static <T> boolean addAll(
            Collection<T> addTo, Iterable<? extends T> elementsToAdd) {
        if (elementsToAdd instanceof Collection) {
            Collection<? extends T> c = (Collection<? extends T>) elementsToAdd;
            return addTo.addAll(c);
        }
        return IteratorUtils.addAll(addTo, elementsToAdd.iterator());
    }


}
