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

import top.lytree.bean.TypeUtils;
import top.lytree.convert.AbstractConverter;
import top.lytree.convert.CompositeConverter;


import java.lang.reflect.Type;
import java.util.concurrent.atomic.AtomicReference;

/**
 * {@link AtomicReference}转换器
 *
 * @author Looly
 * @since 3.0.8
 */
public class AtomicReferenceConverter extends AbstractConverter {
	private static final long serialVersionUID = 1L;

	@Override
	protected AtomicReference<?> convertInternal(final Class<?> targetClass, final Object value) {

		//尝试将值转换为Reference泛型的类型
		Object targetValue = null;
		final Type paramType = TypeUtils.getTypeArgument(AtomicReference.class);
		if(!TypeUtils.isUnknown(paramType)){
			targetValue = CompositeConverter.getInstance().convert(paramType, value);
		}
		if(null == targetValue){
			targetValue = value;
		}

		return new AtomicReference<>(targetValue);
	}

}
