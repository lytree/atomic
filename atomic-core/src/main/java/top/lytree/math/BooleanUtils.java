package top.lytree.math;

public class BooleanUtils {

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

    /**
     * Compares two {@code boolean} values. This is the same functionality as provided in Java 7.
     *
     * @param x the first {@code boolean} to compare
     * @param y the second {@code boolean} to compare
     * @return the value {@code 0} if {@code x == y};
     *         a value less than {@code 0} if {@code !x && y}; and
     *         a value greater than {@code 0} if {@code x && !y}
     * @since 3.4
     */
    public static int compare(final boolean x, final boolean y) {
        if (x == y) {
            return 0;
        }
        return x ? 1 : -1;
    }
}
