package top.lytree.collections;

import static top.lytree.collections.ListUtils.reverse;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;
import top.lytree.base.Assert;
import top.lytree.math.NumberUtils;

public class Lists {


    private Lists() {
    }

    static class ReverseList<T> extends AbstractList<T> {

        private final List<T> forwardList;

        ReverseList(List<T> forwardList) {
            this.forwardList = Assert.notNull(forwardList);
        }

        List<T> getForwardList() {
            return forwardList;
        }

        private int reverseIndex(int index) {
            int size = size();
            Assert.checkIndex(index, size);
            return (size - 1) - index;
        }

        private int reversePosition(int index) {
            int size = size();
            Assert.checkIndex(index, size);
            return size - index;
        }

        @Override
        public void add(int index, T element) {
            forwardList.add(reversePosition(index), element);
        }

        @Override
        public void clear() {
            forwardList.clear();
        }

        @Override

        public T remove(int index) {
            return forwardList.remove(reverseIndex(index));
        }

        @Override
        protected void removeRange(int fromIndex, int toIndex) {
            subList(fromIndex, toIndex).clear();
        }

        @Override

        public T set(int index, T element) {
            return forwardList.set(reverseIndex(index), element);
        }

        @Override

        public T get(int index) {
            return forwardList.get(reverseIndex(index));
        }

        @Override
        public int size() {
            return forwardList.size();
        }

        @Override
        public List<T> subList(int fromIndex, int toIndex) {
            Assert.checkIndex(fromIndex, size());
            Assert.checkIndex(toIndex, size());
            return reverse(forwardList.subList(reversePosition(toIndex), reversePosition(fromIndex)));
        }

        @Override
        public Iterator<T> iterator() {
            return listIterator();
        }

        @Override
        public ListIterator<T> listIterator(int index) {
            int start = reversePosition(index);
            final ListIterator<T> forwardIterator = forwardList.listIterator(start);
            return new ListIterator<T>() {

                boolean canRemoveOrSet;

                @Override
                public void add(T e) {
                    forwardIterator.add(e);
                    forwardIterator.previous();
                    canRemoveOrSet = false;
                }

                @Override
                public boolean hasNext() {
                    return forwardIterator.hasPrevious();
                }

                @Override
                public boolean hasPrevious() {
                    return forwardIterator.hasNext();
                }

                @Override

                public T next() {
                    if (!hasNext()) {
                        throw new NoSuchElementException();
                    }
                    canRemoveOrSet = true;
                    return forwardIterator.previous();
                }

                @Override
                public int nextIndex() {
                    return reversePosition(forwardIterator.nextIndex());
                }

                @Override

                public T previous() {
                    if (!hasPrevious()) {
                        throw new NoSuchElementException();
                    }
                    canRemoveOrSet = true;
                    return forwardIterator.next();
                }

                @Override
                public int previousIndex() {
                    return nextIndex() - 1;
                }

                @Override
                public void remove() {
                    Assert.isTrue(canRemoveOrSet);
                    forwardIterator.remove();
                    canRemoveOrSet = false;
                }

                @Override
                public void set(T e) {
                    Assert.isTrue(canRemoveOrSet);
                    forwardIterator.set(e);
                }
            };
        }
    }

    static int computeArrayListCapacity(int arraySize) {
        Assert.checkNonNegative(arraySize, "arraySize");
        return NumberUtils.saturatedCast(5L + arraySize + (arraySize / 10));
    }

    static class RandomAccessReverseList<T> extends ReverseList<T>
            implements RandomAccess {

        RandomAccessReverseList(List<T> forwardList) {
            super(forwardList);
        }
    }

    /** Returns an implementation of {@link List#listIterator(int)}. */
    static <E> ListIterator<E> listIteratorImpl(List<E> list, int index) {
        return new AbstractListWrapper<>(list).listIterator(index);
    }

    /** An implementation of {@link List#subList(int, int)}. */
    static <E> List<E> subListImpl(
            final List<E> list, int fromIndex, int toIndex) {
        List<E> wrapper;
        if (list instanceof RandomAccess) {
            wrapper =
                    new RandomAccessListWrapper<E>(list) {
                        @Override
                        public ListIterator<E> listIterator(int index) {
                            return backingList.listIterator(index);
                        }

                        private static final long serialVersionUID = 0;
                    };
        } else {
            wrapper =
                    new AbstractListWrapper<E>(list) {
                        @Override
                        public ListIterator<E> listIterator(int index) {
                            return backingList.listIterator(index);
                        }

                        private static final long serialVersionUID = 0;
                    };
        }
        return wrapper.subList(fromIndex, toIndex);
    }

    private static class AbstractListWrapper<E> extends AbstractList<E> {

        final List<E> backingList;

        AbstractListWrapper(List<E> backingList) {
            this.backingList = Assert.notNull(backingList);
        }

        @Override
        public void add(int index, E element) {
            backingList.add(index, element);
        }

        @Override
        public boolean addAll(int index, Collection<? extends E> c) {
            return backingList.addAll(index, c);
        }

        @Override
        public E get(int index) {
            return backingList.get(index);
        }

        @Override

        public E remove(int index) {
            return backingList.remove(index);
        }

        @Override

        public E set(int index, E element) {
            return backingList.set(index, element);
        }

        @Override
        public boolean contains(Object o) {
            return backingList.contains(o);
        }

        @Override
        public int size() {
            return backingList.size();
        }
    }

    private static class RandomAccessListWrapper<E>
            extends AbstractListWrapper<E> implements RandomAccess {

        RandomAccessListWrapper(List<E> backingList) {
            super(backingList);
        }
    }
}
