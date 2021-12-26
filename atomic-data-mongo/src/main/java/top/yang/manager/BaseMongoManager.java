package top.yang.manager;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import top.yang.domain.pojo.BaseBean;
import top.yang.repository.BaseMongoRepository;

/**
 * @author PrideYang
 */
public class BaseMongoManager<R extends BaseMongoRepository, T extends BaseBean, ID> {

  @Autowired
  protected R repository;

  public T findById(ID id) {
    return (T) repository.findById(id).get();
  }

  public List<T> findAll() {
    return repository.findAll();
  }

  public List<T> findAll(Sort sort) {
    return repository.findAll(sort);
  }

  public Page<T> findAllPage(Pageable pageable) {
    return (Page<T>) repository.findAll(pageable);
  }

  public List<T> findAllByIds(Set<ID> ids) {
    Iterable<T> all = repository.findAllById(ids);
    return StreamSupport.stream(all.spliterator(), false).collect(Collectors.toList());
  }

  @Transactional(rollbackFor = Exception.class)
  public T save(T t) {
    return (T) repository.save(t);
  }

  @Transactional(rollbackFor = Exception.class)
  public List<T> save(List<T> t) {
    return (List<T>) repository.saveAll(t);
  }

  @Transactional(rollbackFor = Exception.class)
  public T update(T t) {
    return (T) repository.save(t);
  }

  @Transactional(rollbackFor = Exception.class)
  public List<T> update(List<T> t) {
    return (List<T>) repository.saveAll(t);
  }

  @Transactional(rollbackFor = Exception.class)
  public void delete(ID id) {
    Optional optional = repository.findById(id);
    if (optional.isPresent()) {
      repository.deleteById(id);
    }
  }

  @Transactional(rollbackFor = Exception.class)
  public void deleteByIds(Collection<ID> ids) {
    ids.forEach(id -> repository.deleteById(id));
  }

}
