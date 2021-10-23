package top.yang.manager;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Streamable;
import org.springframework.transaction.annotation.Transactional;
import top.yang.compoment.BaseJdbcComponent;
import top.yang.pojo.BaseBean;
import top.yang.repository.BaseJdbcRepository;

/**
 * @author PrideYang
 */
public class BaseJdbcManager<R extends BaseJdbcComponent, T extends BaseBean, ID extends Serializable> {

  @Autowired
  protected R component;


  public T findById(ID id) {
    return (T) component.findById(id);
  }

  public List<T> findAll() {

    Iterable<T> all = component.findAll();
    return Streamable.of(all).stream() //
        .collect(Collectors.toList());
  }

  public List<T> findAll(Sort sort) {
    Iterable<T> all = component.findAll(sort);
    return Streamable.of(all).stream() //
        .collect(Collectors.toList());
  }

  public Page<T> findAllPage(Pageable pageable) {
    return (Page<T>) component.findAllPage(pageable);
  }

  public List<T> findAllByIds(Set<ID> ids) {
    Iterable<T> all = component.findAllByIds(ids);
    return Streamable.of(all).stream() //
        .collect(Collectors.toList());
  }

  public boolean existsById(ID id) {
    return component.existsById(id);
  }

  public long count() {
    return component.count();
  }

  public T save(T t) {
    return (T) component.save(t);
  }

  public List<T> save(List<T> t) {
    return (List<T>) component.save(t);
  }

  public T update(T t) {
    return (T) component.save(t);
  }


  public List<T> update(List<T> t) {
    return (List<T>) component.save(t);
  }


  public void delete(T instance) {
    component.delete(instance);
  }

  public void deleteById(ID id) {
    component.deleteById(id);
  }

  public void deleteByIds(Collection<ID> ids) {
    ids.forEach(id -> component.deleteById(id));
  }


  public void deleteAll() {
    component.deleteAll();
  }
}
