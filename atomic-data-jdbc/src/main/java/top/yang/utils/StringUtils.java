package top.yang.utils;

import java.math.BigDecimal;

public class StringUtils {

    public static final String EMPTY = "";


    /**
     * @methodName: upperCase
     * @description: 首字母小写变大写
     * @author: xiangfeng@yintong.com.cn
     * @date: 2018/3/26
     **/
    public static String upperCase(String str) {
        char[] ch = str.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z') {
            ch[0] = (char) (ch[0] - 32);
        }
        return new String(ch);
    }


    /**
     * @methodName: isEmpty
     * @description: 字符串非空校验
     * @author: xiangfeng@yintong.com.cn
     * @date: 2018/3/31
     **/
    public static boolean isEmpty(Object str) {
        if (null == str) {
            return true;
        }

        if (str instanceof String) {
            return "".equals(((String) str).trim());
        }

        return false;
    }

    /**
     * 人数转化 例如10000转为1万
     *
     * @param number
     * @param unit
     * @param scale
     * @param point
     * @param suf
     * @return
     */
    public static String numberFormat(Object number, int unit, int scale, int point, String suf) {
        if (number == null) {
            return "0";
        } else if (Integer.valueOf(number.toString()) < unit) {
            return number.toString();
        } else if ((Integer.valueOf(number.toString()) % unit) < point) {

            return (new BigDecimal(String.valueOf(number))).divide(new BigDecimal(unit)).setScale(0, 4).toString() + suf;
        } else {
            return (new BigDecimal(String.valueOf(number))).divide(new BigDecimal(unit)).setScale(scale, 4).toString() + suf;
        }
    }


    public static boolean isNotEmpty(Object str) {
        return !isEmpty(str);
    }

    private static boolean isNotEmojiCharacter(char codePoint) {
        return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA)
                || (codePoint == 0xD)
                || ((codePoint >= 0x20) && (codePoint <= 0xD7FF))
                || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD))
                || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF));
    }

    /**
     * 过滤emoji 或者 其他非文字类型的字符
     *
     * @param source
     * @return
     */
    public static String filterEmoji(String source) {
        if (StringUtils.isEmpty(source)) {
            return source;
        }
        StringBuilder buf = null;
        int len = source.length();
        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);
            if (isNotEmojiCharacter(codePoint)) {
                if (buf == null) {
                    buf = new StringBuilder(source.length());
                }
                buf.append(codePoint);
            }
        }
        if (buf == null) {
            return "";
        } else {
            if (buf.length() == len) {
                buf = null;
                return source;
            } else {
                return buf.toString();
            }
        }
    }

    /**
     * 半角转全角
     *
     * @param input String.
     * @return 全角字符串.
     */
    public static String toSBC(String input) {
        char c[] = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == ' ') {
                c[i] = '\u3000';
            } else if (c[i] < '\177') {
                c[i] = (char) (c[i] + 65248);

            }
        }
        return new String(c);
    }

    /**
     * 全角转半角
     *
     * @param input String.
     * @return 半角字符串
     */
    public static String toDBC(String input) {

        char c[] = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == '\u3000') {
                c[i] = ' ';
            } else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
                c[i] = (char) (c[i] - 65248);

            }
        }
        String returnString = new String(c);

        return returnString;
    }

}
