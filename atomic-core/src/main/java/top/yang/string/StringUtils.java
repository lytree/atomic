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
package top.yang.string;


import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.regex.Pattern;
import org.apache.commons.lang3.ObjectUtils;
import top.yang.codec.CharEncoding;
import top.yang.collections.ArrayUtils;
import top.yang.text.StringFormatter;


/**
 * <p>Operations on {@link String} that are
 * {@code null} safe.</p>
 *
 * <ul>
 *  <li><b>IsEmpty/IsBlank</b>
 *      - checks if a String contains text</li>
 *  <li><b>Trim/Strip</b>
 *      - removes leading and trailing whitespace</li>
 *  <li><b>Equals/Compare</b>
 *      - compares two strings in a null-safe manner</li>
 *  <li><b>startsWith</b>
 *      - check if a String starts with a prefix in a null-safe manner</li>
 *  <li><b>endsWith</b>
 *      - check if a String ends with a suffix in a null-safe manner</li>
 *  <li><b>IndexOf/LastIndexOf/Contains</b>
 *      - null-safe index-of checks
 *  <li><b>IndexOfAny/LastIndexOfAny/IndexOfAnyBut/LastIndexOfAnyBut</b>
 *      - index-of any of a set of Strings</li>
 *  <li><b>ContainsOnly/ContainsNone/ContainsAny</b>
 *      - checks if String contains only/none/any of these characters</li>
 *  <li><b>Substring/Left/Right/Mid</b>
 *      - null-safe substring extractions</li>
 *  <li><b>SubstringBefore/SubstringAfter/SubstringBetween</b>
 *      - substring extraction relative to other strings</li>
 *  <li><b>Split/Join</b>
 *      - splits a String into an array of substrings and vice versa</li>
 *  <li><b>Remove/Delete</b>
 *      - removes part of a String</li>
 *  <li><b>Replace/Overlay</b>
 *      - Searches a String and replaces one String with another</li>
 *  <li><b>Chomp/Chop</b>
 *      - removes the last part of a String</li>
 *  <li><b>AppendIfMissing</b>
 *      - appends a suffix to the end of the String if not present</li>
 *  <li><b>PrependIfMissing</b>
 *      - prepends a prefix to the start of the String if not present</li>
 *  <li><b>LeftPad/RightPad/Center/Repeat</b>
 *      - pads a String</li>
 *  <li><b>UpperCase/LowerCase/SwapCase/Capitalize/Uncapitalize</b>
 *      - changes the case of a String</li>
 *  <li><b>CountMatches</b>
 *      - counts the number of occurrences of one String in another</li>
 *  <li><b>IsAlpha/IsNumeric/IsWhitespace/IsAsciiPrintable</b>
 *      - checks the characters in a String</li>
 *  <li><b>DefaultString</b>
 *      - protects against a null input String</li>
 *  <li><b>Rotate</b>
 *      - rotate (circular shift) a String</li>
 *  <li><b>Reverse/ReverseDelimited</b>
 *      - reverses a String</li>
 *  <li><b>Abbreviate</b>
 *      - abbreviates a string using ellipses or another given String</li>
 *  <li><b>Difference</b>
 *      - compares Strings and reports on their differences</li>
 *  <li><b>LevenshteinDistance</b>
 *      - the number of changes needed to change one String into another</li>
 * </ul>
 *
 * <p>The {@code StringUtils} class defines certain words related to
 * String handling.</p>
 *
 * <ul>
 *  <li>null - {@code null}</li>
 *  <li>empty - a zero-length string ({@code ""})</li>
 *  <li>space - the space character ({@code ' '}, char 32)</li>
 *  <li>whitespace - the characters defined by {@link Character#isWhitespace(char)}</li>
 *  <li>trim - the characters &lt;= 32 as in {@link String#trim()}</li>
 * </ul>
 *
 * <p>{@code StringUtils} handles {@code null} input Strings quietly.
 * That is to say that a {@code null} input will return {@code null}.
 * Where a {@code boolean} or {@code int} is being returned
 * details vary by method.</p>
 *
 * <p>A side effect of the {@code null} handling is that a
 * {@code NullPointerException} should be considered a bug in
 * {@code StringUtils}.</p>
 *
 * <p>Methods in this class include sample code in their Javadoc comments to explain their operation.
 * The symbol {@code *} is used to indicate any input including {@code null}.</p>
 *
 * <p>#ThreadSafe#</p>
 *
 * @see String
 * @since 1.0
 */
