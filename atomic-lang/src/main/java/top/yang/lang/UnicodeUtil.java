package top.yang.lang;

import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 提供Unicode字符串和普通字符串之间的转换
 */
public class UnicodeUtil {
    /**
     * 用于建立十六进制字符的输出的小写字符数组
     */
    private static final char[] DIGITS_LOWER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    /**
     * 用于建立十六进制字符的输出的大写字符数组
     */
    private static final char[] DIGITS_UPPER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};


    /**
     * Unicode字符串转为普通字符串<br>
     * Unicode字符串的表现方式为：\\uXXXX
     *
     * @param unicode Unicode字符串
     * @return 普通字符串
     */
    public static String toString(String unicode) {
        if (StringUtils.isBlank(unicode)) {
            return unicode;
        }

        final int len = unicode.length();
        StringBuilder sb = new StringBuilder();
        int i;
        int pos = 0;
        while ((i = StringUtils.indexOfIgnoreCase(unicode, "\\u", pos)) != -1) {
            sb.append(unicode, pos, i);//写入Unicode符之前的部分
            pos = i;
            if (i + 5 < len) {
                char c;
                try {
                    c = (char) Integer.parseInt(unicode.substring(i + 2, i + 6), 16);
                    sb.append(c);
                    pos = i + 6;//跳过整个Unicode符
                } catch (NumberFormatException e) {
                    //非法Unicode符，跳过
                    sb.append(unicode, pos, i + 2);//写入"\\u"
                    pos = i + 2;
                }
            } else {
                //非Unicode符，结束
                break;
            }
        }

        if (pos < len) {
            sb.append(unicode, pos, len);
        }
        return sb.toString();
    }

    /**
     * 字符串编码为Unicode形式
     *
     * @param str 被编码的字符串
     * @return Unicode字符串
     */
    public static String toUnicode(String str) {
        return toUnicode(str, true);
    }

    /**
     * 字符串编码为Unicode形式
     *
     * @param str         被编码的字符串
     * @param isSkipAscii 是否跳过ASCII字符（只跳过可见字符）
     * @return Unicode字符串
     */
    public static String toUnicode(String str, boolean isSkipAscii) {
        if (StringUtils.isEmpty(str)) {
            return str;
        }

        final int len = str.length();
        final StringBuilder unicode = new StringBuilder(str.length() * 6);
        char c;
        for (int i = 0; i < len; i++) {
            c = str.charAt(i);
            if (isSkipAscii && CharUtils.isAsciiPrintable(c)) {
                unicode.append(c);
            } else {
                unicode.append(toUnicodeHex(c));
            }
        }
        return unicode.toString();
    }

    /**
     * 将指定int值转换为Unicode字符串形式，常用于特殊字符（例如汉字）转Unicode形式<br>
     * 转换的字符串如果u后不足4位，则前面用0填充，例如：
     *
     * <pre>
     * '我' =》\u4f60
     * </pre>
     *
     * @param value int值，也可以是char
     * @return Unicode表现形式
     */
    private static String toUnicodeHex(int value) {
        final StringBuilder builder = new StringBuilder(6);

        builder.append("\\u");
        String hex = toHex(value);
        int len = hex.length();
        if (len < 4) {
            builder.append("0000", 0, 4 - len);// 不足4位补0
        }
        builder.append(hex);

        return builder.toString();
    }

    /**
     * 将指定char值转换为Unicode字符串形式，常用于特殊字符（例如汉字）转Unicode形式<br>
     * 转换的字符串如果u后不足4位，则前面用0填充，例如：
     *
     * <pre>
     * '我' =》\u4f60
     * </pre>
     *
     * @param ch char值
     * @return Unicode表现形式
     * @since 4.0.1
     */
    private static String toUnicodeHex(char ch) {
        return "\\u" +//
                DIGITS_LOWER[(ch >> 12) & 15] +//
                DIGITS_LOWER[(ch >> 8) & 15] +//
                DIGITS_LOWER[(ch >> 4) & 15] +//
                DIGITS_LOWER[(ch) & 15];
    }

    /**
     * 转为16进制字符串
     *
     * @param value int值
     * @return 16进制字符串
     * @since 4.4.1
     */
    private static String toHex(int value) {
        return Integer.toHexString(value);
    }
}
