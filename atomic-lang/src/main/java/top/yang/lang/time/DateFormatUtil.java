package top.yang.lang.time;

import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;

public class DateFormatUtil extends DateFormatUtils {

    public static String format(final Date date) {
        return format(date, DatePattern.NORM_DATETIME_PATTERN, null, null);
    }
}
