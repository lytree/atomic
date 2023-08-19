package top.lytree.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import top.lytree.bean.ObjectUtils;
import top.lytree.collections.ArrayUtils;
import top.lytree.lang.StringUtils;

/**
 * @author PrideYang
 */
public class JSONObject extends JSON {



    public static Object parseObject(String value) {
        return parseObject(value, Object.class);
    }

    public static <T> T parseObject(String value, Class<T> tClass) {
        return StringUtils.isNotBlank(value) ? parseObject(value, tClass, () -> null) : null;
    }

    public static <T> T parseObject(Object value, Class<T> tClass) {
        return value != null ? parseObject(toJSONString(value), tClass, () -> null) : null;
    }

    private static <T> T parseObject(String value, Class<T> tClass, Supplier<T> defaultSupplier) {
        try {
            if (StringUtils.isBlank(value)) {
                return defaultSupplier.get();
            }
            return MAPPER.readValue(value, tClass);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static <T> T parseObject(byte[] value, Class<T> tClass, Supplier<T> defaultSupplier){
        try {
            if (ArrayUtils.isEmpty(value)) {
                return defaultSupplier.get();
            }
            return getObjectMapper().readValue(value,tClass);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    // 简单地直接用json复制或者转换(Cloneable)
    public static <T> T jsonCopy(Object obj, Class<T> tClass) {
        return obj != null ? parseObject(toJSONString(obj), tClass) : null;
    }


    public static <T> Map<String, T> toMap(String value) {
        return StringUtils.isNotBlank(value) ? toMap(value, () -> null) : null;
    }

    public static <T> Map<String, T> toMap(Object value) {
        return value != null ? toMap(value, () -> null) : null;
    }


    public static <K, V> Map<K, V> toMap(String value, Class<K> keyType, Class<V> valueType) {
        return StringUtils.isNotBlank(value) ? toMap(value, keyType, valueType, () -> null) : null;
    }

    public static <K, V> Map<K, V> toMap(Object value, Class<K> keyType, Class<V> valueType) {
        return value != null ? toMap(toJSONString(value), keyType, valueType, () -> null) : null;
    }


    /**
     * JSON字符串反序列化为Map集合
     *
     * @param value     JSON字符串
     * @param keyType   key的泛型类型
     * @param valueType value的泛型类型
     * @return Map<K, V>
     */
    public static <K, V> Map<K, V> toMap(String value, Class<K> keyType, Class<V> valueType, Supplier<Map<K, V>> defaultSupplier) {
        if (StringUtils.isBlank(value)) {
            return defaultSupplier.get();
        }
        if (keyType == null || valueType == null) {
            throw new IllegalArgumentException("keyType or valueType is null!");
        }
        try {
            //第二参数是 map 的 key 的类型，第三参数是 map 的 value 的类型
            MapType javaType = MAPPER.getTypeFactory().constructMapType(LinkedHashMap.class, keyType, valueType);
            return MAPPER.readValue(value, javaType);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static <T> Map<String, T> toMap(Object value, Supplier<Map<String, T>> defaultSupplier) {
        if (value == null) {
            return defaultSupplier.get();
        }
        try {
            if (value instanceof Map) {
                return (Map<String, T>) value;
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("fail to convert" + toJSONString(value), e);
        }
        return toMap(toJSONString(value), defaultSupplier);
    }

    public static <T> Map<String, T> toMap(String value, Supplier<Map<String, T>> defaultSupplier) {
        if (StringUtils.isBlank(value)) {
            return defaultSupplier.get();
        }
        try {
            return parseObject(value, LinkedHashMap.class);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static List<Map<String, Object>> toMapList(String value) {
        return toMapList(value, String.class, Object.class, () -> null);
    }

    public static <K, V> List<Map<K, V>> toMapList(String value, Class<K> keyType, Class<V> valueType) {
        return toMapList(value, keyType, valueType, () -> null);
    }

    /**
     * JSON数组字符串反序列化为泛型为<Map<K,V>的List集合
     *
     * @param value     JSON数组字符串
     * @param keyType   Map的Key类型
     * @param valueType Map的Value类型
     * @return List<Map < K, V>>
     */
    public static <K, V> List<Map<K, V>> toMapList(String value, Class<K> keyType, Class<V> valueType, Supplier<List<Map<K, V>>> defaultSupplier) {
        if (StringUtils.isBlank(value)) {
            return defaultSupplier.get();
        }
        if (keyType == null || valueType == null) {
            throw new IllegalArgumentException("keyType or valueType is null!");
        }
        try {
            TypeFactory typeFactory = MAPPER.getTypeFactory();
            MapType mapType = typeFactory.constructMapType(Map.class, keyType, valueType);
            CollectionType collectionType = typeFactory.constructCollectionType(List.class, mapType);
            return MAPPER.readValue(value, collectionType);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }


    public static List<Map<String, Object>> toMapList(Object value) {
        return toMapList(value, String.class, Object.class, () -> null);
    }

    public static <K, V> List<Map<K, V>> toMapList(Object value, Class<K> keyType, Class<V> valueType) {
        return toMapList(value, keyType, valueType, () -> null);
    }

    /**
     * Object类型的对象转为泛型为<Map<K,V>的List集合
     *
     * @param object    当前对象
     * @param keyType   Map的Key类型
     * @param valueType Map的Value类型
     * @return List<Map < K, V>>
     */
    public static <K, V> List<Map<K, V>> toMapList(Object object, Class<K> keyType, Class<V> valueType, Supplier<List<Map<K, V>>> defaultSupplier) {
        if (object == null || StringUtils.isBlank(object.toString())) {
            return defaultSupplier.get();
        }
        if (keyType == null || valueType == null) {
            throw new IllegalArgumentException("keyType or valueType is null!");
        }
        try {
            TypeFactory typeFactory = MAPPER.getTypeFactory();
            MapType mapType = typeFactory.constructMapType(Map.class, keyType, valueType);
            CollectionType collectionType = MAPPER.getTypeFactory().constructCollectionType(List.class, mapType);
            if (object instanceof String) {
                return MAPPER.readValue((String) object, collectionType);
            }
            return MAPPER.readValue(toJSONString(object), collectionType);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }


    /**
     * JSON字符串转换为JSON
     *
     * @param value JSON对象字符串
     * @return JsonNode
     */
    public static JsonNode toJSONNode(String value) {
        if (StringUtils.isNotBlank(value)) {
            return null;
        }
        try {
            return MAPPER.readTree(value);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static JsonNode toJSONNode(InputStream inputStream) {
        try {
            return MAPPER.readTree(inputStream);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static JsonNode toJSONNode(byte[] content) {
        try {
            return MAPPER.readTree(content);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static JsonNode toJSONNode(JsonParser content) {
        try {
            return MAPPER.readTree(content);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static ObjectNode parseJSONObject(Object object) {
        return parseJSONObject(object, () -> null);
    }

    /**
     * Object类型转换为JSON对象
     *
     * @param object 当前对象
     * @return ObjectNode
     */
    public static ObjectNode parseJSONObject(Object object, Supplier<ObjectNode> defaultSupplier) {
        if (object == null || StringUtils.isNotBlank(object.toString())) {
            return defaultSupplier.get();
        }
        try {
            if (ObjectNode.class.isAssignableFrom(object.getClass())) {
                return (ObjectNode) object;
            }
            if (object instanceof String) {
                return (ObjectNode) MAPPER.readTree((String) object);
            }
            return (ObjectNode) MAPPER.readTree(toJSONString(object));
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static ObjectNode parseJSONObject(String value) {
        return parseJSONObject(value, () -> null);
    }

    public static ObjectNode parseJSONObject(String value, Supplier<ObjectNode> defaultSupplier) {
        try {
            if (StringUtils.isBlank(value)) {
                return defaultSupplier.get();
            }
            return (ObjectNode) MAPPER.readTree(value);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }


    /**
     * 创建一个JSON对象
     *
     * @return ObjectNode
     */
    public static ObjectNode newJSONObject() {
        return MAPPER.createObjectNode();
    }


}
