package top.yang.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.util.List;
import java.util.function.Supplier;
import top.yang.lang.StringUtils;

/**
 * @author PrideYang
 */
public class JSONArray extends JSON {

    public static <T> List<T> parseArray(String value, Class<T> tClass) {
        return StringUtils.isNotBlank(value) ? parseArray(value, tClass, () -> null) : null;
    }

    public static <T> List<T> parseArray(Object obj, Class<T> tClass) {
        return obj != null ? parseArray(toJSONString(obj), tClass, () -> null) : null;
    }


    public static List<Object> parseArray(String value) {
        return StringUtils.isNotBlank(value) ? parseArray(value, Object.class, () -> null) : null;
    }

    public static List<Object> parseArray(Object value, Supplier<List<Object>> defaultSupplier) {
        try {
            if (null == value) {
                return defaultSupplier.get();
            }
            if (value instanceof List) {
                return (List<Object>) value;
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("toJavaObjectList exception \n%s", value), e);
        }
        return parseArray(toJSONString(value), Object.class, defaultSupplier);
    }

    public static <T> List<T> parseArray(String value, Class<T> tClass, Supplier<List<T>> defaultSupplier) {
        try {
            if (StringUtils.isBlank(value)) {
                return defaultSupplier.get();
            }
            JavaType javaType = MAPPER.getTypeFactory().constructParametricType(List.class, tClass);
            return MAPPER.readValue(value, javaType);
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("toJavaObjectList exception \n%s\n%s", value, tClass), e);
        }
    }

    public static ArrayNode parseJSONArray(String value) {
        return parseJSONArray(value, () -> null);
    }

    public static ArrayNode parseJSONArray(Object value) {
        return parseJSONArray(value, () -> null);
    }

    /**
     * JSON字符串转换为JSON数组
     *
     * @param value JSON数组字符串
     * @return ArrayNode
     */
    public static ArrayNode parseJSONArray(String value, Supplier<ArrayNode> defaultSupplier) {
        if (StringUtils.isNotBlank(value)) {
            return defaultSupplier.get();
        }
        try {
            return (ArrayNode) MAPPER.readTree(value);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Object类型转换为JSON数组
     *
     * @param object 当前对象
     * @return ArrayNode
     */
    public static ArrayNode parseJSONArray(Object object, Supplier<ArrayNode> defaultSupplier) {
        if (object == null || StringUtils.isBlank(object.toString())) {
            return defaultSupplier.get();
        }
        try {
            if (ArrayNode.class.isAssignableFrom(object.getClass())) {
                return (ArrayNode) object;
            }
            if (object instanceof String) {
                return (ArrayNode) MAPPER.readTree((String) object);
            }
            return (ArrayNode) MAPPER.readTree(toJSONString(object));
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    // 简单地直接用json复制或者转换(Cloneable)
    public static <T> List<T> jsonCopy(Object obj, Class<T> tClass) {
        return obj != null ? parseArray(toJSONString(obj), tClass) : null;
    }

    /**
     * 创建一个JSON数组
     *
     * @return ArrayNode
     */
    public static ArrayNode newJSONArray() {
        return MAPPER.createArrayNode();
    }
}
