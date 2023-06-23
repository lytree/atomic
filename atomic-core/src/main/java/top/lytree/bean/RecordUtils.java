package top.lytree.bean;

import top.lytree.utils.JdkUtils;

import java.lang.reflect.Method;

public class RecordUtils {
    private static volatile Class<?> RECORD_CLASS;

    private static volatile Method METHOD_GET_RECORD_COMPONENTS;
    private static volatile Method METHOD_COMPONENT_GET_NAME;
    private static volatile Method METHOD_COMPONENT_GET_GENERIC_TYPE;

    /**
     * 判断给定类是否为Record类
     *
     * @param clazz 类
     * @return 是否为Record类
     */
    public static boolean isRecord(final Class<?> clazz) {
        if (JdkUtils.JVM_VERSION < 14) {
            // JDK14+支持Record类
            return false;
        }
        final Class<?> superClass = clazz.getSuperclass();
        if (superClass == null) {
            return false;
        }

        if (RECORD_CLASS == null) {
            // 此处不使用同步代码，重复赋值并不影响判断
            final String superclassName = superClass.getName();
            if ("java.lang.Record".equals(superclassName)) {
                RECORD_CLASS = superClass;
                return true;
            } else {
                return false;
            }
        }

        return superClass == RECORD_CLASS;
    }


}
