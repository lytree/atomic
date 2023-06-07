package top.lytree.configuration;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.jdbc.core.mapping.JdbcValue;
import org.springframework.lang.NonNull;

import java.sql.JDBCType;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import static java.time.ZoneId.systemDefault;

public class JdbcSqliteDialect extends SqliteDialect {
    public static final JdbcSqliteDialect INSTANCE = new JdbcSqliteDialect();

    @Override
    public Collection<Object> getConverters() {

        ArrayList<Object> converters = new ArrayList<>(super.getConverters());
        converters.add(OffsetDateTimeToTimestampJdbcValueConverter.INSTANCE);
        converters.add(LocalDateTimeToDateConverter.INSTANCE);

        return converters;
    }

    @WritingConverter
    enum OffsetDateTimeToTimestampJdbcValueConverter implements Converter<OffsetDateTime, JdbcValue> {

        INSTANCE;

        @Override
        public JdbcValue convert(OffsetDateTime source) {
            return JdbcValue.of(source, JDBCType.TIMESTAMP);
        }
    }

    @ReadingConverter
    enum LocalDateTimeToDateConverter implements Converter<LocalDateTime, Date> {

        INSTANCE;

        @NonNull
        @Override
        public Date convert(LocalDateTime source) {
            return Date.from(source.atZone(systemDefault()).toInstant());
        }
    }
}
