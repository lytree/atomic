package top.lytree.time;


import java.io.Serial;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import top.lytree.base.Assert;
import top.lytree.bean.ObjectUtils;
import top.lytree.exception.DateException;
import top.lytree.lang.StringUtils;

/**
 * 包装{@link Date}<br>
 * 此类继承了{@link Date}，并提供扩展方法，如时区等。<br>
 * 此类重写了父类的{@code toString()}方法，返回值为"yyyy-MM-dd HH:mm:ss"格式
 *
 * @author xiaoleilu
 */
public class DateTime extends Date {

    @Serial
    private static final long serialVersionUID = -5395712593979185936L;

    private static boolean useJdkToStringStyle = false;

    /**
     * 设置全局的，是否使用{@link Date}默认的toString()格式<br>
     * 如果为{@code true}，则调用toString()时返回"EEE MMM dd HH:mm:ss zzz yyyy"格式，<br>
     * 如果为{@code false}，则返回"yyyy-MM-dd HH:mm:ss"，<br>
     * 默认为{@code false}
     *
     * @param customUseJdkToStringStyle 是否使用{@link Date}默认的toString()格式
     *
     * @since 5.7.21
     */
    public static void setUseJdkToStringStyle(final boolean customUseJdkToStringStyle) {
        useJdkToStringStyle = customUseJdkToStringStyle;
    }

    /**
     * 是否可变对象
     */
    private boolean mutable = true;
    /**
     * 一周的第一天，默认是周一， 在设置或获得 WEEK_OF_MONTH 或 WEEK_OF_YEAR 字段时，Calendar 必须确定一个月或一年的第一个星期，以此作为参考点。
     */
    private Week firstDayOfWeek = Week.MONDAY;
    /**
     * 时区
     */
    private TimeZone timeZone;

    /**
     * 第一周最少天数
     */
    private int minimalDaysInFirstWeek;

    /**
     * 转换时间戳为 DateTime
     *
     * @param timeMillis 时间戳，毫秒数
     *
     * @return DateTime
     *
     * @since 4.6.3
     */
    public static DateTime of(final long timeMillis) {
        return new DateTime(timeMillis);
    }

    /**
     * 转换JDK date为 DateTime
     *
     * @param date JDK Date
     *
     * @return DateTime
     */
    public static DateTime of(final Date date) {
        if (date instanceof DateTime) {
            return (DateTime) date;
        }
        return new DateTime(date);
    }

    /**
     * 转换 {@link Calendar} 为 DateTime
     *
     * @param calendar {@link Calendar}
     *
     * @return DateTime
     */
    public static DateTime of(final Calendar calendar) {
        return new DateTime(calendar);
    }

    /**
     * 构造
     *
     * @param dateStr Date字符串
     * @param format  格式
     *
     * @return this
     *
     * @see DatePattern
     */
    public static DateTime of(final String dateStr, final String format) {
        return new DateTime(dateStr, format);
    }

    /**
     * 现在的时间
     *
     * @return 现在的时间
     */
    public static DateTime now() {
        return new DateTime();
    }

    // -------------------------------------------------------------------- Constructor start

    /**
     * 当前时间
     */
    public DateTime() {
        this(TimeZone.getDefault());
    }

    /**
     * 当前时间
     *
     * @param timeZone 时区
     *
     * @since 4.1.2
     */
    public DateTime(final TimeZone timeZone) {
        this(System.currentTimeMillis(), timeZone);
    }

    /**
     * 给定日期的构造
     *
     * @param date 日期
     */
    public DateTime(final Date date) {
        this(
                date,//
                (date instanceof DateTime) ? ((DateTime) date).timeZone : TimeZone.getDefault()
        );
    }

    /**
     * 给定日期的构造
     *
     * @param date     日期
     * @param timeZone 时区
     *
     * @since 4.1.2
     */
    public DateTime(final Date date, final TimeZone timeZone) {
        this(ObjectUtils.defaultIfNull(date, Date::new).getTime(), timeZone);
    }

