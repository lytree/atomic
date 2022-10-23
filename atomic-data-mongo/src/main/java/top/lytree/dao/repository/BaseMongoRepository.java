package top.lytree.dao.repository;

import java.io.Serializable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;
import top.lytree.oss.model.entity.BaseEntity;

/**
 * @author Y
 */
@NoRepositoryBean
public interface BaseMongoRepository<T extends BaseEntity, ID extends Serializable> extends MongoRepository<T, ID> {

}
