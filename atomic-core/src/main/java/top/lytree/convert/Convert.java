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

package top.lytree.convert;




import com.fasterxml.jackson.core.type.TypeReference;


import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 类型转换器
 *
 * @author xiaoleilu
 *
 */
public class Convert {

	/**
	 * 转换为字符串<br>
	 * 如果给定的值为null，或者转换失败，返回默认值<br>
	 * 转换失败不会报错
	 *
	 * @param value 被转换的值
	 * @param defaultValue 转换错误时的默认值
	 * @return 结果
	 */
	public static String toStr(final Object value, final String defaultValue) {
		return convertQuietly(String.class, value, defaultValue);
	}

	/**
	 * 转换为字符串<br>
	 * 如果给定的值为{@code null}，或者转换失败，返回默认值{@code null}<br>
	 * 转换失败不会报错
	 *
	 * @param value 被转换的值
	 * @return 结果
	 */
	public static String toStr(final Object value) {
		return toStr(value, null);
	}

	/**
	 * 转换为String数组
	 *
	 * @param value 被转换的值
	 * @return String数组
	 * @since 3.2.0
	 */
	public static String[] toStrArray(final Object value) {
		return convert(String[].class, value);
	}

	/**
	 * 转换为字符<br>
	 * 如果给定的值为null，或者转换失败，返回默认值<br>
	 * 转换失败不会报错
	 *
	 * @param value 被转换的值
	 * @param defaultValue 转换错误时的默认值
	 * @return 结果
	 */
	public static Character toChar(final Object value, final Character defaultValue) {
		return convertQuietly(Character.class, value, defaultValue);
	}

	/**
	 * 转换为字符<br>
	 * 如果给定的值为{@code null}，或者转换失败，返回默认值{@code null}<br>
	 * 转换失败不会报错
	 *
	 * @param value 被转换的值
	 * @return 结果
	 */
	public static Character toChar(final Object value) {
		return toChar(value, null);
	}

	/**
	 * 转换为Character数组
	 *
	 * @param value 被转换的值
	 * @return Character数组
	 * @since 3.2.0
	 */
	public static Character[] toCharArray(final Object value) {
		return convert(Character[].class, value);
	}

	/**
	 * 转换为byte<br>
	 * 如果给定的值为{@code null}，或者转换失败，返回默认值<br>
	 * 转换失败不会报错
	 *
	 * @param value 被转换的值
	 * @param defaultValue 转换错误时的默认值
	 * @return 结果
	 */
	public static Byte toByte(final Object value, final Byte defaultValue) {
		return convertQuietly(Byte.class, value, defaultValue);
	}

	/**
	 * 转换为byte<br>
	 * 如果给定的值为{@code null}，或者转换失败，返回默认值{@code null}<br>
	 * 转换失败不会报错
	 *
	 * @param value 被转换的值
	 * @return 结果
	 */
	public static Byte toByte(final Object value) {
		return toByte(value, null);
	}

	/**
	 * 转换为Byte数组
	 *
	 * @param value 被转换的值
	 * @return Byte数组
	 * @since 3.2.0
	 */
	public static Byte[] toByteArray(final Object value) {
		return convert(Byte[].class, value);
	}

	/**
	 * 转换为Byte数组
	 *
	 * @param value 被转换的值
	 * @return Byte数组
	 * @since 5.1.1
	 */
	public static byte[] toPrimitiveByteArray(final Object value) {
		return convert(byte[].class, value);
	}

	/**
	 * 转换为Short<br>
	 * 如果给定的值为{@code null}，或者转换失败，返回默认值<br>
	 * 转换失败不会报错
	 *
	 * @param value 被转换的值
	 * @param defaultValue 转换错误时的默认值
	 * @return 结果
	 */
	public static Short toShort(final Object value, final Short defaultValue) {
		return convertQuietly(Short.class, value, defaultValue);
	}

	/**
	 * 转换为Short<br>
	 * 如果给定的值为{@code null}，或者转换失败，返回默认值{@code null}<br>
	 * 转换失败不会报错
	 *
	 * @param value 被转换的值
	 * @return 结果
	 */
	public static Short toShort(final Object value) {
		return toShort(value, null);
	}

	/**
	 * 转换为Short数组
	 *
	 * @param value 被转换的值
	 * @return Short数组
	 * @since 3.2.0
	 */
	public static Short[] toShortArray(final Object value) {
		return convert(Short[].class, value);
	}

	/**
	 * 转换为Number<br>
	 * 如果给定的值为空，或者转换失败，返回默认值<br>
	 * 转换失败不会报错
	 *
	 * @param value 被转换的值
	 * @param defaultValue 转换错误时的默认值
	 * @return 结果
	 */
	public static Number toNumber(final Object value, final Number defaultValue) {
		return convertQuietly(Number.class, value, defaultValue);
	}

	/**
	 * 转换为Number<br>
	 * 如果给定的值为空，或者转换失败，返回默认值{@code null}<br>
	 * 转换失败不会报错
	 *
	 * @param value 被转换的值
	 * @return 结果
	 */
	public static Number toNumber(final Object value) {
		return toNumber(value, null);
	}

