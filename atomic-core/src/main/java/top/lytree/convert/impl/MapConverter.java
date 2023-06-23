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
//import com.fasterxml.jackson.core.type.TypeReference;
//import top.lytree.bean.BeanUtils;
//import top.lytree.bean.TypeUtils;
//import top.lytree.collections.MapUtils;
//import top.lytree.convert.ConvertException;
//import top.lytree.convert.Converter;
//
//import java.io.Serial;
//import java.io.Serializable;
//import java.lang.reflect.Type;
//import java.util.LinkedHashMap;
//import java.util.Map;
//import java.util.Objects;
//
///**
// * {@link Map} 转换器，通过预定义key和value的类型，实现：
// * <ul>
// *     <li>Map 转 Map，key和value类型自动转换</li>
// *     <li>Bean 转 Map，字段和字段值类型自动转换</li>
// * </ul>
// *
// * @author Looly
// * @since 3.0.8
// */
//public class MapConverter implements Converter, Serializable {
//	@Serial
//	private static final long serialVersionUID = 1L;
//
//	/**
//	 * 单例
//	 */
//	public static MapConverter INSTANCE = new MapConverter();
//
//	@Override
//	public Object convert(Type targetType, final Object value) throws ConvertException {
//		if (targetType instanceof TypeReference) {
//			targetType = ((TypeReference<?>) targetType).getType();
//		}
//		final Type keyType = TypeUtils.getTypeArgument(targetType, 0);
//		final Type valueType = TypeUtils.getTypeArgument(targetType, 1);
//
//		return convert(targetType, keyType, valueType, value);
//	}
//
//	/**
//	 * 转换对象为指定键值类型的指定类型Map
//	 *
//	 * @param targetType 目标的Map类型
//	 * @param keyType    键类型
//	 * @param valueType  值类型
//	 * @param value      被转换的值
//	 * @return 转换后的Map
//	 * @throws ConvertException 转换异常或不支持的类型
//	 */
//	@SuppressWarnings("rawtypes")
//	public Map<?, ?> convert(final Type targetType, final Type keyType, final Type valueType, final Object value)
//		throws ConvertException{
//		Map map;
//		if (value instanceof Map) {
//			final Class<?> valueClass = value.getClass();
//			if (valueClass.equals(targetType)) {
//				final Type[] typeArguments = TypeUtils.getTypeArguments(valueClass);
//				if (null != typeArguments //
//						&& 2 == typeArguments.length//
//						&& Objects.equals(keyType, typeArguments[0]) //
//						&& Objects.equals(valueType, typeArguments[1])) {
//					//对于键值对类型一致的Map对象，不再做转换，直接返回原对象
//					return (Map) value;
//				}
//			}
//
//			map = MapUtils.createMap(targetType.getClass(), LinkedHashMap::new);
//			convertMapToMap(keyType, valueType, (Map) value, map);
//		}  else {
//			throw new ConvertException("Unsupported to map from [{}] of type: {}", value, value.getClass().getName());
//		}
//		return map;
//	}
//
//	/**
//	 * Map转Map
//	 *
//	 * @param srcMap    源Map
//	 * @param targetMap 目标Map
//	 */
//	@SuppressWarnings({"rawtypes", "unchecked"})
//	private void convertMapToMap(final Type keyType, final Type valueType, final Map<?, ?> srcMap, final Map targetMap) {
//		final CompositeConverter convert = CompositeConverter.getInstance();
//		srcMap.forEach((key, value) -> targetMap.put(
//			TypeUtil.isUnknown(keyType) ? key : convert.convert(keyType, key),
//			TypeUtil.isUnknown(valueType) ? value : convert.convert(valueType, value)
//		));
//	}
//}
