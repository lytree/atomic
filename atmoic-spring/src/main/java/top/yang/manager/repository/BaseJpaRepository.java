package top.yang.manager.repository;

import java.io.Serializable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import top.yang.pojo.BaseBean;

/**
 * @author Y
 */
@NoRepositoryBean
public interface BaseJpaRepository<T extends BaseBean, ID extends Serializable> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

}

