package top.yang.manager.impl;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import top.yang.component.BaseJdbcComponent;
import top.yang.domain.dto.BaseDto;
import top.yang.domain.pojo.BaseBean;
import top.yang.spring.exception.PojoInstanceFailException;

public abstract class BaseManagerImpl<C extends BaseJdbcComponent, T extends BaseBean, ID extends Serializable> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    protected C compoment;
    protected Class pojoClass = null;


    public abstract Class getEntityClass();

    public BaseManagerImpl() {
        Class c = this.getClass();
        Type t = c.getGenericSuperclass();
        if (t instanceof ParameterizedType) {
            Type[] p = ((ParameterizedType) t).getActualTypeArguments();
            pojoClass = (Class) p[1];
        }
    }

    public T findById(ID id) {
        return (T) compoment.findById(id);

    }

    public List<T> findAll() {
        return compoment.findAll();
    }


    public List<T> findAllByIds(Collection<ID> ids) {
        return compoment.findAllByIds(ids);

    }

    public boolean existsById(ID id) {
        return compoment.existsById(id);
    }

    public long count() {
        return compoment.count();
    }

    @Transactional
    public T save(T t) {
        return (T) compoment.save(t);

    }

    @Transactional
    public List<T> save(List<T> t) {
        return compoment.save(t);
    }

    @Transactional
    public void delete(T instance) {
        compoment.delete(instance);
    }


    public void deleteById(ID id) {
        compoment.deleteById(id);
    }


    public void deleteByIds(Collection<ID> ids) {
        compoment.deleteByIds(ids);
    }


    public void deleteAll() {
        compoment.deleteAll();
    }
}
