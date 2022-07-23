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
package top.yang.time;

import top.yang.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * <p>使用{@link区域设置}时的辅助操作。<p>
 * <p>该类试图优雅地处理{@code null}输入。<p>
 * <p> 对于{@code null}输入，不会引发异常。每个方法更详细地记录其行为<p>
 */
public class LocaleUtils {

    // class to avoid synchronization (Init on demand)
    static class SyncAvoid {

        /**
         * 不可修改的可用区域设置列表。
         */
        private static final List<Locale> AVAILABLE_LOCALE_LIST;
        /**
         * 不可修改的可用区域设置集。
         */
        private static final Set<Locale> AVAILABLE_LOCALE_SET;

        static {
            final List<Locale> list = new ArrayList<>(Arrays.asList(Locale.getAvailableLocales()));  // extra safe
            AVAILABLE_LOCALE_LIST = Collections.unmodifiableList(list);
            AVAILABLE_LOCALE_SET = Collections.unmodifiableSet(new HashSet<>(list));
        }
    }

    /**
     * 按国家划分的语言区域的并发地图。
     */
    private static final ConcurrentMap<String, List<Locale>> cLanguagesByCountry =
            new ConcurrentHashMap<>();

    /**
     * 对应地图的国家的地区语言。
     */
    private static final ConcurrentMap<String, List<Locale>> cCountriesByLanguage =
            new ConcurrentHashMap<>();

    /**
     * <p>获取已安装区域设置的不可修改列表。<p>
     * <p>该方法是对{@link Locale#getAvailableLocales()}的包装。 <p>
     * <p> 这样更有效，因为JDK方法每次被调用时都必须创建一个新的数组。<p>
     *
     * @return 不可修改的可用地区列表
     */
    public static List<Locale> availableLocaleList() {
        return SyncAvoid.AVAILABLE_LOCALE_LIST;
    }

    /**
     * <p>获取不可修改的已安装区域设置集</p>
     *
     *
     * <p>他的方法是对{@link Locale#getAvailableLocales()}的包装。<p>
     * <p>这样更有效，因为JDK方法每次被调用时都必须创建一个新的数组<p>
     *
     * @return 不可修改的可用区域设置集
     */
    public static Set<Locale> availableLocaleSet() {
        return SyncAvoid.AVAILABLE_LOCALE_SET;
    }

    /**
     * <p>获取给定语言支持的国家列表。</p>
     *
     * <p>此方法采用一种语言代码，并搜索该语言可用的国家。删除了不同的区域设置。</p>
     *
     * @param languageCode 2个字母的语言代码，null返回空
     * @return 一个不可修改的Locale对象列表，而不是null
     */
    public static List<Locale> countriesByLanguage(final String languageCode) {
        if (languageCode == null) {
            return Collections.emptyList();
        }
        List<Locale> countries = cCountriesByLanguage.get(languageCode);
        if (countries == null) {
            countries = new ArrayList<>();
            final List<Locale> locales = availableLocaleList();
            for (final Locale locale : locales) {
                if (languageCode.equals(locale.getLanguage()) &&
                        !locale.getCountry().isEmpty() &&
                        locale.getVariant().isEmpty()) {
                    countries.add(locale);
                }
            }
            countries = Collections.unmodifiableList(countries);
            cCountriesByLanguage.putIfAbsent(languageCode, countries);
            countries = cCountriesByLanguage.get(languageCode);
        }
        return countries;
    }

    /**
     * <p>检查指定的区域设置是否在可用区域设置列表中。</p>
     *
     * @param locale Locale对象来检查它是否可用
     * @return 如果区域设置是已知的，则为True
     */
    public static boolean isAvailableLocale(final Locale locale) {
        return availableLocaleList().contains(locale);
    }

    /**
     * 检查给定的String是否为ISO 3166 alpha-2国家代码。
     *
     * @param str 要检查的字符串
     * @return true, 字符串是符合ISO 3166的国家代码
     */
    private static boolean isISO3166CountryCode(final String str) {
        return StringUtils.isAllUpperCase(str) && str.length() == 2;
    }

