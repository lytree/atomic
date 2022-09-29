package top.yang.bean;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.ClassUtils;
import top.yang.base.Filter;
import top.yang.collections.ArrayUtils;
import top.yang.collections.UniqueKeySet;
import top.yang.base.Assert;

/**
 * @author PrideYang
 */
public class MethodUtils extends org.apache.commons.lang3.reflect.MethodUtils {

    /**
     * 获得指定类过滤后的Public方法列表
     *
     * @param clazz  查找方法的类
     * @param filter 过滤器
     * @return 过滤后的方法列表
     * @throws SecurityException 安全异常
     */
    public static Method[] getMethods(Class<?> clazz, Filter<Method> filter) throws SecurityException {
        if (null == clazz) {
            return null;
        }
        return ArrayUtils.filter(getMethods(clazz), filter);
    }

    /**
     * 获得一个类中所有方法列表，包括其父类中的方法
     *
     * @param beanClass 类，非{@code null}
     * @return 方法列表
     * @throws SecurityException 安全检查异常
     */
    public static Method[] getMethods(Class<?> beanClass) throws SecurityException {
        Assert.notNull(beanClass);
        return getMethodsDirectly(beanClass, true, true);
    }

    /**
     * 获得一个类中所有方法列表，直接反射获取，无缓存<br> 接口获取方法和默认方法，获取的方法包括：
     * <ul>
     *     <li>本类中的所有方法（包括static方法）</li>
     *     <li>父类中的所有方法（包括static方法）</li>
     *     <li>Object中（包括static方法）</li>
     * </ul>
     *
     * @param beanClass            类或接口
     * @param withSupers           是否包括父类或接口的方法列表
     * @param withMethodFromObject 是否包括Object中的方法
     * @return 方法列表
     * @throws SecurityException 安全检查异常
     */
    public static Method[] getMethodsDirectly(Class<?> beanClass, boolean withSupers, boolean withMethodFromObject) throws SecurityException {
        Assert.notNull(beanClass);

        if (beanClass.isInterface()) {
            // 对于接口，直接调用Class.getMethods方法获取所有方法，因为接口都是public方法
            return withSupers ? beanClass.getMethods() : beanClass.getDeclaredMethods();
        }

        final UniqueKeySet<String, Method> result = new UniqueKeySet<>(true, ObjectUtils::getUniqueKey);
        Class<?> searchType = beanClass;
        while (searchType != null) {
            if (false == withMethodFromObject && Object.class == searchType) {
                break;
            }
            result.addAllIfAbsent(Arrays.asList(searchType.getDeclaredMethods()));
            result.addAllIfAbsent(getDefaultMethodsFromInterface(searchType));

            searchType = (withSupers && false == searchType.isInterface()) ? searchType.getSuperclass() : null;
        }

        return result.toArray(new Method[0]);
    }


    /**
     * <p>调用参数类型与对象类型匹配的命名方法。</p>
     *
     * <p>此方法支持通过在封装类中传递原语参数来调用方法。举个例子, {@code Boolean}对象将匹配{@code Boolean}原语.</p>
     *
     * @param object         调用此对象上的方法
     * @param forceAccess    强制访问调用方法，即使该方法不可访问
     * @param methodName     使用此名称获取方法
     * @param args           使用这些参数-将null视为空数组
     * @param parameterTypes 匹配这些参数-将null视为空数组
     * @return 被调用方法返回的值
     * @throws NoSuchMethodException     如果没有这样的通达方法
     * @throws InvocationTargetException 包装由调用的方法引发的异常
     * @throws IllegalAccessException    如果请求的方法不能通过反射访问
     * 
     */
    public static Object invokeMethod(final Object object, final boolean forceAccess, final String methodName,
            Object[] args, Class<?>[] parameterTypes)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        parameterTypes = org.apache.commons.lang3.ArrayUtils.nullToEmpty(parameterTypes);
        args = org.apache.commons.lang3.ArrayUtils.nullToEmpty(args);

        final String messagePrefix;
        Method method = null;

        if (forceAccess) {
            messagePrefix = "No such method: ";
            method = getMatchingMethod(object.getClass(),
                    methodName, parameterTypes);
            if (method != null && !method.isAccessible()) {
                method.setAccessible(true);
            }
        } else {
            messagePrefix = "No such accessible method: ";
            method = getMatchingAccessibleMethod(object.getClass(),
                    methodName, parameterTypes);
        }

        if (method == null) {
            throw new NoSuchMethodException(messagePrefix
                    + methodName + "() on object: "
                    + object.getClass().getName());
        }
        args = toVarArgs(method, args);

        return method.invoke(object, args);
    }

    /**
     * 获取类对应接口中的非抽象方法（default方法）
     *
     * @param clazz 类
     * @return 方法列表
     */
    private static List<Method> getDefaultMethodsFromInterface(Class<?> clazz) {
        List<Method> result = new ArrayList<>();
        for (Class<?> ifc : clazz.getInterfaces()) {
            for (Method m : ifc.getMethods()) {
                if (!ModifierUtil.isAbstract(m)) {
                    result.add(m);
                }
            }
        }
        return result;
    }

    private static Object[] toVarArgs(final Method method, Object[] args) {
        if (method.isVarArgs()) {
            final Class<?>[] methodParameterTypes = method.getParameterTypes();
            args = getVarArgs(args, methodParameterTypes);
        }
        return args;
    }

    /**
     * <p>给定传递给varargs方法的参数数组，返回一个标准形式的参数数组，即一个声明了参数数量的数组，其最后一个参数是varargs类型的数组。
     * </p>
     *
     * @param args                 传递给varags方法的参数数组
     * @param methodParameterTypes 方法参数类型声明的数组
     * @return 传递给方法的可变参数数组
     * 
     */
    static Object[] getVarArgs(final Object[] args, final Class<?>[] methodParameterTypes) {
        if (args.length == methodParameterTypes.length && (args[args.length - 1] == null ||
                args[args.length - 1].getClass().equals(methodParameterTypes[methodParameterTypes.length - 1]))) {
            // The args array is already in the canonical form for the method.
            return args;
        }

        // Construct a new array matching the method's declared parameter types.
        final Object[] newArgs = new Object[methodParameterTypes.length];

        // Copy the normal (non-varargs) parameters
        System.arraycopy(args, 0, newArgs, 0, methodParameterTypes.length - 1);

        // Construct a new array for the variadic parameters
        final Class<?> varArgComponentType = methodParameterTypes[methodParameterTypes.length - 1].getComponentType();
        final int varArgLength = args.length - methodParameterTypes.length + 1;

        Object varArgsArray = Array.newInstance(ClassUtils.primitiveToWrapper(varArgComponentType), varArgLength);
        // Copy the variadic arguments into the varargs array.
        System.arraycopy(args, methodParameterTypes.length - 1, varArgsArray, 0, varArgLength);

        if (varArgComponentType.isPrimitive()) {
            // unbox from wrapper type to primitive type
            varArgsArray = org.apache.commons.lang3.ArrayUtils.toPrimitive(varArgsArray);
        }

        // Store the varargs array in the last position of the array to return
        newArgs[methodParameterTypes.length - 1] = varArgsArray;

        // Return the canonical varargs array.
        return newArgs;
    }
}
