package top.yang.manager;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import top.yang.domain.dto.BaseDto;

public interface BaseManager<T extends BaseDto, ID extends Serializable> {

    T findById(ID id);

    List<T> findAll();

    List<T> findAllByIds(Collection<ID> ids);

    boolean existsById(ID id);

    long count();

    T save(T t);

    List<T> save(List<T> t);


}
