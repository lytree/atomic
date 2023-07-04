package top.lytree.bean;


import top.lytree.exception.CloneFailedException;
import top.lytree.lang.StreamUtil;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author PrideYang
 */
public class ObjectUtils {

    /**
     * 获取方法的唯一键，结构为:
     * <pre>
     *     返回类型#方法名:参数1类型,参数2类型...
     * </pre>
     *
     * @param method 方法
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
     * Returns a default value if the object passed is {@code null}.
     *
     * <pre>
     * ObjectUtils.defaultIfNull(null, null)      = null
     * ObjectUtils.defaultIfNull(null, "")        = ""
     * ObjectUtils.defaultIfNull(null, "zz")      = "zz"
     * ObjectUtils.defaultIfNull("abc", *)        = "abc"
     * ObjectUtils.defaultIfNull(Boolean.TRUE, *) = Boolean.TRUE
     * </pre>
     *
     * @param <T>          the type of the object
     * @param object       the {@link Object} to test, may be {@code null}
     * @param defaultValue the default value to return, may be {@code null}
     * @return {@code object} if it is not {@code null}, defaultValue otherwise
     * TODO Rename to getIfNull in 4.0
     */
    public static <T> T defaultIfNull(final T object, final T defaultValue) {
        return object != null ? object : defaultValue;
    }

    /**
     * 如果给定对象为{@code null} 返回默认值, 如果不为null 返回自定义handle处理后的返回值
     *
     * @param <T>             被检查对象类型
     * @param source          Object 类型对象
     * @param defaultSupplier 默认为空的处理逻辑
     * @return 处理后的返回值
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
     * @return 处理后的返回值
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
     * @return 处理后的返回值
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
     * @return 是否为null
     */
    public static boolean isNull(final Object obj) {
        return null == obj;
    }

    /**
     * 检查对象是否不为{@code null}
     *
     * @param obj 对象
     * @return 是否为null
     */
    public static boolean isNotNull(final Object obj) {
        return null != obj;
    }

    /**
     * Returns the first value in the array which is not {@code null}.
     * If all the values are {@code null} or the array is {@code null}
     * or empty then {@code null} is returned.
     *
     * <pre>
     * ObjectUtils.firstNonNull(null, null)      = null
     * ObjectUtils.firstNonNull(null, "")        = ""
     * ObjectUtils.firstNonNull(null, null, "")  = ""
     * ObjectUtils.firstNonNull(null, "zz")      = "zz"
     * ObjectUtils.firstNonNull("abc", *)        = "abc"
     * ObjectUtils.firstNonNull(null, "xyz", *)  = "xyz"
     * ObjectUtils.firstNonNull(Boolean.TRUE, *) = Boolean.TRUE
     * ObjectUtils.firstNonNull()                = null
     * </pre>
     *
     * @param <T>    the component type of the array
     * @param values the values to test, may be {@code null} or empty
     * @return the first value from {@code values} which is not {@code null},
     * or {@code null} if there are no non-null values
     * @since 3.0
     */
    @SafeVarargs
    public static <T> T firstNonNull(final T... values) {
        return StreamUtil.of(values).filter(Objects::nonNull).findFirst().orElse(null);
    }

    /**
     * Checks if all values in the array are not {@code nulls}.
     *
     * <p>
     * If any value is {@code null} or the array is {@code null} then
     * {@code false} is returned. If all elements in array are not
     * {@code null} or the array is empty (contains no elements) {@code true}
     * is returned.
     * </p>
     *
     * <pre>
     * ObjectUtils.allNotNull(*)             = true
     * ObjectUtils.allNotNull(*, *)          = true
     * ObjectUtils.allNotNull(null)          = false
     * ObjectUtils.allNotNull(null, null)    = false
     * ObjectUtils.allNotNull(null, *)       = false
     * ObjectUtils.allNotNull(*, null)       = false
     * ObjectUtils.allNotNull(*, *, null, *) = false
     * </pre>
     *
     * @param values the values to test, may be {@code null} or empty
     * @return {@code false} if there is at least one {@code null} value in the array or the array is {@code null},
     * {@code true} if all values in the array are not {@code null}s or array contains no elements.
     * @since 3.5
     */
    public static boolean allNotNull(final Object... values) {
        return values != null && StreamUtil.of(values).noneMatch(Objects::isNull);
    }

