package top.yang.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class ReflectField {

    private Field field;

    private Object target;

    public ReflectField(Field field, Object target) {
        this.field = field;
        this.target = target;
    }
    
    /**
     * @methodName: name
     * @description: 获取名称
     **/
    public String name() {
        return field.getName();
    }

    /**
     * @methodName: get
     * @description: 获取target中的值对象
     **/
    public Object get() {
        ReflectBean bean = new ReflectBean(target);
        Object result = bean.getter(name()).invoke();
        return result;
    }

    /**
     * @methodName: set
     * @description: 设置数据到变量
     **/
    public void set(Object param) {
        ReflectBean bean = new ReflectBean(target);
        bean.setter(name(), field.getType()).invoke(param);
    }

    /**
     * @methodName: annotations
     * @description: 获取所有的注解
     **/
    public Annotation[] annotations() {
        return field.getAnnotations();
    }

    /**
      * @methodName: annotation
      * @description: 获取单个注解
    **/
    public Annotation annotation(Class<? extends Annotation> annotation) {

        Annotation anno = field.getAnnotation(annotation);
        return anno;
    }

    /**
     * @methodName: type
     * @description: 获取属性类型
     **/
    public Class type() {
        return field.getType();
    }

    /**
      * @methodName: isAnnotationPresent
      * @description: 判断是否有注解
    **/
    public boolean isAnnotationPresent(Class<? extends Annotation> annotation) {
        return field.isAnnotationPresent(annotation);
    }
}

