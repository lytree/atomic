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

import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.mutable.MutableObject;

/**
 * <p>Helpers to process Strings using regular expressions.</p>
 *
 * @author Pride_Yang
 */
public class RegExUtils {

    /**
     * <p>Removes each substring of the text String that matches the given regular expression pattern.</p>
     * <p>
     * This method is a {@code null} safe equivalent to:
     * <ul>
     *  <li>{@code pattern.matcher(text).replaceAll(StringUtils.EMPTY)}</li>
     * </ul>
     *
     * <p>A {@code null} reference passed to this method is a no-op.</p>
     *
     * <pre>
     * StringUtils.removeAll(null, *)      = null
     * StringUtils.removeAll("any", (Pattern) null)  = "any"
     * StringUtils.removeAll("any", Pattern.compile(""))    = "any"
     * StringUtils.removeAll("any", Pattern.compile(".*"))  = ""
     * StringUtils.removeAll("any", Pattern.compile(".+"))  = ""
     * StringUtils.removeAll("abc", Pattern.compile(".?"))  = ""
     * StringUtils.removeAll("A&lt;__&gt;\n&lt;__&gt;B", Pattern.compile("&lt;.*&gt;"))      = "A\nB"
     * StringUtils.removeAll("A&lt;__&gt;\n&lt;__&gt;B", Pattern.compile("(?s)&lt;.*&gt;"))  = "AB"
     * StringUtils.removeAll("A&lt;__&gt;\n&lt;__&gt;B", Pattern.compile("&lt;.*&gt;", Pattern.DOTALL))  = "AB"
     * StringUtils.removeAll("ABCabc123abc", Pattern.compile("[a-z]"))     = "ABC123"
     * </pre>
     *
     * @param text  text to remove from, may be null
     * @param regex the regular expression to which this string is to be matched
     * @return the text with any removes processed, {@code null} if null String input
     * @see #replaceAll(String, Pattern, String)
     * @see java.util.regex.Matcher#replaceAll(String)
     * @see Pattern
     */
    public static String removeAll(final String text, final Pattern regex) {
        return replaceAll(text, regex, StringUtils.EMPTY);
    }

    /**
     * <p>Removes each substring of the text String that matches the given regular expression.</p>
     * <p>
     * This method is a {@code null} safe equivalent to:
     * <ul>
     *  <li>{@code text.replaceAll(regex, StringUtils.EMPTY)}</li>
     *  <li>{@code Pattern.compile(regex).matcher(text).replaceAll(StringUtils.EMPTY)}</li>
     * </ul>
     *
     * <p>A {@code null} reference passed to this method is a no-op.</p>
     *
     * <p>Unlike in the {@link #removePattern(String, String)} method, the {@link Pattern#DOTALL} option
     * is NOT automatically added.
     * To use the DOTALL option prepend {@code "(?s)"} to the regex.
     * DOTALL is also known as single-line mode in Perl.</p>
     *
     * <pre>
     * StringUtils.removeAll(null, *)      = null
     * StringUtils.removeAll("any", (String) null)  = "any"
     * StringUtils.removeAll("any", "")    = "any"
     * StringUtils.removeAll("any", ".*")  = ""
     * StringUtils.removeAll("any", ".+")  = ""
     * StringUtils.removeAll("abc", ".?")  = ""
     * StringUtils.removeAll("A&lt;__&gt;\n&lt;__&gt;B", "&lt;.*&gt;")      = "A\nB"
     * StringUtils.removeAll("A&lt;__&gt;\n&lt;__&gt;B", "(?s)&lt;.*&gt;")  = "AB"
     * StringUtils.removeAll("ABCabc123abc", "[a-z]")     = "ABC123"
     * </pre>
     *
     * @param text  text to remove from, may be null
     * @param regex the regular expression to which this string is to be matched
     * @return the text with any removes processed, {@code null} if null String input
     * @throws java.util.regex.PatternSyntaxException if the regular expression's syntax is invalid
     * @see #replaceAll(String, String, String)
     * @see #removePattern(String, String)
     * @see String#replaceAll(String, String)
     * @see Pattern
     * @see Pattern#DOTALL
     */
    public static String removeAll(final String text, final String regex) {
        return replaceAll(text, regex, StringUtils.EMPTY);
    }