    /**
     * Checks if all values in the given array are {@code null}.
     *
     * <p>
     * If all the values are {@code null} or the array is {@code null}
     * or empty, then {@code true} is returned, otherwise {@code false} is returned.
     * </p>
     *
     * <pre>
     * ObjectUtils.allNull(*)                = false
     * ObjectUtils.allNull(*, null)          = false
     * ObjectUtils.allNull(null, *)          = false
     * ObjectUtils.allNull(null, null, *, *) = false
     * ObjectUtils.allNull(null)             = true
     * ObjectUtils.allNull(null, null)       = true
     * </pre>
     *
     * @param values the values to test, may be {@code null} or empty
     * @return {@code true} if all values in the array are {@code null}s,
     * {@code false} if there is at least one non-null value in the array.
     * @since 3.11
     */
    public static boolean allNull(final Object... values) {
        return !anyNotNull(values);
    }

    /**
     * Checks if any value in the given array is not {@code null}.
     *
     * <p>
     * If all the values are {@code null} or the array is {@code null}
     * or empty then {@code false} is returned. Otherwise {@code true} is returned.
     * </p>
     *
     * <pre>
     * ObjectUtils.anyNotNull(*)                = true
     * ObjectUtils.anyNotNull(*, null)          = true
     * ObjectUtils.anyNotNull(null, *)          = true
     * ObjectUtils.anyNotNull(null, null, *, *) = true
     * ObjectUtils.anyNotNull(null)             = false
     * ObjectUtils.anyNotNull(null, null)       = false
     * </pre>
     *
     * @param values the values to test, may be {@code null} or empty
     * @return {@code true} if there is at least one non-null value in the array,
     * {@code false} if all values in the array are {@code null}s.
     * If the array is {@code null} or empty {@code false} is also returned.
     * @since 3.5
     */
    public static boolean anyNotNull(final Object... values) {
        return firstNonNull(values) != null;
    }

    /**
     * Checks if any value in the given array is {@code null}.
     *
     * <p>
     * If any of the values are {@code null} or the array is {@code null},
     * then {@code true} is returned, otherwise {@code false} is returned.
     * </p>
     *
     * <pre>
     * ObjectUtils.anyNull(*)             = false
     * ObjectUtils.anyNull(*, *)          = false
     * ObjectUtils.anyNull(null)          = true
     * ObjectUtils.anyNull(null, null)    = true
     * ObjectUtils.anyNull(null, *)       = true
     * ObjectUtils.anyNull(*, null)       = true
     * ObjectUtils.anyNull(*, *, null, *) = true
     * </pre>
     *
     * @param values the values to test, may be {@code null} or empty
     * @return {@code true} if there is at least one {@code null} value in the array,
     * {@code false} if all the values are non-null.
     * If the array is {@code null} or empty, {@code true} is also returned.
     * @since 3.11
     */
    public static boolean anyNull(final Object... values) {
        return !allNotNull(values);
    }

    /**
     * Checks, whether the given object is an Object array or a primitive array in a null-safe manner.
     *
     * <p>
     * A {@code null} {@code object} Object will return {@code false}.
     * </p>
     *
     * <pre>
     * ObjectUtils.isArray(null)             = false
     * ObjectUtils.isArray("")               = false
     * ObjectUtils.isArray("ab")             = false
     * ObjectUtils.isArray(new int[]{})      = true
     * ObjectUtils.isArray(new int[]{1,2,3}) = true
     * ObjectUtils.isArray(1234)             = false
     * </pre>
     *
     * @param object the object to check, may be {@code null}
     * @return {@code true} if the object is an {@code array}, {@code false} otherwise
     * @since 3.13.0
     */
    public static boolean isArray(final Object object) {
        return object != null && object.getClass().isArray();
    }

