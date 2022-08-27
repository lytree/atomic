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

/**
 * This class contains static utility methods that operate on or return objects of type {@link Iterator}. Except as noted, each method has a corresponding {@link Iterable}-based
 * method in the {@link Iterables} class.
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
