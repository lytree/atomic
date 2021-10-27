package top.yang.manager;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import top.yang.domain.pojo.BaseBean;

public interface BaseManager<T extends BaseBean, ID extends Serializable> {

  public T findById(ID id);

  public List<T> findAll();

  public List<T> findAll(Sort sort);

  public Page<T> findAllPage(Pageable pageable);

  public List<T> findAllByIds(Collection<Serializable> ids);

  public boolean existsById(ID id);

  public long count();

  public T save(T t);

  public List<T> save(List<T> t);

  public T update(T t);

  public List<T> update(List<T> t);

  public void delete(T instance);


  public void deleteById(Serializable id);


  public void deleteByIds(Collection<Serializable> ids);


  public void deleteAll();

}
