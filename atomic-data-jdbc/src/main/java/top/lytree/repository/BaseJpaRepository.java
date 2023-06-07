package top.lytree.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jdbc.core.JdbcAggregateOperations;
import org.springframework.data.jdbc.core.convert.JdbcConverter;
import org.springframework.data.jdbc.repository.support.SimpleJdbcRepository;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * @author Y
 */
@NoRepositoryBean
public class BaseJpaRepository<T, ID> extends SimpleJdbcRepository<T, ID> {
    final Logger logger = LoggerFactory.getLogger(BaseJpaRepository.class);


    public BaseJpaRepository(JdbcAggregateOperations entityOperations, PersistentEntity<T, ?> entity, JdbcConverter converter) {
        super(entityOperations, entity, converter);
    }
}

