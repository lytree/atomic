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
//
//
//
//import top.lytree.convert.AbstractConverter;
//import top.lytree.convert.ConvertException;
//
//import java.util.Map;
//
///**
// * Record类的转换器，支持：
// * <pre>
// *   Map =》 Record
// *   Bean =》 Record
// *   ValueProvider =》 Record
// * </pre>
// */
//public class RecordConverter extends AbstractConverter {
//	private static final long serialVersionUID = 1L;
//
//	/**
//	 * 单例对象
//	 */
//	public static RecordConverter INSTANCE = new RecordConverter();
//
//	@SuppressWarnings("unchecked")
//	@Override
//	protected Object convertInternal(final Class<?> targetClass, final Object value) {
//		ValueProvider<String> valueProvider = null;
//		if (value instanceof ValueProvider) {
//			valueProvider = (ValueProvider<String>) value;
//		} else if (value instanceof Map) {
//			valueProvider = new MapValueProvider((Map<String, ?>) value);
//		} else if (BeanUtil.isReadableBean(value.getClass())) {
//			valueProvider = new BeanValueProvider(value);
//		}
//
//		if (null != valueProvider) {
//			return RecordUtil.newInstance(targetClass, valueProvider);
//		}
//
//		throw new ConvertException("Unsupported source type: [{}] to [{}]", value.getClass(), targetClass);
//	}
//}