    /**
     * 给定日期的构造
     *
     * @param calendar {@link Calendar}
     */
    public DateTime(final Calendar calendar) {
        this(calendar.getTime(), calendar.getTimeZone());
        this.setFirstDayOfWeek(Week.of(calendar.getFirstDayOfWeek()));
    }

    /**
     * 给定日期Instant的构造
     *
     * @param instant {@link Instant} 对象
     *
     * @since 5.0.0
     */
    public DateTime(final Instant instant) {
        this(instant.toEpochMilli());
    }

    /**
     * 给定日期Instant的构造
     *
     * @param instant {@link Instant} 对象
     * @param zoneId  时区ID
     *
     * @since 5.0.5
     */
    public DateTime(final Instant instant, final ZoneId zoneId) {
        this(instant.toEpochMilli(), TimeZones.toTimeZone(zoneId));
    }

    /**
     * 给定日期TemporalAccessor的构造
     *
     * @param temporalAccessor {@link TemporalAccessor} 对象
     *
     * @since 5.0.0
     */
    public DateTime(final TemporalAccessor temporalAccessor) {
        this(TemporalAccessorUtil.toInstant(temporalAccessor));
    }

    /**
     * 给定日期ZonedDateTime的构造
     *
     * @param zonedDateTime {@link ZonedDateTime} 对象
     *
     * @since 5.0.5
     */
    public DateTime(final ZonedDateTime zonedDateTime) {
        this(zonedDateTime.toInstant(), zonedDateTime.getZone());
    }

    /**
     * 给定日期毫秒数的构造
     *
     * @param timeMillis 日期毫秒数
     *
     * @since 4.1.2
     */
    public DateTime(final long timeMillis) {
        this(timeMillis, TimeZone.getDefault());
    }

    /**
     * 给定日期毫秒数的构造
     *
     * @param timeMillis 日期毫秒数
     * @param timeZone   时区
     *
     * @since 4.1.2
     */
    public DateTime(final long timeMillis, final TimeZone timeZone) {
        super(timeMillis);
        this.timeZone = ObjectUtils.defaultIfNull(timeZone, TimeZone::getDefault);
    }

    /**
     * 构造
     *
     * @param dateStr Date字符串
     * @param format  格式
     *
     * @see DatePattern
     */
    public DateTime(final CharSequence dateStr, final String format) {
        this(parse(dateStr, DateUtils.newSimpleFormat(format)));
    }

    /**
     * 构造
     *
     * @param dateStr    Date字符串
     * @param dateFormat 格式化器 {@link SimpleDateFormat}
     *
     * @see DatePattern
     */
    public DateTime(final CharSequence dateStr, final DateFormat dateFormat) {
        this(parse(dateStr, dateFormat), dateFormat.getTimeZone());
    }

    /**
     * 构建DateTime对象
     *
     * @param dateStr   Date字符串
     * @param formatter 格式化器,{@link DateTimeFormatter}
     *
     * @since 5.0.0
     */
    public DateTime(final CharSequence dateStr, final DateTimeFormatter formatter) {
        this(TemporalAccessorUtil.toInstant(formatter.parse(dateStr)), formatter.getZone());
    }

    // -------------------------------------------------------------------- Constructor end

    // -------------------------------------------------------------------- offset start

    /**
     * 调整日期和时间<br>
     * 如果此对象为可变对象，返回自身，否则返回新对象，设置是否可变对象见{@link #setMutable(boolean)}
     *
     * @param datePart 调整的部分 {@link DateField}
     * @param offset   偏移量，正数为向后偏移，负数为向前偏移
     *
     * @return 如果此对象为可变对象，返回自身，否则返回新对象
     */
    public DateTime offset(final DateField datePart, final int offset) {
        if (DateField.ERA == datePart) {
            throw new IllegalArgumentException("ERA is not support offset!");
        }

        final Calendar cal = toCalendar();
        //noinspection MagicConstant
        cal.add(datePart.getValue(), offset);

        final DateTime dt = mutable ? this : ObjectUtils.clone(this);
        return dt.setTimeInternal(cal.getTimeInMillis());
    }

