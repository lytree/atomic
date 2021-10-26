package top.yang.compoment;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Streamable;
import org.springframework.transaction.annotation.Transactional;
import top.yang.pojo.BaseBean;
import top.yang.repository.BaseJdbcRepository;

public class BaseJdbcComponent<R extends BaseJdbcRepository, T extends BaseBean, ID extends Serializable> {

  @Autowired
  protected R repository;
  protected Class pojoClass = null;

  public BaseJdbcComponent() {
    Class c = this.getClass();
    Type t = c.getGenericSuperclass();
    if (t instanceof ParameterizedType) {
      Type[] p = ((ParameterizedType) t).getActualTypeArguments();
      pojoClass = (Class) p[1];
    }
  }

  public T findById(ID id) {
    return (T) repository.findById(id).get();
  }

  public List<T> findAll() {

    Iterable<T> all = repository.findAll();
    return Streamable.of(all).stream() //
        .collect(Collectors.toList());
  }

  public List<T> findAll(Sort sort) {
    Iterable<T> all = repository.findAll(sort);
    return Streamable.of(all).stream() //
        .collect(Collectors.toList());
  }

  public Page<T> findAllPage(Pageable pageable) {
    return (Page<T>) repository.findAll(pageable);
  }

  public List<T> findAllByIds(Collection<Serializable> ids) {
    Iterable<T> all = repository.findAllById(ids);
    return Streamable.of(all).stream() //
        .collect(Collectors.toList());
  }

  public boolean existsById(ID id) {
    return repository.existsById(id);
  }

  public long count() {
    return repository.count();
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
  public void delete(T instance) {
    repository.delete(instance);
  }

  @Transactional(rollbackFor = Exception.class)
  public void deleteById(Serializable id) {
    repository.deleteById(id);
  }

  @Transactional(rollbackFor = Exception.class)
  public void deleteByIds(Collection<Serializable> ids) {
    ids.forEach(id -> repository.deleteById(id));
  }


  @Transactional(rollbackFor = Exception.class)
  public void deleteAll() {
    repository.deleteAll();
  }

}
