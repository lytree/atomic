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


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import top.lytree.base.Assert;
import top.lytree.base.Filter;
import top.lytree.bean.ObjectUtils;
import top.lytree.math.Ints;


/**
 * <p>Operations on arrays, primitive arrays (like {@code int[]}) and
 * primitive wrapper arrays (like {@code Integer[]}).
 *
 * <p>This class tries to handle {@code null} input gracefully.
 * An exception will not be thrown for a {@code null} array input. However, an Object array that contains a {@code null} element may throw an exception. Each method documents its
 * behavior.
 *
 * <p>#ThreadSafe#
 */
public class ArraysUtils extends org.apache.commons.lang3.ArrayUtils {

    /**
     * 数组是否为空<br> 此方法会匹配单一对象，如果此对象为{@code null}则返回true<br> 如果此对象为非数组，理解为此对象为数组的第一个元素，则返回false<br> 如果此对象为数组对象，数组长度大于0情况下返回false，否则返回true
     *
     * @param array 数组
     *
     * @return 是否为空
     */
    public static boolean isEmpty(Object array) {
        if (array != null) {
            if (isArray(array)) {
                return 0 == Array.getLength(array);
            }
            return false;
        }
        return true;
    }

    /**
     * 对象是否为数组对象
     *
     * @param obj 对象
     *
     * @return 是否为数组对象，如果为{@code null} 返回false
     */
    public static boolean isArray(Object obj) {
        if (null == obj) {
            // throw new NullPointerException("Object check for isArray is null");
            return false;
        }
        return obj.getClass().isArray();
    }


    /**
     * 包装 {@link System#arraycopy(Object, int, Object, int, int)}<br> 数组复制
     *
     * @param src     源数组
     * @param srcPos  源数组开始位置
     * @param dest    目标数组
     * @param destPos 目标数组开始位置
     * @param length  拷贝数组长度
     *
     * @return 目标数组
     */
    public static Object copy(Object src, int srcPos, Object dest, int destPos, int length) {
        //noinspection SuspiciousSystemArraycopy
        System.arraycopy(src, srcPos, dest, destPos, length);
        return dest;
    }

    /**
     * 包装 {@link System#arraycopy(Object, int, Object, int, int)}<br> 数组复制，缘数组和目标数组都是从位置0开始复制
     *
     * @param src    源数组
     * @param dest   目标数组
     * @param length 拷贝数组长度
     *
     * @return 目标数组
     */
    public static Object copy(Object src, Object dest, int length) {
        //noinspection SuspiciousSystemArraycopy
        System.arraycopy(src, 0, dest, 0, length);
        return dest;
    }

    /**
     * 数组或集合转String
     *
     * @param obj 集合或数组对象
     *
     * @return 数组字符串，与集合转字符串格式相同
     */
    public static String toString(Object obj) {
        if (null == obj) {
            return null;
        }

        if (obj instanceof long[]) {
            return Arrays.toString((long[]) obj);
        } else if (obj instanceof int[]) {
            return Arrays.toString((int[]) obj);
        } else if (obj instanceof short[]) {
            return Arrays.toString((short[]) obj);
        } else if (obj instanceof char[]) {
            return Arrays.toString((char[]) obj);
        } else if (obj instanceof byte[]) {
            return Arrays.toString((byte[]) obj);
        } else if (obj instanceof boolean[]) {
            return Arrays.toString((boolean[]) obj);
        } else if (obj instanceof float[]) {
            return Arrays.toString((float[]) obj);
        } else if (obj instanceof double[]) {
            return Arrays.toString((double[]) obj);
        } else if (isArray(obj)) {
            // 对象数组
            try {
                return Arrays.deepToString((Object[]) obj);
            } catch (Exception ignore) {
                //ignore
            }
        }

        return obj.toString();
    }

    /**
     * 新建一个空数组
     *
     * @param <T>           数组元素类型
     * @param componentType 元素类型
     * @param newSize       大小
     *
     * @return 空数组
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] newArray(Class<?> componentType, int newSize) {
        return (T[]) Array.newInstance(componentType, newSize);
    }

    /**
     * 新建一个空数组
     *
     * @param newSize 大小
     *
     * @return 空数组
     */
    public static Object[] newArray(int newSize) {
        return new Object[newSize];
    }

    /**
     * 是否包含{@code null}元素
     *
     * @param <T>   数组元素类型
     * @param array 被检查的数组
     *
     * @return 是否包含{@code null}元素
     */
    @SuppressWarnings("unchecked")
    public static <T> boolean hasNull(T... array) {
        return ObjectUtils.anyNull(array);
    }

    /**
     * 获取数组对象的元素类型
     *
     * @param array 数组对象
     *
     * @return 元素类型
     */
    public static Class<?> getComponentType(Object array) {
        return null == array ? null : array.getClass().getComponentType();
    }

