package top.yang.component;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Streamable;
import org.springframework.transaction.annotation.Transactional;
import top.yang.domain.pojo.BaseBean;
import top.yang.repository.BaseJdbcRepository;

public abstract class BaseJdbcComponent<R extends BaseJdbcRepository, T extends BaseBean, ID extends Serializable> {

  protected abstract R getRepository();

  public T findById(ID id) {
    return (T) getRepository().findById(id).get();
  }

  public List<T> findAll() {

    Iterable<T> all = getRepository().findAll();
    return Streamable.of(all).stream() //
        .collect(Collectors.toList());
  }

  public List<T> findAll(Sort sort) {
    Iterable<T> all = getRepository().findAll(sort);
    return Streamable.of(all).stream() //
        .collect(Collectors.toList());
  }

  public Page<T> findAllPage(Pageable pageable) {
    return (Page<T>) getRepository().findAll(pageable);
  }

  public List<T> findAllByIds(Collection<Serializable> ids) {
    Iterable<T> all = getRepository().findAllById(ids);
    return Streamable.of(all).stream() //
        .collect(Collectors.toList());
  }

  public boolean existsById(ID id) {
    return getRepository().existsById(id);
  }

  public long count() {
    return getRepository().count();
  }

  @Transactional(rollbackFor = Exception.class)
  public T save(T t) {
    return (T) getRepository().save(t);
  }

  @Transactional(rollbackFor = Exception.class)
  public List<T> save(List<T> t) {
    return (List<T>) getRepository().saveAll(t);
  }

  @Transactional(rollbackFor = Exception.class)
  public T update(T t) {
    return (T) getRepository().save(t);
  }

  @Transactional(rollbackFor = Exception.class)
  public List<T> update(List<T> t) {
    return (List<T>) getRepository().saveAll(t);
  }

  @Transactional(rollbackFor = Exception.class)
  public void delete(T instance) {
    getRepository().delete(instance);
  }

  @Transactional(rollbackFor = Exception.class)
  public void deleteById(ID id) {
    getRepository().deleteById(id);
  }

  @Transactional(rollbackFor = Exception.class)
  public void deleteByIds(Collection<ID> ids) {
    ids.forEach(id -> getRepository().deleteById(id));
  }


  @Transactional(rollbackFor = Exception.class)
  public void deleteAll() {
    getRepository().deleteAll();
  }
}
