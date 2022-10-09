package top.lytree.configuration;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.lang.NonNull;


public class CustomNamedParameterJdbcTemplate extends NamedParameterJdbcTemplate {

    public CustomNamedParameterJdbcTemplate(DataSource dataSource) {
        super(dataSource);
    }

    public CustomNamedParameterJdbcTemplate(JdbcOperations classicJdbcTemplate) {
        super(classicJdbcTemplate);
    }


    @Override
    public <T> T queryForObject(@NonNull String sql, @NonNull SqlParameterSource paramSource, @NonNull RowMapper<T> rowMapper) throws DataAccessException {
        List<T> results = getJdbcOperations().query(getPreparedStatementCreator(sql, paramSource), rowMapper);
        return requiredSingleResult(results);
    }

    @Override
    public <T> T queryForObject(@NonNull String sql, @NonNull Map<String, ?> paramMap, @NonNull RowMapper<T> rowMapper) throws DataAccessException {
        return queryForObject(sql, new MapSqlParameterSource(paramMap), rowMapper);
    }

    @Override
    public <T> T queryForObject(@NonNull String sql, @NonNull SqlParameterSource paramSource, @NonNull Class<T> requiredType) throws DataAccessException {
        return queryForObject(sql, paramSource, new SingleColumnRowMapper<>(requiredType));
    }

    @Override
    public <T> T queryForObject(@NonNull String sql, @NonNull Map<String, ?> paramMap, @NonNull Class<T> requiredType) throws DataAccessException {
        return queryForObject(sql, paramMap, new SingleColumnRowMapper<>(requiredType));
    }

    private static <T> T requiredSingleResult(Collection<T> results) throws IncorrectResultSizeDataAccessException {
        int size = (results != null ? results.size() : 0);
        if (size == 0) {
            return null;
        }
        if (results.size() > 1) {
            throw new IncorrectResultSizeDataAccessException(1, size);
        }
        return results.iterator().next();
    }
}
