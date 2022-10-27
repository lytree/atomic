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


import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import top.lytree.base.Filter;
import top.lytree.bean.ObjectUtils;
import top.lytree.collections.ArraysUtils;
import top.lytree.text.StringFormatter;


/**
 * 字符串工具类
 */
//@Immutable
public class StringUtils extends org.apache.commons.lang3.StringUtils {

    private static final int STRING_BUILDER_SIZE = 256;

    public static final String US_ASCII = "US-ASCII";


    public static final String UTF_16 = "UTF-16";


    public static final String UTF_16BE = "UTF-16BE";


    public static final String UTF_16LE = "UTF-16LE";


    /**
     * ISO-8859-1
     */
    public static final String ISO_8859_1 = CharsetUtils.ISO_8859_1;
    /**
     * UTF-8
     */
    public static final String UTF_8 = CharsetUtils.UTF_8;


    public static final String DEFAULT_ENCODING = UTF_8;
    /**
     * GBK
     */
    public static final String GBK = CharsetUtils.GBK;
    /**
     * 字符串常量：制表符 {@code "\t"}
     */
    public static final String TAB = "	";

    /**
     * 字符串常量：点 {@code "."}
     */
    public static final String DOT = ".";

    /**
     * 字符串常量：双点 {@code ".."} <br> 用途：作为指向上级文件夹的路径，如：{@code "../path"}
     */
    public static final String DOUBLE_DOT = "..";

    /**
     * 字符串常量：斜杠 {@code "/"}
     */
    public static final String SLASH = "/";

    /**
     * 字符串常量：反斜杠 {@code "\\"}
     */
    public static final String BACKSLASH = "\\";

    /**
     * 字符串常量：回车符 {@code "\r"} <br> 解释：该字符常用于表示 Linux 系统和 MacOS 系统下的文本换行
     */
    public static final String CR = "\r";

    /**
     * 字符串常量：换行符 {@code "\n"}
     */
    public static final String LF = "\n";

    /**
     * 字符串常量：Windows 换行 {@code "\r\n"} <br> 解释：该字符串常用于表示 Windows 系统下的文本换行
     */
    public static final String CRLF = "\r\n";

    /**
     * 字符串常量：下划线 {@code "_"}
     */
    public static final String UNDERLINE = "_";

    /**
     * 字符串常量：减号（连接符） {@code "-"}
     */
    public static final String DASHED = "-";

    /**
     * 字符串常量：逗号 {@code ","}
     */
    public static final String COMMA = ",";

    /**
     * 字符串常量：花括号（左） <code>"{"</code>
     */
    public static final String DELIM_START = "{";

    /**
     * 字符串常量：花括号（右） <code>"}"</code>
     */
    public static final String DELIM_END = "}";

    /**
     * 字符串常量：中括号（左） {@code "["}
     */
    public static final String BRACKET_START = "[";

    /**
     * 字符串常量：中括号（右） {@code "]"}
     */
    public static final String BRACKET_END = "]";

    /**
     * 字符串常量：冒号 {@code ":"}
     */
    public static final String COLON = ":";

    /**
     * 字符串常量：艾特 {@code "@"}
     */
    public static final String AT = "@";

    /**
     * 字符串常量：{@code "null"} <br> 注意：{@code "null" != null}
     */
    public static final String NULL = "null";

    /**
     * 字符串常量：空字符串 {@code ""}
     */
    public static final String EMPTY = "";


    /**
     * 字符串常量：空格符 {@code " "}
     */
    public static final String SPACE = " ";

    /**
     * 字符串常量：HTML 空格转义 {@code "&nbsp;" -> " "}
     */
    public static final String HTML_NBSP = "&nbsp;";

    /**
     * 字符串常量：HTML And 符转义 {@code "&amp;" -> "&"}
     */
    public static final String HTML_AMP = "&amp;";

    /**
     * 字符串常量：HTML 双引号转义 {@code "&quot;" -> "\""}
     */
    public static final String HTML_QUOTE = "&quot;";

    /**
     * 字符串常量：HTML 单引号转义 {@code "&apos" -> "'"}
     */
    public static final String HTML_APOS = "&apos;";

    /**
     * 字符串常量：HTML 小于号转义 {@code "&lt;" -> "<"}
     */
    public static final String HTML_LT = "&lt;";

    /**
     * 字符串常量：HTML 大于号转义 {@code "&gt;" -> ">"}
     */
    public static final String HTML_GT = "&gt;";

    /**
     * 字符串常量：空 JSON {@code "{}"}
     */
    public static final String EMPTY_JSON = "{}";

