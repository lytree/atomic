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
package top.lytree.lang;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;

/**
 * CharsetUtils required of every implementation of the Java platform.
 * <p>
 * From the Java documentation <a href="http://docs.oracle.com/javase/6/docs/api/java/nio/charset/Charset.html">Standard
 * charsets</a>:
 * <p>
 * <cite>Every implementation of the Java platform is required to support the following character encodings. Consult the
 * release documentation for your implementation to see if any other encodings are supported. Consult the release
 * documentation for your implementation to see if any other encodings are supported. </cite>
 * </p>
 *
 * <ul>
 * <li>{@code US-ASCII}<p>
 * Seven-bit ASCII, a.k.a. ISO646-US, a.k.a. the Basic Latin block of the Unicode character set.</p></li>
 * <li>{@code ISO-8859-1}<p>
 * ISO Latin Alphabet No. 1, a.k.a. ISO-LATIN-1.</p></li>
 * <li>{@code UTF-8}<p>
 * Eight-bit Unicode Transformation Format.</p></li>
 * <li>{@code UTF-16BE}<p>
 * Sixteen-bit Unicode Transformation Format, big-endian byte order.</p></li>
 * <li>{@code UTF-16LE}<p>
 * Sixteen-bit Unicode Transformation Format, little-endian byte order.</p></li>
 * <li>{@code UTF-16}<p>
 * Sixteen-bit Unicode Transformation Format, byte order specified by a mandatory initial byte-order mark (either order
 * accepted on input, big-endian used on output.)</p></li>
 * </ul>
 * <p>
 * This perhaps would best belong in the Commons Lang project. Even if a similar class is defined in Commons Lang, it is
 * not foreseen that Commons Codec would be made to depend on Commons Lang.
 *
 * <p>
 * This class is immutable and thread-safe.
 * </p>
 *
 * @see <a href="http://docs.oracle.com/javase/6/docs/api/java/nio/charset/Charset.html">Standard charsets</a>
 */
public class CharsetUtils {

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
     *
     * @return 给定的{ @code字符集 }或默认的字符集，如果{ @code字符集 }为空。
     */
    public static Charset toCharset(final Charset charset) {
        return charset == null ? Charset.defaultCharset() : charset;
    }

    /**
     * 返回给定的{ @code字符集 }，如果{ @code字符集 }为空则返回默认的字符集。
     *
     * @param charsetName 一个Charset或null。
     *
     * @return 给定的{ @code字符集 }或默认的字符集，如果{ @code字符集 }为空
     */
    public static Charset toCharset(final String charsetName) {
        return charsetName == null ? Charset.defaultCharset() : Charset.forName(charsetName);
    }

    /**
     * 返回给定的{ @code字符集 }，如果{ @code字符集 }为空则返回默认的字符集。
     *
     * @param charsetName 一个Charset或null。
     *
     * @return 给定的{ @code字符集 }或默认的字符集，如果{ @code字符集 }为空。
     */
    public static String toCharsetName(final String charsetName) {
        return charsetName == null ? Charset.defaultCharset().name() : charsetName;
    }

}
