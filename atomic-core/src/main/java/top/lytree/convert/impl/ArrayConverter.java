/*
 * Copyright (c) 2023 looly(loolly@aliyun.com)
 * Hutool is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
 * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package top.lytree.convert.impl;


import top.lytree.collections.ArrayUtils;
import top.lytree.collections.ListUtils;
import top.lytree.convert.AbstractConverter;
import top.lytree.convert.Convert;
import top.lytree.lang.StringUtils;



import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * 数组转换器，包括原始类型数组
 *
 * @author Looly
 */
public class ArrayConverter extends AbstractConverter {
    private static final long serialVersionUID = 1L;

    /**
     * 单例
     */
    public static final ArrayConverter INSTANCE = new ArrayConverter();

    /**
     * 是否忽略元素转换错误
     */
    private boolean ignoreElementError;

    /**
     * 构造
     */
    public ArrayConverter() {
        this(false);
    }

    /**
     * 构造
     *
     * @param ignoreElementError 是否忽略元素转换错误
     */
    public ArrayConverter(final boolean ignoreElementError) {
        this.ignoreElementError = ignoreElementError;
    }

    @Override
    protected Object convertInternal(final Class<?> targetClass, final Object value) {
        if (null == value) {
            return null;
        }
        if (targetClass.isArray() && value.getClass().isArray()) {
            return convertArrayToArray(targetClass.getComponentType(), value);
        } else {
            throw new UnsupportedOperationException(StringUtils.format("value type is {} , The conversion to an array is not supported", value.getClass().getComponentType().getName()));
        }

    }


    // -------------------------------------------------------------------------------------- Private method start

    /**
     * 数组对数组转换
     *
     * @param array 被转换的数组值
     * @return 转换后的数组
     */
    private Object convertArrayToArray(final Class<?> targetComponentType, final Object array) {
        final Class<?> valueComponentType = array.getClass().getComponentType();

        if (valueComponentType == targetComponentType) {
            return array;
        }

        final int len = Array.getLength(array);
        final Object result = Array.newInstance(targetComponentType, len);

        for (int i = 0; i < len; i++) {
            Array.set(result, i, convertComponentType(targetComponentType, Array.get(array, i)));
        }
        return result;
    }


    /**
     * 转换元素类型
     *
     * @param value 值
     * @return 转换后的值，转换失败若{@link #ignoreElementError}为true，返回null，否则抛出异常
     * @since 5.4.3
     */
    private Object convertComponentType(final Class<?> targetComponentType, final Object value) {
        return Convert.convertWithCheck(targetComponentType, value, null, this.ignoreElementError);
    }
    // -------------------------------------------------------------------------------------- Private method end
}