    /**
     * <p>Removes the first substring of the text string that matches the given regular expression pattern.</p>
     * <p>
     * This method is a {@code null} safe equivalent to:
     * <ul>
     *  <li>{@code pattern.matcher(text).replaceFirst(StringUtils.EMPTY)}</li>
     * </ul>
     *
     * <p>A {@code null} reference passed to this method is a no-op.</p>
     *
     * <pre>
     * StringUtils.removeFirst(null, *)      = null
     * StringUtils.removeFirst("any", (Pattern) null)  = "any"
     * StringUtils.removeFirst("any", Pattern.compile(""))    = "any"
     * StringUtils.removeFirst("any", Pattern.compile(".*"))  = ""
     * StringUtils.removeFirst("any", Pattern.compile(".+"))  = ""
     * StringUtils.removeFirst("abc", Pattern.compile(".?"))  = "bc"
     * StringUtils.removeFirst("A&lt;__&gt;\n&lt;__&gt;B", Pattern.compile("&lt;.*&gt;"))      = "A\n&lt;__&gt;B"
     * StringUtils.removeFirst("A&lt;__&gt;\n&lt;__&gt;B", Pattern.compile("(?s)&lt;.*&gt;"))  = "AB"
     * StringUtils.removeFirst("ABCabc123", Pattern.compile("[a-z]"))          = "ABCbc123"
     * StringUtils.removeFirst("ABCabc123abc", Pattern.compile("[a-z]+"))      = "ABC123abc"
     * </pre>
     *
     * @param text  text to remove from, may be null
     * @param regex the regular expression pattern to which this string is to be matched
     * @return the text with the first replacement processed, {@code null} if null String input
     * @see #replaceFirst(String, Pattern, String)
     * @see java.util.regex.Matcher#replaceFirst(String)
     * @see Pattern
     */
    public static String removeFirst(final String text, final Pattern regex) {
        return replaceFirst(text, regex, StringUtils.EMPTY);
    }

    /**
     * <p>Removes the first substring of the text string that matches the given regular expression.</p>
     * <p>
     * This method is a {@code null} safe equivalent to:
     * <ul>
     *  <li>{@code text.replaceFirst(regex, StringUtils.EMPTY)}</li>
     *  <li>{@code Pattern.compile(regex).matcher(text).replaceFirst(StringUtils.EMPTY)}</li>
     * </ul>
     *
     * <p>A {@code null} reference passed to this method is a no-op.</p>
     *
     * <p>The {@link Pattern#DOTALL} option is NOT automatically added.
     * To use the DOTALL option prepend {@code "(?s)"} to the regex.
     * DOTALL is also known as single-line mode in Perl.</p>
     *
     * <pre>
     * StringUtils.removeFirst(null, *)      = null
     * StringUtils.removeFirst("any", (String) null)  = "any"
     * StringUtils.removeFirst("any", "")    = "any"
     * StringUtils.removeFirst("any", ".*")  = ""
     * StringUtils.removeFirst("any", ".+")  = ""
     * StringUtils.removeFirst("abc", ".?")  = "bc"
     * StringUtils.removeFirst("A&lt;__&gt;\n&lt;__&gt;B", "&lt;.*&gt;")      = "A\n&lt;__&gt;B"
     * StringUtils.removeFirst("A&lt;__&gt;\n&lt;__&gt;B", "(?s)&lt;.*&gt;")  = "AB"
     * StringUtils.removeFirst("ABCabc123", "[a-z]")          = "ABCbc123"
     * StringUtils.removeFirst("ABCabc123abc", "[a-z]+")      = "ABC123abc"
     * </pre>
     *
     * @param text  text to remove from, may be null
     * @param regex the regular expression to which this string is to be matched
     * @return the text with the first replacement processed, {@code null} if null String input
     * @throws java.util.regex.PatternSyntaxException if the regular expression's syntax is invalid
     * @see #replaceFirst(String, String, String)
     * @see String#replaceFirst(String, String)
     * @see Pattern
     * @see Pattern#DOTALL
     */
    public static String removeFirst(final String text, final String regex) {
        return replaceFirst(text, regex, StringUtils.EMPTY);
    }

