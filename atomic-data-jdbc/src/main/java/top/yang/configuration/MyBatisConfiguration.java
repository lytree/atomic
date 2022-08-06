package top.yang.configuration;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.core.convert.DataAccessStrategy;
import org.springframework.data.jdbc.core.convert.JdbcConverter;
import org.springframework.data.jdbc.core.mapping.JdbcMappingContext;
import org.springframework.data.jdbc.mybatis.MyBatisDataAccessStrategy;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.data.relational.core.dialect.Dialect;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

@Configuration
@EnableJdbcRepositories
@ConditionalOnBean(MybatisJdbcProperties.class)
public class MyBatisConfiguration extends AbstractJdbcConfiguration {

    private @Autowired
    MybatisJdbcProperties mybatisJdbcProperties;
    private @Autowired
    SqlSession session;


    @Bean
    @Override
    public DataAccessStrategy dataAccessStrategyBean(NamedParameterJdbcOperations operations, JdbcConverter jdbcConverter,
            JdbcMappingContext context, Dialect dialect) {

        return MyBatisDataAccessStrategy.createCombinedAccessStrategy(context, jdbcConverter, operations, session, new MybatisNamespaceStrategy(mybatisJdbcProperties.getPrefix(),
                mybatisJdbcProperties.getSuffix()), dialect);
    }
}
