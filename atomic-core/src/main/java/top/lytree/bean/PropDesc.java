package top.lytree.bean;


import java.beans.Transient;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import top.lytree.exception.BeanException;

/**
 * 属性描述，包括了字段、getter、setter和相应的方法执行
 *
 * @author looly
 */
public class PropDesc {

    /**
     * 字段
     */
    final Field field;
    /**
     * Getter方法
     */
    protected Method getter;
    /**
     * Setter方法
     */
    protected Method setter;

    /**
     * 构造<br>
     * Getter和Setter方法设置为默认可访问
     *
     * @param field  字段
     * @param getter get方法
     * @param setter set方法
     */
    public PropDesc(final Field field, final Method getter, final Method setter) {
        this.field = field;
        this.getter = getter;
        this.setter = setter;
    }

    /**
     * 获取字段名，如果存在Alias注解，读取注解的值作为名称
     *
     * @return 字段名
     */
    public String getFieldName() {
        return this.field.getName();
    }

    /**
     * 获取字段名称
     *
     * @return 字段名
     *
     * @since 5.1.6
     */
    public String getRawFieldName() {
        return null == this.field ? null : this.field.getName();
    }

    /**
     * 获取字段
     *
     * @return 字段
     */
    public Field getField() {
        return this.field;
    }

    /**
     * 获得字段类型<br>
     * 先获取字段的类型，如果字段不存在，则获取Getter方法的返回类型，否则获取Setter的第一个参数类型
     *
     * @return 字段类型
     */
    public Type getFieldType() {
        if (null != this.field) {
            return this.field.getGenericType();
        }
        return findPropType(getter, setter);
    }

    /**
     * 获得字段类型<br>
     * 先获取字段的类型，如果字段不存在，则获取Getter方法的返回类型，否则获取Setter的第一个参数类型
     *
     * @return 字段类型
     */
    public Class<?> getFieldClass() {
        if (null != this.field) {
            return this.field.getType();
        }
        return findPropClass(getter, setter);
    }

    /**
     * 获取Getter方法，可能为{@code null}
     *
     * @return Getter方法
     */
    public Method getGetter() {
        return this.getter;
    }

    /**
     * 获取Setter方法，可能为{@code null}
     *
     * @return {@link Method}Setter 方法对象
     */
    public Method getSetter() {
        return this.setter;
    }

    /**
     * 检查属性是否可读（即是否可以通过{@link #getValue(Object)}获取到值）
     *
     * @param checkTransient 是否检查Transient关键字或注解
     *
     * @return 是否可读
     *
     * @since 5.4.2
     */
    public boolean isReadable(final boolean checkTransient) {
        // 检查是否有getter方法或是否为public修饰
        if (null == this.getter && false == ModifierUtils.isPublic(this.field)) {
            return false;
        }

        // 检查transient关键字和@Transient注解
        if (checkTransient && isTransientForGet()) {
            return false;
        }

        return true;
    }

    /**
     * 获取属性值<br>
     * 首先调用字段对应的Getter方法获取值，如果Getter方法不存在，则判断字段如果为public，则直接获取字段值<br>
     * 此方法不检查任何注解，使用前需调用 {@link #isReadable(boolean)} 检查是否可读
     *
     * @param bean Bean对象
     *
     * @return 字段值
     *
     * @since 4.0.5
     */
    public Object getValue(final Object bean) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        if (null != this.getter) {
            return MethodUtils.invokeMethod(bean, this.getter.getName());
        } else if (ModifierUtils.isPublic(this.field)) {
            return FieldUtils.getFieldValue(bean, this.field);
        }

