package top.yang.configuration;

import org.springframework.data.relational.core.dialect.AbstractDialect;
import org.springframework.data.relational.core.dialect.LimitClause;
import org.springframework.data.relational.core.dialect.LockClause;
import org.springframework.data.relational.core.sql.IdentifierProcessing;

import org.springframework.data.relational.core.sql.IdentifierProcessing.Quoting;
import org.springframework.data.relational.core.sql.LockOptions;
import org.springframework.util.Assert;
import top.yang.configuration.MySqlIdentifierProcessing.LetterCasing;

/**
 * @author pride
 */
public class MySqlDialect extends AbstractDialect {

    /**
     * MySQL defaults for {@link IdentifierProcessing}.
     */
    public static final IdentifierProcessing MYSQL_IDENTIFIER_PROCESSING = new MySqlIdentifierProcessing(new Quoting("`"),
            LetterCasing.UNDERLINE_CASE);
    /**
     * Singleton instance.
     */
    public static final MySqlDialect INSTANCE = new MySqlDialect();

    private final IdentifierProcessing identifierProcessing;

    protected MySqlDialect() {
        this(MYSQL_IDENTIFIER_PROCESSING);
    }

    /**
     * Creates a new {@link org.springframework.data.relational.core.dialect.MySqlDialect} given {@link IdentifierProcessing}.
     *
     * @param identifierProcessing must not be null.
     * @since 2.0
     */
    public MySqlDialect(IdentifierProcessing identifierProcessing) {

        Assert.notNull(identifierProcessing, "IdentifierProcessing must not be null");

        this.identifierProcessing = identifierProcessing;
    }

    private static final LimitClause LIMIT_CLAUSE = new LimitClause() {

        /*
         * (non-Javadoc)
         * @see org.springframework.data.relational.core.dialect.LimitClause#getLimit(long)
         */
        @Override
        public String getLimit(long limit) {
            return "LIMIT " + limit;
        }

        /*
         * (non-Javadoc)
         * @see org.springframework.data.relational.core.dialect.LimitClause#getOffset(long)
         */
        @Override
        public String getOffset(long offset) {
            // Ugly but the official workaround for offset without limit
            // see: https://stackoverflow.com/a/271650
            return String.format("LIMIT %d, 18446744073709551615", offset);
        }

        /*
         * (non-Javadoc)
         * @see org.springframework.data.relational.core.dialect.LimitClause#getClause(long, long)
         */
        @Override
        public String getLimitOffset(long limit, long offset) {

            // LIMIT {[offset,] row_count}
            return String.format("LIMIT %s, %s", offset, limit);
        }

        /*
         * (non-Javadoc)
         * @see org.springframework.data.relational.core.dialect.LimitClause#getClausePosition()
         */
        @Override
        public Position getClausePosition() {
            return Position.AFTER_ORDER_BY;
        }
    };

    private static final LockClause LOCK_CLAUSE = new LockClause() {

        /*
         * (non-Javadoc)
         * @see org.springframework.data.relational.core.dialect.LockClause#getLock(LockOptions)
         */
        @Override
        public String getLock(LockOptions lockOptions) {
            switch (lockOptions.getLockMode()) {

                case PESSIMISTIC_WRITE:
                    return "FOR UPDATE";

                case PESSIMISTIC_READ:
                    return "LOCK IN SHARE MODE";

                default:
                    return "";
            }
        }

        /*
         * (non-Javadoc)
         * @see org.springframework.data.relational.core.dialect.LockClause#getClausePosition()
         */
        @Override
        public Position getClausePosition() {
            return Position.AFTER_ORDER_BY;
        }
    };

    /*
     * (non-Javadoc)
     * @see org.springframework.data.relational.core.dialect.Dialect#limit()
     */
    @Override
    public LimitClause limit() {
        return LIMIT_CLAUSE;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.relational.core.dialect.Dialect#lock()
     */
    @Override
    public LockClause lock() {
        return LOCK_CLAUSE;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.relational.core.dialect.Dialect#getIdentifierProcessing()
     */
    @Override
    public IdentifierProcessing getIdentifierProcessing() {
        return identifierProcessing;
    }
}
