package top.yang.manager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import top.yang.pojo.BaseBean;


import java.io.Serializable;

/**
 * @author Y
 */
@NoRepositoryBean
public interface BaseRepository<T extends BaseBean, ID extends Serializable> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {
}

