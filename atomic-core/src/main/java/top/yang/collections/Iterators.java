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


import java.util.Collection;
import java.util.Iterator;
import top.yang.lang.Assert;
import top.yang.math.Ints;

/**
 * 该类包含操作或返回{@link Iterator}类型对象的静态实用程序方法。除了如上所述，每个方法在{@link Iterables}类中都有一个对应的基于{@link Iterable}的方法。
 *
 * <p><i>Performance notes:</i> Unless otherwise noted, all of the iterators produced in this class
 * are <i>lazy</i>, which means that they only advance the backing iteration when absolutely necessary.
 *
 * <p>See the Guava User Guide section on <a href=
 * "https://github.com/google/guava/wiki/CollectionUtilitiesExplained#iterables">{@code Iterators}</a>.
 *
 * @author Kevin Bourrillion
 * @author Jared Levy
 * @since 2.0
 */

public final class Iterators {

    private Iterators() {
    }

    /**
     * Returns the number of elements remaining in {@code iterator}. The iterator will be left exhausted: its {@code hasNext()} method will return {@code false}.
     */
    public static int size(Iterator<?> iterator) {
        long count = 0L;
        while (iterator.hasNext()) {
            iterator.next();
            count++;
        }
        return Ints.saturatedCast(count);
    }

    /**
     * Returns {@code true} if {@code iterator} contains {@code element}.
     */
    public static boolean contains(Iterator<?> iterator, Object element) {
        if (element == null) {
            while (iterator.hasNext()) {
                if (iterator.next() == null) {
                    return true;
                }
            }
        } else {
            while (iterator.hasNext()) {
                if (element.equals(iterator.next())) {
                    return true;
                }
            }
        }
        return false;
    }

    // Methods only in Iterators, not in Iterables
    public static boolean removeAll(Iterator<?> removeFrom, Collection<?> elementsToRemove) {
        Assert.notNull(elementsToRemove);
        boolean result = false;
        while (removeFrom.hasNext()) {
            if (elementsToRemove.contains(removeFrom.next())) {
                removeFrom.remove();
                result = true;
            }
        }
        return result;
    }

    /**
     * Clears the iterator using its remove method.
     */
    static void clear(Iterator<?> iterator) {
        Assert.notNull(iterator);
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
    }
}
