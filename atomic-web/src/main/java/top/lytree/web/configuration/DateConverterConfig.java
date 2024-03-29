package top.lytree.web.configuration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.core.convert.converter.Converter;

/**
 * @author pride
 */
public class DateConverterConfig implements Converter<String, LocalDateTime> {

    private static final List<String> formarts = new ArrayList<>(4);

    static {
        formarts.add("yyyy-MM");
        formarts.add("yyyy-MM-dd");
        formarts.add("yyyy-MM-dd HH:mm");
        formarts.add("yyyy-MM-dd HH:mm:ss");
        formarts.add("yyyy-MM-dd'T'HH:mm:ss");
        formarts.add("yyyy-MM-dd'T'HH:mm:ss'Z'");
    }


    @Override
    public LocalDateTime convert(String source) {
        String value = source.trim();
        if ("".equals(value)) {
            return null;
        }
        if (source.matches("^\\d{4}-\\d{1,2}$")) {
            return parseDate(source, formarts.get(0));
        } else if (source.matches("^\\d{4}-\\d{1,2}-\\d{1,2}$")) {
            return parseDate(source, formarts.get(1));
        } else if (source.matches("^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}$")) {
            return parseDate(source, formarts.get(2));
        } else if (source.matches("^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}:\\d{1,2}$")) {
            return parseDate(source, formarts.get(3));
        } else if (source.matches("^\\d{4}-\\d{1,2}-\\d{1,2}'T'{1}\\d{1,2}:\\d{1,2}:\\d{1,2}$")) {
            return parseDate(source, formarts.get(4));
        } else if (source.matches("^\\d{4}-\\d{1,2}-\\d{1,2}'T'{1}\\d{1,2}:\\d{1,2}:\\d{1,2}'Z'$")) {
            return parseDate(source, formarts.get(4));
        } else {
            throw new IllegalArgumentException("Invalid boolean value '" + source + "'");
        }
    }

    /**
     * 格式化日期
     *
     * @param dateStr String 字符型日期
     * @param format  String 格式
     * @return Date 日期
     */
    public LocalDateTime parseDate(String dateStr, String format) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);
        return LocalDateTime.parse(dateStr, dateTimeFormatter);
    }
}