    /**
     * <p>Removes each substring of the source String that matches the given regular expression using the DOTALL option.</p>
     * <p>
     * This call is a {@code null} safe equivalent to:
     * <ul>
     * <li>{@code text.replaceAll(&quot;(?s)&quot; + regex, StringUtils.EMPTY)}</li>
     * <li>{@code Pattern.compile(regex, Pattern.DOTALL).matcher(text).replaceAll(StringUtils.EMPTY)}</li>
     * </ul>
     *
     * <p>A {@code null} reference passed to this method is a no-op.</p>
     *
     * <pre>
     * StringUtils.removePattern(null, *)       = null
     * StringUtils.removePattern("any", (String) null)   = "any"
     * StringUtils.removePattern("A&lt;__&gt;\n&lt;__&gt;B", "&lt;.*&gt;")  = "AB"
     * StringUtils.removePattern("ABCabc123", "[a-z]")    = "ABC123"
     * </pre>
     *
     * @param text  the source string
     * @param regex the regular expression to which this string is to be matched
     * @return The resulting {@code String}
     * @see #replacePattern(String, String, String)
     * @see String#replaceAll(String, String)
     * @see Pattern#DOTALL
     */
    public static String removePattern(final String text, final String regex) {
        return replacePattern(text, regex, StringUtils.EMPTY);
    }

    /**
     * <p>Replaces each substring of the text String that matches the given regular expression pattern with the given replacement.</p>
     * <p>
     * This method is a {@code null} safe equivalent to:
     * <ul>
     *  <li>{@code pattern.matcher(text).replaceAll(replacement)}</li>
     * </ul>
     *
     * <p>A {@code null} reference passed to this method is a no-op.</p>
     *
     * <pre>
     * StringUtils.replaceAll(null, *, *)       = null
     * StringUtils.replaceAll("any", (Pattern) null, *)   = "any"
     * StringUtils.replaceAll("any", *, null)   = "any"
     * StringUtils.replaceAll("", Pattern.compile(""), "zzz")    = "zzz"
     * StringUtils.replaceAll("", Pattern.compile(".*"), "zzz")  = "zzz"
     * StringUtils.replaceAll("", Pattern.compile(".+"), "zzz")  = ""
     * StringUtils.replaceAll("abc", Pattern.compile(""), "ZZ")  = "ZZaZZbZZcZZ"
     * StringUtils.replaceAll("&lt;__&gt;\n&lt;__&gt;", Pattern.compile("&lt;.*&gt;"), "z")                 = "z\nz"
     * StringUtils.replaceAll("&lt;__&gt;\n&lt;__&gt;", Pattern.compile("&lt;.*&gt;", Pattern.DOTALL), "z") = "z"
     * StringUtils.replaceAll("&lt;__&gt;\n&lt;__&gt;", Pattern.compile("(?s)&lt;.*&gt;"), "z")             = "z"
     * StringUtils.replaceAll("ABCabc123", Pattern.compile("[a-z]"), "_")       = "ABC___123"
     * StringUtils.replaceAll("ABCabc123", Pattern.compile("[^A-Z0-9]+"), "_")  = "ABC_123"
     * StringUtils.replaceAll("ABCabc123", Pattern.compile("[^A-Z0-9]+"), "")   = "ABC123"
     * StringUtils.replaceAll("Lorem ipsum  dolor   sit", Pattern.compile("( +)([a-z]+)"), "_$2")  = "Lorem_ipsum_dolor_sit"
     * </pre>
     *
     * @param text        text to search and replace in, may be null
     * @param regex       the regular expression pattern to which this string is to be matched
     * @param replacement the string to be substituted for each match
     * @return the text with any replacements processed, {@code null} if null String input
     * @see java.util.regex.Matcher#replaceAll(String)
     * @see Pattern
     */
    public static String replaceAll(final String text, final Pattern regex, final String replacement) {
        if (ObjectUtils.anyNull(text, regex, replacement)) {
            return text;
        }
        return regex.matcher(text).replaceAll(replacement);
    }

