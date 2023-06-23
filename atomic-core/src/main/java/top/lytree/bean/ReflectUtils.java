package top.lytree.bean;

import top.lytree.utils.Assert;
import top.lytree.Filter;
import top.lytree.collections.ArrayUtils;
import top.lytree.convert.Convert;
import top.lytree.exception.BeanException;
import top.lytree.lang.StringUtils;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ReflectUtils {

    /**
     * 获取字段名，，读取注解的值作为名称
     *
     * @param field 字段
     * @return 字段名
     * @since 5.1.6
     */
    public static String getFieldName(Field field) {
        if (null == field) {
            return null;
        }

        return field.getName();
    }

    /**
     * 查找指定类中的指定name的字段（包括非public字段），也包括父类和Object类的字段， 字段不存在则返回{@code null}
     *
     * @param beanClass 被查找字段的类,不能为null
     * @param name      字段名
     * @return 字段
     * @throws SecurityException 安全异常
     */
    public static Field getField(Class<?> beanClass, String name) throws SecurityException {
        final Field[] fields = getFields(beanClass);
        return Arrays.stream(fields).filter((field) -> name.equals(getFieldName(field))).findFirst().orElse(null);

    }

    /**
     * 获取指定类中字段名和字段对应的有序Map，包括其父类中的字段<br>
     * 如果子类与父类中存在同名字段，则这两个字段同时存在，子类字段在前，父类字段在后。
     *
     * @param beanClass 类
     * @return 字段名和字段对应的Map，有序
     * @since 5.0.7
     */
    public static Map<String, Field> getFieldMap(Class<?> beanClass) {
        final Field[] fields = getFields(beanClass);
        final HashMap<String, Field> map = new HashMap<>(fields.length);
        for (Field field : fields) {
            map.put(field.getName(), field);
        }
        return map;
    }

    /**
     * 获得一个类中所有字段列表，包括其父类中的字段<br>
     * 如果子类与父类中存在同名字段，则这两个字段同时存在，子类字段在前，父类字段在后。
     *
     * @param beanClass 类
     * @return 字段列表
     * @throws SecurityException 安全检查异常
     */
    public static Field[] getFields(Class<?> beanClass) throws SecurityException {
        Assert.notNull(beanClass);
        return getFieldsDirectly(beanClass, true);
    }


    /**
     * 获得一个类中所有满足条件的字段列表，包括其父类中的字段<br>
     * 如果子类与父类中存在同名字段，则这两个字段同时存在，子类字段在前，父类字段在后。
     *
     * @param beanClass   类
     * @param fieldFilter field过滤器，过滤掉不需要的field
     * @return 字段列表
     * @throws SecurityException 安全检查异常
     * @since 5.7.14
     */
    public static Field[] getFields(Class<?> beanClass, Filter<Field> fieldFilter) throws SecurityException {
        return Arrays.stream(getFields(beanClass)).filter(fieldFilter::accept).toList().toArray(new Field[]{});
    }

    /**
     * 获得一个类中所有字段列表，直接反射获取，无缓存<br>
     * 如果子类与父类中存在同名字段，则这两个字段同时存在，子类字段在前，父类字段在后。
     *
     * @param beanClass            类
     * @param withSuperClassFields 是否包括父类的字段列表
     * @return 字段列表
     * @throws SecurityException 安全检查异常
     */
    public static Field[] getFieldsDirectly(Class<?> beanClass, boolean withSuperClassFields) throws SecurityException {
        Assert.notNull(beanClass);

        Field[] allFields = null;
        Class<?> searchType = beanClass;
        Field[] declaredFields;
        while (searchType != null) {
            declaredFields = searchType.getDeclaredFields();
            if (null == allFields) {
                allFields = declaredFields;
            } else {
                allFields = ArrayUtils.append(allFields, declaredFields);
            }
            searchType = withSuperClassFields ? searchType.getSuperclass() : null;
        }

        return allFields;
    }

    /**
     * 获取字段值
     *
     * @param obj       对象，如果static字段，此处为类
     * @param fieldName 字段名
     * @return 字段值
     * @throws BeanException 包装IllegalAccessException异常
     */

    public static Object getFieldValue(Object obj, String fieldName) throws BeanException {
        if (null == obj || StringUtils.isBlank(fieldName)) {
            return null;
        }
        return getFieldValue(obj, getField(obj instanceof Class ? (Class<?>) obj : obj.getClass(), fieldName));
    }

    /**
     * 获取静态字段值
     *
     * @param field 字段
     * @return 字段值
     * @throws BeanException 包装IllegalAccessException异常
     * @since 5.1.0
     */
    public static Object getStaticFieldValue(Field field) throws BeanException {
        return getFieldValue(null, field);
    }

    /**
     * 获取字段值
     *
     * @param obj   对象，static字段则此字段为null
     * @param field 字段
     * @return 字段值
     * @throws BeanException 包装IllegalAccessException异常
     */
    public static Object getFieldValue(Object obj, Field field) throws BeanException {
        if (null == field) {
            return null;
        }
        if (obj instanceof Class) {
            // 静态字段获取时对象为null
            obj = null;
        }

        setAccessible(field);
        Object result;
        try {
            result = field.get(obj);
        } catch (IllegalAccessException e) {
            throw new BeanException(e, "IllegalAccess for {}.{}", field.getDeclaringClass(), field.getName());
        }
        return result;
    }

    /**
     * 获取所有字段的值
     *
     * @param obj bean对象，如果是static字段，此处为类class
     * @return 字段值数组
     * @since 4.1.17
     */
    public static Object[] getFieldsValue(Object obj) {
        if (null != obj) {
            final Field[] fields = getFields(obj instanceof Class ? (Class<?>) obj : obj.getClass());
            if (null != fields) {
                final Object[] values = new Object[fields.length];
                for (int i = 0; i < fields.length; i++) {
                    values[i] = getFieldValue(obj, fields[i]);
                }
                return values;
            }
        }
        return null;
    }

    /**
     * 设置final的field字段可以被修改
     * 只要不会被编译器内联优化的 final 属性就可以通过反射有效的进行修改 --  修改后代码中可使用到新的值;
     * <p>以下属性，编译器会内联优化，无法通过反射修改：</p>
     * <ul>
     *     <li> 基本类型 byte, char, short, int, long, float, double, boolean</li>
     *     <li> Literal String 类型(直接双引号字符串)</li>
     * </ul>
     * <h3>以下属性，可以通过反射修改：</h3>
     * <ul>
     *     <li>基本类型的包装类 Byte、Character、Short、Long、Float、Double、Boolean</li>
     *     <li>字符串，通过 new String("")实例化</li>
     *     <li>自定义java类</li>
     * </ul>
     * <pre class="code">
     * {@code
     *      //示例，移除final修饰符
     *      class JdbcDialects {private static final List<Number> dialects = new ArrayList<>();}
     *      Field field = ReflectUtil.getField(JdbcDialects.class, fieldName);
     * 		ReflectUtil.removeFinalModify(field);
     * 		ReflectUtil.setFieldValue(JdbcDialects.class, fieldName, dialects);
     *    }
     * </pre>
     *
     * @param field 被修改的field，不可以为空
     * @throws BeanException IllegalAccessException等异常包装
     * @author dazer
     * @since 5.8.8
     */
    public static void removeFinalModify(Field field) {
        ModifierUtils.removeFinalModify(field);
    }

    /**
     * 设置方法为可访问（私有方法可以被外部调用）
     *
     * @param <T>              AccessibleObject的子类，比如Class、Method、Field等
     * @param accessibleObject 可设置访问权限的对象，比如Class、Method、Field等
     * @return 被设置可访问的对象
     * @since 4.6.8
     */
    public static <T extends AccessibleObject> T setAccessible(T accessibleObject) {
        if (null != accessibleObject && false == accessibleObject.isAccessible()) {
            accessibleObject.setAccessible(true);
        }
        return accessibleObject;
    }

    /**
     * 设置字段值<br>
     * 若值类型与字段类型不一致，则会尝试通过 {@link Convert} 进行转换<br>
     * 若字段类型是原始类型而传入的值是 null，则会将字段设置为对应原始类型的默认值（见 {@link ClassUtils#getDefaultValue(Class)}）
     * 如果是final字段，setFieldValue，调用这可以先调用 {@link ReflectUtils#removeFinalModify(Field)}方法去除final修饰符<br>
     *
     * @param obj       对象,static字段则此处传Class
     * @param fieldName 字段名
     * @param value     值，当值类型与字段类型不匹配时，会尝试转换
     * @throws BeanException
     */
    public static void setFieldValue(Object obj, String fieldName, Object value) throws BeanException {
        Assert.notNull(obj);
        Assert.notBlank(fieldName);

        final Field field = getField((obj instanceof Class) ? (Class<?>) obj : obj.getClass(), fieldName);
        Assert.notNull(field, "Field [{}] is not exist in [{}]", fieldName, obj.getClass().getName());
        setFieldValue(obj, field, value);
    }

    /**
     * 设置字段值<br>
     * 若值类型与字段类型不一致，则会尝试通过 {@link Convert} 进行转换<br>
     * 若字段类型是原始类型而传入的值是 null，则会将字段设置为对应原始类型的默认值（见 {@link ClassUtils#getDefaultValue(Class)}）<br>
     * 如果是final字段，setFieldValue，调用这可以先调用 {@link ReflectUtils#removeFinalModify(Field)}方法去除final修饰符
     *
     * @param obj   对象，如果是static字段，此参数为null
     * @param field 字段
     * @param value 值，当值类型与字段类型不匹配时，会尝试转换
     * @throws BeanException UtilException 包装IllegalAccessException异常
     */
    public static void setFieldValue(Object obj, Field field, Object value) throws BeanException {
        Assert.notNull(field, "Field in [{}] not exist !", obj);

        final Class<?> fieldType = field.getType();
        if (null != value) {
            if (false == fieldType.isAssignableFrom(value.getClass())) {
                //todo 暂时不进行转换
                //对于类型不同的字段，尝试转换，转换失败则使用原对象类型
                final Object targetValue = Convert.convert(fieldType, value);
                if (null != targetValue) {
                    value = targetValue;
                }
            }
        } else {
            // 获取null对应默认值，防止原始类型造成空指针问题
            value = ClassUtils.getDefaultValue(fieldType);
        }

        setAccessible(field);
        try {
            field.set(obj instanceof Class ? null : obj, value);
        } catch (IllegalAccessException e) {
            throw new BeanException(e, "IllegalAccess for {}.{}", obj, field.getName());
        }
    }
}