    /**
     * 调整日期和时间<br>
     * 返回调整后的新DateTime，不影响原对象
     *
     * @param datePart 调整的部分 {@link DateField}
     * @param offset   偏移量，正数为向后偏移，负数为向前偏移
     *
     * @return 如果此对象为可变对象，返回自身，否则返回新对象
     *
     * @since 3.0.9
     */
    public DateTime offsetNew(final DateField datePart, final int offset) {
        final Calendar cal = toCalendar();
        //noinspection MagicConstant
        cal.add(datePart.getValue(), offset);

        return ObjectUtils.clone(this).setTimeInternal(cal.getTimeInMillis());
    }
    // -------------------------------------------------------------------- offset end

    // -------------------------------------------------------------------- Part of Date start

    /**
     * 获得日期的某个部分<br>
     * 例如获得年的部分，则使用 getField(DatePart.YEAR)
     *
     * @param field 表示日期的哪个部分的枚举 {@link DateField}
     *
     * @return 某个部分的值
     */
    public int getField(final DateField field) {
        return getField(field.getValue());
    }

    /**
     * 获得日期的某个部分<br>
     * 例如获得年的部分，则使用 getField(Calendar.YEAR)
     *
     * @param field 表示日期的哪个部分的int值 {@link Calendar}
     *
     * @return 某个部分的值
     */
    public int getField(final int field) {
        return toCalendar().get(field);
    }

    /**
     * 设置日期的某个部分<br>
     * 如果此对象为可变对象，返回自身，否则返回新对象，设置是否可变对象见{@link #setMutable(boolean)}
     *
     * @param field 表示日期的哪个部分的枚举 {@link DateField}
     * @param value 值
     *
     * @return this
     */
    public DateTime setField(final DateField field, final int value) {
        return setField(field.getValue(), value);
    }

    /**
     * 设置日期的某个部分<br>
     * 如果此对象为可变对象，返回自身，否则返回新对象，设置是否可变对象见{@link #setMutable(boolean)}
     *
     * @param field 表示日期的哪个部分的int值 {@link Calendar}
     * @param value 值
     *
     * @return this
     */
    public DateTime setField(final int field, final int value) {
        final Calendar calendar = toCalendar();
        calendar.set(field, value);

        DateTime dt = this;
        if (false == mutable) {
            dt = ObjectUtils.clone(this);
        }
        return dt.setTimeInternal(calendar.getTimeInMillis());
    }

    @Override
    public void setTime(final long time) {
        if (mutable) {
            super.setTime(time);
        } else {
            throw new DateException("This is not a mutable object !");
        }
    }

    /**
     * 获得年的部分
     *
     * @return 年的部分
     */
    public int year() {
        return getField(DateField.YEAR);
    }

    /**
     * 获得当前日期所属季度，从1开始计数<br>
     *
     * @return 第几个季度 {@link Quarter}
     */
    public int quarter() {
        return month() / 3 + 1;
    }

    /**
     * 获得当前日期所属季度<br>
     *
     * @return 第几个季度 {@link Quarter}
     */
    public Quarter quarterEnum() {
        return Quarter.of(quarter());
    }

    /**
     * 获得月份，从0开始计数
     *
     * @return 月份
     */
    public int month() {
        return getField(DateField.MONTH);
    }

    /**
     * 获取月，从1开始计数
     *
     * @return 月份，1表示一月
     *
     * @since 5.4.1
     */
    public int monthBaseOne() {
        return month() + 1;
    }

    /**
     * 获得月份，从1开始计数<br>
     * 由于{@link Calendar} 中的月份按照0开始计数，导致某些需求容易误解，因此如果想用1表示一月，2表示二月则调用此方法
     *
     * @return 月份
     */
    public int monthStartFromOne() {
        return month() + 1;
    }

    /**
     * 获得月份
     *
     * @return {@link Month}
     */
    public Month monthEnum() {
        return Month.of(month());
    }

