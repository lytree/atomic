package top.yang.lang;

import org.apache.commons.lang3.RegExUtils;

import java.util.regex.Pattern;

public class RegExUtil extends RegExUtils {
    /**
     * 给定内容是否匹配正则
     *
     * @param pattern 模式
     * @param content 内容
     * @return 正则为null或者""则不检查，返回true，内容为null返回false
     */
    public static boolean isMatch(Pattern pattern, CharSequence content) {
        if (content == null || pattern == null) {
            // 提供null的字符串为不匹配
            return false;
        }
        return pattern.matcher(content).matches();
    }

    /**
     * 给定内容是否匹配正则
     *
     * @param regex   正则
     * @param content 内容
     * @return 正则为null或者""则不检查，返回true，内容为null返回false
     */
    public static boolean isMatch(String regex, CharSequence content) {
        if (content == null) {
            // 提供null的字符串为不匹配
            return false;
        }

        if (StringUtil.isEmpty(regex)) {
            // 正则不存在则为全匹配
            return true;
        }
        Pattern pattern = Pattern.compile(regex);
        return isMatch(pattern, content);
    }

    /**
     * 验证是否为手机号码（中国）
     *
     * @param value 值
     * @return 是否为手机号码（中国）
     * @since 5.3.11
     */
    public static boolean isMobile(CharSequence value) {
        return isMatch(CommonPattern.MOBILE, value);
    }

    /**
     * 验证是否为座机号码（中国）
     *
     * @param value 值
     * @return 是否为座机号码（中国）
     * @since 5.3.11
     */
    public static boolean isTel(CharSequence value) {
        return isMatch(CommonPattern.TEL, value);
    }

    /**
     * 验证是否为座机号码+手机号码（中国）
     *
     * @param value 值
     * @return 是否为座机号码+手机号码（中国）
     * @since 5.3.11
     */
    public static boolean isPhone(CharSequence value) {
        return isMobile(value) || isTel(value);
    }

}
