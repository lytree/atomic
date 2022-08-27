/*
 * Copyright (C) 2008 The Guava Authors
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


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;


/**
 * Provides static methods for serializing collection classes.
 *
 * <p>This class assists the implementation of collection classes. Do not use this class to
 * serialize collections that are defined elsewhere.
 *
 * @author Jared Levy
 */

final class Serialization {

    private Serialization() {
    }

    /**
     * Reads a count corresponding to a serialized map, multiset, or multimap. It returns the size of a map serialized by {@link #writeMap(Map, ObjectOutputStream)}, the number of
     * distinct elements in a multiset serialized by {@link #writeMultiset(Multiset, ObjectOutputStream)}, or the number of distinct keys in a multimap serialized by .
     */
    static int readCount(ObjectInputStream stream) throws IOException {
        return stream.readInt();
    }

    /**
     * Stores the contents of a map in an output stream, as part of serialization. It does not support concurrent maps whose content may change while the method is running.
     *
     * <p>The serialized output consists of the number of entries, first key, first value, second key,
     * second value, and so on.
     */
    static <K extends Object, V extends Object> void writeMap(
            Map<K, V> map, ObjectOutputStream stream) throws IOException {
        stream.writeInt(map.size());
        for (Map.Entry<K, V> entry : map.entrySet()) {
            stream.writeObject(entry.getKey());
            stream.writeObject(entry.getValue());
        }
    }

    /**
     * Populates a map by reading an input stream, as part of deserialization. See {@link #writeMap} for the data format.
     */
    static <K extends Object, V extends Object> void populateMap(
            Map<K, V> map, ObjectInputStream stream) throws IOException, ClassNotFoundException {
        int size = stream.readInt();
        populateMap(map, stream, size);
    }

    /**
     * Populates a map by reading an input stream, as part of deserialization. See {@link #writeMap} for the data format. The size is determined by a prior call to {@link
     * #readCount}.
     */
    static <K extends Object, V extends Object> void populateMap(
            Map<K, V> map, ObjectInputStream stream, int size)
            throws IOException, ClassNotFoundException {
        for (int i = 0; i < size; i++) {
            @SuppressWarnings("unchecked") // reading data stored by writeMap
            K key = (K) stream.readObject();
            @SuppressWarnings("unchecked") // reading data stored by writeMap
            V value = (V) stream.readObject();
            map.put(key, value);
        }
    }

    /**
     * Stores the contents of a multiset in an output stream, as part of serialization. It does not support concurrent multisets whose content may change while the method is
     * running.
     *
     * <p>The serialized output consists of the number of distinct elements, the first element, its
     * count, the second element, its count, and so on.
     */
    static <E extends Object> void writeMultiset(
            Multiset<E> multiset, ObjectOutputStream stream) throws IOException {
        int entryCount = multiset.entrySet().size();
        stream.writeInt(entryCount);
        for (Multiset.Entry<E> entry : multiset.entrySet()) {
            stream.writeObject(entry.getElement());
            stream.writeInt(entry.getCount());
        }
    }

    /**
     * Populates a multiset by reading an input stream, as part of deserialization. See {@link #writeMultiset} for the data format.
     */
    static <E extends Object> void populateMultiset(
            Multiset<E> multiset, ObjectInputStream stream) throws IOException, ClassNotFoundException {
        int distinctElements = stream.readInt();
        populateMultiset(multiset, stream, distinctElements);
    }

    /**
     * Populates a multiset by reading an input stream, as part of deserialization. See {@link #writeMultiset} for the data format. The number of distinct elements is determined by
     * a prior call to {@link #readCount}.
     */
    static <E extends Object> void populateMultiset(
            Multiset<E> multiset, ObjectInputStream stream, int distinctElements)
            throws IOException, ClassNotFoundException {
        for (int i = 0; i < distinctElements; i++) {
            @SuppressWarnings("unchecked") // reading data stored by writeMultiset
            E element = (E) stream.readObject();
            int count = stream.readInt();
            multiset.add(element, count);
        }
    }


}