    /**
     * <p>字符串是否为空白，空白的定义如下：</p>
     * <ol>
     *     <li>{@code null}</li>
     *     <li>空字符串：{@code ""}</li>
     *     <li>空格、全角空格、制表符、换行符，等不可见字符</li>
     * </ol>
     *
     * <p>例：</p>
     * <ul>
     *     <li>{@code StrUtil.isBlank(null)     // true}</li>
     *     <li>{@code StrUtil.isBlank("")       // true}</li>
     *     <li>{@code StrUtil.isBlank(" \t\n")  // true}</li>
     *     <li>{@code StrUtil.isBlank("abc")    // false}</li>
     * </ul>
     *
     * <p>注意：该方法与 {@link #isEmpty(CharSequence)} 的区别是：
     * 该方法会校验空白字符，且性能相对于 {@link #isEmpty(CharSequence)} 略慢。</p>
     * <br>
     *
     * <p>建议：</p>
     * <ul>
     *     <li>该方法建议仅对于客户端（或第三方接口）传入的参数使用该方法。</li>
     *     <li>需要同时校验多个字符串时，建议采用 {@link #hasBlank(CharSequence...)} 或 {@link #isAllBlank(CharSequence...)}</li>
     * </ul>
     *
     * @param str 被检测的字符串
     *
     * @return 若为空白，则返回 true
     *
     * @see #isEmpty(CharSequence)
     */
    public static boolean isBlank(CharSequence str) {
        int length;

        if ((str == null) || ((length = str.length()) == 0)) {
            return true;
        }

        for (int i = 0; i < length; i++) {
            // 只要有一个非空字符即为非空字符串
            if (!CharUtils.isBlankChar(str.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    /**
     * <p>字符串是否为非空白，非空白的定义如下： </p>
     * <ol>
     *     <li>不为 {@code null}</li>
     *     <li>不为空字符串：{@code ""}</li>
     *     <li>不为空格、全角空格、制表符、换行符，等不可见字符</li>
     * </ol>
     *
     * <p>例：</p>
     * <ul>
     *     <li>{@code StrUtil.isNotBlank(null)     // false}</li>
     *     <li>{@code StrUtil.isNotBlank("")       // false}</li>
     *     <li>{@code StrUtil.isNotBlank(" \t\n")  // false}</li>
     *     <li>{@code StrUtil.isNotBlank("abc")    // true}</li>
     * </ul>
     *
     * <p>注意：该方法与 {@link #isNotEmpty(CharSequence)} 的区别是：
     * 该方法会校验空白字符，且性能相对于 {@link #isNotEmpty(CharSequence)} 略慢。</p>
     * <p>建议：仅对于客户端（或第三方接口）传入的参数使用该方法。</p>
     *
     * @param str 被检测的字符串
     *
     * @return 是否为非空
     *
     * @see #isBlank(CharSequence)
     */
    public static boolean isNotBlank(CharSequence str) {
        return !isBlank(str);
    }

    /**
     * <p>指定字符串数组中，是否包含空字符串。</p>
     * <p>如果指定的字符串数组的长度为 0，或者其中的任意一个元素是空字符串，则返回 true。</p>
     * <br>
     *
     * <p>例：</p>
     * <ul>
     *     <li>{@code StrUtil.hasBlank()                  // true}</li>
     *     <li>{@code StrUtil.hasBlank("", null, " ")     // true}</li>
     *     <li>{@code StrUtil.hasBlank("123", " ")        // true}</li>
     *     <li>{@code StrUtil.hasBlank("123", "abc")      // false}</li>
     * </ul>
     *
     * <p>注意：该方法与 {@link #isAllBlank(CharSequence...)} 的区别在于：</p>
     * <ul>
     *     <li>hasBlank(CharSequence...)            等价于 {@code isBlank(...) || isBlank(...) || ...}</li>
     *     <li>{@link #isAllBlank(CharSequence...)} 等价于 {@code isBlank(...) && isBlank(...) && ...}</li>
     * </ul>
     *
     * @param strs 字符串列表
     *
     * @return 是否包含空字符串
     */
    public static boolean hasBlank(CharSequence... strs) {
        if (ArraysUtils.isEmpty(strs)) {
            return true;
        }

        for (CharSequence str : strs) {
            if (isBlank(str)) {
                return true;
            }
        }
        return false;
    }

    /**
     * <p>指定字符串数组中的元素，是否全部为空字符串。</p>
     * <p>如果指定的字符串数组的长度为 0，或者所有元素都是空字符串，则返回 true。</p>
     * <br>
     *
     * <p>例：</p>
     * <ul>
     *     <li>{@code StrUtil.isAllBlank()                  // true}</li>
     *     <li>{@code StrUtil.isAllBlank("", null, " ")     // true}</li>
     *     <li>{@code StrUtil.isAllBlank("123", " ")        // false}</li>
     *     <li>{@code StrUtil.isAllBlank("123", "abc")      // false}</li>
     * </ul>
     *
     * <p>注意：该方法与 {@link #hasBlank(CharSequence...)} 的区别在于：</p>
     * <ul>
     *     <li>{@link #hasBlank(CharSequence...)}   等价于 {@code isBlank(...) || isBlank(...) || ...}</li>
     *     <li>isAllBlank(CharSequence...)          等价于 {@code isBlank(...) && isBlank(...) && ...}</li>
     * </ul>
     *
     * @param strs 字符串列表
     *
     * @return 所有字符串是否为空白
     */
    public static boolean isAllBlank(CharSequence... strs) {
        if (ArraysUtils.isEmpty(strs)) {
            return true;
        }

        for (CharSequence str : strs) {
            if (isNotBlank(str)) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>字符串是否为空，空的定义如下：</p>
     * <ol>
     *     <li>{@code null}</li>
     *     <li>空字符串：{@code ""}</li>
     * </ol>
     *
     * <p>例：</p>
     * <ul>
     *     <li>{@code StrUtil.isEmpty(null)     // true}</li>
     *     <li>{@code StrUtil.isEmpty("")       // true}</li>
     *     <li>{@code StrUtil.isEmpty(" \t\n")  // false}</li>
     *     <li>{@code StrUtil.isEmpty("abc")    // false}</li>
     * </ul>
     *
     * <p>注意：该方法与 {@link #isBlank(CharSequence)} 的区别是：该方法不校验空白字符。</p>
     * <p>建议：</p>
     * <ul>
     *     <li>该方法建议用于工具类或任何可以预期的方法参数的校验中。</li>
     *     <li>需要同时校验多个字符串时，建议采用 {@link #hasEmpty(CharSequence...)} 或 {@link #isAllEmpty(CharSequence...)}</li>
     * </ul>
     *
     * @param str 被检测的字符串
     *
     * @return 是否为空
     *
     * @see #isBlank(CharSequence)
     */
    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

    /**
     * <p>字符串是否为非空白，非空白的定义如下： </p>
     * <ol>
     *     <li>不为 {@code null}</li>
     *     <li>不为空字符串：{@code ""}</li>
     * </ol>
     *
     * <p>例：</p>
     * <ul>
     *     <li>{@code StrUtil.isNotEmpty(null)     // false}</li>
     *     <li>{@code StrUtil.isNotEmpty("")       // false}</li>
     *     <li>{@code StrUtil.isNotEmpty(" \t\n")  // true}</li>
     *     <li>{@code StrUtil.isNotEmpty("abc")    // true}</li>
     * </ul>
     *
     * <p>注意：该方法与 {@link #isNotBlank(CharSequence)} 的区别是：该方法不校验空白字符。</p>
     * <p>建议：该方法建议用于工具类或任何可以预期的方法参数的校验中。</p>
     *
     * @param str 被检测的字符串
     *
     * @return 是否为非空
     *
     * @see #isEmpty(CharSequence)
     */
    public static boolean isNotEmpty(CharSequence str) {
        return !isEmpty(str);
    }

    /**
     * 当给定字符串为null时，转换为Empty
     *
     * @param str 被检查的字符串
     *
     * @return 原字符串或者空串
     *
     * @see #defaultIfNull(CharSequence)
     */
    public static String emptyIfNull(CharSequence str) {
        return defaultIfNull(str);
    }

    /**
     * 当给定字符串为null时，转换为Empty
     *
     * @param str 被转换的字符串
     *
     * @return 转换后的字符串
     */
    public static String defaultIfNull(CharSequence str) {
        return defaultIfNull(str, EMPTY);
    }

    /**
     * 如果字符串是 {@code null}，则返回指定默认字符串，否则返回字符串本身。
     *
     * <pre>
     * defaultIfNull(null, &quot;default&quot;)  = &quot;default&quot;
     * defaultIfNull(&quot;&quot;, &quot;default&quot;)    = &quot;&quot;
     * defaultIfNull(&quot;  &quot;, &quot;default&quot;)  = &quot;  &quot;
     * defaultIfNull(&quot;bat&quot;, &quot;default&quot;) = &quot;bat&quot;
     * </pre>
     *
     * @param str        要转换的字符串
     * @param defaultStr 默认字符串
     *
     * @return 字符串本身或指定的默认字符串
     */
    public static String defaultIfNull(CharSequence str, String defaultStr) {
        return (str == null) ? defaultStr : str.toString();
    }

    /**
     * 如果字符串是{@code null}或者&quot;&quot;，则返回指定默认字符串，否则返回字符串本身。
     *
     * <pre>
     * defaultIfEmpty(null, &quot;default&quot;)  = &quot;default&quot;
     * defaultIfEmpty(&quot;&quot;, &quot;default&quot;)    = &quot;default&quot;
     * defaultIfEmpty(&quot;  &quot;, &quot;default&quot;)  = &quot;  &quot;
     * defaultIfEmpty(&quot;bat&quot;, &quot;default&quot;) = &quot;bat&quot;
     * </pre>
     *
     * @param str        要转换的字符串
     * @param defaultStr 默认字符串
     *
     * @return 字符串本身或指定的默认字符串
     */
    public static String defaultIfEmpty(CharSequence str, String defaultStr) {
        return isEmpty(str) ? defaultStr : str.toString();
    }

    /**
     * 如果字符串是{@code null}或者&quot;&quot;或者空白，则返回指定默认字符串，否则返回字符串本身。
     *
     * <pre>
     * defaultIfBlank(null, &quot;default&quot;)  = &quot;default&quot;
     * defaultIfBlank(&quot;&quot;, &quot;default&quot;)    = &quot;default&quot;
     * defaultIfBlank(&quot;  &quot;, &quot;default&quot;)  = &quot;default&quot;
     * defaultIfBlank(&quot;bat&quot;, &quot;default&quot;) = &quot;bat&quot;
     * </pre>
     *
     * @param str        要转换的字符串
     * @param defaultStr 默认字符串
     *
     * @return 字符串本身或指定的默认字符串
     */
    public static String defaultIfBlank(CharSequence str, String defaultStr) {
        return isBlank(str) ? defaultStr : str.toString();
    }

    /**
     * 当给定字符串为空字符串时，转换为{@code null}
     *
     * @param str 被转换的字符串
     *
     * @return 转换后的字符串
     */
    public static String nullIfEmpty(CharSequence str) {
        return isEmpty(str) ? null : str.toString();
    }

    /**
     * <p>是否包含空字符串。</p>
     * <p>如果指定的字符串数组的长度为 0，或者其中的任意一个元素是空字符串，则返回 true。</p>
     * <br>
     *
     * <p>例：</p>
     * <ul>
     *     <li>{@code StrUtil.hasEmpty()                  // true}</li>
     *     <li>{@code StrUtil.hasEmpty("", null)          // true}</li>
     *     <li>{@code StrUtil.hasEmpty("123", "")         // true}</li>
     *     <li>{@code StrUtil.hasEmpty("123", "abc")      // false}</li>
     *     <li>{@code StrUtil.hasEmpty(" ", "\t", "\n")   // false}</li>
     * </ul>
     *
     * <p>注意：该方法与 {@link #isAllEmpty(CharSequence...)} 的区别在于：</p>
     * <ul>
     *     <li>hasEmpty(CharSequence...)            等价于 {@code isEmpty(...) || isEmpty(...) || ...}</li>
     *     <li>{@link #isAllEmpty(CharSequence...)} 等价于 {@code isEmpty(...) && isEmpty(...) && ...}</li>
     * </ul>
     *
     * @param strs 字符串列表
     *
     * @return 是否包含空字符串
     */
    public static boolean hasEmpty(CharSequence... strs) {
        if (ArraysUtils.isEmpty(strs)) {
            return true;
        }

        for (CharSequence str : strs) {
            if (isEmpty(str)) {
                return true;
            }
        }
        return false;
    }

    /**
     * <p>指定字符串数组中的元素，是否全部为空字符串。</p>
     * <p>如果指定的字符串数组的长度为 0，或者所有元素都是空字符串，则返回 true。</p>
     * <br>
     *
     * <p>例：</p>
     * <ul>
     *     <li>{@code StrUtil.isAllEmpty()                  // true}</li>
     *     <li>{@code StrUtil.isAllEmpty("", null)          // true}</li>
     *     <li>{@code StrUtil.isAllEmpty("123", "")         // false}</li>
     *     <li>{@code StrUtil.isAllEmpty("123", "abc")      // false}</li>
     *     <li>{@code StrUtil.isAllEmpty(" ", "\t", "\n")   // false}</li>
     * </ul>
     *
     * <p>注意：该方法与 {@link #hasEmpty(CharSequence...)} 的区别在于：</p>
     * <ul>
     *     <li>{@link #hasEmpty(CharSequence...)}   等价于 {@code isEmpty(...) || isEmpty(...) || ...}</li>
     *     <li>isAllEmpty(CharSequence...)          等价于 {@code isEmpty(...) && isEmpty(...) && ...}</li>
     * </ul>
     *
     * @param strs 字符串列表
     *
     * @return 所有字符串是否为空白
     */
    public static boolean isAllEmpty(CharSequence... strs) {
        if (ArraysUtils.isEmpty(strs)) {
            return true;
        }

        for (CharSequence str : strs) {
            if (isNotEmpty(str)) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>指定字符串数组中的元素，是否都不为空字符串。</p>
     * <p>如果指定的字符串数组的长度不为 0，或者所有元素都不是空字符串，则返回 true。</p>
     * <br>
     *
     * <p>例：</p>
     * <ul>
     *     <li>{@code StrUtil.isAllNotEmpty()                  // false}</li>
     *     <li>{@code StrUtil.isAllNotEmpty("", null)          // false}</li>
     *     <li>{@code StrUtil.isAllNotEmpty("123", "")         // false}</li>
     *     <li>{@code StrUtil.isAllNotEmpty("123", "abc")      // true}</li>
     *     <li>{@code StrUtil.isAllNotEmpty(" ", "\t", "\n")   // true}</li>
     * </ul>
     *
     * <p>注意：该方法与 {@link #isAllEmpty(CharSequence...)} 的区别在于：</p>
     * <ul>
     *     <li>{@link #isAllEmpty(CharSequence...)}    等价于 {@code isEmpty(...) && isEmpty(...) && ...}</li>
     *     <li>isAllNotEmpty(CharSequence...)          等价于 {@code !isEmpty(...) && !isEmpty(...) && ...}</li>
     * </ul>
     *
     * @param args 字符串数组
     *
     * @return 所有字符串是否都不为为空白
     */
    public static boolean isAllNotEmpty(CharSequence... args) {
        return false == hasEmpty(args);
    }

    /**
     * 是否存都不为{@code null}或空对象或空白符的对象，通过{@link #hasBlank(CharSequence...)} 判断元素
     *
     * @param args 被检查的对象,一个或者多个
     *
     * @return 是否都不为空
     */
    public static boolean isAllNotBlank(CharSequence... args) {
        return false == hasBlank(args);
    }

    /**
     * 检查字符串是否为null、“null”、“undefined”
     *
     * @param str 被检查的字符串
     *
     * @return 是否为null、“null”、“undefined”
     */
    public static boolean isNullOrUndefined(CharSequence str) {
        if (null == str) {
            return true;
        }
        return isNullOrUndefinedStr(str);
    }

    /**
     * 检查字符串是否为null、“”、“null”、“undefined”
     *
     * @param str 被检查的字符串
     *
     * @return 是否为null、“”、“null”、“undefined”
     */
    public static boolean isEmptyOrUndefined(CharSequence str) {
        if (isEmpty(str)) {
            return true;
        }
        return isNullOrUndefinedStr(str);
    }

    /**
     * 检查字符串是否为null、空白串、“null”、“undefined”
     *
     * @param str 被检查的字符串
     *
     * @return 是否为null、空白串、“null”、“undefined”
     */
    public static boolean isBlankOrUndefined(CharSequence str) {
        if (isBlank(str)) {
            return true;
        }
        return isNullOrUndefinedStr(str);
    }

    /**
     * 是否为“null”、“undefined”，不做空指针检查
     *
     * @param str 字符串
     *
     * @return 是否为“null”、“undefined”
     */
    private static boolean isNullOrUndefinedStr(CharSequence str) {
        String strString = str.toString().trim();
        return NULL.equals(strString) || "undefined".equals(strString);
    }

    // ------------------------------------------------------------------------ indexOf

    /**
     * <p>查找CharSequence中的第一个索引，处理{@code null}。如果可能的话，这个方法使用{@link String#indexOf(String, int)}。</p>
     *
     * <p>一个{@code null} CharSequence将返回{@code -1}。</p>
     *
     * <pre>
     * StringUtils.indexOf(null, *)          = -1
     * StringUtils.indexOf(*, null)          = -1
     * StringUtils.indexOf("", "")           = 0
     * StringUtils.indexOf("", *)            = -1 (except when * = "")
     * StringUtils.indexOf("aabaabaa", "a")  = 0
     * StringUtils.indexOf("aabaabaa", "b")  = 2
     * StringUtils.indexOf("aabaabaa", "ab") = 1
     * StringUtils.indexOf("aabaabaa", "")   = 0
     * </pre>
     *
     * @param seq       要检查的CharSequence，可以为空
     * @param searchSeq 查找的CharSequence可能为空
     *
     * @return 搜索CharSequence的第一个索引，如果没有匹配或{@code null}字符串输入，则为-1
     */
    public static int indexOf(final CharSequence seq, final CharSequence searchSeq) {
        if (seq == null || searchSeq == null) {
            return INDEX_NOT_FOUND;
        }
        return CharSequenceUtils.indexOf(seq, searchSeq, 0);
    }

    /**
     * <p>查找CharSequence中的第一个索引，处理{@code null}。如果可能的话，这个方法使用{@link String#indexOf(String, int)}。</p>
     *
     * <p>一个{@code null} CharSequence将返回{@code -1}。负的起始位置被视为零。一个空的("")搜索CharSequence总是匹配的。大于字符串长度的起始位置只匹配一个空的搜索CharSequence。</p>
     *
     * <pre>
     * StringUtils.indexOf(null, *, *)          = -1
     * StringUtils.indexOf(*, null, *)          = -1
     * StringUtils.indexOf("", "", 0)           = 0
     * StringUtils.indexOf("", *, 0)            = -1 (except when * = "")
     * StringUtils.indexOf("aabaabaa", "a", 0)  = 0
     * StringUtils.indexOf("aabaabaa", "b", 0)  = 2
     * StringUtils.indexOf("aabaabaa", "ab", 0) = 1
     * StringUtils.indexOf("aabaabaa", "b", 3)  = 5
     * StringUtils.indexOf("aabaabaa", "b", 9)  = -1
     * StringUtils.indexOf("aabaabaa", "b", -1) = 2
     * StringUtils.indexOf("aabaabaa", "", 2)   = 2
     * StringUtils.indexOf("abc", "", 9)        = 3
     * </pre>
     *
     * @param seq       要检查的CharSequence，可以为空
     * @param searchSeq 查找的CharSequence可能为空
     * @param startPos  起始位置，负的作为零
     *
     * @return CharSequence的第一个索引(总是 & ge ; startPos)， -1如果没有匹配或{@code null}字符串输入
     */
    public static int indexOf(final CharSequence seq, final CharSequence searchSeq, final int startPos) {
        if (seq == null || searchSeq == null) {
            return INDEX_NOT_FOUND;
        }
        return CharSequenceUtils.indexOf(seq, searchSeq, startPos);
    }

    /**
     * 返回指定字符在{@code seq}内第一个匹配项的索引。如果一个值为{@code searchChar}的字符出现在由{@code seq} {@code CharSequence}对象所表示的字符序列中，那么将返回第一个出现该字符的索引(以Unicode代码单位)。对于{@code
     * searchChar}在0到0xFFFF(包括)范围内的值，这是最小的值<i>k</i> such that:
     * <blockquote><pre>
     * this.charAt(<i>k</i>) == searchChar
     * </pre></blockquote>
     * 是真的。对于{@code searchChar}的其他值，它是最小的值<i>k</i> such that:
     * <blockquote><pre>
     * this.codePointAt(<i>k</i>) == searchChar
     * </pre></blockquote>
     * 是真的。在这两种情况下，如果在{@code seq}中没有出现这样的字符，则返回{@code INDEX_NOT_FOUND(-1)}。
     *
     * <p>此外，{@code null}或空("")CharSequence将
     * 返回 {@code INDEX_NOT_FOUND (-1)}.</p>
     *
     * <pre>
     * StringUtils.indexOf(null, *)         = -1
     * StringUtils.indexOf("", *)           = -1
     * StringUtils.indexOf("aabaabaa", 'a') = 0
     * StringUtils.indexOf("aabaabaa", 'b') = 2
     * </pre>
     *
     * @param seq        要检查的CharSequence，可以为空
     * @param searchChar 要找的角色
     *
     * @return 搜索字符的第一个索引，如果没有匹配或{@code null}字符串输入，则为-1
     */
    public static int indexOf(final CharSequence seq, final int searchChar) {
        if (isEmpty(seq)) {
            return INDEX_NOT_FOUND;
        }
        return CharSequenceUtils.indexOf(seq, searchChar, 0);
    }

    /**
     * 返回指定字符在{@code seq}内的第一个匹配项的索引，从指定索引处开始搜索。
     * <p>
     * 如果一个值为{@code searchChar}的字符出现在由{@code seq} {@code CharSequence}对象所表示的字符序列中，索引不小于{@code startPos}，那么将返回第一个出现该字符的索引。对于{@code searchChar}在0到0xFFFF(包括)范围内的值，这是最小的值 <i>k</i> such
     * that:
     * <blockquote><pre>
     * (this.charAt(<i>k</i>) == searchChar) &amp;&amp; (<i>k</i> &gt;= startPos)
     * </pre></blockquote>
     * 是真的。对于{@code searchChar}的其他值，它是最小的值 <i>k</i> such that:
     * <blockquote><pre>
     * (this.codePointAt(<i>k</i>) == searchChar) &amp;&amp; (<i>k</i> &gt;= startPos)
     * </pre></blockquote>
     * is true. In either case, if no such character occurs in {@code seq} at or after position {@code startPos}, then {@code -1} is returned.是真的。在这两种情况下，如果没有这样的字符出现在{@code
     * seq}位置{@code startPos}或之后，则返回{@code -1}。
     *
     * <p>
     * {@code startPos}的值没有限制。如果它是负的，它的效果就像它是零:可以搜索整个字符串。如果它大于这个字符串的长度，它的效果与等于这个字符串的长度相同:返回{@code (INDEX_NOT_FOUND) -1}。此外，{@code null}或空("")CharSequence将返回{@code (INDEX_NOT_FOUND)
     * -1}。
     *
     * <p>所有索引都在{@code char}值中指定
     * (Unicode code units).
     *
     * <pre>
     * StringUtils.indexOf(null, *, *)          = -1
     * StringUtils.indexOf("", *, *)            = -1
     * StringUtils.indexOf("aabaabaa", 'b', 0)  = 2
     * StringUtils.indexOf("aabaabaa", 'b', 3)  = 5
     * StringUtils.indexOf("aabaabaa", 'b', 9)  = -1
     * StringUtils.indexOf("aabaabaa", 'b', -1) = 2
     * </pre>
     *
     * @param seq        要检查的CharSequence，可以为空
     * @param searchChar 要找的角色
     * @param startPos   起始位置，负的作为零
     *
     * @return 搜索字符的第一个索引(总是 & ge ; startPos)， -1如果没有匹配或{@code null}字符串输入
     */
    public static int indexOf(final CharSequence seq, final int searchChar, final int startPos) {
        if (isEmpty(seq)) {
            return INDEX_NOT_FOUND;
        }
        return CharSequenceUtils.indexOf(seq, searchChar, startPos);
    }

    /**
     * <p>搜索CharSequence以查找给定字符集中任何字符的第一个索引。</p>
     *
     * <p>一个{@code null}字符串将返回{@code -1}。一个{@code null}或零长度的搜索数组将返回{@code -1}。</p>
     *
     * <pre>
     * StringUtils.indexOfAny(null, *)                  = -1
     * StringUtils.indexOfAny("", *)                    = -1
     * StringUtils.indexOfAny(*, null)                  = -1
     * StringUtils.indexOfAny(*, [])                    = -1
     * StringUtils.indexOfAny("zzabyycdxx", ['z', 'a']) = 0
     * StringUtils.indexOfAny("zzabyycdxx", ['b', 'y']) = 3
     * StringUtils.indexOfAny("aba", ['z'])             = -1
     * </pre>
     *
     * @param cs          要检查的CharSequence，可以为空
     * @param searchChars 要搜索的字符可能为空
     *
     * @return 任何字符的索引，如果没有匹配或输入为空，则为-1
     */
    public static int indexOfAny(final CharSequence cs, final char... searchChars) {
        if (isEmpty(cs) || ArraysUtils.isEmpty(searchChars)) {
            return INDEX_NOT_FOUND;
        }
        final int csLen = cs.length();
        final int csLast = csLen - 1;
        final int searchLen = searchChars.length;
        final int searchLast = searchLen - 1;
        for (int i = 0; i < csLen; i++) {
            final char ch = cs.charAt(i);
            for (int j = 0; j < searchLen; j++) {
                if (searchChars[j] == ch) {
                    if (i < csLast && j < searchLast && Character.isHighSurrogate(ch)) {
                        // ch is a supplementary character
                        if (searchChars[j + 1] == cs.charAt(i + 1)) {
                            return i;
                        }
                    } else {
                        return i;
                    }
                }
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * <p>找到一组潜在子字符串的第一个下标。</p>
     *
     * <p>一个{@code null} CharSequence将返回{@code -1}。
     * <p>一个{@code null}或零长度的搜索数组将返回{@code -1}。
     * <p>{@code null}搜索数组条目将被忽略，但是如果{@code str}不为空，包含""的搜索数组将返回{@code 0}。
     * 如果可能的话，这个方法使用{@link String#indexOf(String)}
     * <pre>
     * StringUtils.indexOfAny(null, *)                      = -1
     * StringUtils.indexOfAny(*, null)                      = -1
     * StringUtils.indexOfAny(*, [])                        = -1
     * StringUtils.indexOfAny("zzabyycdxx", ["ab", "cd"])   = 2
     * StringUtils.indexOfAny("zzabyycdxx", ["cd", "ab"])   = 2
     * StringUtils.indexOfAny("zzabyycdxx", ["mn", "op"])   = -1
     * StringUtils.indexOfAny("zzabyycdxx", ["zab", "aby"]) = 1
     * StringUtils.indexOfAny("zzabyycdxx", [""])           = 0
     * StringUtils.indexOfAny("", [""])                     = 0
     * StringUtils.indexOfAny("", ["a"])                    = -1
     * </pre>
     *
     * @param str        要检查的CharSequence，可以为空
     * @param searchStrs 要搜索的charsequence可以为空
     *
     * @return str中所有搜索字符串的第一个索引，如果没有匹配，则为-1
     */
    public static int indexOfAny(final CharSequence str, final CharSequence... searchStrs) {
        if (str == null || searchStrs == null) {
            return INDEX_NOT_FOUND;
        }

        // String's can't have a MAX_VALUEth index.
        int ret = Integer.MAX_VALUE;

        int tmp = 0;
        for (final CharSequence search : searchStrs) {
            if (search == null) {
                continue;
            }
            tmp = CharSequenceUtils.indexOf(str, search, 0);
            if (tmp == INDEX_NOT_FOUND) {
                continue;
            }

            if (tmp < ret) {
                ret = tmp;
            }
        }

        return ret == Integer.MAX_VALUE ? INDEX_NOT_FOUND : ret;
    }

    /**
     * <p>搜索CharSequence以查找给定字符集中任何字符的第一个索引。</p>
     *
     * <p>一个{@code null}字符串将返回{@code -1}。
     * 一个{@code null}搜索字符串将返回{@code -1}。</p>
     *
     * <pre>
     * StringUtils.indexOfAny(null, *)            = -1
     * StringUtils.indexOfAny("", *)              = -1
     * StringUtils.indexOfAny(*, null)            = -1
     * StringUtils.indexOfAny(*, "")              = -1
     * StringUtils.indexOfAny("zzabyycdxx", "za") = 0
     * StringUtils.indexOfAny("zzabyycdxx", "by") = 3
     * StringUtils.indexOfAny("aba", "z")         = -1
     * </pre>
     *
     * @param cs          要检查的CharSequence，可以为空
     * @param searchChars 要搜索的字符可能为空
     *
     * @return 任何字符的索引，如果没有匹配或输入为空，则为-1
     */
    public static int indexOfAny(final CharSequence cs, final String searchChars) {
        if (isEmpty(cs) || isEmpty(searchChars)) {
            return INDEX_NOT_FOUND;
        }
        return indexOfAny(cs, searchChars.toCharArray());
    }

    /**
     * <p>搜索CharSequence以查找不在给定字符集中的任何字符的第一个索引。</p>
     *
     * <p>一个{@code null} CharSequence将返回{@code -1}。
     * 一个{@code null}或零长度的搜索数组将返回{@code -1}。</p>
     *
     * <pre>
     * StringUtils.indexOfAnyBut(null, *)                              = -1
     * StringUtils.indexOfAnyBut("", *)                                = -1
     * StringUtils.indexOfAnyBut(*, null)                              = -1
     * StringUtils.indexOfAnyBut(*, [])                                = -1
     * StringUtils.indexOfAnyBut("zzabyycdxx", new char[] {'z', 'a'} ) = 3
     * StringUtils.indexOfAnyBut("aba", new char[] {'z'} )             = 0
     * StringUtils.indexOfAnyBut("aba", new char[] {'a', 'b'} )        = -1
     *
     * </pre>
     *
     * @param cs          要检查的CharSequence，可以为空
     * @param searchChars 要搜索的字符可能为空
     *
     * @return 任何字符的索引，如果没有匹配或输入为空，则为-1
     */
    public static int indexOfAnyBut(final CharSequence cs, final char... searchChars) {
        if (isEmpty(cs) || ArraysUtils.isEmpty(searchChars)) {
            return INDEX_NOT_FOUND;
        }
        final int csLen = cs.length();
        final int csLast = csLen - 1;
        final int searchLen = searchChars.length;
        final int searchLast = searchLen - 1;
        outer:
        for (int i = 0; i < csLen; i++) {
            final char ch = cs.charAt(i);
            for (int j = 0; j < searchLen; j++) {
                if (searchChars[j] == ch) {
                    if (i < csLast && j < searchLast && Character.isHighSurrogate(ch)) {
                        if (searchChars[j + 1] == cs.charAt(i + 1)) {
                            continue outer;
                        }
                    } else {
                        continue outer;
                    }
                }
            }
            return i;
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * <p>搜索CharSequence以查找不在给定字符集中的任何字符的第一个索引。</p>
     *
     * <p>一个{@code null} CharSequence将返回{@code -1}。
     * 一个{@code null}或空的搜索字符串将返回{@code -1}。</p>
     *
     * <pre>
     * StringUtils.indexOfAnyBut(null, *)            = -1
     * StringUtils.indexOfAnyBut("", *)              = -1
     * StringUtils.indexOfAnyBut(*, null)            = -1
     * StringUtils.indexOfAnyBut(*, "")              = -1
     * StringUtils.indexOfAnyBut("zzabyycdxx", "za") = 3
     * StringUtils.indexOfAnyBut("zzabyycdxx", "")   = -1
     * StringUtils.indexOfAnyBut("aba", "ab")        = -1
     * </pre>
     *
     * @param seq         要检查的CharSequence，可以为空
     * @param searchChars 要搜索的字符可能为空
     *
     * @return 任何字符的索引，如果没有匹配或输入为空，则为-1
     */
    public static int indexOfAnyBut(final CharSequence seq, final CharSequence searchChars) {
        if (isEmpty(seq) || isEmpty(searchChars)) {
            return INDEX_NOT_FOUND;
        }
        final int strLen = seq.length();
        for (int i = 0; i < strLen; i++) {
            final char ch = seq.charAt(i);
            final boolean chFound = CharSequenceUtils.indexOf(searchChars, ch, 0) >= 0;
            if (i + 1 < strLen && Character.isHighSurrogate(ch)) {
                final char ch2 = seq.charAt(i + 1);
                if (chFound && CharSequenceUtils.indexOf(searchChars, ch2, 0) < 0) {
                    return i;
                }
            } else if (!chFound) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * <p>比较数组中的所有charsequence并返回charsequence开始不同的索引。</p>
     *
     * <p>For example,
     * {@code indexOfDifference(new String[] {"i am a machine", "i am a robot"}) -> 7}</p>
     *
     * <pre>
     * StringUtils.indexOfDifference(null) = -1
     * StringUtils.indexOfDifference(new String[] {}) = -1
     * StringUtils.indexOfDifference(new String[] {"abc"}) = -1
     * StringUtils.indexOfDifference(new String[] {null, null}) = -1
     * StringUtils.indexOfDifference(new String[] {"", ""}) = -1
     * StringUtils.indexOfDifference(new String[] {"", null}) = 0
     * StringUtils.indexOfDifference(new String[] {"abc", null, null}) = 0
     * StringUtils.indexOfDifference(new String[] {null, null, "abc"}) = 0
     * StringUtils.indexOfDifference(new String[] {"", "abc"}) = 0
     * StringUtils.indexOfDifference(new String[] {"abc", ""}) = 0
     * StringUtils.indexOfDifference(new String[] {"abc", "abc"}) = -1
     * StringUtils.indexOfDifference(new String[] {"abc", "a"}) = 1
     * StringUtils.indexOfDifference(new String[] {"ab", "abxyz"}) = 2
     * StringUtils.indexOfDifference(new String[] {"abcde", "abxyz"}) = 2
     * StringUtils.indexOfDifference(new String[] {"abcde", "xyz"}) = 0
     * StringUtils.indexOfDifference(new String[] {"xyz", "abcde"}) = 0
     * StringUtils.indexOfDifference(new String[] {"i am a machine", "i am a robot"}) = 7
     * </pre>
     *
     * @param css charsequence数组，条目可以为空
     *
     * @return 字符串开始不同的索引;-1如果它们都相等
     */
    public static int indexOfDifference(final CharSequence... css) {
        if (ArraysUtils.getLength(css) <= 1) {
            return INDEX_NOT_FOUND;
        }
        boolean anyStringNull = false;
        boolean allStringsNull = true;
        final int arrayLen = css.length;
        int shortestStrLen = Integer.MAX_VALUE;
        int longestStrLen = 0;

        // find the min and max string lengths; this avoids checking to make
        // sure we are not exceeding the length of the string each time through
        // the bottom loop.
        for (final CharSequence cs : css) {
            if (cs == null) {
                anyStringNull = true;
                shortestStrLen = 0;
            } else {
                allStringsNull = false;
                shortestStrLen = Math.min(cs.length(), shortestStrLen);
                longestStrLen = Math.max(cs.length(), longestStrLen);
            }
        }

        // handle lists containing all nulls or all empty strings
        if (allStringsNull || longestStrLen == 0 && !anyStringNull) {
            return INDEX_NOT_FOUND;
        }

        // handle lists containing some nulls or some empty strings
        if (shortestStrLen == 0) {
            return 0;
        }

        // find the position with the first difference across all strings
        int firstDiff = -1;
        for (int stringPos = 0; stringPos < shortestStrLen; stringPos++) {
            final char comparisonChar = css[0].charAt(stringPos);
            for (int arrayPos = 1; arrayPos < arrayLen; arrayPos++) {
                if (css[arrayPos].charAt(stringPos) != comparisonChar) {
                    firstDiff = stringPos;
                    break;
                }
            }
            if (firstDiff != -1) {
                break;
            }
        }

        if (firstDiff == -1 && shortestStrLen != longestStrLen) {
            // we compared all of the characters up to the length of the
            // shortest string and didn't find a match, but the string lengths
            // vary, so return the length of the shortest string.
            return shortestStrLen;
        }
        return firstDiff;
    }

    /**
     * <p>比较两个charsequence，并返回charsequence开始不同的索引。</p>
     *
     * <p>For example,
     * {@code indexOfDifference("i am a machine", "i am a robot") -> 7}</p>
     *
     * <pre>
     * StringUtils.indexOfDifference(null, null) = -1
     * StringUtils.indexOfDifference("", "") = -1
     * StringUtils.indexOfDifference("", "abc") = 0
     * StringUtils.indexOfDifference("abc", "") = 0
     * StringUtils.indexOfDifference("abc", "abc") = -1
     * StringUtils.indexOfDifference("ab", "abxyz") = 2
     * StringUtils.indexOfDifference("abcde", "abxyz") = 2
     * StringUtils.indexOfDifference("abcde", "xyz") = 0
     * </pre>
     *
     * @param cs1 第一个CharSequence，可以为空
     * @param cs2 第二个CharSequence，可以为空
     *
     * @return cs1和cs2开始不同的索引;如果相等-1
     */
    public static int indexOfDifference(final CharSequence cs1, final CharSequence cs2) {
        if (cs1 == cs2) {
            return INDEX_NOT_FOUND;
        }
        if (cs1 == null || cs2 == null) {
            return 0;
        }
        int i;
        for (i = 0; i < cs1.length() && i < cs2.length(); ++i) {
            if (cs1.charAt(i) != cs2.charAt(i)) {
                break;
            }
        }
        if (i < cs2.length() || i < cs1.length()) {
            return i;
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * <p>CharSequence中第一个索引的大小写敏感查找。</p>
     *
     * <p>一个{@code null} CharSequence将返回{@code -1}。
     * <p>负的起始位置被视为零。一个空的("")搜索CharSequence总是匹配的。
     * <p>大于字符串长度的起始位置只匹配一个空的搜索CharSequence。
     * </p>
     *
     * <pre>
     * StringUtils.indexOfIgnoreCase(null, *)          = -1
     * StringUtils.indexOfIgnoreCase(*, null)          = -1
     * StringUtils.indexOfIgnoreCase("", "")           = 0
     * StringUtils.indexOfIgnoreCase("aabaabaa", "a")  = 0
     * StringUtils.indexOfIgnoreCase("aabaabaa", "b")  = 2
     * StringUtils.indexOfIgnoreCase("aabaabaa", "ab") = 1
     * </pre>
     *
     * @param str       要检查的CharSequence，可以为空
     * @param searchStr 查找的CharSequence可能为空
     *
     * @return 搜索CharSequence的第一个索引，如果没有匹配或{@code null}字符串输入，则为-1
     */
    public static int indexOfIgnoreCase(final CharSequence str, final CharSequence searchStr) {
        return indexOfIgnoreCase(str, searchStr, 0);
    }

    /**
     * <p>从指定位置开始的CharSequence中的第一个索引的大小写敏感查找。</p>
     *
     * <p>一个{@code null} CharSequence将返回{@code -1}。
     * <p>负的起始位置被视为零。一个空的("")搜索CharSequence总是匹配的。大于字符串长度的起始位置只匹配一个空的搜索CharSequence。</p>
     *
     * <pre>
     * StringUtils.indexOfIgnoreCase(null, *, *)          = -1
     * StringUtils.indexOfIgnoreCase(*, null, *)          = -1
     * StringUtils.indexOfIgnoreCase("", "", 0)           = 0
     * StringUtils.indexOfIgnoreCase("aabaabaa", "A", 0)  = 0
     * StringUtils.indexOfIgnoreCase("aabaabaa", "B", 0)  = 2
     * StringUtils.indexOfIgnoreCase("aabaabaa", "AB", 0) = 1
     * StringUtils.indexOfIgnoreCase("aabaabaa", "B", 3)  = 5
     * StringUtils.indexOfIgnoreCase("aabaabaa", "B", 9)  = -1
     * StringUtils.indexOfIgnoreCase("aabaabaa", "B", -1) = 2
     * StringUtils.indexOfIgnoreCase("aabaabaa", "", 2)   = 2
     * StringUtils.indexOfIgnoreCase("abc", "", 9)        = -1
     * </pre>
     *
     * @param str       要检查的CharSequence，可以为空
     * @param searchStr 查找的CharSequence可能为空
     * @param startPos  起始位置，负的作为零
     *
     * @return CharSequence的第一个索引(总是 & ge ; startPos)， -1如果没有匹配或{@code null}字符串输入
     */
    public static int indexOfIgnoreCase(final CharSequence str, final CharSequence searchStr, int startPos) {
        if (str == null || searchStr == null) {
            return INDEX_NOT_FOUND;
        }
        if (startPos < 0) {
            startPos = 0;
        }
        final int endLimit = str.length() - searchStr.length() + 1;
        if (startPos > endLimit) {
            return INDEX_NOT_FOUND;
        }
        if (searchStr.length() == 0) {
            return startPos;
        }
        for (int i = startPos; i < endLimit; i++) {
            if (CharSequenceUtils.regionMatches(str, true, i, searchStr, 0, searchStr.length())) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * <p>查找CharSequence中的最后一个索引，处理{@code null}。如果可能的话，这个方法使用{@link String#lastIndexOf(String)}。</p>
     *
     * <p>一个{@code null} CharSequence将返回{@code -1}。</p>
     *
     * <pre>
     * StringUtils.lastIndexOf(null, *)          = -1
     * StringUtils.lastIndexOf(*, null)          = -1
     * StringUtils.lastIndexOf("", "")           = 0
     * StringUtils.lastIndexOf("aabaabaa", "a")  = 7
     * StringUtils.lastIndexOf("aabaabaa", "b")  = 5
     * StringUtils.lastIndexOf("aabaabaa", "ab") = 4
     * StringUtils.lastIndexOf("aabaabaa", "")   = 8
     * </pre>
     *
     * @param seq       要检查的CharSequence，可以为空
     * @param searchSeq 查找的CharSequence可能为空
     *
     * @return 搜索字符串的最后一个索引，如果没有匹配或{@code null}字符串输入，则为-1
     */
    public static int lastIndexOf(final CharSequence seq, final CharSequence searchSeq) {
        if (seq == null) {
            return INDEX_NOT_FOUND;
        }
        return CharSequenceUtils.lastIndexOf(seq, searchSeq, seq.length());
    }

    /**
     * <p>查找CharSequence中的最后一个索引，处理{@code null}。如果可能的话，这个方法使用{@link String#lastIndexOf(String, int)}。</p>
     *
     * <p>一个{@code null} CharSequence将返回{@code -1}。
     * <p>负的起始位置将返回{@code -1}。空的("")搜索CharSequence总是匹配的，除非开始位置是负的。
     * <p>大于字符串长度的起始位置将搜索整个字符串。搜索从startPos开始并反向进行;
     * <p>从起始位置开始的匹配将被忽略。
     * </p>
     *
     * <pre>
     * StringUtils.lastIndexOf(null, *, *)          = -1
     * StringUtils.lastIndexOf(*, null, *)          = -1
     * StringUtils.lastIndexOf("aabaabaa", "a", 8)  = 7
     * StringUtils.lastIndexOf("aabaabaa", "b", 8)  = 5
     * StringUtils.lastIndexOf("aabaabaa", "ab", 8) = 4
     * StringUtils.lastIndexOf("aabaabaa", "b", 9)  = 5
     * StringUtils.lastIndexOf("aabaabaa", "b", -1) = -1
     * StringUtils.lastIndexOf("aabaabaa", "a", 0)  = 0
     * StringUtils.lastIndexOf("aabaabaa", "b", 0)  = -1
     * StringUtils.lastIndexOf("aabaabaa", "b", 1)  = -1
     * StringUtils.lastIndexOf("aabaabaa", "b", 2)  = 2
     * StringUtils.lastIndexOf("aabaabaa", "ba", 2)  = 2
     * </pre>
     *
     * @param seq       要检查的CharSequence，可以为空
     * @param searchSeq 查找的CharSequence可能为空
     * @param startPos  起始位置，负的作为零
     *
     * @return CharSequence(总是 & le ; startPos)， -1如果没有匹配或{@code null}字符串输入
     */
    public static int lastIndexOf(final CharSequence seq, final CharSequence searchSeq, final int startPos) {
        return CharSequenceUtils.lastIndexOf(seq, searchSeq, startPos);
    }

    /**
     * index返回指定字符在{@code seq}内最后一次出现的索引。对于范围从0到0xFFFF(包括)的{@code searchChar}的值，索引 (in Unicode code units) returned is the largest value <i>k</i> such that:
     * <blockquote><pre>
     * this.charAt(<i>k</i>) == searchChar
     * </pre></blockquote>
     * 是真的。对于{@code searchChar}的其他值，它是最大的值 <i>k</i> such that:
     * <blockquote><pre>
     * this.codePointAt(<i>k</i>) == searchChar
     * </pre></blockquote>
     * <p>
     * return是真的。在这两种情况下，如果这个字符串中没有出现这样的字符，则返回{@code -1}。此外，将返回一个{@code null}或空(""){@code CharSequence} {@code -1}. The {@code seq} {@code CharSequence} object is searched
     * 从最后一个字符开始向后。
     *
     * <pre>
     * StringUtils.lastIndexOf(null, *)         = -1
     * StringUtils.lastIndexOf("", *)           = -1
     * StringUtils.lastIndexOf("aabaabaa", 'a') = 7
     * StringUtils.lastIndexOf("aabaabaa", 'b') = 5
     * </pre>
     *
     * @param seq        {@code CharSequence}检查，可以为空
     * @param searchChar 要找的角色
     *
     * @return 搜索字符的最后一个索引，如果没有匹配或{@code null}字符串输入，则为-1
     */
    public static int lastIndexOf(final CharSequence seq, final int searchChar) {
        if (isEmpty(seq)) {
            return INDEX_NOT_FOUND;
        }
        return CharSequenceUtils.lastIndexOf(seq, searchChar, seq.length());
    }

    /**
     * 返回指定字符在{@code seq}内最后一次出现的索引，从指定索引开始向后搜索。对于{@code searchChar}在0到0xFFFF(包括)范围内的值，返回的索引是最大的值 <i>k</i> such that:
     * <blockquote><pre>
     * (this.charAt(<i>k</i>) == searchChar) &amp;&amp; (<i>k</i> &lt;= startPos)
     * </pre></blockquote>
     * 是真的。对于{@code searchChar}的其他值，它是最大的值 <i>k</i> such that:
     * <blockquote><pre>
     * (this.codePointAt(<i>k</i>) == searchChar) &amp;&amp; (<i>k</i> &lt;= startPos)
     * </pre></blockquote>
     * 是真的。在这两种情况下，如果在{@code seq}中没有这样的字符出现在{@code startPos}位置或之前，那么将返回{@code -1}。此外，{@code null}或空(""){@code CharSequence}将返回{@code -1}。大于字符串长度的起始位置将搜索整个字符串。搜索从{@code
     * startPos}开始并反向工作;从起始位置开始的匹配将被忽略。
     *
     * <p>所有索引都在{@code char}值中指定
     * (Unicode code units).
     *
     * <pre>
     * StringUtils.lastIndexOf(null, *, *)          = -1
     * StringUtils.lastIndexOf("", *,  *)           = -1
     * StringUtils.lastIndexOf("aabaabaa", 'b', 8)  = 5
     * StringUtils.lastIndexOf("aabaabaa", 'b', 4)  = 2
     * StringUtils.lastIndexOf("aabaabaa", 'b', 0)  = -1
     * StringUtils.lastIndexOf("aabaabaa", 'b', 9)  = 5
     * StringUtils.lastIndexOf("aabaabaa", 'b', -1) = -1
     * StringUtils.lastIndexOf("aabaabaa", 'a', 0)  = 0
     * </pre>
     *
     * @param seq        要检查的CharSequence，可以为空
     * @param searchChar 要找的角色
     * @param startPos   一开始的位置
     *
     * @return 搜索字符的最后一个索引(总是 & le ; startPos)， -1如果没有匹配或{@code null}字符串输入
     */
    public static int lastIndexOf(final CharSequence seq, final int searchChar, final int startPos) {
        if (isEmpty(seq)) {
            return INDEX_NOT_FOUND;
        }
        return CharSequenceUtils.lastIndexOf(seq, searchChar, startPos);
    }

    /**
     * <p>查找一组潜在子字符串中任何子字符串的最新索引。</p>
     *
     * <p>一个{@code null} CharSequence将返回{@code -1}。
     * 一个{@code null}搜索数组将返回{@code -1}。{@code null}或零长度的搜索数组条目将被忽略，但如果{@code str}不为空，包含""的搜索数组将返回{@code str}的长度。如果可能的话，这个方法使用{@link String#indexOf(String)}</p>
     *
     * <pre>
     * StringUtils.lastIndexOfAny(null, *)                    = -1
     * StringUtils.lastIndexOfAny(*, null)                    = -1
     * StringUtils.lastIndexOfAny(*, [])                      = -1
     * StringUtils.lastIndexOfAny(*, [null])                  = -1
     * StringUtils.lastIndexOfAny("zzabyycdxx", ["ab", "cd"]) = 6
     * StringUtils.lastIndexOfAny("zzabyycdxx", ["cd", "ab"]) = 6
     * StringUtils.lastIndexOfAny("zzabyycdxx", ["mn", "op"]) = -1
     * StringUtils.lastIndexOfAny("zzabyycdxx", ["mn", "op"]) = -1
     * StringUtils.lastIndexOfAny("zzabyycdxx", ["mn", ""])   = 10
     * </pre>
     *
     * @param str        要检查的CharSequence，可以为空
     * @param searchStrs 要搜索的charsequence可以为空
     *
     * @return 任何charsequence的最后一个索引，如果不匹配，则为-1
     */
    public static int lastIndexOfAny(final CharSequence str, final CharSequence... searchStrs) {
        if (str == null || searchStrs == null) {
            return INDEX_NOT_FOUND;
        }
        int ret = INDEX_NOT_FOUND;
        int tmp = 0;
        for (final CharSequence search : searchStrs) {
            if (search == null) {
                continue;
            }
            tmp = CharSequenceUtils.lastIndexOf(str, search, str.length());
            if (tmp > ret) {
                ret = tmp;
            }
        }
        return ret;
    }

    /**
     * <p>CharSequence中最后一个索引的大小写敏感查找。</p>
     *
     * <p>一个{@code null} CharSequence将返回{@code -1}。负的起始位置将返回{@code -1}。空的("")搜索CharSequence总是匹配的，除非开始位置是负的。大于字符串长度的起始位置将搜索整个字符串。</p>
     *
     * <pre>
     * StringUtils.lastIndexOfIgnoreCase(null, *)          = -1
     * StringUtils.lastIndexOfIgnoreCase(*, null)          = -1
     * StringUtils.lastIndexOfIgnoreCase("aabaabaa", "A")  = 7
     * StringUtils.lastIndexOfIgnoreCase("aabaabaa", "B")  = 5
     * StringUtils.lastIndexOfIgnoreCase("aabaabaa", "AB") = 4
     * </pre>
     *
     * @param str       要检查的CharSequence，可以为空
     * @param searchStr 查找的CharSequence可能为空
     *
     * @return 搜索CharSequence的第一个索引，如果没有匹配或{@code null}字符串输入，则为-1
     */
    public static int lastIndexOfIgnoreCase(final CharSequence str, final CharSequence searchStr) {
        if (str == null || searchStr == null) {
            return INDEX_NOT_FOUND;
        }
        return lastIndexOfIgnoreCase(str, searchStr, str.length());
    }

    /**
     * <p>从指定位置开始的CharSequence中最后一个索引的大小写敏感查找。</p>
     *
     * <p>一个{@code null} CharSequence将返回{@code -1}。负的起始位置将返回{@code -1}。空的("")搜索CharSequence总是匹配的，除非开始位置是负的。大于字符串长度的起始位置将搜索整个字符串。搜索从startPos开始并反向进行;从起始位置开始的匹配将被忽略。</p>
     *
     * <pre>
     * StringUtils.lastIndexOfIgnoreCase(null, *, *)          = -1
     * StringUtils.lastIndexOfIgnoreCase(*, null, *)          = -1
     * StringUtils.lastIndexOfIgnoreCase("aabaabaa", "A", 8)  = 7
     * StringUtils.lastIndexOfIgnoreCase("aabaabaa", "B", 8)  = 5
     * StringUtils.lastIndexOfIgnoreCase("aabaabaa", "AB", 8) = 4
     * StringUtils.lastIndexOfIgnoreCase("aabaabaa", "B", 9)  = 5
     * StringUtils.lastIndexOfIgnoreCase("aabaabaa", "B", -1) = -1
     * StringUtils.lastIndexOfIgnoreCase("aabaabaa", "A", 0)  = 0
     * StringUtils.lastIndexOfIgnoreCase("aabaabaa", "B", 0)  = -1
     * </pre>
     *
     * @param str       要检查的CharSequence，可以为空
     * @param searchStr 查找的CharSequence可能为空
     * @param startPos  一开始的位置
     *
     * @return CharSequence(总是 & le ; startPos)， -1如果没有匹配或{@code null}输入
     */
    public static int lastIndexOfIgnoreCase(final CharSequence str, final CharSequence searchStr, int startPos) {
        if (str == null || searchStr == null) {
            return INDEX_NOT_FOUND;
        }
        final int searchStrLength = searchStr.length();
        final int strLength = str.length();
        if (startPos > strLength - searchStrLength) {
            startPos = strLength - searchStrLength;
        }
        if (startPos < 0) {
            return INDEX_NOT_FOUND;
        }
        if (searchStrLength == 0) {
            return startPos;
        }

        for (int i = startPos; i >= 0; i--) {
            if (CharSequenceUtils.regionMatches(str, true, i, searchStr, 0, searchStrLength)) {
                return i;
            }
        }
        return INDEX_NOT_FOUND;
    }

    /**
     * <p>查找字符串中的最后n个索引，处理{@code null}。这个方法使用{@link String#lastIndexOf(String)}。</p>
     *
     * <p>一个{@code null}字符串将返回{@code -1}。</p>
     *
     * <pre>
     * StringUtils.lastOrdinalIndexOf(null, *, *)          = -1
     * StringUtils.lastOrdinalIndexOf(*, null, *)          = -1
     * StringUtils.lastOrdinalIndexOf("", "", *)           = 0
     * StringUtils.lastOrdinalIndexOf("aabaabaa", "a", 1)  = 7
     * StringUtils.lastOrdinalIndexOf("aabaabaa", "a", 2)  = 6
     * StringUtils.lastOrdinalIndexOf("aabaabaa", "b", 1)  = 5
     * StringUtils.lastOrdinalIndexOf("aabaabaa", "b", 2)  = 2
     * StringUtils.lastOrdinalIndexOf("aabaabaa", "ab", 1) = 4
     * StringUtils.lastOrdinalIndexOf("aabaabaa", "ab", 2) = 1
     * StringUtils.lastOrdinalIndexOf("aabaabaa", "", 1)   = 8
     * StringUtils.lastOrdinalIndexOf("aabaabaa", "", 2)   = 8
     * </pre>
     *
     * <p>注意'tail(CharSequence str, int n)'可以被实现为: </p>
     *
     * <pre>
     *   str.substring(lastOrdinalIndexOf(str, "\n", n) + 1)
     * </pre>
     *
     * @param str       要检查的CharSequence，可以为空
     * @param searchStr 查找的CharSequence可能为空
     * @param ordinal   最后找到的第n个{@code searchStr}
     *
     * @return 搜索 CharSequence的最后n个索引，如果没有匹配或输入{@code null}字符串，则为{@code -1} ({@code INDEX_NOT_FOUND})
     */
    public static int lastOrdinalIndexOf(final CharSequence str, final CharSequence searchStr, final int ordinal) {
        return ordinalIndexOf(str, searchStr, ordinal, true);
    }

    /**
     * <p>查找CharSequence中的第n个索引，处理{@code null}。如果可能的话，这个方法使用{@link String#indexOf(String)}。</p>
     * <p><b>Note:</b> 代码从目标开始寻找匹配，每次成功匹配后开始索引加1(除非{@code searchStr}是一个空字符串，在这种情况下位置永远不会加1，{@code 0}立即返回)。这意味着匹配可能会重叠。</p>
     * <p>一个{@code null} CharSequence将返回{@code -1}。</p>
     *
     * <pre>
     * StringUtils.ordinalIndexOf(null, *, *)          = -1
     * StringUtils.ordinalIndexOf(*, null, *)          = -1
     * StringUtils.ordinalIndexOf("", "", *)           = 0
     * StringUtils.ordinalIndexOf("aabaabaa", "a", 1)  = 0
     * StringUtils.ordinalIndexOf("aabaabaa", "a", 2)  = 1
     * StringUtils.ordinalIndexOf("aabaabaa", "b", 1)  = 2
     * StringUtils.ordinalIndexOf("aabaabaa", "b", 2)  = 5
     * StringUtils.ordinalIndexOf("aabaabaa", "ab", 1) = 1
     * StringUtils.ordinalIndexOf("aabaabaa", "ab", 2) = 4
     * StringUtils.ordinalIndexOf("aabaabaa", "", 1)   = 0
     * StringUtils.ordinalIndexOf("aabaabaa", "", 2)   = 0
     * </pre>
     *
     * <p>匹配可能重叠:</p>
     * <pre>
     * StringUtils.ordinalIndexOf("ababab", "aba", 1)   = 0
     * StringUtils.ordinalIndexOf("ababab", "aba", 2)   = 2
     * StringUtils.ordinalIndexOf("ababab", "aba", 3)   = -1
     *
     * StringUtils.ordinalIndexOf("abababab", "abab", 1) = 0
     * StringUtils.ordinalIndexOf("abababab", "abab", 2) = 2
     * StringUtils.ordinalIndexOf("abababab", "abab", 3) = 4
     * StringUtils.ordinalIndexOf("abababab", "abab", 4) = -1
     * </pre>
     *
     * <p>注意'head(CharSequence str, int n)'可以被实现为: </p>
     *
     * <pre>
     *   str.substring(0, lastOrdinalIndexOf(str, "\n", n))
     * </pre>
     *
     * @param str       要检查的CharSequence，可以为空
     * @param searchStr 查找的CharSequence可能为空
     * @param ordinal   要查找的第n个{@code searchStr
     *
     * @return 搜索 CharSequence的第n个索引，如果没有匹配或输入{@code null}字符串，则为{@code -1} ({@code INDEX_NOT_FOUND})
     */
    public static int ordinalIndexOf(final CharSequence str, final CharSequence searchStr, final int ordinal) {
        return ordinalIndexOf(str, searchStr, ordinal, false);
    }

    /**
     * <p>查找字符串中的第n个索引，处理{@code null}。如果可能的话，这个方法使用{@link String#indexOf(String)}。<p> <p>注意，匹配可能会重叠<p>
     *
     * <p>一个{@code null} CharSequence将返回{@code -1}。</p>
     *
     * @param str       要检查的CharSequence，可以为空
     * @param searchStr 查找的CharSequence可能为空
     * @param ordinal   第n个{@code searchStr}查找重叠匹配是允许的。
     * @param lastIndex 如果lastOrdinalIndexOf()为真，如果ordinalIndexOf()为假
     *
     * @return 搜索CharSequence的第n个索引，如果没有匹配或输入{@code null}字符串，则为{@code -1} ({@code INDEX_NOT_FOUND})
     */
    // Shared code between ordinalIndexOf(String, String, int) and lastOrdinalIndexOf(String, String, int)
    private static int ordinalIndexOf(final CharSequence str, final CharSequence searchStr, final int ordinal, final boolean lastIndex) {
        if (str == null || searchStr == null || ordinal <= 0) {
            return INDEX_NOT_FOUND;
        }
        if (searchStr.length() == 0) {
            return lastIndex ? str.length() : 0;
        }
        int found = 0;
        // set the initial index beyond the end of the string
        // this is to allow for the initial index decrement/increment
        int index = lastIndex ? str.length() : INDEX_NOT_FOUND;
        do {
            if (lastIndex) {
                index = CharSequenceUtils.lastIndexOf(str, searchStr, index - 1); // step backwards thru string
            } else {
                index = CharSequenceUtils.indexOf(str, searchStr, index + 1); // step forwards through string
            }
            if (index < 0) {
                return index;
            }
            found++;
        } while (found < ordinal);
        return index;
    }
    // ------------------------------------------------------------------------ lower and upper

    /**
     * 原字符串首字母大写并在其首部添加指定字符串 例如：str=name, preString=get =》 return getName
     *
     * @param str       被处理的字符串
     * @param preString 添加的首部
     *
     * @return 处理后的字符串
     */
    public static String upperFirstAndAddPre(String str, String preString) {
        if (str == null || preString == null) {
            return null;
        }
        return preString + upperFirst(str);
    }

    /**
     * 大写首字母<br> 例如：str = name, return Name
     *
     * @param str 字符串
     *
     * @return 字符串
     */
    public static String upperFirst(String str) {
        if (null == str) {
            return null;
        }
        if (str.length() > 0) {
            char firstChar = str.charAt(0);
            if (Character.isLowerCase(firstChar)) {
                return Character.toUpperCase(firstChar) + substring(str, 1);
            }
        }
        return str;
    }

    /**
     * 小写首字母<br> 例如：str = Name, return name
     *
     * @param str 字符串
     *
     * @return 字符串
     */
    public static String lowerFirst(String str) {
        if (null == str) {
            return null;
        }
        if (str.length() > 0) {
            char firstChar = str.charAt(0);
            if (Character.isUpperCase(firstChar)) {
                return Character.toLowerCase(firstChar) + substring(str, 1);
            }
        }
        return str;
    }

    /**
     * 格式化文本, {} 表示占位符<br> 此方法只是简单将占位符 {} 按照顺序替换为参数<br> 如果想输出 {} 使用 \\转义 { 即可，如果想输出 {} 之前的 \ 使用双转义符 \\\\ 即可<br> 例：<br> 通常使用：format("this is {} for {}", "a", "b") -> this is a for
     * b<br> 转义{}： format("this is \\{} for {}", "a", "b") -> this is \{} for a<br> 转义\： format("this is \\\\{} for {}", "a", "b") -> this is \a for b<br>
     *
     * @param template 文本模板，被替换的部分用 {} 表示
     * @param params   参数值
     *
     * @return 格式化后的文本
     */
    public static String format(String template, Object... params) {
        if (ObjectUtils.isEmpty(params) || isEmpty(template)) {
            return template;
        }
        return StringFormatter.format(template, params);
    }


    /**
     * 检查给定的{@code CharSequence}是否包含实际的
     * <pre>
     * StringUtils.hasText(null) = false
     * StringUtils.hasText("") = false
     * StringUtils.hasText(" ") = false
     * StringUtils.hasText("12345") = true
     * StringUtils.hasText(" 12345 ") = true
     * </pre>
     *
     * @param str 受检查参数
     *
     * @return {@code true}如果{@code CharSequence}不是{@code null}，它的长度大于0，并且不只包含空格
     *
     * @see #hasText(String)
     * @see #isEmpty(CharSequence)
     * @see Character#isWhitespace
     */
    public static boolean hasText(String str) {
        return (str != null && str.length() > 0 && containsText(str));
    }


    /**
     * 字符串转换为byteBuffer
     *
     * @param str     字符串
     * @param charset 编码
     *
     * @return byteBuffer
     */
    public static ByteBuffer byteBuffer(String str, Charset charset) {
        return ByteBuffer.wrap(getBytes(str, charset));
    }

    // ------------------------------------------------------------------------ str

    /**
     * 解码字节码
     *
     * @param data    字符串
     * @param charset 字符集，如果此字段为空，则解码的结果取决于平台
     *
     * @return 解码后的字符串
     */
    public static String toString(byte[] data, Charset charset) {
        if (data == null) {
            return null;
        }

        if (null == charset) {
            return new String(data);
        }
        return new String(data, charset);
    }


    /**
     * 解码字节码
     *
     * @param data    字符串
     * @param charset 字符集，如果此字段为空，则解码的结果取决于平台
     *
     * @return 解码后的字符串
     */
    public static String toString(Byte[] data, Charset charset) {
        if (data == null) {
            return null;
        }

        byte[] bytes = new byte[data.length];
        Byte dataByte;
        for (int i = 0; i < data.length; i++) {
            dataByte = data[i];
            bytes[i] = (null == dataByte) ? -1 : dataByte;
        }

        return toString(bytes, charset);
    }

    /**
     * 将编码的byteBuffer数据转换为字符串
     *
     * @param data    数据
     * @param charset 字符集，如果为空使用当前系统字符集
     *
     * @return 字符串
     */
    public static String toString(ByteBuffer data, String charset) {
        if (data == null) {
            return null;
        }

        return toString(data, Charset.forName(charset));
    }

    /**
     * 将编码的byteBuffer数据转换为字符串
     *
     * @param data    数据
     * @param charset 字符集，如果为空使用当前系统字符集
     *
     * @return 字符串
     */
    public static String toString(ByteBuffer data, Charset charset) {
        if (null == charset) {
            charset = Charset.defaultCharset();
        }
        return charset.decode(data).toString();
    }

    /**
     * 将对象转为字符串<br>
     *
     * <pre>
     * 1、Byte数组和ByteBuffer会被转换为对应字符串的数组
     * 2、对象数组会调用Arrays.toString方法
     * </pre>
     *
     * @param obj 对象
     *
     * @return 字符串
     */
    public static String toString(Object obj) {
        return toString(obj, CharsetUtils.CHARSET_UTF_8);
    }


    /**
     * 将对象转为字符串
     * <pre>
     * 	 1、Byte数组和ByteBuffer会被转换为对应字符串的数组
     * 	 2、对象数组会调用Arrays.toString方法
     * </pre>
     *
     * @param obj     对象
     * @param charset 字符集
     *
     * @return 字符串
     */
    public static String toString(Object obj, Charset charset) {
        if (null == obj) {
            return null;
        }

        if (obj instanceof String) {
            return (String) obj;
        } else if (obj instanceof byte[]) {
            return toString((byte[]) obj, charset);
        } else if (obj instanceof Byte[]) {
            return toString((Byte[]) obj, charset);
        } else if (obj instanceof ByteBuffer) {
            return toString((ByteBuffer) obj, charset);
        } else if (ArraysUtils.isArray(obj)) {
            return ArraysUtils.toString(obj);
        }

        return obj.toString();
    }

    /**
     * 是否包含字符串
     *
     * @param str  验证字符串
     * @param strs 字符串组
     *
     * @return 包含返回true
     */
    public static boolean inStringIgnoreCase(String str, String... strs) {
        if (str != null && strs != null) {
            for (String s : strs) {
                if (str.equalsIgnoreCase(trim(s))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Calls {@link String#getBytes(Charset)}
     *
     * @param string  The string to encode (if null, return null).
     * @param charset The {@link Charset} to encode the {@code String}
     *
     * @return the encoded bytes
     */
    private static ByteBuffer getByteBuffer(final String string, final Charset charset) {
        if (string == null) {
            return null;
        }
        return ByteBuffer.wrap(string.getBytes(charset));
    }

    /**
     * 使用UTF-8字符集将给定字符串编码到字节缓冲区，并将结果存储到新的字节数组中。
     *
     * @param string 要编码的字符串，可以是{@code null}
     *
     * @return 已编码的字节，如果输入字符串为{@code null}，则为{@code null}
     *
     * @see #getBytesUnchecked(String, String)
     */
    public static ByteBuffer getByteBufferUtf8(final String string) {
        return getByteBuffer(string, StandardCharsets.UTF_8);
    }


    /**
     * 使用ISO-8859-1字符集将给定字符串编码为字节序列，并将结果存储到新的字节数组中。
     *
     * @param string 要编码的字符串，可以是{@code null}
     *
     * @return 已编码的字节，如果输入字符串为{@code null}，则为{@code null}
     *
     * @see #getBytesUnchecked(String, String)
     */
    public static byte[] getBytesIso8859_1(final String string) {
        return getBytes(string, StandardCharsets.ISO_8859_1);
    }


    /**
     * 使用命名字符集将给定字符串编码为字节序列，并将结果存储到新的字节数组中。
     *
     * @param string      要编码的字符串，可以是{@code null}
     * @param charsetName 所需的名称{@link java.nio.charset.Charset}
     *
     * @return 已编码的字节，如果输入字符串为{@code null}，则为{@code null}
     *
     * @see String#getBytes(String)
     */
    public static byte[] getBytesUnchecked(final String string, final String charsetName) {
        if (string == null) {
            return null;
        }
        try {
            return string.getBytes(charsetName);
        } catch (final UnsupportedEncodingException e) {
            throw StringUtils.newIllegalStateException(charsetName, e);
        }
    }

    /**
     * 使用US-ASCII字符集将给定字符串编码为字节序列，并将结果存储到新的字节数组中。
     *
     * @param string 编码的字符串，可以是{@code null}
     *
     * @return 已编码的字节，如果输入字符串为{@code null}，则为{@code null}
     *
     * @see #getBytesUnchecked(String, String)
     */
    public static byte[] getBytesUsAscii(final String string) {
        return getBytes(string, StandardCharsets.US_ASCII);
    }

    /**
     * 使用UTF-16字符集将给定字符串编码为字节序列，并将结果存储到新的字节数组中。
     *
     * @param string the String to encode, may be {@code null}
     *
     * @return encoded bytes, or {@code null} if the input string was {@code null}
     *
     * @throws NullPointerException Thrown if {@link StandardCharsets#UTF_16} is not initialized, which should never happen since it is required by the Java platform
     *                              specification.
     * @see <a href="http://download.oracle.com/javase/7/docs/api/java/nio/charset/Charset.html">Standard charsets</a>
     * @see #getBytesUnchecked(String, String)
     */
    public static byte[] getBytesUtf16(final String string) {
        return getBytes(string, StandardCharsets.UTF_16);
    }

    /**
     * 使用UTF-16BE字符集将给定字符串编码为字节序列，并将结果存储到新的字节数组中
     *
     * @param string the String to encode, may be {@code null}
     *
     * @return encoded bytes, or {@code null} if the input string was {@code null}
     *
     * @throws NullPointerException Thrown if {@link StandardCharsets#UTF_16BE} is not initialized, which should never happen since it is required by the Java platform
     *                              specification.
     * @see <a href="http://download.oracle.com/javase/7/docs/api/java/nio/charset/Charset.html">Standard charsets</a>
     * @see #getBytesUnchecked(String, String)
     */
    public static byte[] getBytesUtf16Be(final String string) {
        return getBytes(string, StandardCharsets.UTF_16BE);
    }

    /**
     * 使用UTF-16LE字符集将给定字符串编码为字节序列，并将结果存储到新的字节数组中。
     *
     * @param string the String to encode, may be {@code null}
     *
     * @return encoded bytes, or {@code null} if the input string was {@code null}
     *
     * @throws NullPointerException Thrown if {@link StandardCharsets#UTF_16LE} is not initialized, which should never happen since it is required by the Java platform
     *                              specification.
     * @see <a href="http://download.oracle.com/javase/7/docs/api/java/nio/charset/Charset.html">Standard charsets</a>
     * @see #getBytesUnchecked(String, String)
     */
    public static byte[] getBytesUtf16Le(final String string) {
        return getBytes(string, StandardCharsets.UTF_16LE);
    }

    /**
     * 使用UTF-8字符集将给定字符串编码为字节序列，并将结果存储到新的字节数组中。
     *
     * @param string the String to encode, may be {@code null}
     *
     * @return encoded bytes, or {@code null} if the input string was {@code null}
     *
     * @throws NullPointerException Thrown if {@link StandardCharsets#UTF_8} is not initialized, which should never happen since it is required by the Java platform specification.
     * @see <a href="http://download.oracle.com/javase/7/docs/api/java/nio/charset/Charset.html">Standard charsets</a>
     * @see #getBytesUnchecked(String, String)
     */
    public static byte[] getBytesUtf8(final String string) {
        return getBytes(string, StandardCharsets.UTF_8);
    }


    /**
     * 通过使用给定字符集解码指定的字节数组来构造一个新的{@code String}
     *
     * @param bytes   The bytes to be decoded into characters
     * @param charset The {@link Charset} to encode the {@code String}; not {@code null}
     *
     * @return A new {@code String} decoded from the specified array of bytes using the given charset, or {@code null} if the input byte array was {@code null}.
     *
     * @throws NullPointerException Thrown if charset is {@code null}
     */
    private static String newString(final byte[] bytes, final Charset charset) {
        return bytes == null ? null : new String(bytes, charset);
    }

    /**
     * 通过使用给定字符集解码指定的字节数组来构造一个新的{@code String}。
     * <p>
     * This method catches {@link UnsupportedEncodingException} and re-throws it as {@link IllegalStateException}, which should never happen for a required charset name. Use this
     * method when the encoding is required to be in the JRE.
     * </p>
     *
     * @param bytes       The bytes to be decoded into characters, may be {@code null}
     * @param charsetName The name of a required {@link java.nio.charset.Charset}
     *
     * @return A new {@code String} decoded from the specified array of bytes using the given charset, or {@code null} if the input byte array was {@code null}.
     *
     * @throws IllegalStateException Thrown when a {@link UnsupportedEncodingException} is caught, which should never happen for a required charset name.
     * @see String#String(byte[], String)
     */
    public static String newString(final byte[] bytes, final String charsetName) {
        if (bytes == null) {
            return null;
        }
        try {
            return new String(bytes, charsetName);
        } catch (final UnsupportedEncodingException e) {
            throw StringUtils.newIllegalStateException(charsetName, e);
        }
    }

    /**
     * 通过使用ISO-8859-1字符集解码指定的字节数组来构造一个新的{@code String}。
     *
     * @param bytes The bytes to be decoded into characters, may be {@code null}
     *
     * @return A new {@code String} decoded from the specified array of bytes using the ISO-8859-1 charset, or {@code null} if the input byte array was {@code null}.
     *
     * @throws NullPointerException Thrown if {@link StandardCharsets#ISO_8859_1} is not initialized, which should never happen since it is required by the Java platform
     *                              specification.
     */
    public static String newStringIso8859_1(final byte[] bytes) {
        return newString(bytes, StandardCharsets.ISO_8859_1);
    }

    /**
     * 通过使用US-ASCII字符集解码指定的字节数组来构造一个新的{@code String}。
     *
     * @param bytes The bytes to be decoded into characters
     *
     * @return A new {@code String} decoded from the specified array of bytes using the US-ASCII charset, or {@code null} if the input byte array was {@code null}.
     *
     * @throws NullPointerException Thrown if {@link StandardCharsets#US_ASCII} is not initialized, which should never happen since it is required by the Java platform
     *                              specification.
     */
    public static String newStringUsAscii(final byte[] bytes) {
        return newString(bytes, StandardCharsets.US_ASCII);
    }

    /**
     * 通过使用UTF-16字符集解码指定的字节数组来构造一个新的{@code String}。
     *
     * @param bytes The bytes to be decoded into characters
     *
     * @return A new {@code String} decoded from the specified array of bytes using the UTF-16 charset or {@code null} if the input byte array was {@code null}.
     *
     * @throws NullPointerException Thrown if {@link StandardCharsets#UTF_16} is not initialized, which should never happen since it is required by the Java platform
     *                              specification.
     */
    public static String newStringUtf16(final byte[] bytes) {
        return newString(bytes, StandardCharsets.UTF_16);
    }

    /**
     * 通过使用UTF-16BE字符集解码指定的字节数组来构造一个新的{@code String}。
     *
     * @param bytes The bytes to be decoded into characters
     *
     * @return A new {@code String} decoded from the specified array of bytes using the UTF-16BE charset, or {@code null} if the input byte array was {@code null}.
     *
     * @throws NullPointerException Thrown if {@link StandardCharsets#UTF_16BE} is not initialized, which should never happen since it is required by the Java platform
     *                              specification.
     */
    public static String newStringUtf16Be(final byte[] bytes) {
        return newString(bytes, StandardCharsets.UTF_16BE);
    }

    /**
     * 通过使用UTF-16LE字符集解码指定的字节数组来构造一个新的{@code String}。
     *
     * @param bytes The bytes to be decoded into characters
     *
     * @return A new {@code String} decoded from the specified array of bytes using the UTF-16LE charset, or {@code null} if the input byte array was {@code null}.
     *
     * @throws NullPointerException Thrown if {@link StandardCharsets#UTF_16LE} is not initialized, which should never happen since it is required by the Java platform
     *                              specification.
     */
    public static String newStringUtf16Le(final byte[] bytes) {
        return newString(bytes, StandardCharsets.UTF_16LE);
    }

    /**
     * 通过使用UTF-8字符集解码指定的字节数组来构造一个新的{@code String}。
     *
     * @param bytes The bytes to be decoded into characters
     *
     * @return A new {@code String} decoded from the specified array of bytes using the UTF-8 charset, or {@code null} if the input byte array was {@code null}.
     *
     * @throws NullPointerException Thrown if {@link StandardCharsets#UTF_8} is not initialized, which should never happen since it is required by the Java platform specification.
     */
    public static String newStringUtf8(final byte[] bytes) {
        return newString(bytes, StandardCharsets.UTF_8);
    }

    /**
     * 清理空白字符
     *
     * @param str 被清理的字符串
     *
     * @return 清理后的字符串
     */
    public static String cleanBlank(String str) {
        return filter(str, c -> !CharUtils.isBlankChar(c));
    }
    // ------------------------------------------------------------------------ filter

    /**
     * 过滤字符串
     *
     * @param str    字符串
     * @param filter 过滤器，{@link Filter#accept(Object)}返回为{@code true}的保留字符
     *
     * @return 过滤后的字符串
     */
    public static String filter(CharSequence str, final Filter<Character> filter) {
        if (str == null || filter == null) {
            return null;
        }

        int len = str.length();
        final StringBuilder sb = new StringBuilder(len);
        char c;
        for (int i = 0; i < len; i++) {
            c = str.charAt(i);
            if (filter.accept(c)) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    // ------------------------------------------------------------------------ private
    private static IllegalStateException newIllegalStateException(final String charsetName,
            final UnsupportedEncodingException e) {
        return new IllegalStateException(charsetName + ": " + e);
    }

    private static boolean containsText(CharSequence str) {
        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

}