    /**
     * 检查给定的String是否符合ISO 639语言代码。
     *
     * @param str 要检查的字符串
     * @return true, 如果给定的String是符合ISO 639的语言代码。
     */
    private static boolean isISO639LanguageCode(final String str) {
        return StringUtils.isAllLowerCase(str) && (str.length() == 2 || str.length() == 3);
    }

    /**
     * 检查给定的String是否为UN M.49数字区号。
     *
     * @param str 要检查的字符串
     * @return true, 所给的字符串是UN M.49的数字区号.
     */
    private static boolean isNumericAreaCode(final String str) {
        return StringUtils.isNumeric(str) && str.length() == 3;
    }

    /**
     * <p>获取给定国家支持的语言列表。</p>
     *
     * <p>此方法获取一个国家代码，并搜索以查找该国家可用的语言。删除了不同的区域设置。</p>
     *
     * @param countryCode 2个字母的国家代码，null返回空
     * @return 一个不可修改的Locale对象列表，而不是null
     */
    public static List<Locale> languagesByCountry(final String countryCode) {
        if (countryCode == null) {
            return Collections.emptyList();
        }
        List<Locale> langs = cLanguagesByCountry.get(countryCode);
        if (langs == null) {
            langs = new ArrayList<>();
            final List<Locale> locales = availableLocaleList();
            for (final Locale locale : locales) {
                if (countryCode.equals(locale.getCountry()) &&
                        locale.getVariant().isEmpty()) {
                    langs.add(locale);
                }
            }
            langs = Collections.unmodifiableList(langs);
            cLanguagesByCountry.putIfAbsent(countryCode, langs);
            langs = cLanguagesByCountry.get(countryCode);
        }
        return langs;
    }

    /**
     * <p>在执行区域设置搜索时，获取要搜索的区域设置列表。</p>
     *
     * <pre>
     * localeLookupList(Locale("fr", "CA", "xxx"))
     *   = [Locale("fr", "CA", "xxx"), Locale("fr", "CA"), Locale("fr")]
     * </pre>
     *
     * @param locale 从现场开始
     * @return Locale对象的不可修改列表，0表示Locale，而不是null
     */
    public static List<Locale> localeLookupList(final Locale locale) {
        return localeLookupList(locale, locale);
    }

    /**
     * <p>在执行区域设置搜索时，获取要搜索的区域设置列表。</p>
     *
     * <pre>
     * localeLookupList(Locale("fr", "CA", "xxx"), Locale("en"))
     *   = [Locale("fr", "CA", "xxx"), Locale("fr", "CA"), Locale("fr"), Locale("en"]
     * </pre>
     *
     * <p>结果列表从最特定的区域设置开始，然后是更一般的区域设置，以此类推，最后是默认区域设置。该列表不会两次包含相同的区域设置
     *
     * @param locale        要开始的区域设置，null返回空列表
     * @param defaultLocale 如果没有找到其他地区，将使用的默认区域设置
     * @return Locale对象的不可修改列表，0表示Locale，而不是null
     */
    public static List<Locale> localeLookupList(final Locale locale, final Locale defaultLocale) {
        final List<Locale> list = new ArrayList<>(4);
        if (locale != null) {
            list.add(locale);
            if (!locale.getVariant().isEmpty()) {
                list.add(new Locale(locale.getLanguage(), locale.getCountry()));
            }
            if (!locale.getCountry().isEmpty()) {
                list.add(new Locale(locale.getLanguage(), StringUtils.EMPTY));
            }
            if (!list.contains(defaultLocale)) {
                list.add(defaultLocale);
            }
        }
        return Collections.unmodifiableList(list);
    }

