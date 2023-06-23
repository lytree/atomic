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


import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.collections4.iterators.EnumerationIterator;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;


/**
 * 为{@link Enumeration}实例提供实用程序方法。
 */
public class EnumerationUtils {
    public static <T> T get(final Enumeration<T> e, final int index) {
        return org.apache.commons.collections4.EnumerationUtils.get(e, index);
    }

    /**
     * Creates a list based on an enumeration.
     *
     * <p>As the enumeration is traversed, an ArrayList of its values is
     * created. The new list is returned.</p>
     *
     * @param <E>         the element type
     * @param enumeration the enumeration to traverse, which should not be <code>null</code>.
     * @return a list containing all elements of the given enumeration
     * @throws NullPointerException if the enumeration parameter is <code>null</code>.
     */
    public static <E> List<E> toList(final Enumeration<? extends E> enumeration) {
        return org.apache.commons.collections4.EnumerationUtils.toList(enumeration);
    }

    /**
     * Override toList(Enumeration) for StringTokenizer as it implements Enumeration&lt;Object&gt;
     * for the sake of backward compatibility.
     *
     * @param stringTokenizer the tokenizer to convert to a {@link List}&lt;{@link String}&gt;
     * @return a list containing all tokens of the given StringTokenizer
     */
    public static List<String> toList(final StringTokenizer stringTokenizer) {
        return org.apache.commons.collections4.EnumerationUtils.toList(stringTokenizer);
    }

}
