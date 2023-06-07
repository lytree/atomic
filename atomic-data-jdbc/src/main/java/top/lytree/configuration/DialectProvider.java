package top.lytree.configuration;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;


import javax.sql.DataSource;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.data.jdbc.core.dialect.JdbcDb2Dialect;
import org.springframework.data.jdbc.core.dialect.JdbcMySqlDialect;
import org.springframework.data.jdbc.core.dialect.JdbcPostgresDialect;
import org.springframework.data.jdbc.core.dialect.JdbcSqlServerDialect;
import org.springframework.data.jdbc.repository.config.DialectResolver;
import org.springframework.data.relational.core.dialect.Dialect;
import org.springframework.data.relational.core.dialect.H2Dialect;
import org.springframework.data.relational.core.dialect.HsqlDbDialect;
import org.springframework.data.relational.core.dialect.MariaDbDialect;
import org.springframework.data.relational.core.dialect.OracleDialect;
import org.springframework.data.relational.core.sql.IdentifierProcessing;
import org.springframework.data.util.Optionals;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

public class DialectProvider implements DialectResolver.JdbcDialectProvider {
    private static final Logger LOG = LoggerFactory.getLogger(DialectResolver.class);

    @Override
    public Optional<Dialect> getDialect(JdbcOperations operations) {
        Optional<Dialect> dialect =
                Optional.ofNullable(
                        operations.execute((ConnectionCallback<Dialect>) DialectProvider::getDialect));
        if (dialect.isPresent()) {
            return dialect;
        } else {
            return new DialectResolver.DefaultDialectProvider().getDialect(operations);
        }
    }


    @Nullable
    private static Dialect getDialect(Connection connection) throws SQLException {

        DatabaseMetaData metaData = connection.getMetaData();

        String name = metaData.getDatabaseProductName().toLowerCase(Locale.ENGLISH);

        if (name.contains("hsql")) {
            return HsqlDbDialect.INSTANCE;
        }
        if (name.contains("h2")) {
            return H2Dialect.INSTANCE;
        }
        if (name.contains("mysql")) {
            return new JdbcMySqlDialect(getIdentifierProcessing(metaData));
        }
        if (name.contains("mariadb")) {
            return new MariaDbDialect(getIdentifierProcessing(metaData));
        }
        if (name.contains("postgresql")) {
            return JdbcPostgresDialect.INSTANCE;
        }
        if (name.contains("microsoft")) {
            return JdbcSqlServerDialect.INSTANCE;
        }
        if (name.contains("db2")) {
            return JdbcDb2Dialect.INSTANCE;
        }
        if (name.contains("oracle")) {
            return OracleDialect.INSTANCE;
        }
        if (name.contains("sqlite")) {
            return JdbcSqliteDialect.INSTANCE;
        }
        LOG.info(String.format("Couldn't determine Dialect for \"%s\"", name));
        return null;
    }

    private static IdentifierProcessing getIdentifierProcessing(DatabaseMetaData metaData) throws SQLException {

        // getIdentifierQuoteString() returns a space " " if identifier quoting is not
        // supported.
        String quoteString = metaData.getIdentifierQuoteString();
        IdentifierProcessing.Quoting quoting = StringUtils.hasText(quoteString)
                ? new IdentifierProcessing.Quoting(quoteString)
                : IdentifierProcessing.Quoting.NONE;

        IdentifierProcessing.LetterCasing letterCasing;
        // IdentifierProcessing tries to mimic the behavior of unquoted identifiers for their quoted variants.
        if (metaData.supportsMixedCaseIdentifiers()) {
            letterCasing = IdentifierProcessing.LetterCasing.AS_IS;
        } else if (metaData.storesUpperCaseIdentifiers()) {
            letterCasing = IdentifierProcessing.LetterCasing.UPPER_CASE;
        } else if (metaData.storesLowerCaseIdentifiers()) {
            letterCasing = IdentifierProcessing.LetterCasing.LOWER_CASE;
        } else { // this shouldn't happen since one of the previous cases should be true.
            // But if it does happen, we go with the ANSI default.
            letterCasing = IdentifierProcessing.LetterCasing.UPPER_CASE;
        }

        return IdentifierProcessing.create(quoting, letterCasing);
    }
}
