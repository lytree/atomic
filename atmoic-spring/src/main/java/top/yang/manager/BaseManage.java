package top.yang.manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import top.yang.entity.BaseBean;
import top.yang.manager.repository.BaseRepository;

import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author PrideYang
 */
public abstract class BaseManage<R extends BaseRepository, T extends BaseBean, ID> {
    @Autowired
    protected R repository;

    public List<T> findAll() {
        return repository.findAll();
    }

    public List<T> findAll(Sort sort) {
        return repository.findAll(sort);
    }

    public void  findAllPage(Pageable pageable){
        Page<T> all = repository.findAll(pageable);
    }

    public List<T> findAllByIds(Set<ID> ids) {
        return repository.findAllById(ids);
    }

    public T save(T t) {
        if (Objects.isNull(t.getDtCreate())) {
            t.setDtCreate(LocalDateTime.now());
        }
        return (T) repository.save(t);
    }

    public List<T> save(List<T> t) {
        List<T> collect = t.stream().map(t1 -> {
            t1.setDtCreate(LocalDateTime.now());
            return t1;
        }).collect(Collectors.toList());
        return (List<T>) repository.saveAll(collect);
    }

    public T update(T t) {
        t.setDtUpdate(LocalDateTime.now());
        return (T) repository.save(t);
    }

    public List<T> update(List<T> t) {
        List<T> collect = t.stream().map(t1 -> {
            t1.setDtUpdate(LocalDateTime.now());
            return t1;
        }).collect(Collectors.toList());
        return (List<T>) repository.saveAll(collect);
    }

    public void delete(ID id) {
        repository.delete(id);
    }

    public void delete(List<ID> ids) {
        repository.deleteAll(ids);
    }
}
