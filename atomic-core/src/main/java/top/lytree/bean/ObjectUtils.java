package top.lytree.bean;


import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author PrideYang
 */
public class ObjectUtils extends org.apache.commons.lang3.ObjectUtils {

    /**
     * 获取方法的唯一键，结构为:
     * <pre>
     *     返回类型#方法名:参数1类型,参数2类型...
     * </pre>
     *
     * @param method 方法
     *
     * @return 方法唯一键
     */
    public static String getUniqueKey(Method method) {
        final StringBuilder sb = new StringBuilder();
        sb.append(method.getReturnType().getName()).append('#');
        sb.append(method.getName());
        Class<?>[] parameters = method.getParameterTypes();
        for (int i = 0; i < parameters.length; i++) {
            if (i == 0) {
                sb.append(':');
            } else {
                sb.append(',');
            }
            sb.append(parameters[i].getName());
        }
        return sb.toString();
    }

    /**
     * 如果给定对象为{@code null} 返回默认值, 如果不为null 返回自定义handle处理后的返回值
     *
     * @param <T>             被检查对象类型
     * @param source          Object 类型对象
     * @param defaultSupplier 默认为空的处理逻辑
     *
     * @return 处理后的返回值
     *
     * @since 5.4.6
     */
    public static <T> T defaultIfNull(final T source, final Supplier<? extends T> defaultSupplier) {
        if (isNotNull(source)) {
            return source;
        }
        return defaultSupplier.get();
    }

    /**
     * 如果给定对象为{@code null} 返回默认值, 如果不为null 返回自定义handle处理后的返回值
     *
     * @param <R>             返回值类型
     * @param <T>             被检查对象类型
     * @param source          Object 类型对象
     * @param handler         非空时自定义的处理方法
     * @param defaultSupplier 默认为空的处理逻辑
     *
     * @return 处理后的返回值
     *
     * @since 6.0.0
     */
    public static <T, R> R defaultIfNull(final T source, final Function<? super T, ? extends R> handler, final Supplier<? extends R> defaultSupplier) {
        if (isNotNull(source)) {
            return handler.apply(source);
        }
        return defaultSupplier.get();
    }

    /**
     * 如果给定对象为{@code null}返回默认值, 如果不为null返回自定义handle处理后的返回值
     *
     * @param <R>          返回值类型
     * @param <T>          被检查对象类型
     * @param source       Object 类型对象
     * @param handler      非空时自定义的处理方法
     * @param defaultValue 默认为空的返回值
     *
     * @return 处理后的返回值
     *
     * @since 6.0.0
     */
    public static <T, R> R defaultIfNull(
            final T source, final Function<? super T, ? extends R> handler, final R defaultValue) {
        return isNull(source) ? defaultValue : handler.apply(source);
    }

    /**
     * 检查对象是否为{@code null}
     *
     * @param obj 对象
     *
     * @return 是否为null
     */
    public static boolean isNull(final Object obj) {
        return null == obj;
    }

    /**
     * 检查对象是否不为{@code null}
     *
     * @param obj 对象
     *
     * @return 是否为null
     */
    public static boolean isNotNull(final Object obj) {
        return null != obj;
    }

    public static boolean equal(Object a, Object b) {
        return a == b || (a != null && a.equals(b));
    }

    public static int hashCode(Object... objects) {
        return Arrays.hashCode(objects);
    }

}