	/**
	 * 转换为Number数组
	 *
	 * @param value 被转换的值
	 * @return Number数组
	 * @since 3.2.0
	 */
	public static Number[] toNumberArray(final Object value) {
		return convert(Number[].class, value);
	}

	/**
	 * 转换为int<br>
	 * 如果给定的值为空，或者转换失败，返回默认值<br>
	 * 转换失败不会报错
	 *
	 * @param value 被转换的值
	 * @param defaultValue 转换错误时的默认值
	 * @return 结果
	 */
	public static Integer toInt(final Object value, final Integer defaultValue) {
		return convertQuietly(Integer.class, value, defaultValue);
	}

	/**
	 * 转换为int<br>
	 * 如果给定的值为{@code null}，或者转换失败，返回默认值{@code null}<br>
	 * 转换失败不会报错
	 *
	 * @param value 被转换的值
	 * @return 结果
	 */
	public static Integer toInt(final Object value) {
		return toInt(value, null);
	}

	/**
	 * 转换为Integer数组<br>
	 *
	 * @param value 被转换的值
	 * @return 结果
	 */
	public static Integer[] toIntArray(final Object value) {
		return convert(Integer[].class, value);
	}

	/**
	 * 转换为long<br>
	 * 如果给定的值为空，或者转换失败，返回默认值<br>
	 * 转换失败不会报错
	 *
	 * @param value 被转换的值
	 * @param defaultValue 转换错误时的默认值
	 * @return 结果
	 */
	public static Long toLong(final Object value, final Long defaultValue) {
		return convertQuietly(Long.class, value, defaultValue);
	}

	/**
	 * 转换为long<br>
	 * 如果给定的值为{@code null}，或者转换失败，返回默认值{@code null}<br>
	 * 转换失败不会报错
	 *
	 * @param value 被转换的值
	 * @return 结果
	 */
	public static Long toLong(final Object value) {
		return toLong(value, null);
	}

	/**
	 * 转换为Long数组<br>
	 *
	 * @param value 被转换的值
	 * @return 结果
	 */
	public static Long[] toLongArray(final Object value) {
		return convert(Long[].class, value);
	}

	/**
	 * 转换为double<br>
	 * 如果给定的值为空，或者转换失败，返回默认值<br>
	 * 转换失败不会报错
	 *
	 * @param value 被转换的值
	 * @param defaultValue 转换错误时的默认值
	 * @return 结果
	 */
	public static Double toDouble(final Object value, final Double defaultValue) {
		return convertQuietly(Double.class, value, defaultValue);
	}

	/**
	 * 转换为double<br>
	 * 如果给定的值为空，或者转换失败，返回默认值{@code null}<br>
	 * 转换失败不会报错
	 *
	 * @param value 被转换的值
	 * @return 结果
	 */
	public static Double toDouble(final Object value) {
		return toDouble(value, null);
	}

	/**
	 * 转换为Double数组<br>
	 *
	 * @param value 被转换的值
	 * @return 结果
	 */
	public static Double[] toDoubleArray(final Object value) {
		return convert(Double[].class, value);
	}

	/**
	 * 转换为Float<br>
	 * 如果给定的值为空，或者转换失败，返回默认值<br>
	 * 转换失败不会报错
	 *
	 * @param value 被转换的值
	 * @param defaultValue 转换错误时的默认值
	 * @return 结果
	 */
	public static Float toFloat(final Object value, final Float defaultValue) {
		return convertQuietly(Float.class, value, defaultValue);
	}

	/**
	 * 转换为Float<br>
	 * 如果给定的值为空，或者转换失败，返回默认值{@code null}<br>
	 * 转换失败不会报错
	 *
	 * @param value 被转换的值
	 * @return 结果
	 */
	public static Float toFloat(final Object value) {
		return toFloat(value, null);
	}

	/**
	 * 转换为Float数组<br>
	 *
	 * @param value 被转换的值
	 * @return 结果
	 */
	public static Float[] toFloatArray(final Object value) {
		return convert(Float[].class, value);
	}

	/**
	 * 转换为boolean<br>
	 * String支持的值为：true、false、yes、ok、no，1,0 如果给定的值为空，或者转换失败，返回默认值<br>
	 * 转换失败不会报错
	 *
	 * @param value 被转换的值
	 * @param defaultValue 转换错误时的默认值
	 * @return 结果
	 */
	public static Boolean toBoolean(final Object value, final Boolean defaultValue) {
		return convertQuietly(Boolean.class, value, defaultValue);
	}

	/**
	 * 转换为boolean<br>
	 * 如果给定的值为空，或者转换失败，返回默认值{@code null}<br>
	 * 转换失败不会报错
	 *
	 * @param value 被转换的值
	 * @return 结果
	 */
	public static Boolean toBoolean(final Object value) {
		return toBoolean(value, null);
	}

	/**
	 * 转换为Boolean数组<br>
	 *
	 * @param value 被转换的值
	 * @return 结果
	 */
	public static Boolean[] toBooleanArray(final Object value) {
		return convert(Boolean[].class, value);
	}

