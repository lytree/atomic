package top.yang.compoment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import top.yang.pojo.BaseBean;
import top.yang.repository.BaseMongoRepository;

public class BaseMongoCompoment<R extends BaseMongoRepository, T extends BaseBean, ID> {

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

    Iterable<T> all = repository.findAllById(ids);
    return StreamSupport.stream(all.spliterator(), false).collect(Collectors.toList());
  }

  @Transactional(rollbackFor = Exception.class)
  public T save(T t) {
    return (T) repository.save(t);
  }

  @Transactional(rollbackFor = Exception.class)
  public List<T> save(List<T> t) {
    List<T> collect = t.stream().map(t1 -> {
      t1.setCreateTime(LocalDateTime.now());
      return t1;
    }).collect(Collectors.toList());
    return (List<T>) repository.saveAll(collect);
  }

  @Transactional(rollbackFor = Exception.class)
  public T update(T t) {
    t.setUpdateTime(LocalDateTime.now());
    return (T) repository.save(t);
  }

  @Transactional(rollbackFor = Exception.class)
  public List<T> update(List<T> t) {
    List<T> collect = t.stream().map(t1 -> {
      t1.setUpdateTime(LocalDateTime.now());
      return t1;
    }).collect(Collectors.toList());
    return (List<T>) repository.saveAll(collect);
  }

  @Transactional(rollbackFor = Exception.class)
  public void delete(ID id) {
    Optional optional = repository.findById(id);
    if (optional.isPresent()) {
      repository.deleteById(id);
    }
  }
}