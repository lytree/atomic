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


import top.lytree.convert.AbstractConverter;
import top.lytree.convert.ConvertException;

/**
 * 无泛型检查的枚举转换器
 *
 * @author Looly
 * @since 4.0.2
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class EnumConverter extends AbstractConverter {
    private static final long serialVersionUID = 1L;

    /**
     * 单例
     */
    public static final EnumConverter INSTANCE = new EnumConverter();


    @Override
    protected Object convertInternal(final Class<?> targetClass, final Object value) {
        Enum enumValue = tryConvertEnum(value, targetClass);
        if (null == enumValue && !(value instanceof String)) {
            // 最后尝试先将value转String，再valueOf转换
            enumValue = Enum.valueOf((Class) targetClass, convertToStr(value));
        }

        if (null != enumValue) {
            return enumValue;
        }

        throw new ConvertException("Can not convert {} to {}", value, targetClass);
    }

    /**
     * 尝试转换，转换规则为：
     * <ul>
     *     <li>找到类似转换的静态方法调用实现转换且优先使用</li>
     *     <li>约定枚举类应该提供 valueOf(String) 和 valueOf(Integer)用于转换</li>
     *     <li>oriInt /name 转换托底</li>
     * </ul>
     *
     * @param value     被转换的值
     * @param enumClass enum类
     * @return 对应的枚举值
     */
    protected static Enum tryConvertEnum(final Object value, final Class enumClass) {
        if (value == null) {
            return null;
        }


        //oriInt 应该滞后使用 以 GB/T 2261.1-2003 性别编码为例，对应整数并非连续数字会导致数字转枚举时失败
        //0 - 未知的性别
        //1 - 男性
        //2 - 女性
        //5 - 女性改(变)为男性
        //6 - 男性改(变)为女性
        //9 - 未说明的性别
        Enum enumResult = null;
        if (value instanceof Integer) {
            enumResult = getEnumAt(enumClass, (Integer) value);
        } else if (value instanceof String) {
            try {
                enumResult = Enum.valueOf(enumClass, (String) value);
            } catch (final IllegalArgumentException e) {
                //ignore
            }
        }

        return enumResult;
    }

    /**
     * 获取给定位置的枚举值
     *
     * @param <E>       枚举类型泛型
     * @param enumClass 枚举类
     * @param index     枚举索引
     * @return 枚举值，null表示无此对应枚举
     * @since 5.1.6
     */
    private static <E extends Enum<E>> E getEnumAt(final Class<E> enumClass, final int index) {
        final E[] enumConstants = enumClass.getEnumConstants();
        return index >= 0 && index < enumConstants.length ? enumConstants[index] : null;
    }
}
