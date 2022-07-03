/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.yang.lang;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import javax.annotation.Nullable;
import org.apache.commons.lang3.CharSet;
import org.apache.commons.lang3.CharSetUtils;

/**
 * Charset 工具
 */
public class CharsetUtils extends CharSetUtils {

    /**
     * ISO-8859-1
     */
    public static final String ISO_8859_1 = "ISO-8859-1";
    /**
     * UTF-8
     */
    public static final String UTF_8 = "UTF-8";
    /**
     * GBK
     */
    public static final String GBK = "GBK";

    /**
     * ISO-8859-1
     */
    public static final Charset CHARSET_ISO_8859_1 = StandardCharsets.ISO_8859_1;
    /**
     * UTF-8
     */
    public static final Charset CHARSET_UTF_8 = StandardCharsets.UTF_8;
    /**
     * GBK
     */
    public static final Charset CHARSET_GBK;

    public static final Charset CHARSET_DEFAULT = Charset.defaultCharset();

    static {
        //避免不支持GBK的系统中运行报错 issue#731
        Charset _CHARSET_GBK = null;
        try {
            _CHARSET_GBK = Charset.forName(GBK);
        } catch (UnsupportedCharsetException e) {
            //ignore
        }
        CHARSET_GBK = _CHARSET_GBK;
    }

    /**
     * 返回给定的{ @code字符集 }，如果{ @code字符集 }为空则返回默认的字符集。
     *
     * @param charset 一个Charset或null。
     * @return 给定的{ @code字符集 }或默认的字符集，如果{ @code字符集 }为空。
     */
    public static Charset toCharset(@Nullable final Charset charset) {
        return charset == null ? Charset.defaultCharset() : charset;
    }

    /**
     * 返回给定的{ @code字符集 }，如果{ @code字符集 }为空则返回默认的字符集。
     *
     * @param charsetName 一个Charset或null。
     * @return 给定的{ @code字符集 }或默认的字符集，如果{ @code字符集 }为空
     */
    public static Charset toCharset(@Nullable final String charsetName) {
        return charsetName == null ? Charset.defaultCharset() : Charset.forName(charsetName);
    }

    /**
     * 返回给定的{ @code字符集 }，如果{ @code字符集 }为空则返回默认的字符集。
     *
     * @param charsetName 一个Charset或null。
     * @return 给定的{ @code字符集 }或默认的字符集，如果{ @code字符集 }为空。
     */
    public static String toCharsetName(@Nullable final String charsetName) {
        return charsetName == null ? Charset.defaultCharset().name() : charsetName;
    }

}