    /**
     * Checks if an Object is empty or null.
     * <p>
     * The following types are supported:
     * <ul>
     * <li>{@link CharSequence}: Considered empty if its length is zero.</li>
     * <li>{@link Array}: Considered empty if its length is zero.</li>
     * <li>{@link Collection}: Considered empty if it has zero elements.</li>
     * <li>{@link Map}: Considered empty if it has zero key-value mappings.</li>
     * <li>{@link Optional}: Considered empty if {@link Optional#isPresent} returns false, regardless of the "emptiness" of the contents.</li>
     * </ul>
     *
     * <pre>
     * ObjectUtils.isEmpty(null)             = true
     * ObjectUtils.isEmpty("")               = true
     * ObjectUtils.isEmpty("ab")             = false
     * ObjectUtils.isEmpty(new int[]{})      = true
     * ObjectUtils.isEmpty(new int[]{1,2,3}) = false
     * ObjectUtils.isEmpty(1234)             = false
     * ObjectUtils.isEmpty(1234)             = false
     * ObjectUtils.isEmpty(Optional.of(""))  = false
     * ObjectUtils.isEmpty(Optional.empty()) = true
     * </pre>
     *
     * @param object the {@link Object} to test, may be {@code null}
     * @return {@code true} if the object has a supported type and is empty or null,
     * {@code false} otherwise
     * @since 3.9
     */
    public static boolean isEmpty(final Object object) {
        if (object == null) {
            return true;
        }
        if (object instanceof CharSequence) {
            return ((CharSequence) object).length() == 0;
        }
        if (isArray(object)) {
            return Array.getLength(object) == 0;
        }
        if (object instanceof Collection<?>) {
            return ((Collection<?>) object).isEmpty();
        }
        if (object instanceof Map<?, ?>) {
            return ((Map<?, ?>) object).isEmpty();
        }
        if (object instanceof Optional<?>) {
            // TODO Java 11 Use Optional#isEmpty()
            return !((Optional<?>) object).isPresent();
        }
        return false;
    }
    /**
     * Delegates to {@link Object#getClass()} using generics.
     *
     * @param <T> The argument type or null.
     * @param object The argument.
     * @return The argument Class or null.
     * @since 3.13.0
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> getClass(final T object) {
        return object == null ? null : (Class<T>) object.getClass();
    }
    /**
     * Clone an object.
     *
     * @param <T> the type of the object
     * @param obj  the object to clone, null returns null
     * @return the clone if the object implements {@link Cloneable} otherwise {@code null}
     * @throws CloneFailedException if the object is cloneable and the clone operation fails
     * @since 3.0
     */
    public static <T> T clone(final T obj) {
        if (obj instanceof Cloneable) {
            final Object result;
            if (isArray(obj)) {
                final Class<?> componentType = obj.getClass().getComponentType();
                if (componentType.isPrimitive()) {
                    int length = Array.getLength(obj);
                    result = Array.newInstance(componentType, length);
                    while (length-- > 0) {
                        Array.set(result, length, Array.get(obj, length));
                    }
                } else {
                    result = ((Object[]) obj).clone();
                }
            } else {
                try {
                    final Method clone = obj.getClass().getMethod("clone");
                    result = clone.invoke(obj);
                } catch (final NoSuchMethodException e) {
                    throw new CloneFailedException("Cloneable type "
                            + obj.getClass().getName()
                            + " has no clone method", e);
                } catch (final IllegalAccessException e) {
                    throw new CloneFailedException("Cannot clone Cloneable type "
                            + obj.getClass().getName(), e);
                } catch (final InvocationTargetException e) {
                    throw new CloneFailedException("Exception cloning Cloneable type "
                            + obj.getClass().getName(), e.getCause());
                }
            }
            @SuppressWarnings("unchecked") // OK because input is of type T
            final T checked = (T) result;
            return checked;
        }

        return null;
    }

}
