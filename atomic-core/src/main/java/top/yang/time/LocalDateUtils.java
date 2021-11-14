package top.yang.time;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.TimeZone;
import top.yang.reflect.ObjectUtils;

public class LocalDateUtils {

  private final static String DEFAULT_FORMATTER = DatePattern.NORM_DATE_PATTERN;

  public static String getDefaultFormatter() {
    return DEFAULT_FORMATTER;
  }

  public static Date localDateToDate(LocalDate localDate) {
    ZoneId zone = ZoneId.systemDefault();
    Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
    return Date.from(instant);
  }

  public static String getLocalDate() {
    LocalDate now = LocalDate.now();
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(getDefaultFormatter());
    return dateTimeFormatter.format(now);
  }

  public static String getLocalDate(String dateFormatter) {
    LocalDate now = LocalDate.now();
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateFormatter);
    return dateTimeFormatter.format(now);
  }

  public static String getLocalDate(DateTimeFormatter dateFormatter) {
    LocalDate now = LocalDate.now();
    return dateFormatter.format(now);
  }

  /**
   * {@link Instant}转{@link LocalDate}，使用默认时区
   *
   * @param instant {@link Instant}
   * @return {@link LocalDate}
   */
  public static LocalDate of(Instant instant) {
    return of(instant, ZoneId.systemDefault());
  }

  /**
   * {@link Instant}转{@link LocalDate}，使用UTC时区
   *
   * @param instant {@link Instant}
   * @return {@link LocalDate}
   */
  public static LocalDate ofUTC(Instant instant) {
    return of(instant, ZoneId.of("UTC"));
  }

  /**
   * {@link ZonedDateTime}转{@link LocalDate}
   *
   * @param zonedDateTime {@link ZonedDateTime}
   * @return {@link LocalDate}
   */
  public static LocalDate of(ZonedDateTime zonedDateTime) {
    if (null == zonedDateTime) {
      return null;
    }
    return zonedDateTime.toLocalDate();
  }

  /**
   * {@link Instant}转{@link LocalDate}
   *
   * @param instant {@link Instant}
   * @param zoneId  时区
   * @return {@link LocalDate}
   */
  public static LocalDate of(Instant instant, ZoneId zoneId) {
    if (null == instant) {
      return null;
    }

    return LocalDate.ofInstant(instant, ObjectUtils.getIfNull(zoneId, ZoneId::systemDefault));
  }

  /**
   * {@link Instant}转{@link LocalDate}
   *
   * @param instant  {@link Instant}
   * @param timeZone 时区
   * @return {@link LocalDate}
   */
  public static LocalDate of(Instant instant, TimeZone timeZone) {
    if (null == instant) {
      return null;
    }

    return of(instant, ObjectUtils.getIfNull(timeZone, TimeZone::getDefault).toZoneId());
  }

  /**
   * 毫秒转{@link LocalDate}，使用默认时区
   *
   * <p>注意：此方法使用默认时区，如果非UTC，会产生时间偏移</p>
   *
   * @param epochMilli 从1970-01-01T00:00:00Z开始计数的毫秒数
   * @return {@link LocalDate}
   */
  public static LocalDate of(long epochMilli) {
    return of(Instant.ofEpochMilli(epochMilli));
  }

  /**
   * 毫秒转{@link LocalDate}，使用UTC时区
   *
   * @param epochMilli 从1970-01-01T00:00:00Z开始计数的毫秒数
   * @return {@link LocalDate}
   */
  public static LocalDate ofUTC(long epochMilli) {
    return ofUTC(Instant.ofEpochMilli(epochMilli));
  }

  /**
   * 毫秒转{@link LocalDate}，根据时区不同，结果会产生时间偏移
   *
   * @param epochMilli 从1970-01-01T00:00:00Z开始计数的毫秒数
   * @param zoneId     时区
   * @return {@link LocalDate}
   */
  public static LocalDate of(long epochMilli, ZoneId zoneId) {
    return of(Instant.ofEpochMilli(epochMilli), zoneId);
  }

  /**
   * 毫秒转{@link LocalDate}，结果会产生时间偏移
   *
   * @param epochMilli 从1970-01-01T00:00:00Z开始计数的毫秒数
   * @param timeZone   时区
   * @return {@link LocalDate}
   */
  public static LocalDate of(long epochMilli, TimeZone timeZone) {
    return of(Instant.ofEpochMilli(epochMilli), timeZone);
  }

  /**
   * {@link Date}转{@link LocalDate}，使用默认时区
   *
   * @param date Date对象
   * @return {@link LocalDate}
   */
  public static LocalDate of(Date date) {
    if (null == date) {
      return null;
    }

    return of(date.toInstant());
  }

  /**
   * {@link TemporalAccessor}转{@link LocalDate}，使用默认时区
   *
   * @param temporalAccessor {@link TemporalAccessor}
   * @return {@link LocalDate}
   */
  public static LocalDate of(TemporalAccessor temporalAccessor) {
    if (null == temporalAccessor) {
      return null;
    }

    if (temporalAccessor instanceof LocalDate) {
      return ((LocalDate) temporalAccessor);
    }

    return LocalDate.of(
        TemporalAccessorUtil.get(temporalAccessor, ChronoField.YEAR),
        TemporalAccessorUtil.get(temporalAccessor, ChronoField.MONTH_OF_YEAR),
        TemporalAccessorUtil.get(temporalAccessor, ChronoField.DAY_OF_MONTH)
    );
  }

}