    /**
     * <p>Replaces each substring of the text String that matches the given regular expression
     * with the given replacement.</p>
     * <p>
     * This method is a {@code null} safe equivalent to:
     * <ul>
     *  <li>{@code text.replaceAll(regex, replacement)}</li>
     *  <li>{@code Pattern.compile(regex).matcher(text).replaceAll(replacement)}</li>
     * </ul>
     *
     * <p>A {@code null} reference passed to this method is a no-op.</p>
     *
     * <p>Unlike in the {@link #replacePattern(String, String, String)} method, the {@link Pattern#DOTALL} option
     * is NOT automatically added.
     * To use the DOTALL option prepend {@code "(?s)"} to the regex.
     * DOTALL is also known as single-line mode in Perl.</p>
     *
     * <pre>
     * StringUtils.replaceAll(null, *, *)       = null
     * StringUtils.replaceAll("any", (String) null, *)   = "any"
     * StringUtils.replaceAll("any", *, null)   = "any"
     * StringUtils.replaceAll("", "", "zzz")    = "zzz"
     * StringUtils.replaceAll("", ".*", "zzz")  = "zzz"
     * StringUtils.replaceAll("", ".+", "zzz")  = ""
     * StringUtils.replaceAll("abc", "", "ZZ")  = "ZZaZZbZZcZZ"
     * StringUtils.replaceAll("&lt;__&gt;\n&lt;__&gt;", "&lt;.*&gt;", "z")      = "z\nz"
     * StringUtils.replaceAll("&lt;__&gt;\n&lt;__&gt;", "(?s)&lt;.*&gt;", "z")  = "z"
     * StringUtils.replaceAll("ABCabc123", "[a-z]", "_")       = "ABC___123"
     * StringUtils.replaceAll("ABCabc123", "[^A-Z0-9]+", "_")  = "ABC_123"
     * StringUtils.replaceAll("ABCabc123", "[^A-Z0-9]+", "")   = "ABC123"
     * StringUtils.replaceAll("Lorem ipsum  dolor   sit", "( +)([a-z]+)", "_$2")  = "Lorem_ipsum_dolor_sit"
     * </pre>
     *
     * @param text        text to search and replace in, may be null
     * @param regex       the regular expression to which this string is to be matched
     * @param replacement the string to be substituted for each match
     * @return the text with any replacements processed, {@code null} if null String input
     * @throws java.util.regex.PatternSyntaxException if the regular expression's syntax is invalid
     * @see #replacePattern(String, String, String)
     * @see String#replaceAll(String, String)
     * @see Pattern
     * @see Pattern#DOTALL
     */
    public static String replaceAll(final String text, final String regex, final String replacement) {
        if (ObjectUtils.anyNull(text, regex, replacement)) {
            return text;
        }
        return text.replaceAll(regex, replacement);
    }

    /**
     * <p>Replaces the first substring of the text string that matches the given regular expression pattern
     * with the given replacement.</p>
     * <p>
     * This method is a {@code null} safe equivalent to:
     * <ul>
     *  <li>{@code pattern.matcher(text).replaceFirst(replacement)}</li>
     * </ul>
     *
     * <p>A {@code null} reference passed to this method is a no-op.</p>
     *
     * <pre>
     * StringUtils.replaceFirst(null, *, *)       = null
     * StringUtils.replaceFirst("any", (Pattern) null, *)   = "any"
     * StringUtils.replaceFirst("any", *, null)   = "any"
     * StringUtils.replaceFirst("", Pattern.compile(""), "zzz")    = "zzz"
     * StringUtils.replaceFirst("", Pattern.compile(".*"), "zzz")  = "zzz"
     * StringUtils.replaceFirst("", Pattern.compile(".+"), "zzz")  = ""
     * StringUtils.replaceFirst("abc", Pattern.compile(""), "ZZ")  = "ZZabc"
     * StringUtils.replaceFirst("&lt;__&gt;\n&lt;__&gt;", Pattern.compile("&lt;.*&gt;"), "z")      = "z\n&lt;__&gt;"
     * StringUtils.replaceFirst("&lt;__&gt;\n&lt;__&gt;", Pattern.compile("(?s)&lt;.*&gt;"), "z")  = "z"
     * StringUtils.replaceFirst("ABCabc123", Pattern.compile("[a-z]"), "_")          = "ABC_bc123"
     * StringUtils.replaceFirst("ABCabc123abc", Pattern.compile("[^A-Z0-9]+"), "_")  = "ABC_123abc"
     * StringUtils.replaceFirst("ABCabc123abc", Pattern.compile("[^A-Z0-9]+"), "")   = "ABC123abc"
     * StringUtils.replaceFirst("Lorem ipsum  dolor   sit", Pattern.compile("( +)([a-z]+)"), "_$2")  = "Lorem_ipsum  dolor   sit"
     * </pre>
     *
     * @param text        text to search and replace in, may be null
     * @param regex       the regular expression pattern to which this string is to be matched
     * @param replacement the string to be substituted for the first match
     * @return the text with the first replacement processed, {@code null} if null String input
     * @see java.util.regex.Matcher#replaceFirst(String)
     * @see Pattern
     */
    public static String replaceFirst(final String text, final Pattern regex, final String replacement) {
        if (text == null || regex == null || replacement == null) {
            return text;
        }
        return regex.matcher(text).replaceFirst(replacement);
    }

