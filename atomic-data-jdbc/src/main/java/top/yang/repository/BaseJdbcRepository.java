package top.yang.repository;

import java.io.Serializable;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;
import top.yang.pojo.BaseBean;

/**
 * @author Y
 */
@NoRepositoryBean
public interface BaseJdbcRepository<T extends BaseBean, ID extends Serializable> extends PagingAndSortingRepository<T, ID> {

}