    /**
     * 获得指定日期是所在年份的第几周<br>
     * 此方法返回值与一周的第一天有关，比如：<br>
     * 2016年1月3日为周日，如果一周的第一天为周日，那这天是第二周（返回2）<br>
     * 如果一周的第一天为周一，那这天是第一周（返回1）<br>
     * 跨年的那个星期得到的结果总是1
     *
     * @return 周
     *
     * @see #setFirstDayOfWeek(Week)
     */
    public int weekOfYear() {
        return getField(DateField.WEEK_OF_YEAR);
    }

    /**
     * 获得指定日期是所在月份的第几周<br>
     * 此方法返回值与一周的第一天有关，比如：<br>
     * 2016年1月3日为周日，如果一周的第一天为周日，那这天是第二周（返回2）<br>
     * 如果一周的第一天为周一，那这天是第一周（返回1）
     *
     * @return 周
     *
     * @see #setFirstDayOfWeek(Week)
     */
    public int weekOfMonth() {
        return getField(DateField.WEEK_OF_MONTH);
    }

    /**
     * 获得指定日期是这个日期所在月份的第几天，从1开始
     *
     * @return 天，1表示第一天
     */
    public int dayOfMonth() {
        return getField(DateField.DAY_OF_MONTH);
    }

    /**
     * 获得指定日期是这个日期所在年份的第几天，从1开始
     *
     * @return 天，1表示第一天
     *
     * @since 5.3.6
     */
    public int dayOfYear() {
        return getField(DateField.DAY_OF_YEAR);
    }

    /**
     * 获得指定日期是星期几，1表示周日，2表示周一
     *
     * @return 星期几
     */
    public int dayOfWeek() {
        return getField(DateField.DAY_OF_WEEK);
    }

    /**
     * 获得天所在的周是这个月的第几周
     *
     * @return 天
     */
    public int dayOfWeekInMonth() {
        return getField(DateField.DAY_OF_WEEK_IN_MONTH);
    }

    /**
     * 获得指定日期是星期几
     *
     * @return {@link Week}
     */
    public Week dayOfWeekEnum() {
        return Week.of(dayOfWeek());
    }

    /**
     * 获得指定日期的小时数部分<br>
     *
     * @param is24HourClock 是否24小时制
     *
     * @return 小时数
     */
    public int hour(final boolean is24HourClock) {
        return getField(is24HourClock ? DateField.HOUR_OF_DAY : DateField.HOUR);
    }

    /**
     * 获得指定日期的分钟数部分<br>
     * 例如：10:04:15.250 =》 4
     *
     * @return 分钟数
     */
    public int minute() {
        return getField(DateField.MINUTE);
    }

    /**
     * 获得指定日期的秒数部分<br>
     *
     * @return 秒数
     */
    public int second() {
        return getField(DateField.SECOND);
    }

    /**
     * 获得指定日期的毫秒数部分<br>
     *
     * @return 毫秒数
     */
    public int millisecond() {
        return getField(DateField.MILLISECOND);
    }

    /**
     * 是否为上午
     *
     * @return 是否为上午
     */
    public boolean isAM() {
        return Calendar.AM == getField(DateField.AM_PM);
    }

    /**
     * 是否为下午
     *
     * @return 是否为下午
     */
    public boolean isPM() {
        return Calendar.PM == getField(DateField.AM_PM);
    }

    /**
     * 是否为周末，周末指周六或者周日
     *
     * @return 是否为周末，周末指周六或者周日
     *
     * @since 4.1.14
     */
    public boolean isWeekend() {
        final int dayOfWeek = dayOfWeek();
        return Calendar.SATURDAY == dayOfWeek || Calendar.SUNDAY == dayOfWeek;
    }
    // -------------------------------------------------------------------- Part of Date end


    /**
     * 转换为Calendar, 默认 {@link Locale}
     *
     * @return {@link Calendar}
     */
    public Calendar toCalendar() {
        return toCalendar(Locale.getDefault(Locale.Category.FORMAT));
    }

