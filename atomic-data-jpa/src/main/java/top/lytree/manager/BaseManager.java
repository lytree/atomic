package top.lytree.manager;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.util.Streamable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import top.lytree.oss.model.entity.BaseEntity;
import top.lytree.oss.model.exception.exception.DataException;
import top.lytree.oss.model.request.support.BaseQuery;
import top.lytree.oss.model.request.support.PageQuery;
import top.lytree.repository.BaseJpaRepository;

public abstract class BaseManager<T extends BaseEntity, ID extends Serializable> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private final String domainName;

    private final BaseJpaRepository<T, ID> repository;

    protected BaseManager(BaseJpaRepository<T, ID> repository) {
        this.repository = repository;
        Class<T> domainClass = (Class<T>) fetchType(0);
        domainName = domainClass.getSimpleName();
    }

    /**
     * Gets actual generic type.
     *
     * @param index generic type index
     * @return real generic type will be returned
     */
    private Type fetchType(int index) {
        Assert.isTrue(index >= 0 && index <= 1, "type index must be between 0 to 1");
        return ((ParameterizedType) this.getClass().getGenericSuperclass())
                .getActualTypeArguments()[index];
    }

    public Optional<T> findByIdOfNullable(ID id) {
        return repository.findById(id);
    }

    public T findById(ID id) throws DataException {
        Optional<T> optional = findByIdOfNullable(id);
        if (optional.isPresent()) {
            return optional.get();
        } else {
            throw new DataException(domainName + " was not found or has been deleted");
        }
    }

    public List<T> findAll() {
        Iterable<T> all = repository.findAll();
        return Streamable.of(all).stream() //
                .collect(Collectors.toList());
    }

    public List<T> findAllByCondition(BaseQuery baseQuery) {
        return repository.findAllByCondition(baseQuery);

    }

    public Page<T> pageByCondition(PageQuery pageQuery) {
        PageRequest pageRequest = PageRequest.of(pageQuery.getPage(), pageQuery.getPageSize());
        return repository.pageByCondition(pageQuery, pageRequest);
    }

    public List<T> findAllByComplexCondition(BaseQuery baseQuery) {
        return repository.findAllByComplexCondition(baseQuery);

    }

    public Page<T> pageByComplexCondition(PageQuery pageQuery) {
        PageRequest pageRequest = PageRequest.of(pageQuery.getPage(), pageQuery.getPageSize());
        return repository.pageByComplexCondition(pageQuery, pageRequest);
    }

    public List<T> findAllByIds(Collection<ID> ids) {
        Iterable<T> all = repository.findAllById(ids);
        return Streamable.of(all).stream() //
                .collect(Collectors.toList());

    }

    public boolean existsById(ID id) {
        return repository.existsById(id);
    }

    public void mustExistById(ID id) throws DataException {
        if (!existsById(id)) {
            throw new DataException(domainName + " was not exist");
        }
    }

    public long count() {
        return repository.count();
    }

    @Transactional(rollbackFor = Exception.class)
    public T save(T t) {
        return repository.save(t);

    }

    @Transactional(rollbackFor = Exception.class)
    public List<T> save(List<T> t) {
        return CollectionUtils.isEmpty(t) ? Collections.emptyList() : repository.saveAll(t);
    }


    /**
     * Removes by id
     *
     * @param id id
     * @return T
     */
    @Transactional(rollbackFor = Exception.class)
    public T removeById(ID id) throws DataException {
        // Get non null domain by id
        T domain = findById(id);
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
    public T removeByIdOfNullable(ID id) {
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
    public void remove(T domain) {
        Assert.notNull(domain, domainName + " data must not be null");
        repository.delete(domain);
    }

    /**
     * Remove by ids
     *
     * @param ids ids
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeInBatch(Collection<ID> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            logger.debug(domainName + " id collection is empty");
            return;
        }

        repository.deleteByIdIn(ids);
    }

    /**
     * Remove all by domains
     *
     * @param domains domains
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeAll(Collection<T> domains) {
        if (CollectionUtils.isEmpty(domains)) {
            logger.debug(domainName + " collection is empty");
            return;
        }
        repository.deleteAllInBatch(domains);
    }

    /**
     * Remove all
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeAll() {
        repository.deleteAll();
    }

    @Transactional(rollbackFor = Exception.class)
    public T update(T domain) {
        Assert.notNull(domain, domainName + " data must not be null");
        return repository.saveAndFlush(domain);
    }

    public void flush() {
        repository.flush();
    }
}
