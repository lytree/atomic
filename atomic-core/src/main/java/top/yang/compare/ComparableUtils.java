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
package top.yang.compare;

import java.util.Comparator;
import java.util.function.Predicate;

/**
 * <p>Utility library to provide helper methods for translating {@link Comparable#compareTo} result into a boolean.</p>
 *
 * <p>Example: {@code boolean x = is(myComparable).lessThanOrEqualTo(otherComparable)}</p>
 *
 * <p>#ThreadSafe#</p>
 *
 * @since 3.10
 */
public class ComparableUtils {

    /**
     * Provides access to the available methods
     *
     * @param <A> the type of objects that this object may be compared against.
     */
    public static class ComparableCheckBuilder<A extends Comparable<A>> {

        private final A a;

        private ComparableCheckBuilder(final A a) {
            this.a = a;
        }

        /**
         * Checks if {@code [b <= a <= c]} or {@code [b >= a >= c]} where the {@code a} is object passed to {@link #is}.
         *
         * @param b the object to compare to the base object
         * @param c the object to compare to the base object
         * @return true if the base object is between b and c
         */
        public boolean between(final A b, final A c) {
            return betweenOrdered(b, c) || betweenOrdered(c, b);
        }

        /**
         * Checks if {@code (b < a < c)} or {@code (b > a > c)} where the {@code a} is object passed to {@link #is}.
         *
         * @param b the object to compare to the base object
         * @param c the object to compare to the base object
         * @return true if the base object is between b and c and not equal to those
         */
        public boolean betweenExclusive(final A b, final A c) {
            return betweenOrderedExclusive(b, c) || betweenOrderedExclusive(c, b);
        }

        private boolean betweenOrdered(final A b, final A c) {
            return greaterThanOrEqualTo(b) && lessThanOrEqualTo(c);
        }

        private boolean betweenOrderedExclusive(final A b, final A c) {
            return greaterThan(b) && lessThan(c);
        }

        /**
         * Checks if the object passed to {@link #is} is equal to {@code b}
         *
         * @param b the object to compare to the base object
         * @return true if the value returned by {@link Comparable#compareTo} is equal to {@code 0}
         */
        public boolean equalTo(final A b) {
            return a.compareTo(b) == 0;
        }

        /**
         * Checks if the object passed to {@link #is} is greater than {@code b}
         *
         * @param b the object to compare to the base object
         * @return true if the value returned by {@link Comparable#compareTo} is greater than {@code 0}
         */
        public boolean greaterThan(final A b) {
            return a.compareTo(b) > 0;
        }

        /**
         * Checks if the object passed to {@link #is} is greater than or equal to {@code b}
         *
         * @param b the object to compare to the base object
         * @return true if the value returned by {@link Comparable#compareTo} is greater than or equal to {@code 0}
         */
        public boolean greaterThanOrEqualTo(final A b) {
            return a.compareTo(b) >= 0;
        }

        /**
         * Checks if the object passed to {@link #is} is less than {@code b}
         *
         * @param b the object to compare to the base object
         * @return true if the value returned by {@link Comparable#compareTo} is less than {@code 0}
         */
        public boolean lessThan(final A b) {
            return a.compareTo(b) < 0;
        }

        /**
         * Checks if the object passed to {@link #is} is less than or equal to {@code b}
         *
         * @param b the object to compare to the base object
         * @return true if the value returned by {@link Comparable#compareTo} is less than or equal to {@code 0}
         */
        public boolean lessThanOrEqualTo(final A b) {
            return a.compareTo(b) <= 0;
        }
    }

    /**
     * Checks if {@code [b <= a <= c]} or {@code [b >= a >= c]} where the {@code a} is the tested object.
     *
     * @param b the object to compare to the tested object
     * @param c the object to compare to the tested object
     * @param <A> type of the test object
     * @return a predicate for true if the tested object is between b and c
     */
    public static <A extends Comparable<A>> Predicate<A> between(final A b, final A c) {
        return a -> is(a).between(b, c);
    }

    /**
     * Checks if {@code (b < a < c)} or {@code (b > a > c)} where the {@code a} is the tested object.
     *
     * @param b the object to compare to the tested object
     * @param c the object to compare to the tested object
     * @param <A> type of the test object
     * @return a predicate for true if the tested object is between b and c and not equal to those
     */
    public static <A extends Comparable<A>> Predicate<A> betweenExclusive(final A b, final A c) {
        return a -> is(a).betweenExclusive(b, c);
    }

    /**
     * Checks if the tested object is greater than or equal to {@code b}
     *
     * @param b the object to compare to the tested object
     * @param <A> type of the test object
     * @return a predicate for true if the value returned by {@link Comparable#compareTo}
     * is greater than or equal to {@code 0}
     */
    public static <A extends Comparable<A>> Predicate<A> ge(final A b) {
        return a -> is(a).greaterThanOrEqualTo(b);
    }