    /**
     * 转换为Calendar
     *
     * @param locale 地域 {@link Locale}
     *
     * @return {@link Calendar}
     */
    public Calendar toCalendar(final Locale locale) {
        return toCalendar(this.timeZone, locale);
    }

    /**
     * 转换为Calendar
     *
     * @param zone 时区 {@link TimeZone}
     *
     * @return {@link Calendar}
     */
    public Calendar toCalendar(final TimeZone zone) {
        return toCalendar(zone, Locale.getDefault(Locale.Category.FORMAT));
    }

    /**
     * 转换为Calendar
     *
     * @param zone   时区 {@link TimeZone}
     * @param locale 地域 {@link Locale}
     *
     * @return {@link Calendar}
     */
    public Calendar toCalendar(final TimeZone zone, Locale locale) {
        if (null == locale) {
            locale = Locale.getDefault(Locale.Category.FORMAT);
        }
        final Calendar cal = (null != zone) ? Calendar.getInstance(zone, locale) : Calendar.getInstance(locale);
        //noinspection MagicConstant
        cal.setFirstDayOfWeek(firstDayOfWeek.getValue());
        // issue#1988@Github
        if (minimalDaysInFirstWeek > 0) {
            cal.setMinimalDaysInFirstWeek(minimalDaysInFirstWeek);
        }
        cal.setTime(this);
        return cal;
    }

    /**
     * 转换为 {@link Date}<br>
     * 考虑到很多框架（例如Hibernate）的兼容性，提供此方法返回JDK原生的Date对象
     *
     * @return {@link Date}
     *
     * @since 3.2.2
     */
    public Date toJdkDate() {
        return new Date(this.getTime());
    }

    /**
     * 转为{@link Timestamp}
     *
     * @return {@link Timestamp}
     */
    public Timestamp toTimestamp() {
        return new Timestamp(this.getTime());
    }

    /**
     * 转为 {@link java.sql.Date}
     *
     * @return {@link java.sql.Date}
     */
    public java.sql.Date toSqlDate() {
        return new java.sql.Date(getTime());
    }

    /**
     * 转换为 {@link LocalDateTime}
     *
     * @return {@link LocalDateTime}
     *
     * @since 5.7.16
     */
    public LocalDateTime toLocalDateTime() {
        return LocalDateTimeUtils.of(this);
    }


    /**
     * 计算相差时长
     *
     * @param date 对比的日期
     * @param unit 单位 {@link TimeUnit}
     *
     * @return 相差时长
     */
    public long between(final Date date, final TimeUnit unit) {
        if (date.after(this)) {
            final long diff = date.getTime() - this.getTime();
            return unit.toMillis(diff);
        } else {
            final long diff = this.getTime() - date.getTime();
            return unit.toMillis(diff);
        }

    }


    /**
     * 当前日期是否在日期指定范围内<br>
     * 起始日期和结束日期可以互换
     *
     * @param beginDate 起始日期（包含）
     * @param endDate   结束日期（包含）
     *
     * @return 是否在范围内
     *
     * @since 3.0.8
     */
    public boolean isIn(final Date beginDate, final Date endDate) {
        return DateUtils.isIn(this, beginDate, endDate);
    }

    /**
     * 是否在给定日期之前
     *
     * @param date 日期
     *
     * @return 是否在给定日期之前
     *
     * @since 4.1.3
     */
    public boolean isBefore(final Date date) {
        if (null == date) {
            throw new NullPointerException("Date to compare is null !");
        }
        return compareTo(date) < 0;
    }

    /**
     * 是否在给定日期之前或与给定日期相等
     *
     * @param date 日期
     *
     * @return 是否在给定日期之前或与给定日期相等
     *
     * @since 3.0.9
     */
    public boolean isBeforeOrEquals(final Date date) {
        if (null == date) {
            throw new NullPointerException("Date to compare is null !");
        }
        return compareTo(date) <= 0;
    }

    /**
     * 是否在给定日期之后
     *
     * @param date 日期
     *
     * @return 是否在给定日期之后
     *
     * @since 4.1.3
     */
    public boolean isAfter(final Date date) {
        if (null == date) {
            throw new NullPointerException("Date to compare is null !");
        }
        return compareTo(date) > 0;
    }