    /**
     * 获取数组对象的元素类型
     *
     * @param arrayClass 数组类
     *
     * @return 元素类型
     */
    public static Class<?> getComponentType(Class<?> arrayClass) {
        return null == arrayClass ? null : arrayClass.getComponentType();
    }

    /**
     * 根据数组元素类型，获取数组的类型<br> 方法是通过创建一个空数组从而获取其类型
     *
     * @param componentType 数组元素类型
     *
     * @return 数组类型
     */
    public static Class<?> getArrayType(Class<?> componentType) {
        return Array.newInstance(componentType, 0).getClass();
    }

    /**
     * 生成一个新的重新设置大小的数组<br> 调整大小后拷贝原数组到新数组下。扩大则占位前N个位置，缩小则截断
     *
     * @param <T>           数组元素类型
     * @param data          原数组
     * @param newSize       新的数组大小
     * @param componentType 数组元素类型
     *
     * @return 调整后的新数组
     */
    public static <T> T[] resize(T[] data, int newSize, Class<?> componentType) {
        if (newSize < 0) {
            return data;
        }

        final T[] newArray = newArray(componentType, newSize);
        if (newSize > 0 && isNotEmpty(data)) {
            System.arraycopy(data, 0, newArray, 0, Math.min(data.length, newSize));
        }
        return newArray;
    }

    /**
     * 生成一个新的重新设置大小的数组<br> 调整大小后拷贝原数组到新数组下。扩大则占位前N个位置，其它位置补充0，缩小则截断
     *
     * @param array   原数组
     * @param newSize 新的数组大小
     *
     * @return 调整后的新数组
     */
    public static Object resize(Object array, int newSize) {
        if (newSize < 0) {
            return array;
        }
        if (null == array) {
            return null;
        }
        final int length = Array.getLength(array);
        final Object newArray = Array.newInstance(array.getClass().getComponentType(), newSize);
        if (newSize > 0 && !isEmpty(array)) {
            //noinspection SuspiciousSystemArraycopy
            System.arraycopy(array, 0, newArray, 0, Math.min(length, newSize));
        }
        return newArray;
    }

    /**
     * 生成一个新的重新设置大小的数组<br> 新数组的类型为原数组的类型，调整大小后拷贝原数组到新数组下。扩大则占位前N个位置，缩小则截断
     *
     * @param <T>     数组元素类型
     * @param buffer  原数组
     * @param newSize 新的数组大小
     *
     * @return 调整后的新数组
     */
    public static <T> T[] resize(T[] buffer, int newSize) {
        return resize(buffer, newSize, buffer.getClass().getComponentType());
    }

    /**
     * 编辑数组<br> 编辑过程通过传入的Editor实现来返回需要的元素内容，这个Editor实现可以实现以下功能：
     *
     * <pre>
     * 1、过滤出需要的对象，如果返回{@code null}表示这个元素对象抛弃
     * 2、修改元素对象，返回集合中为修改后的对象
     * </pre>
     * <p>
     *
     * @param <T>    数组元素类型
     * @param array  数组
     * @param editor 编辑器接口，{@code null}返回原集合
     *
     * @return 编辑后的数组
     */
    public static <T> T[] edit(T[] array, Editor<T> editor) {
        if (null == editor) {
            return array;
        }

        final ArrayList<T> list = new ArrayList<>(array.length);
        T modified;
        for (T t : array) {
            modified = editor.edit(t);
            if (null != modified) {
                list.add(modified);
            }
        }
        final T[] result = newArray(array.getClass().getComponentType(), list.size());
        return list.toArray(result);
    }

    /**
     * 过滤<br> 过滤过程通过传入的Filter实现来过滤返回需要的元素内容，这个Filter实现可以实现以下功能：
     *
     * <pre>
     * 1、过滤出需要的对象，{@link Filter#accept(Object)}方法返回true的对象将被加入结果集合中
     * </pre>
     *
     * @param <T>    数组元素类型
     * @param array  数组
     * @param filter 过滤器接口，用于定义过滤规则，{@code null}返回原集合
     *
     * @return 过滤后的数组
     */
    public static <T> T[] filter(T[] array, Filter<T> filter) {
        if (null == array || null == filter) {
            return array;
        }
        return edit(array, t -> filter.accept(t) ? t : null);
    }

    public static int hashCode(Object... objects) {
        return Arrays.hashCode(objects);
    }


    /**
     * <p>ArraysUtils instances should NOT be constructed in standard programming.
     * Instead, the class should be used as {@code ArraysUtils.clone(new int[] {2})}.
     *
     * <p>This constructor is public to permit tools that require a JavaBean instance
     * to operate.
     */
    private ArraysUtils() {
    }

}
