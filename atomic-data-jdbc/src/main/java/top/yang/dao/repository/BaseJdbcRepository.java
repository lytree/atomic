package top.yang.dao.repository;

import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;
import top.yang.domain.pojo.BaseEntity;

@NoRepositoryBean
public interface BaseJdbcRepository<T extends BaseEntity, ID> extends PagingAndSortingRepository<T, ID> {

}