    /**
     * 是否在给定日期之后或与给定日期相等
     *
     * @param date 日期
     *
     * @return 是否在给定日期之后或与给定日期相等
     *
     * @since 3.0.9
     */
    public boolean isAfterOrEquals(final Date date) {
        if (null == date) {
            throw new NullPointerException("Date to compare is null !");
        }
        return compareTo(date) >= 0;
    }

    /**
     * 对象是否可变<br>
     * 如果为不可变对象，以下方法将返回新方法：
     * <ul>
     * <li>{@link DateTime#offset(DateField, int)}</li>
     * <li>{@link DateTime#setField(DateField, int)}</li>
     * <li>{@link DateTime#setField(int, int)}</li>
     * </ul>
     * 如果为不可变对象，{@link DateTime#setTime(long)}将抛出异常
     *
     * @return 对象是否可变
     */
    public boolean isMutable() {
        return mutable;
    }

    /**
     * 设置对象是否可变 如果为不可变对象，以下方法将返回新方法：
     * <ul>
     * <li>{@link DateTime#offset(DateField, int)}</li>
     * <li>{@link DateTime#setField(DateField, int)}</li>
     * <li>{@link DateTime#setField(int, int)}</li>
     * </ul>
     * 如果为不可变对象，{@link DateTime#setTime(long)}将抛出异常
     *
     * @param mutable 是否可变
     *
     * @return this
     */
    public DateTime setMutable(final boolean mutable) {
        this.mutable = mutable;
        return this;
    }

    /**
     * 获得一周的第一天，默认为周一
     *
     * @return 一周的第一天
     */
    public Week getFirstDayOfWeek() {
        return firstDayOfWeek;
    }

    /**
     * 设置一周的第一天<br>
     * JDK的Calendar中默认一周的第一天是周日，Hutool中将此默认值设置为周一<br>
     * 设置一周的第一天主要影响{@link #weekOfMonth()}和{@link #weekOfYear()} 两个方法
     *
     * @param firstDayOfWeek 一周的第一天
     *
     * @return this
     *
     * @see #weekOfMonth()
     * @see #weekOfYear()
     */
    public DateTime setFirstDayOfWeek(final Week firstDayOfWeek) {
        this.firstDayOfWeek = firstDayOfWeek;
        return this;
    }

    /**
     * 获取时区
     *
     * @return 时区
     *
     * @since 5.0.5
     */
    public TimeZone getTimeZone() {
        return this.timeZone;
    }

    /**
     * 获取时区ID
     *
     * @return 时区ID
     *
     * @since 5.0.5
     */
    public ZoneId getZoneId() {
        return this.timeZone.toZoneId();
    }

    /**
     * 设置时区
     *
     * @param timeZone 时区
     *
     * @return this
     *
     * @since 4.1.2
     */
    public DateTime setTimeZone(final TimeZone timeZone) {
        this.timeZone = ObjectUtils.defaultIfNull(timeZone, TimeZone::getDefault);
        return this;
    }

    /**
     * 设置第一周最少天数
     *
     * @param minimalDaysInFirstWeek 第一周最少天数
     *
     * @return this
     *
     * @since 5.7.17
     */
    public DateTime setMinimalDaysInFirstWeek(final int minimalDaysInFirstWeek) {
        this.minimalDaysInFirstWeek = minimalDaysInFirstWeek;
        return this;
    }

    // -------------------------------------------------------------------- toString start

    /**
     * 转为字符串，如果时区被设置，会转换为其时区对应的时间，否则转换为当前地点对应的时区<br>
     * 可以调用{@link DateTime#setUseJdkToStringStyle(boolean)} 方法自定义默认的风格<br>
     * 如果{@link #useJdkToStringStyle}为{@code true}，返回"EEE MMM dd HH:mm:ss zzz yyyy"格式，<br>
     * 如果为{@code false}，则返回"yyyy-MM-dd HH:mm:ss"
     *
     * @return 格式字符串
     */
    @Override
    public String toString() {
        if (useJdkToStringStyle) {
            return super.toString();
        }
        return toString(this.timeZone);
    }