	/**
	 * 转换为BigInteger<br>
	 * 如果给定的值为空，或者转换失败，返回默认值<br>
	 * 转换失败不会报错
	 *
	 * @param value 被转换的值
	 * @param defaultValue 转换错误时的默认值
	 * @return 结果
	 */
	public static BigInteger toBigInteger(final Object value, final BigInteger defaultValue) {
		return convertQuietly(BigInteger.class, value, defaultValue);
	}

	/**
	 * 转换为BigInteger<br>
	 * 如果给定的值为空，或者转换失败，返回默认值{@code null}<br>
	 * 转换失败不会报错
	 *
	 * @param value 被转换的值
	 * @return 结果
	 */
	public static BigInteger toBigInteger(final Object value) {
		return toBigInteger(value, null);
	}

	/**
	 * 转换为BigDecimal<br>
	 * 如果给定的值为空，或者转换失败，返回默认值<br>
	 * 转换失败不会报错
	 *
	 * @param value 被转换的值
	 * @param defaultValue 转换错误时的默认值
	 * @return 结果
	 */
	public static BigDecimal toBigDecimal(final Object value, final BigDecimal defaultValue) {
		return convertQuietly(BigDecimal.class, value, defaultValue);
	}

	/**
	 * 转换为BigDecimal<br>
	 * 如果给定的值为空，或者转换失败，返回null<br>
	 * 转换失败不会报错
	 *
	 * @param value 被转换的值
	 * @return 结果
	 */
	public static BigDecimal toBigDecimal(final Object value) {
		return toBigDecimal(value, null);
	}



	/**
	 * 转换值为指定类型
	 *
	 * @param <T> 目标类型
	 * @param type 类型
	 * @param value 值
	 * @return 转换后的值
	 * @since 4.0.0
	 * @throws ConvertException 转换器不存在
	 */
	public static <T> T convert(final Class<T> type, final Object value) throws ConvertException{
		return convert((Type)type, value);
	}

	/**
	 * 转换值为指定类型
	 *
	 * @param <T> 目标类型
	 * @param reference 类型参考，用于持有转换后的泛型类型
	 * @param value 值
	 * @return 转换后的值
	 * @throws ConvertException 转换器不存在
	 */
	public static <T> T convert(final TypeReference<T> reference, final Object value) throws ConvertException{
		return convert(reference.getType(), value, null);
	}

	/**
	 * 转换值为指定类型
	 *
	 * @param <T> 目标类型
	 * @param type 类型
	 * @param value 值
	 * @return 转换后的值
	 * @throws ConvertException 转换器不存在
	 */
	public static <T> T convert(final Type type, final Object value) throws ConvertException{
		return convert(type, value, null);
	}

	/**
	 * 转换值为指定类型
	 *
	 * @param <T> 目标类型
	 * @param type 类型
	 * @param value 值
	 * @param defaultValue 默认值
	 * @return 转换后的值
	 * @throws ConvertException 转换器不存在
	 * @since 4.0.0
	 */
	public static <T> T convert(final Class<T> type, final Object value, final T defaultValue) throws ConvertException {
		return convert((Type)type, value, defaultValue);
	}

	/**
	 * 转换值为指定类型
	 *
	 * @param <T> 目标类型
	 * @param type 类型
	 * @param value 值
	 * @param defaultValue 默认值
	 * @return 转换后的值
	 * @throws ConvertException 转换器不存在
	 */
	public static <T> T convert(final Type type, final Object value, final T defaultValue) throws ConvertException {
		return convertWithCheck(type, value, defaultValue, false);
	}

	/**
	 * 转换值为指定类型，不抛异常转换<br>
	 * 当转换失败时返回{@code null}
	 *
	 * @param <T> 目标类型
	 * @param type 目标类型
	 * @param value 值
	 * @return 转换后的值，转换失败返回null
	 * @since 4.5.10
	 */
	public static <T> T convertQuietly(final Type type, final Object value) {
		return convertQuietly(type, value, null);
	}

	/**
	 * 转换值为指定类型，不抛异常转换<br>
	 * 当转换失败时返回默认值
	 *
	 * @param <T> 目标类型
	 * @param type 目标类型
	 * @param value 值
	 * @param defaultValue 默认值
	 * @return 转换后的值
	 * @since 4.5.10
	 */
	public static <T> T convertQuietly(final Type type, final Object value, final T defaultValue) {
		return convertWithCheck(type, value, defaultValue, true);
	}

	/**
	 * 转换值为指定类型，可选是否不抛异常转换<br>
	 * 当转换失败时返回默认值
	 *
	 * @param <T> 目标类型
	 * @param type 目标类型
	 * @param value 值
	 * @param defaultValue 默认值
	 * @param quietly 是否静默转换，true不抛异常
	 * @return 转换后的值
	 * @since 5.3.2
	 */
	public static <T> T convertWithCheck(final Type type, final Object value, final T defaultValue, final boolean quietly) {
		final CompositeConverter compositeConverter = CompositeConverter.getInstance();
		try {
			return compositeConverter.convert(type, value, defaultValue);
		} catch (final Exception e) {
			if(quietly){
				return defaultValue;
			}
			throw e;
		}
	}
}