//@Immutable
public class StringUtils extends org.apache.commons.lang3.StringUtils {

    private static final int STRING_BUILDER_SIZE = 256;
    /**
     * ISO-8859-1
     */
    public static final String ISO_8859_1 = CharsetsUtils.ISO_8859_1;
    /**
     * UTF-8
     */
    public static final String UTF_8 = CharsetsUtils.UTF_8;


    public static final String DEFAULT_ENCODING = UTF_8;
    /**
     * GBK
     */
    public static final String GBK = CharsetsUtils.GBK;
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

    // Performance testing notes (JDK 1.4, Jul03, scolebourne)
    // Whitespace:
    // Character.isWhitespace() is faster than WHITESPACE.indexOf()
    // where WHITESPACE is a string of all whitespace characters
    //
    // Character access:
    // String.charAt(n) versus toCharArray(), then array[n]
    // String.charAt(n) is about 15% worse for a 10K string
    // They are about equal for a length 50 string
    // String.charAt(n) is about 4 times better for a length 3 string
    // String.charAt(n) is best bet overall
    //
    // Append:
    // String.concat about twice as fast as StringBuffer.append
    // (not sure who tested this)

    /**
     * Represents a failed index search.
     *
     * @since 2.1
     */
    public static final int INDEX_NOT_FOUND = -1;

    /**
     * <p>The maximum size to which the padding constant(s) can expand.</p>
     */
    private static final int PAD_LIMIT = 8192;

    /**
     * Pattern used in {@link #stripAccents(String)}.
     */
    private static final Pattern STRIP_ACCENTS_PATTERN = Pattern.compile("\\p{InCombiningDiacriticalMarks}+"); //$NON-NLS-1$


    /**
     * 将下划线大写方式命名的字符串转换为驼峰式。如果转换前的下划线大写方式命名的字符串为空，则返回空字符串。 例如：HELLO_WORLD->HelloWorld
     *
     * @param name 转换前的下划线大写方式命名的字符串
     * @return 转换后的驼峰式命名的字符串
     */
    public static String convertToCamelCase(String name) {
        StringBuilder result = new StringBuilder();
        // 快速检查
        if (name == null || name.isEmpty()) {
            // 没必要转换
            return "";
        } else if (!name.contains("_")) {
            // 不含下划线，仅将首字母大写
            return name.substring(0, 1).toUpperCase() + name.substring(1);
        }
        // 用下划线将原始字符串分割
        String[] camels = name.split("_");
        for (String camel : camels) {
            // 跳过原始字符串中开头、结尾的下换线或双重下划线
            if (camel.isEmpty()) {
                continue;
            }
            // 首字母大写
            result.append(camel.substring(0, 1).toUpperCase());
            result.append(camel.substring(1).toLowerCase());
        }
        return result.toString();
    }

    /**
     * @param name 小驼峰命名法
     * @return 下划线格式
     */
    public static String convertToUnderline(String name) {
        StringBuilder builder = new StringBuilder(name.replace('.', '_'));
        for (int i = 1; i < builder.length() - 1; i++) {
            if (isUnderscoreRequired(builder.charAt(i - 1), builder.charAt(i),
                    builder.charAt(i + 1))) {
                builder.insert(i++, '_');
            }
        }
        return builder.toString().toUpperCase();
    }

    private static boolean isUnderscoreRequired(char before, char current, char after) {
        return Character.isLowerCase(before) && Character.isUpperCase(current)
                && Character.isLowerCase(after);
    }