    /**
     * Checks if the tested object is greater than {@code b}
     *
     * @param b the object to compare to the tested object
     * @param <A> type of the test object
     * @return a predicate for true if the value returned by {@link Comparable#compareTo} is greater than {@code 0}
     */
    public static <A extends Comparable<A>> Predicate<A> gt(final A b) {
        return a -> is(a).greaterThan(b);
    }

    /**
     * Provides access to the available methods
     *
     * @param a base object in the further comparison
     * @param <A> type of the base object
     * @return a builder object with further methods
     */
    public static <A extends Comparable<A>> ComparableCheckBuilder<A> is(final A a) {
        return new ComparableCheckBuilder<>(a);
    }

    /**
     * Checks if the tested object is less than or equal to {@code b}
     *
     * @param b the object to compare to the tested object
     * @param <A> type of the test object
     * @return a predicate for true if the value returned by {@link Comparable#compareTo}
     * is less than or equal to {@code 0}
     */
    public static <A extends Comparable<A>> Predicate<A> le(final A b) {
        return a -> is(a).lessThanOrEqualTo(b);
    }

    /**
     * Checks if the tested object is less than {@code b}
     *
     * @param b the object to compare to the tested object
     * @param <A> type of the test object
     * @return a predicate for true if the value returned by {@link Comparable#compareTo} is less than {@code 0}
     */
    public static <A extends Comparable<A>> Predicate<A> lt(final A b) {
        return a -> is(a).lessThan(b);
    }
    /**
     * 对象比较，比较结果取决于comparator，如果被比较对象为null，传入的comparator对象应处理此情况<br>
     * 如果传入comparator为null，则使用默认规则比较（此时被比较对象必须实现Comparable接口）
     *
     * <p>
     * 一般而言，如果c1 &lt; c2，返回数小于0，c1==c2返回0，c1 &gt; c2 大于0
     *
     * @param <T>        被比较对象类型
     * @param c1         对象1
     * @param c2         对象2
     * @param comparator 比较器
     * @return 比较结果
     * @see java.util.Comparator#compare(Object, Object)
     * @since 4.6.9
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T> int compare(T c1, T c2, Comparator<T> comparator) {
        if (null == comparator) {
            return compare((Comparable) c1, (Comparable) c2);
        }
        return comparator.compare(c1, c2);
    }

    /**
     * {@code null}安全的对象比较，{@code null}对象小于任何对象
     *
     * @param <T> 被比较对象类型
     * @param c1  对象1，可以为{@code null}
     * @param c2  对象2，可以为{@code null}
     * @return 比较结果，如果c1 &lt; c2，返回数小于0，c1==c2返回0，c1 &gt; c2 大于0
     * @see java.util.Comparator#compare(Object, Object)
     */
    public static <T extends Comparable<? super T>> int compare(T c1, T c2) {
        return compare(c1, c2, false);
    }

    /**
     * {@code null}安全的对象比较
     *
     * @param <T>           被比较对象类型（必须实现Comparable接口）
     * @param c1            对象1，可以为{@code null}
     * @param c2            对象2，可以为{@code null}
     * @param isNullGreater 当被比较对象为null时是否排在前面，true表示null大于任何对象，false反之
     * @return 比较结果，如果c1 &lt; c2，返回数小于0，c1==c2返回0，c1 &gt; c2 大于0
     * @see java.util.Comparator#compare(Object, Object)
     */
    public static <T extends Comparable<? super T>> int compare(T c1, T c2, boolean isNullGreater) {
        if (c1 == c2) {
            return 0;
        } else if (c1 == null) {
            return isNullGreater ? 1 : -1;
        } else if (c2 == null) {
            return isNullGreater ? -1 : 1;
        }
        return c1.compareTo(c2);
    }

    /**
     * 自然比较两个对象的大小，比较规则如下：
     *
     * <pre>
     * 1、如果实现Comparable调用compareTo比较
     * 2、o1.equals(o2)返回0
     * 3、比较hashCode值
     * 4、比较toString值
     * </pre>
     *
     * @param <T>           被比较对象类型
     * @param o1            对象1
     * @param o2            对象2
     * @param isNullGreater null值是否做为最大值
     * @return 比较结果，如果o1 &lt; o2，返回数小于0，o1==o2返回0，o1 &gt; o2 大于0
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> int compare(T o1, T o2, boolean isNullGreater) {
        if (o1 == o2) {
            return 0;
        } else if (null == o1) {// null 排在后面
            return isNullGreater ? 1 : -1;
        } else if (null == o2) {
            return isNullGreater ? -1 : 1;
        }

        if (o1 instanceof Comparable && o2 instanceof Comparable) {
            //如果bean可比较，直接比较bean
            return ((Comparable) o1).compareTo(o2);
        }

        if (o1.equals(o2)) {
            return 0;
        }

        int result = Integer.compare(o1.hashCode(), o2.hashCode());
        if (0 == result) {
            result = compare(o1.toString(), o2.toString());
        }

        return result;
    }

    private ComparableUtils() {}
}
