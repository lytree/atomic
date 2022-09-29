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


/**
 * @author pride
 */
public class CharUtils extends org.apache.commons.lang3.CharUtils {

    /**
     * 字符常量：空格符 {@code ' '}
     */
    public final static char SPACE = ' ';
    /**
     * 字符常量：制表符 {@code '\t'}
     */
    public final static char TAB = '	';
    /**
     * 字符常量：点 {@code '.'}
     */
    public final static char DOT = '.';
    /**
     * 字符常量：斜杠 {@code '/'}
     */
    public final static char SLASH = '/';
    /**
     * 字符常量：反斜杠 {@code '\\'}
     */
    public final static char BACKSLASH = '\\';
    /**
     * 字符常量：回车符 {@code '\r'}
     */
    public final static char CR = '\r';
    /**
     * 字符常量：换行符 {@code '\n'}
     */
    public final static char LF = '\n';
    /**
     * 字符常量：减号（连接符） {@code '-'}
     */
    public final static char DASHED = '-';
    /**
     * 字符常量：下划线 {@code '_'}
     */
    public final static char UNDERLINE = '_';
    /**
     * 字符常量：逗号 {@code ','}
     */
    public final static char COMMA = ',';
    /**
     * 字符常量：花括号（左） <code>'{'</code>
     */
    public final static char DELIM_START = '{';
    /**
     * 字符常量：花括号（右） <code>'}'</code>
     */
    public final static char DELIM_END = '}';
    /**
     * 字符常量：中括号（左） {@code '['}
     */
    public final static char BRACKET_START = '[';
    /**
     * 字符常量：中括号（右） {@code ']'}
     */
    public final static char BRACKET_END = ']';
    /**
     * 字符常量：双引号 {@code '"'}
     */
    public final static char DOUBLE_QUOTES = '"';
    /**
     * 字符常量：单引号 {@code '\''}
     */
    public final static char SINGLE_QUOTE = '\'';
    /**
     * 字符常量：与 {@code '&'}
     */
    public final static char AMP = '&';
    /**
     * 字符常量：冒号 {@code ':'}
     */
    public final static char COLON = ':';
    /**
     * 字符常量：艾特 {@code '@'}
     */
    public final static char AT = '@';

    /**
     * 获取给定字符的16进制数值
     *
     * @param b 字符
     * @return 16进制字符
     */
    public static int digit16(int b) {
        return Character.digit(b, 16);
    }

    /**
     * 是否空白符<br> 空白符包括空格、制表符、全角空格和不间断空格<br>
     *
     * @param c 字符
     * @return 是否空白符
     * @see Character#isWhitespace(int)
     * @see Character#isSpaceChar(int)
     */
    public static boolean isBlankChar(char c) {
        return isBlankChar((int) c);
    }

    /**
     * 是否空白符<br> 空白符包括空格、制表符、全角空格和不间断空格<br>
     *
     * @param c 字符
     * @return 是否空白符
     * @see Character#isWhitespace(int)
     * @see Character#isSpaceChar(int)
     */
    public static boolean isBlankChar(int c) {
        return Character.isWhitespace(c)
                || Character.isSpaceChar(c)
                || c == '\ufeff'
                || c == '\u202a'
                || c == '\u0000';
    }

    /**
     * <p>
     * 检查是否为数字字符，数字字符指0~9
     * </p>
     *
     * <pre>
     *   CharUtil.isNumber('a')  = false
     *   CharUtil.isNumber('A')  = false
     *   CharUtil.isNumber('3')  = true
     *   CharUtil.isNumber('-')  = false
     *   CharUtil.isNumber('\n') = false
     *   CharUtil.isNumber('&copy;') = false
     * </pre>
     *
     * @param ch 被检查的字符
     * @return true表示为数字字符，数字字符指0~9
     */
    public static boolean isNumber(char ch) {
        return ch >= '0' && ch <= '9';
    }

    /**
     * 是否为16进制规范的字符，判断是否为如下字符
     * <pre>
     * 1. 0~9
     * 2. a~f
     * 4. A~F
     * </pre>
     *
     * @param c 字符
     * @return 是否为16进制规范的字符
     * 
     */
    public static boolean isHexChar(char c) {
        return isNumber(c) || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F');
    }

    /**
     * 判断是否为emoji表情符<br>
     *
     * @param c 字符
     * @return 是否为emoji
     */
    public static boolean isEmoji(char c) {
        //noinspection ConstantConditions
        return !((c == 0x0) || //
                (c == 0x9) || //
                (c == 0xA) || //
                (c == 0xD) || //
                ((c >= 0x20) && (c <= 0xD7FF)) || //
                ((c >= 0xE000) && (c <= 0xFFFD)) || //
                ((c >= 0x100000) && (c <= 0x10FFFF)));
    }
}
