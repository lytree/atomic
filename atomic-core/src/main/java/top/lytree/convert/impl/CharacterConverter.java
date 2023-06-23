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
import top.lytree.lang.StringUtils;


/**
 * 字符转换器
 *
 * @author Looly
 *
 */
public class CharacterConverter extends AbstractConverter {
	private static final long serialVersionUID = 1L;

	@Override
	protected Character convertInternal(final Class<?> targetClass, final Object value) {
		if (value instanceof Boolean) {
			return (char) ((Boolean) value ? 1 : 0);
		} else {
			final String valueStr = convertToStr(value);
			if (StringUtils.isNotBlank(valueStr)) {
				return valueStr.charAt(0);
			}
		}
		return null;
	}

}
