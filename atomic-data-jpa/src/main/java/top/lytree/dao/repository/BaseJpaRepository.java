package top.lytree.dao.repository;

import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.lang.NonNull;
import top.lytree.oss.model.request.support.BaseQuery;

/**
 * @author Y
 */
@NoRepositoryBean
public interface BaseJpaRepository<T, ID> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

    /**
     * Finds all domain by id list.
     *
     * @param ids  id list of domain must not be null
     * @param sort the specified sort must not be null
     * @return a list of domains
     */
    @NonNull
    List<T> findAllByIdIn(@NonNull Collection<ID> ids, @NonNull Sort sort);

    /**
     * Finds all domain by domain id list.
     *
     * @param ids      must not be null
     * @param pageable must not be null
     * @return a list of domains
     */
    @NonNull
    Page<T> findAllByIdIn(@NonNull Collection<ID> ids, @NonNull Pageable pageable);

    /**
     * Deletes by id list.
     *
     * @param ids id list of domain must not be null
     * @return number of rows affected
     */
    long deleteByIdIn(@NonNull Collection<ID> ids);


    Page<T> pageByCondition(BaseQuery query, Pageable pageable);

    List<T> findAllByCondition(BaseQuery queryReq);

    T findOneByCondition(BaseQuery query);

    Page<T> pageByComplexCondition(BaseQuery queryReq, Pageable pageable);

    List<T> findAllByComplexCondition(BaseQuery queryReq);

    Long countByComplexCondition(BaseQuery queryReq);
}

