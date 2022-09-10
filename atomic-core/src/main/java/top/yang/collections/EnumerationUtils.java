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


import top.yang.collections.iterators.EnumerationIterator;
import top.yang.collections.iterators.IteratorIterable;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;


/**
 * 为{@link Enumeration}实例提供实用程序方法。
 */
public class EnumerationUtils {

    /**
     * Don't allow instances.
     */
    private EnumerationUtils() {
        // no instances.
    }

    /**
     * 创建一个封装了{@link枚举}的{@link Iterable}。返回的{@link Iterable}可以用于单个迭代。
     *
     * @param <T>         元素类型
     * @param enumeration 要使用的枚举不能为空
     * @return 一种新的，一次性使用 {@link Iterable}
     */
    public static <T> Iterable<T> asIterable(final Enumeration<T> enumeration) {
        return new IteratorIterable<>(new EnumerationIterator<>(enumeration));
    }

    /**
     * 返回{@link枚举}中{@code index}的值，如果没有该元素则抛出{@code IndexOutOfBoundsException}。
     * <p>
     * 作为该方法的一个副作用，枚举被提升到{@code index}(或者如果{@code index}超过条目数，则提升到末尾)。
     *
     * @param e     the enumeration to get a value from
     * @param index the index to get
     * @param <T>   the type of object in the {@link Enumeration}
     * @return the object at the specified index
     * @throws IndexOutOfBoundsException if the index is invalid
     * @throws IllegalArgumentException  if the object type is invalid
     */
    public static <T> T get(final Enumeration<T> e, final int index) {
        CollectionUtils.checkIndexBounds(index);
        int i = index;
        while (e.hasMoreElements()) {
            i--;
            if (i == -1) {
                return e.nextElement();
            }
            e.nextElement();
        }
        throw new IndexOutOfBoundsException("Entry does not exist: " + i);
    }

    /**
     * Creates a list based on an enumeration.
     *
     * <p>As the enumeration is traversed, an ArrayList of its values is
     * created. The new list is returned.</p>
     *
     * @param <E>         the element type
     * @param enumeration the enumeration to traverse, which should not be {@code null}.
     * @return a list containing all elements of the given enumeration
     * @throws NullPointerException if the enumeration parameter is {@code null}.
     */
    public static <E> List<E> toList(final Enumeration<? extends E> enumeration) {
        return IteratorUtils.toList(new EnumerationIterator<>(enumeration));
    }

    /**
     * Override toList(Enumeration) for StringTokenizer as it implements Enumeration&lt;Object&gt; for the sake of backward compatibility.
     *
     * @param stringTokenizer the tokenizer to convert to a {@link List}&lt;{@link String}&gt;
     * @return a list containing all tokens of the given StringTokenizer
     */
    public static List<String> toList(final StringTokenizer stringTokenizer) {
        final List<String> result = new ArrayList<>(stringTokenizer.countTokens());
        while (stringTokenizer.hasMoreTokens()) {
            result.add(stringTokenizer.nextToken());
        }
        return result;
    }

}
