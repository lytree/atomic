///*
// * Copyright (c) 2023 looly(loolly@aliyun.com)
// * Hutool is licensed under Mulan PSL v2.
// * You can use this software according to the terms and conditions of the Mulan PSL v2.
// * You may obtain a copy of Mulan PSL v2 at:
// *          http://license.coscl.org.cn/MulanPSL2
// * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND,
// * EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT,
// * MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
// * See the Mulan PSL v2 for more details.
// */
//
//package top.lytree.convert.impl;
//
//
//
//import top.lytree.convert.ConvertException;
//import top.lytree.convert.Converter;
//import top.lytree.utils.Assert;
//
//import java.io.Serializable;
//import java.lang.reflect.Type;
//import java.util.Map;
//
///**
// * Bean转换器，支持：
// * <pre>
// * Map =》 Bean
// * Bean =》 Bean
// * ValueProvider =》 Bean
// * </pre>
// *
// * @author Looly
// * @since 4.0.2
// */
//public class BeanConverter implements Converter, Serializable {
//	private static final long serialVersionUID = 1L;
//
//	/**
//	 * 单例对象
//	 */
//	public static BeanConverter INSTANCE = new BeanConverter();
//
//	private final CopyOptions copyOptions;
//
//	/**
//	 * 构造
//	 */
//	public BeanConverter() {
//		this(CopyOptions.of().setIgnoreError(true));
//	}
//
//	/**
//	 * 构造
//	 *
//	 * @param copyOptions Bean转换选项参数
//	 */
//	public BeanConverter(final CopyOptions copyOptions) {
//		this.copyOptions = copyOptions;
//	}
//
//	@Override
//	public Object convert(final Type targetType, final Object value) throws ConvertException {
//		Assert.notNull(targetType);
//		if (null == value) {
//			return null;
//		}
//
//		// value本身实现了Converter接口，直接调用
//		if(value instanceof Converter){
//			return ((Converter) value).convert(targetType, value);
//		}
//
//		final Class<?> targetClass = TypeUtil.getClass(targetType);
//		Assert.notNull(targetClass, "Target type is not a class!");
//
//		return convertInternal(targetType, targetClass, value);
//	}
//
//	private Object convertInternal(final Type targetType, final Class<?> targetClass, final Object value) {
//		if (value instanceof Map ||
//				value instanceof ValueProvider ||
//				BeanUtil.isWritableBean(value.getClass())) {
//			if (value instanceof Map && targetClass.isInterface()) {
//				// 将Map动态代理为Bean
//				return MapProxy.of((Map<?, ?>) value).toProxyBean(targetClass);
//			}
//
//			//限定被转换对象类型
//			return BeanCopier.of(value, ConstructorUtil.newInstanceIfPossible(targetClass), targetType, this.copyOptions).copy();
//		} else if (value instanceof byte[]) {
//			// 尝试反序列化
//			return SerializeUtil.deserialize((byte[]) value);
//		} else if(StrUtil.isEmptyIfStr(value)){
//			// issue#3136
//			return null;
//		}
//
//		throw new ConvertException("Unsupported source type: [{}] to [{}]", value.getClass(), targetType);
//	}
//}
