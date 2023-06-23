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
//import top.lytree.bean.TypeUtils;
//import top.lytree.convert.Converter;
//
//
//
//import java.lang.reflect.Type;
//import java.util.Collection;
//
///**
// * 各种集合类转换器
// *
// * @author Looly
// * @since 3.0.8
// */
//public class CollectionConverter implements Converter {
//
//	/**
//	 * 单例实体
//	 */
//	public static CollectionConverter INSTANCE = new CollectionConverter();
//
//	@Override
//	public Collection<?> convert(Type targetType, final Object value) {
//		if (targetType instanceof TypeReference) {
//			targetType = ((TypeReference<?>) targetType).getType();
//		}
//
//		return convert(targetType, TypeUtils.getTypeArgument(targetType), value);
//	}
//
//	/**
//	 * 转换
//	 *
//	 * @param collectionType 集合类型
//	 * @param elementType    集合中元素类型
//	 * @param value          被转换的值
//	 * @return 转换后的集合对象
//	 */
//	public Collection<?> convert(final Type collectionType, final Type elementType, final Object value) {
//		// pr#2684，兼容EnumSet创建
//		final Collection<?> collection = CollUtil.create(collectionType.getClass(), elementType.getClass());
//		return CollUtil.addAll(collection, value, elementType);
//	}
//}
