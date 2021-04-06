package top.yang.lang;

import org.apache.commons.lang3.StringUtils;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class StringUtil extends StringUtils {
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
     * 字符串常量：制表符 {@code "\t"}
     */
    public static final String TAB = "	";

    /**
     * 字符串常量：点 {@code "."}
     */
    public static final String DOT = ".";

    /**
     * 字符串常量：双点 {@code ".."} <br>
     * 用途：作为指向上级文件夹的路径，如：{@code "../path"}
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
     * 字符串常量：回车符 {@code "\r"} <br>
     * 解释：该字符常用于表示 Linux 系统和 MacOS 系统下的文本换行
     */
    public static final String CR = "\r";

    /**
     * 字符串常量：换行符 {@code "\n"}
     */
    public static final String LF = "\n";

    /**
     * 字符串常量：Windows 换行 {@code "\r\n"} <br>
     * 解释：该字符串常用于表示 Windows 系统下的文本换行
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
     * 字符串常量：{@code "null"} <br>
     * 注意：{@code "null" != null}
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
     * 编码字符串
     *
     * @param str     字符串
     * @param charset 字符集，如果此字段为空，则解码的结果取决于平台
     * @return 编码后的字节码
     */
    public static byte[] toBytes(String str, String charset) {
        return toBytes(str, isBlank(charset) ? Charset.defaultCharset() : Charset.forName(charset));
    }

    /**
     * 编码字符串
     *
     * @param str     字符串
     * @param charset 字符集，如果此字段为空，则解码的结果取决于平台
     * @return 编码后的字节码
     */
    public static byte[] toBytes(String str, Charset charset) {
        if (str == null) {
            return null;
        }

        if (null == charset) {
            return str.toString().getBytes();
        }
        return str.toString().getBytes(charset);
    }

    /**
     * 补充字符串以满足指定长度，如果提供的字符串大于指定长度，截断之
     *
     * <pre>
     * StrUtil.padPre(null, *, *);//null
     * StrUtil.padPre("1", 3, "ABC");//"AB1"
     * StrUtil.padPre("123", 2, "ABC");//"12"
     * </pre>
     *
     * @param str    字符串
     * @param length 长度
     * @param padStr 补充的字符
     * @return 补充后的字符串
     */
    public static String padPre(String str, int length, String padStr) {
        return leftPad(str, length, padStr);
    }

    /**
     * 补充字符串以满足最小长度，如果提供的字符串大于指定长度，截断之
     *
     * <pre>
     * StrUtil.padPre(null, *, *);//null
     * StrUtil.padPre("1", 3, '0');//"001"
     * StrUtil.padPre("123", 2, '0');//"12"
     * </pre>
     *
     * @param str     字符串
     * @param length  长度
     * @param padChar 补充的字符
     * @return 补充后的字符串
     */
    public static String padPre(String str, int length, char padChar) {
        return leftPad(str, length, padChar);
    }

    /**
     * 补充字符串以满足最小长度，如果提供的字符串大于指定长度，截断之
     *
     * <pre>
     * StrUtil.padAfter(null, *, *);//null
     * StrUtil.padAfter("1", 3, '0');//"100"
     * StrUtil.padAfter("123", 2, '0');//"23"
     * </pre>
     *
     * @param str     字符串，如果为{@code null}，直接返回null
     * @param length  长度
     * @param padChar 补充的字符
     * @return 补充后的字符串
     */
    public static String padAfter(String str, int length, char padChar) {
        return rightPad(str, length, padChar);
    }

    /**
     * 补充字符串以满足最小长度
     *
     * <pre>
     * StrUtil.padAfter(null, *, *);//null
     * StrUtil.padAfter("1", 3, "ABC");//"1AB"
     * StrUtil.padAfter("123", 2, "ABC");//"23"
     * </pre>
     *
     * @param str    字符串，如果为{@code null}，直接返回null
     * @param length 长度
     * @param padStr 补充的字符
     * @return 补充后的字符串
     * @since 4.3.2
     */
    public static String padAfter(String str, int length, String padStr) {
        return rightPad(str, length, padStr);
    }

    // ------------------------------------------------------------------------ center

    /**
     * 居中字符串，两边补充指定字符串，如果指定长度小于字符串，则返回原字符串
     *
     * <pre>
     * StrUtil.center(null, *)   = null
     * StrUtil.center("", 4)     = "    "
     * StrUtil.center("ab", -1)  = "ab"
     * StrUtil.center("ab", 4)   = " ab "
     * StrUtil.center("abcd", 2) = "abcd"
     * StrUtil.center("a", 4)    = " a  "
     * </pre>
     *
     * @param str  字符串
     * @param size 指定长度
     * @return 补充后的字符串
     * @since 4.3.2
     */
    public static String padCenter(String str, final int size) {
        return center(str, size);
    }

    /**
     * 居中字符串，两边补充指定字符串，如果指定长度小于字符串，则返回原字符串
     *
     * <pre>
     * StrUtil.center(null, *, *)     = null
     * StrUtil.center("", 4, ' ')     = "    "
     * StrUtil.center("ab", -1, ' ')  = "ab"
     * StrUtil.center("ab", 4, ' ')   = " ab "
     * StrUtil.center("abcd", 2, ' ') = "abcd"
     * StrUtil.center("a", 4, ' ')    = " a  "
     * StrUtil.center("a", 4, 'y')   = "yayy"
     * StrUtil.center("abc", 7, ' ')   = "  abc  "
     * </pre>
     *
     * @param str     字符串
     * @param size    指定长度
     * @param padChar 两边补充的字符
     * @return 补充后的字符串
     * @since 4.3.2
     */
    public static String padCenter(String str, final int size, char padChar) {
        return center(str, size, padChar);
    }

    /**
     * 居中字符串，两边补充指定字符串，如果指定长度小于字符串，则返回原字符串
     *
     * <pre>
     * StrUtil.center(null, *, *)     = null
     * StrUtil.center("", 4, " ")     = "    "
     * StrUtil.center("ab", -1, " ")  = "ab"
     * StrUtil.center("ab", 4, " ")   = " ab "
     * StrUtil.center("abcd", 2, " ") = "abcd"
     * StrUtil.center("a", 4, " ")    = " a  "
     * StrUtil.center("a", 4, "yz")   = "yayz"
     * StrUtil.center("abc", 7, null) = "  abc  "
     * StrUtil.center("abc", 7, "")   = "  abc  "
     * </pre>
     *
     * @param str    字符串
     * @param size   指定长度
     * @param padStr 两边补充的字符串
     * @return 补充后的字符串
     */
    public static String padCenter(String str, final int size, String padStr) {
        return center(str, size, padStr);
    }


    /**
     * 当给定字符串为null时，转换为Empty
     *
     * @param str 被检查的字符串
     * @return 原字符串或者空串
     * @see #nullToEmpty(String)
     * @since 4.6.3
     */
    public static String emptyIfNull(String str) {
        return nullToEmpty(str);
    }

    /**
     * 当给定字符串为null时，转换为Empty
     *
     * @param str 被转换的字符串
     * @return 转换后的字符串
     */
    public static String nullToEmpty(String str) {
        return nullToDefault(str, EMPTY);
    }

    /**
     * 如果字符串是 {@code null}，则返回指定默认字符串，否则返回字符串本身。
     *
     * <pre>
     * nullToDefault(null, &quot;default&quot;)  = &quot;default&quot;
     * nullToDefault(&quot;&quot;, &quot;default&quot;)    = &quot;&quot;
     * nullToDefault(&quot;  &quot;, &quot;default&quot;)  = &quot;  &quot;
     * nullToDefault(&quot;bat&quot;, &quot;default&quot;) = &quot;bat&quot;
     * </pre>
     *
     * @param str        要转换的字符串
     * @param defaultStr 默认字符串
     * @return 字符串本身或指定的默认字符串
     */
    public static String nullToDefault(String str, String defaultStr) {
        return (str == null) ? defaultStr : str.toString();
    }


    /**
     * 编码字符串<br>
     * 使用系统默认编码
     *
     * @param str 字符串
     * @return 编码后的字节码
     */
    public static byte[] bytes(CharSequence str) {
        return bytes(str, Charset.defaultCharset());
    }

    /**
     * 编码字符串
     *
     * @param str     字符串
     * @param charset 字符集，如果此字段为空，则解码的结果取决于平台
     * @return 编码后的字节码
     */
    public static byte[] bytes(CharSequence str, String charset) {
        return bytes(str, isBlank(charset) ? Charset.defaultCharset() : Charset.forName(charset));
    }

    /**
     * 编码字符串
     *
     * @param str     字符串
     * @param charset 字符集，如果此字段为空，则解码的结果取决于平台
     * @return 编码后的字节码
     */
    public static byte[] bytes(CharSequence str, Charset charset) {
        if (str == null) {
            return null;
        }

        if (null == charset) {
            return str.toString().getBytes();
        }
        return str.toString().getBytes(charset);
    }

    /**
     * 字符串转换为byteBuffer
     *
     * @param str     字符串
     * @param charset 编码
     * @return byteBuffer
     */
    public static ByteBuffer byteBuffer(CharSequence str, String charset) {
        return ByteBuffer.wrap(bytes(str, charset));
    }

    // ------------------------------------------------------------------------ str

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
    public static String utf8Str(Object obj) {
        return str(obj, CharsetUtil.CHARSET_UTF_8);
    }

    /**
     * 将对象转为字符串
     *
     * <pre>
     * 1、Byte数组和ByteBuffer会被转换为对应字符串的数组
     * 2、对象数组会调用Arrays.toString方法
     * </pre>
     *
     * @param obj         对象
     * @param charsetName 字符集
     * @return 字符串
     */
    public static String str(Object obj, String charsetName) {
        return str(obj, Charset.forName(charsetName));
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
    public static String str(Object obj, Charset charset) {
        if (null == obj) {
            return null;
        }

        if (obj instanceof String) {
            return (String) obj;
        } else if (obj instanceof byte[]) {
            return str((byte[]) obj, charset);
        } else if (obj instanceof Byte[]) {
            return str((Byte[]) obj, charset);
        } else if (obj instanceof ByteBuffer) {
            return str((ByteBuffer) obj, charset);
        } else if (ArrayUtil.isArray(obj)) {
            return ArrayUtil.toString(obj);
        }

        return obj.toString();
    }

    /**
     * 将byte数组转为字符串
     *
     * @param bytes   byte数组
     * @param charset 字符集
     * @return 字符串
     */
    public static String str(byte[] bytes, String charset) {
        return str(bytes, isBlank(charset) ? Charset.defaultCharset() : Charset.forName(charset));
    }

    /**
     * 解码字节码
     *
     * @param data    字符串
     * @param charset 字符集，如果此字段为空，则解码的结果取决于平台
     * @return 解码后的字符串
     */
    public static String str(byte[] data, Charset charset) {
        if (data == null) {
            return null;
        }

        if (null == charset) {
            return new String(data);
        }
        return new String(data, charset);
    }

    /**
     * 将Byte数组转为字符串
     *
     * @param bytes   byte数组
     * @param charset 字符集
     * @return 字符串
     */
    public static String str(Byte[] bytes, String charset) {
        return str(bytes, isBlank(charset) ? Charset.defaultCharset() : Charset.forName(charset));
    }

    /**
     * 解码字节码
     *
     * @param data    字符串
     * @param charset 字符集，如果此字段为空，则解码的结果取决于平台
     * @return 解码后的字符串
     */
    public static String str(Byte[] data, Charset charset) {
        if (data == null) {
            return null;
        }

        byte[] bytes = new byte[data.length];
        Byte dataByte;
        for (int i = 0; i < data.length; i++) {
            dataByte = data[i];
            bytes[i] = (null == dataByte) ? -1 : dataByte;
        }

        return str(bytes, charset);
    }

    /**
     * 将编码的byteBuffer数据转换为字符串
     *
     * @param data    数据
     * @param charset 字符集，如果为空使用当前系统字符集
     * @return 字符串
     */
    public static String str(ByteBuffer data, String charset) {
        if (data == null) {
            return null;
        }

        return str(data, Charset.forName(charset));
    }

    /**
     * 将编码的byteBuffer数据转换为字符串
     *
     * @param data    数据
     * @param charset 字符集，如果为空使用当前系统字符集
     * @return 字符串
     */
    public static String str(ByteBuffer data, Charset charset) {
        if (null == charset) {
            charset = Charset.defaultCharset();
        }
        return charset.decode(data).toString();
    }

    /**
     * {@link CharSequence} 转为字符串，null安全
     *
     * @param cs {@link CharSequence}
     * @return 字符串
     */
    public static String str(CharSequence cs) {
        return null == cs ? null : cs.toString();
    }

    /**
     * 清理空白字符
     *
     * @param str 被清理的字符串
     * @return 清理后的字符串
     */
    public static String cleanBlank(CharSequence str) {
        return filter(str, c -> !CharsetUtil.isBlankChar(c));
    }

    /**
     * 过滤字符串
     *
     * @param str    字符串
     * @param filter 过滤器
     * @return 过滤后的字符串
     * @since 5.4.0
     */
    public static String filter(CharSequence str, final Filter<Character> filter) {
        if (str == null || filter == null) {
            return str(str);
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
}