    /**
     * 尝试从给定的String解析区域设置。
     *
     * @param str 用于解析区域设置的字符串。
     * @return 从给定String解析的Locale实例。
     * @throws IllegalArgumentException 如果给定的字符串不能被解析。
     */
    private static Locale parseLocale(final String str) {
        if (isISO639LanguageCode(str)) {
            return new Locale(str);
        }

        final String[] segments = str.split("_", -1);
        final String language = segments[0];
        if (segments.length == 2) {
            final String country = segments[1];
            if (isISO639LanguageCode(language) && isISO3166CountryCode(country) ||
                    isNumericAreaCode(country)) {
                return new Locale(language, country);
            }
        } else if (segments.length == 3) {
            final String country = segments[1];
            final String variant = segments[2];
            if (isISO639LanguageCode(language) &&
                    (country.isEmpty() || isISO3166CountryCode(country) || isNumericAreaCode(country)) &&
                    !variant.isEmpty()) {
                return new Locale(language, country, variant);
            }
        }
        throw new IllegalArgumentException("Invalid locale format: " + str);
    }

    /**
     * 如果非{@code null}，则返回给定的区域设置，否则返回{@link Locale#getDefault()}。
     *
     * @param locale 区域设置或{@code null}。
     * @return 如果非{@code null}，则指定区域设置，否则为{@link Locale#getDefault()}。
     */
    public static Locale toLocale(final Locale locale) {
        return locale != null ? locale : Locale.getDefault();
    }

    /**
     * <p>将字符串转换为区域设置。</p>
     *
     * <p>此方法接受区域设置的字符串格式，并从中创建区域设置对象。</p>
     *
     * <pre>
     *   LocaleUtils.toLocale("")           = new Locale("", "")
     *   LocaleUtils.toLocale("en")         = new Locale("en", "")
     *   LocaleUtils.toLocale("en_GB")      = new Locale("en", "GB")
     *   LocaleUtils.toLocale("en_001")     = new Locale("en", "001")
     *   LocaleUtils.toLocale("en_GB_xxx")  = new Locale("en", "GB", "xxx")   (#)
     * </pre>
     *
     * <p>(#) JDK变体构造函数的行为在JDK1.3和JDK1.4之间发生了变化。在JDK1.3中，构造函数大写变体，而在JDK1.4中则不是。因此，getVariant()的结果可能因JDK而异
     * <p>该方法严格验证输入。语言代码必须是小写的。国家代码必须大写。分隔符必须是下划线。长度必须正确。</p>
     *
     * @param str 要转换的locale String, null返回null
     * @return 区域设置，如果输入为空则为空
     * @throws IllegalArgumentException 如果字符串是无效格式
     * @see Locale#forLanguageTag(String)
     */
    public static Locale toLocale(final String str) {
        if (str == null) {
            return null;
        }
        if (str.isEmpty()) { // LANG-941 - JDK 8 introduced an empty locale where all fields are blank
            return new Locale(StringUtils.EMPTY, StringUtils.EMPTY);
        }
        if (str.contains("#")) { // LANG-879 - Cannot handle Java 7 script & extensions
            throw new IllegalArgumentException("Invalid locale format: " + str);
        }
        final int len = str.length();
        if (len < 2) {
            throw new IllegalArgumentException("Invalid locale format: " + str);
        }
        final char ch0 = str.charAt(0);
        if (ch0 == '_') {
            if (len < 3) {
                throw new IllegalArgumentException("Invalid locale format: " + str);
            }
            final char ch1 = str.charAt(1);
            final char ch2 = str.charAt(2);
            if (!Character.isUpperCase(ch1) || !Character.isUpperCase(ch2)) {
                throw new IllegalArgumentException("Invalid locale format: " + str);
            }
            if (len == 3) {
                return new Locale(StringUtils.EMPTY, str.substring(1, 3));
            }
            if (len < 5) {
                throw new IllegalArgumentException("Invalid locale format: " + str);
            }
            if (str.charAt(3) != '_') {
                throw new IllegalArgumentException("Invalid locale format: " + str);
            }
            return new Locale(StringUtils.EMPTY, str.substring(1, 3), str.substring(4));
        }

        return parseLocale(str);
    }

    /**
     * <p>{@code LocaleUtils}不应该在标准编程中构造实例。相反，这个类应该被用作{@code LocaleUtils.toLocale("en_GB");}.</p>
     *
     * <p>这个构造函数是公共的，允许需要JavaBean实例操作的工具。</p>
     */
    public LocaleUtils() {
    }

}