        return null;
    }

    /**
     * 获取属性值，自动转换属性值类型<br>
     * 首先调用字段对应的Getter方法获取值，如果Getter方法不存在，则判断字段如果为public，则直接获取字段值
     *
     * @param bean        Bean对象
     * @param targetType  返回属性值需要转换的类型，null表示不转换
     * @param ignoreError 是否忽略错误，包括转换错误和注入错误
     *
     * @return this
     *
     * @since 5.4.2
     */
    public Object getValue(final Object bean, final Type targetType, final boolean ignoreError) {
        Object result = null;
        try {
            result = getValue(bean);
        } catch (final Exception e) {
            if (false == ignoreError) {
                throw new BeanException(e, "Get value of [{}] error!", getFieldName());
            }
        }
        return result;
    }

    /**
     * 检查属性是否可读（即是否可以通过{@link #getValue(Object)}获取到值）
     *
     * @param checkTransient 是否检查Transient关键字或注解
     *
     * @return 是否可读
     *
     * @since 5.4.2
     */
    public boolean isWritable(final boolean checkTransient) {
        // 检查是否有getter方法或是否为public修饰
        if (null == this.setter && false == ModifierUtils.isPublic(this.field)) {
            return false;
        }

        // 检查transient关键字和@Transient注解
        if (checkTransient && isTransientForSet()) {
            return false;
        }

        return true;
    }

    /**
     * 设置Bean的字段值<br>
     * 首先调用字段对应的Setter方法，如果Setter方法不存在，则判断字段如果为public，则直接赋值字段值<br>
     * 此方法不检查任何注解，使用前需调用 {@link #isWritable(boolean)} 检查是否可写
     *
     * @param bean  Bean对象
     * @param value 值，必须与字段值类型匹配
     *
     * @return this
     *
     * @since 4.0.5
     */
    public PropDesc setValue(final Object bean, final Object value) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        if (null != this.setter) {
            MethodUtils.invokeMethod(bean, this.setter.getName(), value);
        } else if (ModifierUtils.isPublic(this.field)) {
            FieldUtils.writeField(bean, this.field.getName(), value);
        }
        return this;
    }

    /**
     * 设置属性值，可以自动转换字段类型为目标类型
     *
     * @param bean        Bean对象
     * @param value       属性值，可以为任意类型
     * @param ignoreNull  是否忽略{@code null}值，true表示忽略
     * @param ignoreError 是否忽略错误，包括转换错误和注入错误
     *
     * @return this
     *
     * @since 5.4.2
     */
    public PropDesc setValue(final Object bean, final Object value, final boolean ignoreNull, final boolean ignoreError)
            throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        return setValue(bean, value, ignoreNull, ignoreError, true);
    }

    /**
     * 设置属性值，可以自动转换字段类型为目标类型
     *
     * @param bean        Bean对象
     * @param value       属性值，可以为任意类型
     * @param ignoreNull  是否忽略{@code null}值，true表示忽略
     * @param ignoreError 是否忽略错误，包括转换错误和注入错误
     * @param override    是否覆盖目标值，如果不覆盖，会先读取bean的值，{@code null}则写，否则忽略。如果覆盖，则不判断直接写
     *
     * @return this
     *
     * @since 5.7.17
     */
    public PropDesc setValue(final Object bean, Object value, final boolean ignoreNull, final boolean ignoreError, final boolean override)
            throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        if (null == value && ignoreNull) {
            return this;
        }

        // issue#I4JQ1N@Gitee
        // 非覆盖模式下，如果目标值存在，则跳过
        if (false == override && null != getValue(bean)) {
            return this;
        }

        // 当类型不匹配的时候，执行默认转换
        if (null != value) {
            final Class<?> propClass = getFieldClass();
            if (false == propClass.isInstance(value)) {
                throw new BeanException("value and [{}] Don't agree!", getFieldName());
            }
        }

        // 属性赋值
        if (null != value || false == ignoreNull) {
            try {
                this.setValue(bean, value);
            } catch (final Exception e) {
                if (false == ignoreError) {
                    throw new BeanException(e, "Set value of [{}] error!", getFieldName());
                }
                // 忽略注入失败
            }
        }

        return this;
    }

    //------------------------------------------------------------------------------------ Private method start

    /**
     * 通过Getter和Setter方法中找到属性类型
     *
     * @param getter Getter方法
     * @param setter Setter方法
     *
     * @return {@link Type}
     */
    private Type findPropType(final Method getter, final Method setter) {
        Type type = null;
        if (null != getter) {
            type = getter.getGenericReturnType();
        }
        if (null == type && null != setter) {
            type = TypeUtils.getParamType(setter, 0);
        }
        return type;
    }

    /**
     * 通过Getter和Setter方法中找到属性类型
     *
     * @param getter Getter方法
     * @param setter Setter方法
     *
     * @return {@link Type}
     */
    private Class<?> findPropClass(final Method getter, final Method setter) {
        Class<?> type = null;
        if (null != getter) {
            type = getter.getReturnType();
        }
        if (null == type && null != setter) {
            type = TypeUtils.getFirstParamClass(setter);
        }
        return type;
    }

    /**
     * 字段和Getter方法是否为Transient关键字修饰的
     *
     * @return 是否为Transient关键字修饰的
     *
     * @since 5.3.11
     */
    private boolean isTransientForGet() {
        boolean isTransient = ModifierUtils.hasModifier(this.field, ModifierUtils.ModifierType.TRANSIENT);

        // 检查Getter方法
        if (false == isTransient && null != this.getter) {
            isTransient = ModifierUtils.hasModifier(this.getter, ModifierUtils.ModifierType.TRANSIENT);

            // 检查注解
            if (false == isTransient) {
                isTransient = AnnotationUtils.hasAnnotation(this.getter, Transient.class);
            }
        }

        return isTransient;
    }

    /**
     * 字段和Getter方法是否为Transient关键字修饰的
     *
     * @return 是否为Transient关键字修饰的
     *
     * @since 5.3.11
     */
    private boolean isTransientForSet() {
        boolean isTransient = ModifierUtils.hasModifier(this.field, ModifierUtils.ModifierType.TRANSIENT);

        // 检查Getter方法
        if (false == isTransient && null != this.setter) {
            isTransient = ModifierUtils.hasModifier(this.setter, ModifierUtils.ModifierType.TRANSIENT);

            // 检查注解
            if (false == isTransient) {
                isTransient = AnnotationUtils.hasAnnotation(this.setter, Transient.class);
            }
        }

        return isTransient;
    }
    //------------------------------------------------------------------------------------ Private method end
}
