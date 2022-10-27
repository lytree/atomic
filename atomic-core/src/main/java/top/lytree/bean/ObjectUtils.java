package top.lytree.bean;


import java.lang.reflect.Method;
import java.util.Arrays;

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

    public static boolean equal(Object a, Object b) {
        return a == b || (a != null && a.equals(b));
    }

    public static int hashCode(Object... objects) {
        return Arrays.hashCode(objects);
    }

}
