package top.yang.manager;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import top.yang.domain.pojo.BaseBean;
import top.yang.spring.dto.BaseDto;

public interface BaseManager<T extends BaseDto, ID extends Serializable> {

    T findById(ID id);

    List<T> findAll();

    List<T> findAllByIds(Collection<ID> ids);

    boolean existsById(ID id);

    long count();

    T save(T t);

    List<T> save(List<T> t);


}
