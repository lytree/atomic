package top.yang.manager;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import top.yang.domain.pojo.BaseBean;

public interface BaseManager<T extends BaseBean, ID extends Serializable> {

  T findById(ID id);

  List<T> findAll();

  List<T> findAll(Sort sort);

  Page<T> findAllPage(Pageable pageable);

  List<T> findAllByIds(Collection<ID> ids);

  boolean existsById(ID id);

  long count();

  T save(T t);

  List<T> save(List<T> t);

  T update(T t);

  List<T> update(List<T> t);

  void delete(T instance);


  void deleteById(ID id);


  void deleteByIds(Collection<ID> ids);


  void deleteAll();

}
