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


import top.lytree.lang.StringUtils;

/**
 * 转换异常
 *
 * @author xiaoleilu
 */
public class ConvertException extends RuntimeException {
    private static final long serialVersionUID = 4730597402855274362L;

    /**
     * 构造
     *
     * @param e 异常
     */
    public ConvertException(final Throwable e) {
        super(e);
    }

    /**
     * 构造
     *
     * @param message 消息
     */
    public ConvertException(final String message) {
        super(message);
    }

    /**
     * 构造
     *
     * @param messageTemplate 消息模板
     * @param params          参数
     */
    public ConvertException(final String messageTemplate, final Object... params) {
        super(StringUtils.format(messageTemplate, params));
    }

    /**
     * 构造
     *
     * @param message 消息
     * @param cause   被包装的子异常
     */
    public ConvertException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * 构造
     *
     * @param message            消息
     * @param cause              被包装的子异常
     * @param enableSuppression  是否启用抑制
     * @param writableStackTrace 堆栈跟踪是否应该是可写的
     */
    public ConvertException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    /**
     * 构造
     *
     * @param cause           被包装的子异常
     * @param messageTemplate 消息模板
     * @param params          参数
     */
    public ConvertException(final Throwable cause, final String messageTemplate, final Object... params) {
        super(StringUtils.format(messageTemplate, params), cause);
    }
}