    /**
     * 转为"yyyy-MM-dd HH:mm:ss" 格式字符串<br>
     * 时区使用当前地区的默认时区
     *
     * @return "yyyy-MM-dd HH:mm:ss" 格式字符串
     *
     * @since 4.1.14
     */
    public String toStringDefaultTimeZone() {
        return toString(TimeZone.getDefault());
    }

    /**
     * 转为"yyyy-MM-dd HH:mm:ss" 格式字符串<br>
     * 如果时区不为{@code null}，会转换为其时区对应的时间，否则转换为当前时间对应的时区
     *
     * @param timeZone 时区
     *
     * @return "yyyy-MM-dd HH:mm:ss" 格式字符串
     *
     * @since 4.1.14
     */
    public String toString(final TimeZone timeZone) {
        if (null != timeZone) {
            return toString(DateUtils.newSimpleFormat(DatePattern.NORM_DATETIME_PATTERN, null, timeZone));
        }
        return toString(DatePattern.NORM_DATETIME_PATTERN);
    }

    /**
     * 转为"yyyy-MM-dd" 格式字符串
     *
     * @return "yyyy-MM-dd" 格式字符串
     *
     * @since 4.0.0
     */
    public String toDateStr() {
        if (null != this.timeZone) {
            return toString(DateUtils.newSimpleFormat(DatePattern.NORM_DATE_PATTERN, null, timeZone));
        }
        return toString(DatePattern.NORM_DATE_PATTERN);
    }

    /**
     * 转为"HH:mm:ss" 格式字符串
     *
     * @return "HH:mm:ss" 格式字符串
     *
     * @since 4.1.4
     */
    public String toTimeStr() {
        if (null != this.timeZone) {
            return toString(DateUtils.newSimpleFormat(DatePattern.NORM_TIME_PATTERN, null, timeZone));
        }
        return toString(DatePattern.NORM_TIME_PATTERN);
    }

    /**
     * 转为字符串
     *
     * @param format 日期格式，常用格式见： {@link DatePattern}
     *
     * @return String
     */
    public String toString(final String format) {
        if (null != this.timeZone) {
            return toString(DateUtils.newSimpleFormat(format, null, timeZone));
        }
        return toString(FastDateFormat.getInstance(format));
    }

    /**
     * 转为字符串
     *
     * @param format {@link DatePrinter}
     *
     * @return String
     */
    public String toString(final DatePrinter format) {
        return format.format(this);
    }

    /**
     * 转为字符串
     *
     * @param format {@link SimpleDateFormat}
     *
     * @return String
     */
    public String toString(final DateFormat format) {
        return format.format(this);
    }

    /**
     * @return 输出精确到毫秒的标准日期形式
     */
    public String toMsStr() {
        return toString(DatePattern.NORM_DATETIME_MS_PATTERN);
    }
    // -------------------------------------------------------------------- toString end

    /**
     * 转换字符串为Date
     *
     * @param dateStr    日期字符串
     * @param dateFormat {@link SimpleDateFormat}
     *
     * @return {@link Date}
     */
    private static Date parse(final CharSequence dateStr, final DateFormat dateFormat) {
        Assert.notBlank(dateStr, "Date String must be not blank !");
        try {
            return dateFormat.parse(dateStr.toString());
        } catch (final Exception e) {
            final String pattern;
            if (dateFormat instanceof SimpleDateFormat) {
                pattern = ((SimpleDateFormat) dateFormat).toPattern();
            } else {
                pattern = dateFormat.toString();
            }
            throw new DateException(StringUtils.format("Parse [{}] with format [{}] error!", dateStr, pattern), e);
        }
    }


    /**
     * 设置日期时间
     *
     * @param time 日期时间毫秒
     *
     * @return this
     */
    private DateTime setTimeInternal(final long time) {
        super.setTime(time);
        return this;
    }
}
