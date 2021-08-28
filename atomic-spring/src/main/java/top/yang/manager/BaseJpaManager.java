package top.yang.manager;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import top.yang.pojo.BaseBean;
import top.yang.repository.BaseJpaRepository;

/**
 * @author PrideYang
 */
public class BaseJpaManager<R extends BaseJpaRepository, T extends BaseBean, ID> {

  @Autowired
  protected R repository;

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
    return repository.findAllById(ids);
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
      repository.flush();
    }
  }

}
