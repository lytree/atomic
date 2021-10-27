package top.yang.manager.impl;

import java.io.Serializable;
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
import top.yang.compoment.BaseJdbcComponent;
import top.yang.domain.pojo.BaseBean;

public abstract class BaseManager<C extends BaseJdbcComponent, T extends BaseBean, ID extends Serializable> {

  @Autowired
  protected C compoment;

  public T findById(ID id) {
    return (T) compoment.findById(id);
  }

  public List<T> findAll() {

    return compoment.findAll();

  }

  public List<T> findAll(Sort sort) {
    return compoment.findAll(sort);
  }

  public Page<T> findAllPage(Pageable pageable) {
    return (Page<T>) compoment.findAllPage(pageable);
  }

  public List<T> findAllByIds(Collection<Serializable> ids) {
    return compoment.findAllByIds(ids);

  }

  public boolean existsById(ID id) {
    return compoment.existsById(id);
  }

  public long count() {
    return compoment.count();
  }

  public T save(T t) {
    return (T) compoment.save(t);
  }

  public List<T> save(List<T> t) {
    return (List<T>) compoment.save(t);
  }

  public T update(T t) {
    return (T) compoment.save(t);
  }


  public List<T> update(List<T> t) {
    return (List<T>) compoment.update(t);
  }

  public void delete(T instance) {
    compoment.delete(instance);
  }


  public void deleteById(Serializable id) {
    compoment.deleteById(id);
  }


  public void deleteByIds(Collection<Serializable> ids) {
    compoment.deleteByIds(ids);
  }


  public void deleteAll() {
    compoment.deleteAll();
  }

}
