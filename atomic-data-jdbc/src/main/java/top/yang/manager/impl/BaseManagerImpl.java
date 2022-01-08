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

public abstract class BaseManagerImpl<C extends BaseJdbcComponent, T extends BaseDto, ID extends Serializable> {

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
        BaseBean baseBean = compoment.findById(id);
        T instance = copy(baseBean, pojoClass);
        return instance;
    }

    public List<T> findAll() {
        List<BaseBean> list = compoment.findAll();
        ArrayList<T> results = new ArrayList<>();
        for (BaseBean baseBean : list) {
            results.add(copy(baseBean, pojoClass));
        }
        return results;

    }


    public List<T> findAllByIds(Collection<ID> ids) {
        List<BaseBean> list = compoment.findAllByIds(ids);
        ArrayList<T> results = new ArrayList<>();
        for (BaseBean baseBean : list) {
            results.add(copy(baseBean, pojoClass));
        }
        return results;

    }

    public boolean existsById(ID id) {
        return compoment.existsById(id);
    }

    public long count() {
        return compoment.count();
    }

    @Transactional
    public T save(T t) {
        BaseBean pojo = generatePojo(getEntityClass());
        BeanUtils.copyProperties(t, pojo);
        BaseBean bean = compoment.save(pojo);
        return copy(bean, pojoClass);
    }

    @Transactional
    public List<T> save(List<T> t) {
        ArrayList<T> result = new ArrayList<>();
        for (T baseDto : t) {
            result.add(save(baseDto));
        }
        return result;
    }

    @Transactional
    public void delete(T instance) {
        BaseBean baseBean = generatePojo(getEntityClass());
        BeanUtils.copyProperties(instance, baseBean);
        compoment.delete(baseBean);
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


    private T copy(BaseBean baseBean, Class pojoClass) {
        T instance = null;
        try {
            instance = (T) pojoClass.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(baseBean, instance);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            logger.error("pojo对象初始化异常,对象为：" + pojoClass.getSimpleName(), e);
            throw new PojoInstanceFailException();
        }
        return instance;
    }

    private BaseBean generatePojo(Class pojoClass) {

        try {
            return (BaseBean) getEntityClass().getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            logger.error("pojo对象初始化异常,对象为：" + pojoClass.getSimpleName(), e);
            throw new PojoInstanceFailException();
        }

    }
}
