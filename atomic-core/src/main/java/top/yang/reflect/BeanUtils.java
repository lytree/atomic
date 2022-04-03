package top.yang.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * @author PrideYang
 */
public class BeanUtils {

    /**
     * 判断是否为Bean对象，判定方法是：
     *
     * <pre>
     *     1、是否存在只有一个参数的setXXX方法
     *     2、是否存在public类型的字段
     * </pre>
     *
     * @param clazz 待测试类
     * @return 是否为Bean对象
     * @see #hasSetter(Class)
     * @see #hasPublicField(Class)
     */
    public static boolean isBean(Class<?> clazz) {
        return hasSetter(clazz) || hasPublicField(clazz);
    }

    /**
     * 判断是否有Setter方法<br> 判定方法是否存在只有一个参数的setXXX方法
     *
     * @param clazz 待测试类
     * @return 是否为Bean对象
     * @since 4.2.2
     */
    public static boolean hasSetter(Class<?> clazz) {
        if (ClassUtils.isNormalClass(clazz)) {
            for (Method method : clazz.getMethods()) {
                if (method.getParameterCount() == 1 && method.getName().startsWith("set")) {
                    // 检测包含标准的setXXX方法即视为标准的JavaBean
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断是否为Bean对象<br> 判定方法是否存在只有无参数的getXXX方法或者isXXX方法
     *
     * @param clazz 待测试类
     * @return 是否为Bean对象
     * @since 4.2.2
     */
    public static boolean hasGetter(Class<?> clazz) {
        if (ClassUtils.isNormalClass(clazz)) {
            for (Method method : clazz.getMethods()) {
                if (method.getParameterCount() == 0) {
                    if (method.getName().startsWith("get") || method.getName().startsWith("is")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 指定类中是否有public类型字段(static字段除外)
     *
     * @param clazz 待测试类
     * @return 是否有public类型字段
     * @since 5.1.0
     */
    public static boolean hasPublicField(Class<?> clazz) {
        if (ClassUtils.isNormalClass(clazz)) {
            for (Field field : clazz.getFields()) {
                if (FieldUtils.isPublic(field) && !FieldUtils.isStatic(field)) {
                    //非static的public字段
                    return true;
                }
            }
        }
        return false;
    }


}
