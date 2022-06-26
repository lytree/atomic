package top.yang.bean;


import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import top.yang.collections.ArrayUtils;

public class AnnotationUtils extends org.apache.commons.lang3.AnnotationUtils {

    /**
     * 获取指定注解
     *
     * @param annotationEle {@link AnnotatedElement}，可以是Class、Method、Field、Constructor、ReflectPermission
     * @return 注解对象
     */
    public static Annotation[] getAnnotations(AnnotatedElement annotationEle, boolean isToCombination) {
        return getAnnotations(annotationEle, (Predicate<Annotation>) null);
    }

    /**
     * 获取指定注解
     *
     * @param <T>            注解类型
     * @param annotationEle  {@link AnnotatedElement}，可以是Class、Method、Field、Constructor、ReflectPermission
     * @param annotationType 限定的
     * @return 注解对象数组
     * @since 5.8.0
     */
    public static <T> T[] getAnnotations(AnnotatedElement annotationEle, Class<T> annotationType) {
        final Annotation[] annotations = getAnnotations(annotationEle,
                (annotation -> null == annotationType || annotationType.isAssignableFrom(annotation.getClass())));

        final T[] result = ArrayUtils.newArray(annotationType, annotations.length);
        for (int i = 0; i < annotations.length; i++) {
            //noinspection unchecked
            result[i] = (T) annotations[i];
        }
        return result;
    }

    /**
     * 获取指定注解
     *
     * @param annotationEle {@link AnnotatedElement}，可以是Class、Method、Field、Constructor、ReflectPermission
     * @param predicate     过滤器，{@link Predicate#test(Object)}返回{@code true}保留，否则不保留
     * @return 注解对象
     * @since 5.8.0
     */
    public static Annotation[] getAnnotations(AnnotatedElement annotationEle, Predicate<Annotation> predicate) {
        if (null == annotationEle) {
            return null;
        }

        final Annotation[] result = annotationEle.getAnnotations();
        if (null == predicate) {
            return result;
        }
        return ArrayUtils.filter(result, predicate::test);
    }

    /**
     * 获取指定注解
     *
     * @param <A>            注解类型
     * @param annotationEle  {@link AnnotatedElement}，可以是Class、Method、Field、Constructor、ReflectPermission
     * @param annotationType 注解类型
     * @return 注解对象
     */
    public static <A extends Annotation> A getAnnotation(AnnotatedElement annotationEle, Class<A> annotationType) {
        return (null == annotationEle) ? null : annotationEle.getAnnotation(annotationType);
    }

    /**
     * 检查是否包含指定注解指定注解
     *
     * @param annotationEle  {@link AnnotatedElement}，可以是Class、Method、Field、Constructor、ReflectPermission
     * @param annotationType 注解类型
     * @return 是否包含指定注解
     * @since 5.4.2
     */
    public static boolean hasAnnotation(AnnotatedElement annotationEle, Class<? extends Annotation> annotationType) {
        return null != getAnnotation(annotationEle, annotationType);
    }
}