    /**
     * <p>Replaces the first substring of the text string that matches the given regular expression
     * with the given replacement.</p>
     * <p>
     * This method is a {@code null} safe equivalent to:
     * <ul>
     *  <li>{@code text.replaceFirst(regex, replacement)}</li>
     *  <li>{@code Pattern.compile(regex).matcher(text).replaceFirst(replacement)}</li>
     * </ul>
     *
     * <p>A {@code null} reference passed to this method is a no-op.</p>
     *
     * <p>The {@link Pattern#DOTALL} option is NOT automatically added.
     * To use the DOTALL option prepend {@code "(?s)"} to the regex.
     * DOTALL is also known as single-line mode in Perl.</p>
     *
     * <pre>
     * StringUtils.replaceFirst(null, *, *)       = null
     * StringUtils.replaceFirst("any", (String) null, *)   = "any"
     * StringUtils.replaceFirst("any", *, null)   = "any"
     * StringUtils.replaceFirst("", "", "zzz")    = "zzz"
     * StringUtils.replaceFirst("", ".*", "zzz")  = "zzz"
     * StringUtils.replaceFirst("", ".+", "zzz")  = ""
     * StringUtils.replaceFirst("abc", "", "ZZ")  = "ZZabc"
     * StringUtils.replaceFirst("&lt;__&gt;\n&lt;__&gt;", "&lt;.*&gt;", "z")      = "z\n&lt;__&gt;"
     * StringUtils.replaceFirst("&lt;__&gt;\n&lt;__&gt;", "(?s)&lt;.*&gt;", "z")  = "z"
     * StringUtils.replaceFirst("ABCabc123", "[a-z]", "_")          = "ABC_bc123"
     * StringUtils.replaceFirst("ABCabc123abc", "[^A-Z0-9]+", "_")  = "ABC_123abc"
     * StringUtils.replaceFirst("ABCabc123abc", "[^A-Z0-9]+", "")   = "ABC123abc"
     * StringUtils.replaceFirst("Lorem ipsum  dolor   sit", "( +)([a-z]+)", "_$2")  = "Lorem_ipsum  dolor   sit"
     * </pre>
     *
     * @param text        text to search and replace in, may be null
     * @param regex       the regular expression to which this string is to be matched
     * @param replacement the string to be substituted for the first match
     * @return the text with the first replacement processed, {@code null} if null String input
     * @throws java.util.regex.PatternSyntaxException if the regular expression's syntax is invalid
     * @see String#replaceFirst(String, String)
     * @see Pattern
     * @see Pattern#DOTALL
     */
    public static String replaceFirst(final String text, final String regex, final String replacement) {
        if (text == null || regex == null || replacement == null) {
            return text;
        }
        return text.replaceFirst(regex, replacement);
    }

    /**
     * <p>Replaces each substring of the source String that matches the given regular expression with the given
     * replacement using the {@link Pattern#DOTALL} option. DOTALL is also known as single-line mode in Perl.</p>
     * <p>
     * This call is a {@code null} safe equivalent to:
     * <ul>
     * <li>{@code text.replaceAll(&quot;(?s)&quot; + regex, replacement)}</li>
     * <li>{@code Pattern.compile(regex, Pattern.DOTALL).matcher(text).replaceAll(replacement)}</li>
     * </ul>
     *
     * <p>A {@code null} reference passed to this method is a no-op.</p>
     *
     * <pre>
     * StringUtils.replacePattern(null, *, *)       = null
     * StringUtils.replacePattern("any", (String) null, *)   = "any"
     * StringUtils.replacePattern("any", *, null)   = "any"
     * StringUtils.replacePattern("", "", "zzz")    = "zzz"
     * StringUtils.replacePattern("", ".*", "zzz")  = "zzz"
     * StringUtils.replacePattern("", ".+", "zzz")  = ""
     * StringUtils.replacePattern("&lt;__&gt;\n&lt;__&gt;", "&lt;.*&gt;", "z")       = "z"
     * StringUtils.replacePattern("ABCabc123", "[a-z]", "_")       = "ABC___123"
     * StringUtils.replacePattern("ABCabc123", "[^A-Z0-9]+", "_")  = "ABC_123"
     * StringUtils.replacePattern("ABCabc123", "[^A-Z0-9]+", "")   = "ABC123"
     * StringUtils.replacePattern("Lorem ipsum  dolor   sit", "( +)([a-z]+)", "_$2")  = "Lorem_ipsum_dolor_sit"
     * </pre>
     *
     * @param text        the source string
     * @param regex       the regular expression to which this string is to be matched
     * @param replacement the string to be substituted for each match
     * @return The resulting {@code String}
     * @see #replaceAll(String, String, String)
     * @see String#replaceAll(String, String)
     * @see Pattern#DOTALL
     */
    public static String replacePattern(final String text, final String regex, final String replacement) {
        if (ObjectUtils.anyNull(text, regex, replacement)) {
            return text;
        }
        return Pattern.compile(regex, Pattern.DOTALL).matcher(text).replaceAll(replacement);
    }

