package top.yang.repository;

import java.io.Serializable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;
import top.yang.pojo.BaseBean;

/**
 * @author Y
 */
@NoRepositoryBean
public interface BaseMongoRepository<T extends BaseBean, ID extends Serializable> extends MongoRepository<T, ID> {

}
