package top.lytree.repository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.util.Streamable;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import top.lytree.oss.model.exception.exception.DataException;
import top.lytree.oss.model.request.support.BaseQuery;
import top.lytree.oss.model.request.support.PageQuery;
import top.lytree.repository.impl.BaseJpaRepositoryImpl;

/**
 * @author Y
 */
@NoRepositoryBean
public interface BaseJpaRepository<T, ID> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {
    final Logger logger = LoggerFactory.getLogger(BaseJpaRepository.class);

    /**
     * Finds all domain by id list.
     *
     * @param ids  id list of domain must not be null
     * @param sort the specified sort must not be null
     * @return a list of domains
     */
    List<T> findAllByIdIn(Collection<ID> ids, Sort sort);

    /**
     * Finds all domain by domain id list.
     *
     * @param ids      must not be null
     * @param pageable must not be null
     * @return a list of domains
     */
    Page<T> findAllByIdIn(Collection<ID> ids, Pageable pageable);

    /**
     * Deletes by id list.
     *
     * @param ids id list of domain must not be null
     * @return number of rows affected
     */
    long deleteByIdIn(Collection<ID> ids);


    Page<T> pageByCondition(BaseQuery query, Pageable pageable);

    List<T> findAllByCondition(BaseQuery queryReq);

    T findOneByCondition(BaseQuery query);

    Page<T> pageByComplexCondition(BaseQuery queryReq, Pageable pageable);

    List<T> findAllByComplexCondition(BaseQuery queryReq);

    Long countByComplexCondition(BaseQuery queryReq);


    default Optional<T> findByIdOfNullable(ID id) {
        return Optional.of(getReferenceById(id));
    }

    default T findByIdOfNonNull(ID id) throws DataException {
        Optional<T> optional = findByIdOfNullable(id);
        if (optional.isPresent()) {
            return optional.get();
        } else {
            throw new DataException("entity was not found or has been deleted");
        }
    }

    default List<T> findAllList() {
        Iterable<T> all = findAll();
        return Streamable.of(all).stream() //
                .collect(Collectors.toList());
    }

    default Page<T> pageByCondition(PageQuery pageQuery) {
        PageRequest pageRequest = PageRequest.of(pageQuery.getPage(), pageQuery.getPageSize());
        return pageByCondition(pageQuery, pageRequest);
    }


    default Page<T> pageByComplexCondition(PageQuery pageQuery) {
        PageRequest pageRequest = PageRequest.of(pageQuery.getPage(), pageQuery.getPageSize());
        return pageByComplexCondition(pageQuery, pageRequest);
    }

    default List<T> findAllByIds(Collection<ID> ids) {
        Iterable<T> all = findAllById(ids);
        return Streamable.of(all).stream() //
                .collect(Collectors.toList());

    }

    default void mustExistById(ID id) throws DataException {
        if (!existsById(id)) {
            throw new DataException("entity was not exist");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    default List<T> save(List<T> t) {
        return CollectionUtils.isEmpty(t) ? Collections.emptyList() : saveAll(t);
    }


    /**
     * Removes by id
     *
     * @param id id
     * @return T
     */
    @Transactional(rollbackFor = Exception.class)
    default T removeById(ID id) throws DataException {
        // Get non null domain by id
        T domain = findByIdOfNonNull(id);
        // Remove it
        remove(domain);
        // return the deleted domain
        return domain;
    }

    /**
     * Removes by id if present.
     *
     * @param id id
     * @return T
     */
    @Transactional(rollbackFor = Exception.class)
    default T removeByIdOfNullable(ID id) {
        return findByIdOfNullable(id).map(domain -> {
            remove(domain);
            return domain;
        }).orElse(null);
    }

    /**
     * Remove by domain
     *
     * @param domain domain
     */
    @Transactional(rollbackFor = Exception.class)
    default void remove(T domain) {
        Assert.notNull(domain, "entity data must not be null");
        delete(domain);
    }

    /**
     * Remove by ids
     *
     * @param ids ids
     */
    @Transactional(rollbackFor = Exception.class)
    default void removeInBatch(Collection<ID> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            logger.debug("entity id collection is empty");
            return;
        }

        deleteByIdIn(ids);
    }

    /**
     * Remove all by domains
     *
     * @param domains domains
     */
    @Transactional(rollbackFor = Exception.class)
    default void removeAll(Collection<T> domains) {
        if (CollectionUtils.isEmpty(domains)) {
            logger.debug("entity collection is empty");
            return;
        }
        deleteAllInBatch(domains);
    }
}

