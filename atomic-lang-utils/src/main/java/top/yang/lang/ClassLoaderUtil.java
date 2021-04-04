package top.yang.lang;

public class ClassLoaderUtil {
    /**
     * 获取当前线程的{@link ClassLoader}
     *
     * @return 当前线程的class loader
     * @see Thread#getContextClassLoader()
     */
    public static ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    /**
     * 获取{@link ClassLoader}<br>
     * 获取顺序如下：<br>
     *
     * <pre>
     * 1、获取当前线程的ContextClassLoader
     * 2、获取当前类对应的ClassLoader
     * 3、获取系统ClassLoader（{@link ClassLoader#getSystemClassLoader()}）
     * </pre>
     *
     * @return 类加载器
     */
    public static ClassLoader getClassLoader() {
        ClassLoader classLoader = getContextClassLoader();
        if (classLoader == null) {
            classLoader = ClassLoaderUtil.class.getClassLoader();
            if (null == classLoader) {
                classLoader = ClassLoader.getSystemClassLoader();
            }
        }
        return classLoader;
    }
}