    /**
     * 驼峰式命名法 例如：user_name->userName
     */
    public static String toCamelCase(String s) {
        if (s == null) {
            return null;
        }
        s = s.toLowerCase();
        StringBuilder sb = new StringBuilder(s.length());
        boolean upperCase = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (Objects.equals(toString(c), UNDERLINE)) {
                upperCase = true;
            } else if (upperCase) {
                sb.append(Character.toUpperCase(c));
                upperCase = false;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * 格式化文本, {} 表示占位符<br> 此方法只是简单将占位符 {} 按照顺序替换为参数<br> 如果想输出 {} 使用 \\转义 { 即可，如果想输出 {} 之前的 \ 使用双转义符 \\\\ 即可<br> 例：<br> 通常使用：format("this is {} for {}", "a", "b") -> this is a for
     * b<br> 转义{}： format("this is \\{} for {}", "a", "b") -> this is \{} for a<br> 转义\： format("this is \\\\{} for {}", "a", "b") -> this is \a for b<br>
     *
     * @param template 文本模板，被替换的部分用 {} 表示
     * @param params   参数值
     * @return 格式化后的文本
     */
    public static String format(String template, Object... params) {
        if (ObjectUtils.isEmpty(params) || isEmpty(template)) {
            return template;
        }
        return StringFormatter.format(template, params);
    }

    /**
     * Check that the given {@code CharSequence} is neither {@code null} nor of length 0.
     * <p>Note: this method returns {@code true} for a {@code CharSequence}
     * that purely consists of whitespace.
     * <p><pre class="code">
     * StringUtils.hasLength(null) = false
     * StringUtils.hasLength("") = false
     * StringUtils.hasLength(" ") = true
     * StringUtils.hasLength("Hello") = true
     * </pre>
     *
     * @param str the {@code CharSequence} to check (may be {@code null})
     * @return {@code true} if the {@code CharSequence} is not {@code null} and has length
     * @see #hasLength(String)
     * @see #hasText(CharSequence)
     */
    public static boolean hasLength(CharSequence str) {
        return (str != null && str.length() > 0);
    }

    /**
     * Check that the given {@code String} is neither {@code null} nor of length 0.
     * <p>Note: this method returns {@code true} for a {@code String} that
     * purely consists of whitespace.
     *
     * @param str the {@code String} to check (may be {@code null})
     * @return {@code true} if the {@code String} is not {@code null} and has length
     * @see #hasLength(CharSequence)
     * @see #hasText(String)
     */
    public static boolean hasLength(String str) {
        return (str != null && !str.isEmpty());
    }

    /**
     * Check whether the given {@code CharSequence} contains actual <em>text</em>.
     * <p>More specifically, this method returns {@code true} if the
     * {@code CharSequence} is not {@code null}, its length is greater than 0, and it contains at least one non-whitespace character.
     * <p><pre class="code">
     * StringUtils.hasText(null) = false
     * StringUtils.hasText("") = false
     * StringUtils.hasText(" ") = false
     * StringUtils.hasText("12345") = true
     * StringUtils.hasText(" 12345 ") = true
     * </pre>
     *
     * @param str the {@code CharSequence} to check (may be {@code null})
     * @return {@code true} if the {@code CharSequence} is not {@code null}, its length is greater than 0, and it does not contain whitespace only
     * @see #hasText(String)
     * @see #hasLength(CharSequence)
     * @see Character#isWhitespace
     */
    public static boolean hasText(CharSequence str) {
        return (str != null && str.length() > 0 && containsText(str));
    }

    /**
     * Check whether the given {@code String} contains actual <em>text</em>.
     * <p>More specifically, this method returns {@code true} if the
     * {@code String} is not {@code null}, its length is greater than 0, and it contains at least one non-whitespace character.
     *
     * @param str the {@code String} to check (may be {@code null})
     * @return {@code true} if the {@code String} is not {@code null}, its length is greater than 0, and it does not contain whitespace only
     * @see #hasText(CharSequence)
     * @see #hasLength(String)
     * @see Character#isWhitespace
     */
    public static boolean hasText(String str) {
        return (str != null && !str.isEmpty() && containsText(str));
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

    /**
     * 字符串转换为byteBuffer
     *
     * @param str     字符串
     * @param charset 编码
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
     * @return 字符串
     */
    public static String toString(Object obj) {
        return toString(obj, CharsetsUtils.CHARSET_UTF_8);
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
        } else if (ArrayUtils.isArray(obj)) {
            return ArrayUtils.toString(obj);
        }

        return obj.toString();
    }

    /**
     * 是否包含字符串
     *
     * @param str  验证字符串
     * @param strs 字符串组
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
     * @return the encoded bytes
     */
    private static ByteBuffer getByteBuffer(final String string, final Charset charset) {
        if (string == null) {
            return null;
        }
        return ByteBuffer.wrap(string.getBytes(charset));
    }

    /**
     * Encodes the given string into a byte buffer using the UTF-8 charset, storing the result into a new byte array.
     *
     * @param string the String to encode, may be {@code null}
     * @return encoded bytes, or {@code null} if the input string was {@code null}
     * @throws NullPointerException Thrown if {@link StandardCharsets#UTF_8} is not initialized, which should never happen since it is required by the Java platform specification.
     * @see <a href="http://download.oracle.com/javase/7/docs/api/java/nio/charset/Charset.html">Standard charsets</a>
     * @see #getBytesUnchecked(String, String)
     * @since 1.11
     */
    public static ByteBuffer getByteBufferUtf8(final String string) {
        return getByteBuffer(string, StandardCharsets.UTF_8);
    }


    /**
     * Encodes the given string into a sequence of bytes using the ISO-8859-1 charset, storing the result into a new byte array.
     *
     * @param string the String to encode, may be {@code null}
     * @return encoded bytes, or {@code null} if the input string was {@code null}
     * @throws NullPointerException Thrown if {@link StandardCharsets#ISO_8859_1} is not initialized, which should never happen since it is required by the Java platform
     *                              specification.
     * @see <a href="http://download.oracle.com/javase/7/docs/api/java/nio/charset/Charset.html">Standard charsets</a>
     * @see #getBytesUnchecked(String, String)
     * @since As of 1.7, throws {@link NullPointerException} instead of UnsupportedEncodingException
     */
    public static byte[] getBytesIso8859_1(final String string) {
        return getBytes(string, StandardCharsets.ISO_8859_1);
    }


    /**
     * Encodes the given string into a sequence of bytes using the named charset, storing the result into a new byte array.
     * <p>
     * This method catches {@link UnsupportedEncodingException} and rethrows it as {@link IllegalStateException}, which should never happen for a required charset name. Use this
     * method when the encoding is required to be in the JRE.
     * </p>
     *
     * @param string      the String to encode, may be {@code null}
     * @param charsetName The name of a required {@link java.nio.charset.Charset}
     * @return encoded bytes, or {@code null} if the input string was {@code null}
     * @throws IllegalStateException Thrown when a {@link UnsupportedEncodingException} is caught, which should never happen for a required charset name.
     * @see CharEncoding
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
     * Encodes the given string into a sequence of bytes using the US-ASCII charset, storing the result into a new byte array.
     *
     * @param string the String to encode, may be {@code null}
     * @return encoded bytes, or {@code null} if the input string was {@code null}
     * @throws NullPointerException Thrown if {@link StandardCharsets#US_ASCII} is not initialized, which should never happen since it is required by the Java platform
     *                              specification.
     * @see <a href="http://download.oracle.com/javase/7/docs/api/java/nio/charset/Charset.html">Standard charsets</a>
     * @see #getBytesUnchecked(String, String)
     * @since As of 1.7, throws {@link NullPointerException} instead of UnsupportedEncodingException
     */
    public static byte[] getBytesUsAscii(final String string) {
        return getBytes(string, StandardCharsets.US_ASCII);
    }

    /**
     * Encodes the given string into a sequence of bytes using the UTF-16 charset, storing the result into a new byte array.
     *
     * @param string the String to encode, may be {@code null}
     * @return encoded bytes, or {@code null} if the input string was {@code null}
     * @throws NullPointerException Thrown if {@link StandardCharsets#UTF_16} is not initialized, which should never happen since it is required by the Java platform
     *                              specification.
     * @see <a href="http://download.oracle.com/javase/7/docs/api/java/nio/charset/Charset.html">Standard charsets</a>
     * @see #getBytesUnchecked(String, String)
     * @since As of 1.7, throws {@link NullPointerException} instead of UnsupportedEncodingException
     */
    public static byte[] getBytesUtf16(final String string) {
        return getBytes(string, StandardCharsets.UTF_16);
    }

    /**
     * Encodes the given string into a sequence of bytes using the UTF-16BE charset, storing the result into a new byte array.
     *
     * @param string the String to encode, may be {@code null}
     * @return encoded bytes, or {@code null} if the input string was {@code null}
     * @throws NullPointerException Thrown if {@link StandardCharsets#UTF_16BE} is not initialized, which should never happen since it is required by the Java platform
     *                              specification.
     * @see <a href="http://download.oracle.com/javase/7/docs/api/java/nio/charset/Charset.html">Standard charsets</a>
     * @see #getBytesUnchecked(String, String)
     * @since As of 1.7, throws {@link NullPointerException} instead of UnsupportedEncodingException
     */
    public static byte[] getBytesUtf16Be(final String string) {
        return getBytes(string, StandardCharsets.UTF_16BE);
    }

    /**
     * Encodes the given string into a sequence of bytes using the UTF-16LE charset, storing the result into a new byte array.
     *
     * @param string the String to encode, may be {@code null}
     * @return encoded bytes, or {@code null} if the input string was {@code null}
     * @throws NullPointerException Thrown if {@link StandardCharsets#UTF_16LE} is not initialized, which should never happen since it is required by the Java platform
     *                              specification.
     * @see <a href="http://download.oracle.com/javase/7/docs/api/java/nio/charset/Charset.html">Standard charsets</a>
     * @see #getBytesUnchecked(String, String)
     * @since As of 1.7, throws {@link NullPointerException} instead of UnsupportedEncodingException
     */
    public static byte[] getBytesUtf16Le(final String string) {
        return getBytes(string, StandardCharsets.UTF_16LE);
    }

    /**
     * Encodes the given string into a sequence of bytes using the UTF-8 charset, storing the result into a new byte array.
     *
     * @param string the String to encode, may be {@code null}
     * @return encoded bytes, or {@code null} if the input string was {@code null}
     * @throws NullPointerException Thrown if {@link StandardCharsets#UTF_8} is not initialized, which should never happen since it is required by the Java platform specification.
     * @see <a href="http://download.oracle.com/javase/7/docs/api/java/nio/charset/Charset.html">Standard charsets</a>
     * @see #getBytesUnchecked(String, String)
     * @since As of 1.7, throws {@link NullPointerException} instead of UnsupportedEncodingException
     */
    public static byte[] getBytesUtf8(final String string) {
        return getBytes(string, StandardCharsets.UTF_8);
    }

    private static IllegalStateException newIllegalStateException(final String charsetName,
            final UnsupportedEncodingException e) {
        return new IllegalStateException(charsetName + ": " + e);
    }

    /**
     * Constructs a new {@code String} by decoding the specified array of bytes using the given charset.
     *
     * @param bytes   The bytes to be decoded into characters
     * @param charset The {@link Charset} to encode the {@code String}; not {@code null}
     * @return A new {@code String} decoded from the specified array of bytes using the given charset, or {@code null} if the input byte array was {@code null}.
     * @throws NullPointerException Thrown if charset is {@code null}
     */
    private static String newString(final byte[] bytes, final Charset charset) {
        return bytes == null ? null : new String(bytes, charset);
    }

    /**
     * Constructs a new {@code String} by decoding the specified array of bytes using the given charset.
     * <p>
     * This method catches {@link UnsupportedEncodingException} and re-throws it as {@link IllegalStateException}, which should never happen for a required charset name. Use this
     * method when the encoding is required to be in the JRE.
     * </p>
     *
     * @param bytes       The bytes to be decoded into characters, may be {@code null}
     * @param charsetName The name of a required {@link java.nio.charset.Charset}
     * @return A new {@code String} decoded from the specified array of bytes using the given charset, or {@code null} if the input byte array was {@code null}.
     * @throws IllegalStateException Thrown when a {@link UnsupportedEncodingException} is caught, which should never happen for a required charset name.
     * @see CharEncoding
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
     * Constructs a new {@code String} by decoding the specified array of bytes using the ISO-8859-1 charset.
     *
     * @param bytes The bytes to be decoded into characters, may be {@code null}
     * @return A new {@code String} decoded from the specified array of bytes using the ISO-8859-1 charset, or {@code null} if the input byte array was {@code null}.
     * @throws NullPointerException Thrown if {@link StandardCharsets#ISO_8859_1} is not initialized, which should never happen since it is required by the Java platform
     *                              specification.
     * @since As of 1.7, throws {@link NullPointerException} instead of UnsupportedEncodingException
     */
    public static String newStringIso8859_1(final byte[] bytes) {
        return newString(bytes, StandardCharsets.ISO_8859_1);
    }

    /**
     * Constructs a new {@code String} by decoding the specified array of bytes using the US-ASCII charset.
     *
     * @param bytes The bytes to be decoded into characters
     * @return A new {@code String} decoded from the specified array of bytes using the US-ASCII charset, or {@code null} if the input byte array was {@code null}.
     * @throws NullPointerException Thrown if {@link StandardCharsets#US_ASCII} is not initialized, which should never happen since it is required by the Java platform
     *                              specification.
     * @since As of 1.7, throws {@link NullPointerException} instead of UnsupportedEncodingException
     */
    public static String newStringUsAscii(final byte[] bytes) {
        return newString(bytes, StandardCharsets.US_ASCII);
    }

    /**
     * Constructs a new {@code String} by decoding the specified array of bytes using the UTF-16 charset.
     *
     * @param bytes The bytes to be decoded into characters
     * @return A new {@code String} decoded from the specified array of bytes using the UTF-16 charset or {@code null} if the input byte array was {@code null}.
     * @throws NullPointerException Thrown if {@link StandardCharsets#UTF_16} is not initialized, which should never happen since it is required by the Java platform
     *                              specification.
     * @since As of 1.7, throws {@link NullPointerException} instead of UnsupportedEncodingException
     */
    public static String newStringUtf16(final byte[] bytes) {
        return newString(bytes, StandardCharsets.UTF_16);
    }

    /**
     * Constructs a new {@code String} by decoding the specified array of bytes using the UTF-16BE charset.
     *
     * @param bytes The bytes to be decoded into characters
     * @return A new {@code String} decoded from the specified array of bytes using the UTF-16BE charset, or {@code null} if the input byte array was {@code null}.
     * @throws NullPointerException Thrown if {@link StandardCharsets#UTF_16BE} is not initialized, which should never happen since it is required by the Java platform
     *                              specification.
     * @since As of 1.7, throws {@link NullPointerException} instead of UnsupportedEncodingException
     */
    public static String newStringUtf16Be(final byte[] bytes) {
        return newString(bytes, StandardCharsets.UTF_16BE);
    }

    /**
     * Constructs a new {@code String} by decoding the specified array of bytes using the UTF-16LE charset.
     *
     * @param bytes The bytes to be decoded into characters
     * @return A new {@code String} decoded from the specified array of bytes using the UTF-16LE charset, or {@code null} if the input byte array was {@code null}.
     * @throws NullPointerException Thrown if {@link StandardCharsets#UTF_16LE} is not initialized, which should never happen since it is required by the Java platform
     *                              specification.
     * @since As of 1.7, throws {@link NullPointerException} instead of UnsupportedEncodingException
     */
    public static String newStringUtf16Le(final byte[] bytes) {
        return newString(bytes, StandardCharsets.UTF_16LE);
    }

    /**
     * Constructs a new {@code String} by decoding the specified array of bytes using the UTF-8 charset.
     *
     * @param bytes The bytes to be decoded into characters
     * @return A new {@code String} decoded from the specified array of bytes using the UTF-8 charset, or {@code null} if the input byte array was {@code null}.
     * @throws NullPointerException Thrown if {@link StandardCharsets#UTF_8} is not initialized, which should never happen since it is required by the Java platform specification.
     * @since As of 1.7, throws {@link NullPointerException} instead of UnsupportedEncodingException
     */
    public static String newStringUtf8(final byte[] bytes) {
        return newString(bytes, StandardCharsets.UTF_8);
    }
}
