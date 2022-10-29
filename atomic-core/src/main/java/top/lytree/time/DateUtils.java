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
package top.lytree.time;


import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import javax.xml.datatype.XMLGregorianCalendar;


public class DateUtils extends org.apache.commons.lang3.time.DateUtils {

    /**
     * {@link Date}类型时间转为{@link DateTime}<br>
     * 如果date本身为DateTime对象，则返回强转后的对象，否则新建一个DateTime对象
     *
     * @param date {@link Date}
     *
     * @return 时间对象
     *
     * @since 3.0.7
     */
    public static DateTime date(final Date date) {
        if (date instanceof DateTime) {
            return (DateTime) date;
        }
        return dateNew(date);
    }


    /**
     * 根据已有{@link Date} 产生新的{@link DateTime}对象
     *
     * @param date Date对象
     *
     * @return {@link DateTime}对象
     *
     * @since 4.3.1
     */
    public static DateTime dateNew(final Date date) {
        return new DateTime(date);
    }

    /**
     * 根据已有{@link Date} 产生新的{@link DateTime}对象，并根据指定时区转换
     *
     * @param date     Date对象
     * @param timeZone 时区
     *
     * @return {@link DateTime}对象
     */
    public static DateTime date(final Date date, final TimeZone timeZone) {
        return new DateTime(date, timeZone);
    }

    /**
     * Long类型时间转为{@link DateTime}<br>
     * 只支持毫秒级别时间戳，如果需要秒级别时间戳，请自行×1000L
     *
     * @param date Long类型Date（Unix时间戳）
     *
     * @return 时间对象
     */
    public static DateTime date(final long date) {
        return new DateTime(date);
    }

    /**
     * {@link Calendar}类型时间转为{@link DateTime}<br>
     * 始终根据已有{@link Calendar} 产生新的{@link DateTime}对象
     *
     * @param calendar {@link Calendar}
     *
     * @return 时间对象
     */
    public static DateTime date(final Calendar calendar) {
        return new DateTime(calendar);
    }

    /**
     * {@link TemporalAccessor}类型时间转为{@link DateTime}<br>
     * 始终根据已有{@link TemporalAccessor} 产生新的{@link DateTime}对象
     *
     * @param temporalAccessor {@link TemporalAccessor},常用子类： {@link LocalDateTime}、 LocalDate
     *
     * @return 时间对象
     *
     * @since 5.0.0
     */
    public static DateTime date(final TemporalAccessor temporalAccessor) {
        return new DateTime(temporalAccessor);
    }

    /**
     * 当前时间的时间戳
     *
     * @return 时间
     */
    public static long current() {
        return System.currentTimeMillis();
    }

    /**
     * 当前时间的时间戳（秒）
     *
     * @return 当前时间秒数
     */
    public static long currentSeconds() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * Calls {@link System#nanoTime()}.
     */
    @SuppressWarnings("GoodTime") // reading system time without TimeSource
    public static long systemNanoTime() {
        return System.nanoTime();
    }

    /**
     * 创建{@link SimpleDateFormat}，注意此对象非线程安全！<br>
     * 此对象默认为严格格式模式，即parse时如果格式不正确会报错。
     *
     * @param pattern 表达式
     *
     * @return {@link SimpleDateFormat}
     *
     * @since 5.5.5
     */
    public static SimpleDateFormat newSimpleFormat(final String pattern) {
        return newSimpleFormat(pattern, null, null);
    }

    /**
     * 创建{@link SimpleDateFormat}，注意此对象非线程安全！<br>
     * 此对象默认为严格格式模式，即parse时如果格式不正确会报错。
     *
     * @param pattern  表达式
     * @param locale   {@link Locale}，{@code null}表示默认
     * @param timeZone {@link TimeZone}，{@code null}表示默认
     *
     * @return {@link SimpleDateFormat}
     *
     * @since 5.5.5
     */
    public static SimpleDateFormat newSimpleFormat(final String pattern, Locale locale, final TimeZone timeZone) {
        if (null == locale) {
            locale = Locale.getDefault(Locale.Category.FORMAT);
        }
        final SimpleDateFormat format = new SimpleDateFormat(pattern, locale);
        if (null != timeZone) {
            format.setTimeZone(timeZone);
        }
        format.setLenient(false);
        return format;
    }

    /**
     * 当前日期是否在日期指定范围内<br>
     * 起始日期和结束日期可以互换
     *
     * @param date      被检查的日期
     * @param beginDate 起始日期（包含）
     * @param endDate   结束日期（包含）
     *
     * @return 是否在范围内
     *
     * @since 3.0.8
     */
    public static boolean isIn(final Date date, final Date beginDate, final Date endDate) {
        return isIn(date, beginDate, endDate, true, true);
    }

    /**
     * 当前日期是否在日期指定范围内<br>
     * 起始日期和结束日期可以互换<br>
     * 通过includeBegin, includeEnd参数控制日期范围区间是否为开区间，例如：传入参数：includeBegin=true, includeEnd=false，
     * 则本方法会判断 date ∈ (beginDate, endDate] 是否成立
     *
     * @param date         被检查的日期
     * @param beginDate    起始日期
     * @param endDate      结束日期
     * @param includeBegin 时间范围是否包含起始日期
     * @param includeEnd   时间范围是否包含结束日期
     *
     * @return 是否在范围内
     *
     * @author FengBaoheng
     * @since 5.8.6
     */
    public static boolean isIn(final Date date, final Date beginDate, final Date endDate,
            final boolean includeBegin, final boolean includeEnd) {
        if (date == null || beginDate == null || endDate == null) {
            throw new IllegalArgumentException("参数不可为null");
        }

        final long thisMills = date.getTime();
        final long beginMills = beginDate.getTime();
        final long endMills = endDate.getTime();
        final long rangeMin = Math.min(beginMills, endMills);
        final long rangeMax = Math.max(beginMills, endMills);

        // 先判断是否满足 date ∈ (beginDate, endDate)
        boolean isIn = rangeMin < thisMills && thisMills < rangeMax;

        // 若不满足，则再判断是否在时间范围的边界上
        if (false == isIn && includeBegin) {
            isIn = thisMills == rangeMin;
        }

        if (false == isIn && includeEnd) {
            isIn = thisMills == rangeMax;
        }

        return isIn;
    }

}
