package top.yang.manager.impl;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import top.yang.component.BaseJdbcComponent;
import top.yang.domain.pojo.BaseBean;

public abstract class BaseManagerImpl<C extends BaseJdbcComponent, T extends BaseBean, ID extends Serializable> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    protected C component;


    public abstract Class getEntityClass();


    public T findById(ID id) {
        return (T) component.findById(id);

    }

    public List<T> findAll() {
        return component.findAll();
    }


    public List<T> findAllByIds(Collection<ID> ids) {
        return component.findAllByIds(ids);

    }

    public boolean existsById(ID id) {
        return component.existsById(id);
    }

    public long count() {
        return component.count();
    }

    @Transactional
    public T save(T t) {
        return (T) component.save(t);

    }

    @Transactional
    public List<T> save(List<T> t) {
        return component.save(t);
    }

    @Transactional
    public void delete(T instance) {
        component.delete(instance);
    }


    public void deleteById(ID id) {
        component.deleteById(id);
    }


    public void deleteByIds(Collection<ID> ids) {
        component.deleteByIds(ids);
    }


    public void deleteAll() {
        component.deleteAll();
    }
}