    /**
     * 获得匹配的字符串
     *
     * @param regex      匹配的正则
     * @param content    被匹配的内容
     * @param groupIndex 匹配正则的分组序号
     * @return 匹配后得到的字符串，未匹配返回null
     */
    public static String get(String regex, CharSequence content, int groupIndex) throws ExecutionException {
        if (null == content || null == regex) {
            return null;
        }

        final Pattern pattern = PatternPool.get(regex, Pattern.DOTALL);
        return get(pattern, content, groupIndex);
    }

    /**
     * 获得匹配的字符串
     *
     * @param regex     匹配的正则
     * @param content   被匹配的内容
     * @param groupName 匹配正则的分组名称
     * @return 匹配后得到的字符串，未匹配返回null
     */
    public static String get(String regex, CharSequence content, String groupName) throws ExecutionException {
        if (null == content || null == regex) {
            return null;
        }

        final Pattern pattern = PatternPool.get(regex, Pattern.DOTALL);
        return get(pattern, content, groupName);
    }

    /**
     * 获得匹配的字符串，对应分组0表示整个匹配内容，1表示第一个括号分组内容，依次类推
     *
     * @param pattern    编译后的正则模式
     * @param content    被匹配的内容
     * @param groupIndex 匹配正则的分组序号，0表示整个匹配内容，1表示第一个括号分组内容，依次类推
     * @return 匹配后得到的字符串，未匹配返回null
     */
    public static String get(Pattern pattern, CharSequence content, int groupIndex) {
        if (null == content || null == pattern) {
            return null;
        }

        final MutableObject<String> result = new MutableObject<>();
        get(pattern, content, matcher -> result.setValue(matcher.group(groupIndex)));
        return result.getValue();
    }

    /**
     * 获得匹配的字符串
     *
     * @param pattern   匹配的正则
     * @param content   被匹配的内容
     * @param groupName 匹配正则的分组名称
     * @return 匹配后得到的字符串，未匹配返回null
     *
     */
    public static String get(Pattern pattern, CharSequence content, String groupName) {
        if (null == content || null == pattern || null == groupName) {
            return null;
        }

        final MutableObject<String> result = new MutableObject<>();
        get(pattern, content, matcher -> result.setValue(matcher.group(groupName)));
        return result.getValue();
    }

    /**
     * 在给定字符串中查找给定规则的字符，如果找到则使用{@link Consumer}处理之<br> 如果内容中有多个匹配项，则只处理找到的第一个结果。
     *
     * @param pattern  匹配的正则
     * @param content  被匹配的内容
     * @param consumer 匹配到的内容处理器
     *
     */
    public static void get(Pattern pattern, CharSequence content, Consumer<Matcher> consumer) {
        if (null == content || null == pattern || null == consumer) {
            return;
        }
        final Matcher m = pattern.matcher(content);
        if (m.find()) {
            consumer.accept(m);
        }
    }

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

        if (StringUtils.isEmpty(regex)) {
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
     *
     */
    public static boolean isMobile(CharSequence value) {
        return isMatch(PatternPool.MOBILE, value);
    }

    /**
     * 验证是否为座机号码（中国）
     *
     * @param value 值
     * @return 是否为座机号码（中国）
     *
     */
    public static boolean isTel(CharSequence value) {
        return isMatch(PatternPool.TEL, value);
    }

    /**
     * 验证是否为座机号码+手机号码（中国）
     *
     * @param value 值
     * @return 是否为座机号码+手机号码（中国）
     *
     */
    public static boolean isPhone(CharSequence value) {
        return isMobile(value) || isTel(value);
    }

    /**
     * 验证是否为Hex（16进制）字符串
     *
     * @param value 值
     * @return 是否为Hex（16进制）字符串
     *
     */
    public static boolean isHex(CharSequence value) {
        return isMatch(PatternPool.HEX, value);
    }
}
