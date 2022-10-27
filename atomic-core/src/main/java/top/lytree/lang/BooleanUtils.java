package top.lytree.lang;

public class BooleanUtils extends org.apache.commons.lang3.BooleanUtils {

    /**
     * 给定类是否为Boolean或者boolean
     *
     * @param clazz 类
     *
     * @return 是否为Boolean或者boolean
     *
     * @since 4.5.2
     */
    public static boolean isBoolean(final Class<?> clazz) {
        return (clazz == Boolean.class || clazz == boolean.class);
    }
}
