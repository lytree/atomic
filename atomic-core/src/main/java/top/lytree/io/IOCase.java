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
package top.lytree.io;

import java.util.Objects;

/**
 * Enumeration of IO case sensitivity.
 * <p>
 * Different filing systems have different rules for case-sensitivity. Windows is case-insensitive, Unix is case-sensitive.
 * </p>
 * <p>
 * This class captures that difference, providing an enumeration to control how file name comparisons should be performed. It also provides methods that use the enumeration to
 * perform comparisons.
 * </p>
 * <p>
 * Wherever possible, you should use the {@code check} methods in this class to compare file names.
 * </p>
 */
public enum IOCase {

    /**
     * 对于区分大小写的常量，无论操作系统是什么。
     */
    SENSITIVE("Sensitive", true),

    /**
     * 无论操作系统是什么，该常数都不区分大小写。
     */
    INSENSITIVE("Insensitive", false),

    /**
     * 由当前操作系统确定的区分大小写的常数。在比较文件名时，Windows是不区分大小写的，Unix是区分大小写的。
     * <p>
     * <strong>Note:</strong> This only caters for Windows and Unix. Other operating
     * systems (e.g. OSX and OpenVMS) are treated as case sensitive if they use the Unix file separator and case-insensitive if they use the Windows file separator (see {@link
     * java.io.File#separatorChar}).
     * </p>
     * <p>
     * 如果你在Windows上序列化这个常量，在Unix上反序列化这个常量，反之亦然，那么区分大小写标志的值将会改变。
     * </p>
     */
    SYSTEM("System", !FilenameUtils.isSystemWindows());

    /**
     * Serialization version.
     */
    private static final long serialVersionUID = 1L;

    /**
     * 工厂方法从名称创建IOCase。
     *
     * @param name the name to find
     * @return the IOCase object
     * @throws IllegalArgumentException if the name is invalid
     */
    public static IOCase forName(final String name) {
        for (final IOCase ioCase : IOCase.values()) {
            if (ioCase.getName().equals(name)) {
                return ioCase;
            }
        }
        throw new IllegalArgumentException("Invalid IOCase name: " + name);
    }

    /**
     * Tests for cases sensitivity in a null-safe manner.
     *
     * @param ioCase an IOCase.
     * @return true if the input is non-null and {@link #isCaseSensitive()}.
     */
    public static boolean isCaseSensitive(final IOCase ioCase) {
        return ioCase != null && ioCase.isCaseSensitive();
    }

    /**
     * 如果不为空，则返回给定值，如果为空，则返回defaultValue。
     *
     * @param value        the value to test.
     * @param defaultValue the default value.
     * @return the given value if not-null, the defaultValue if null.
     */
    public static IOCase value(final IOCase value, final IOCase defaultValue) {
        return value != null ? value : defaultValue;
    }

    /**
     * 枚举的名字。
     */
    private final String name;

    /**
     * 不进行序列化
     */
    private final transient boolean sensitive;

    /**
     * Constructs a new instance.
     *
     * @param name      the name
     * @param sensitive the sensitivity
     */
    IOCase(final String name, final boolean sensitive) {
        this.name = name;
        this.sensitive = sensitive;
    }

    /**
     * 比较两个字符串 区分大小写规则。
     * <p>
     * This method mimics {@link String#compareTo} but takes case-sensitivity into account.
     * </p>
     *
     * @param str1 the first string to compare, not null
     * @param str2 the second string to compare, not null
     * @return true if equal using the case rules
     * @throws NullPointerException if either string is null
     */
    public int checkCompareTo(final String str1, final String str2) {
        Objects.requireNonNull(str1, "str1");
        Objects.requireNonNull(str2, "str2");
        return sensitive ? str1.compareTo(str2) : str1.compareToIgnoreCase(str2);
    }

    /**
     * 使用区分大小写规则 检查一个字符串是否以另一个字符串结尾。
     * <p>
     * This method mimics {@link String#endsWith} but takes case-sensitivity into account.
     * </p>
     *
     * @param str the string to check
     * @param end the end to compare against
     * @return true if equal using the case rules, false if either input is null
     */
    public boolean checkEndsWith(final String str, final String end) {
        if (str == null || end == null) {
            return false;
        }
        final int endLen = end.length();
        return str.regionMatches(!sensitive, str.length() - endLen, end, 0, endLen);
    }

    /**
     * 使用区分大小写规则 比较两个字符串。
     * <p>
     * This method mimics {@link String#equals} but takes case-sensitivity into account.
     * </p>
     *
     * @param str1 the first string to compare, not null
     * @param str2 the second string to compare, not null
     * @return true if equal using the case rules
     * @throws NullPointerException if either string is null
     */
    public boolean checkEquals(final String str1, final String str2) {
        Objects.requireNonNull(str1, "str1");
        Objects.requireNonNull(str2, "str2");
        return sensitive ? str1.equals(str2) : str1.equalsIgnoreCase(str2);
    }

    /**
     * 使用区分大小写规则 检查一个字符串是否包含从特定索引开始的另一个字符串。
     * <p>
     * This method mimics parts of {@link String#indexOf(String, int)} but takes case-sensitivity into account.
     * </p>
     *
     * @param str           the string to check, not null
     * @param strStartIndex the index to start at in str
     * @param search        the start to search for, not null
     * @return the first index of the search String, -1 if no match or {@code null} string input
     * @throws NullPointerException if either string is null
     */
    public int checkIndexOf(final String str, final int strStartIndex, final String search) {
        final int endIndex = str.length() - search.length();
        if (endIndex >= strStartIndex) {
            for (int i = strStartIndex; i <= endIndex; i++) {
                if (checkRegionMatches(str, i, search)) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * 使用区分大小写规则 检查一个字符串在特定索引处是否包含另一个字符串。
     * <p>
     * This method mimics parts of {@link String#regionMatches(boolean, int, String, int, int)} but takes case-sensitivity into account.
     * </p>
     *
     * @param str           the string to check, not null
     * @param strStartIndex the index to start at in str
     * @param search        the start to search for, not null
     * @return true if equal using the case rules
     * @throws NullPointerException if either string is null
     */
    public boolean checkRegionMatches(final String str, final int strStartIndex, final String search) {
        return str.regionMatches(!sensitive, strStartIndex, search, 0, search.length());
    }

    /**
     * 使用区分大小写规则 检查一个字符串是否以另一个字符串开头。
     * <p>
     * This method mimics {@link String#startsWith(String)} but takes case-sensitivity into account.
     * </p>
     *
     * @param str   the string to check
     * @param start the start to compare against
     * @return true if equal using the case rules, false if either input is null
     */
    public boolean checkStartsWith(final String str, final String start) {
        return str != null && start != null && str.regionMatches(!sensitive, 0, start, 0, start.length());
    }

    /**
     * 获取常量的名称。
     *
     * @return the name of the constant
     */
    public String getName() {
        return name;
    }

    /**
     * 对象是否表示区分大小写的比较。
     *
     * @return true if case sensitive
     */
    public boolean isCaseSensitive() {
        return sensitive;
    }

    /**
     * 将流中的枚举替换为实枚举。这确保为SYSTEM设置了正确的标志。
     *
     * @return the resolved object
     */
    private Object readResolve() {
        return forName(name);
    }

    /**
     * Gets a string describing the sensitivity.
     *
     * @return a string describing the sensitivity
     */
    @Override
    public String toString() {
        return name;
    }

}
