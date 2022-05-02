package top.yang.manager;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;
import top.yang.domain.pojo.BaseBean;
import top.yang.repository.BaseJpaRepository;

public abstract class BaseManager<R extends BaseJpaRepository, T extends BaseBean, ID extends Serializable> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    protected R repository;

    public T findById(ID id) {
        return (T) repository.findById(id).get();
    }

    public List<T> findAll() {
        Iterable<T> all = repository.findAll();
        return Streamable.of(all).stream() //
                .collect(Collectors.toList());
    }


    public List<T> findAllByIds(Collection<ID> ids) {
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


    public T save(T t) {
        return (T) repository.save(t);

    }


    public List<T> save(List<T> t) {
        return (List<T>) repository.saveAll(t);
    }


    public void delete(T instance) {
        repository.delete(instance);
    }


    public void deleteById(ID id) {
        repository.deleteById(id);
    }


    public void deleteByIds(Collection<ID> ids) {
        for (ID id : ids) {
            repository.deleteById(ids);
        }
    }
}
