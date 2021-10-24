package top.yang.time;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class LocalDateUtils {

  private final static String DEFAULT_FORMATTER = DatePattern.NORM_DATE_PATTERN;

  public static String getDefaultFormatter() {
    return DEFAULT_FORMATTER;
  }

  public static Date localDateTimeToDate(LocalDateTime localDateTime) {
    ZoneId zone = ZoneId.systemDefault();
    Instant instant = localDateTime.atZone(zone).toInstant();
    return Date.from(instant);
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
}
