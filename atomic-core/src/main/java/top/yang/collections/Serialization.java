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
 * 提供用于序列化集合类的静态方法。
 *
 * <p>这个类帮助集合类的实现。不要使用该类序列化在其他地方定义的集合。
 *
 * @author Jared Levy
 */

final class Serialization {

    private Serialization() {
    }

    /**
     * 读取与序列化map、multiset或multimap对应的计数。它返回由{@link #writeMap(map, ObjectOutputStream)}序列化的映射的大小，由{@link #writeMultiset(multiset,
     * ObjectOutputStream)}序列化的多集中不同元素的数量，或由序列化的multimap中不同键的数量。
     */
    static int readCount(ObjectInputStream stream) throws IOException {
        return stream.readInt();
    }

    /**
     * 将映射的内容存储在输出流中，作为序列化的一部分。它不支持在方法运行时内容可能更改的并发映射。
     *
     * <p>序列化的输出包括条目的数量、第一个键、第一个值、第二个键、第二个值，等等.
     */
    static <K, V extends Object> void writeMap(
            Map<K, V> map, ObjectOutputStream stream) throws IOException {
        stream.writeInt(map.size());
        for (Map.Entry<K, V> entry : map.entrySet()) {
            stream.writeObject(entry.getKey());
            stream.writeObject(entry.getValue());
        }
    }

    /**
     * 作为反序列化的一部分，通过读取输入流填充映射。数据格式参见{@link #writeMap}.
     */
    static <K, V extends Object> void populateMap(
            Map<K, V> map, ObjectInputStream stream) throws IOException, ClassNotFoundException {
        int size = stream.readInt();
        populateMap(map, stream, size);
    }

    /**
     * 作为反序列化的一部分，通过读取输入流填充映射。数据格式参见{@link #writeMap}。大小由之前调用{@link #readCount}决定.
     */
    static <K, V extends Object> void populateMap(
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


}
