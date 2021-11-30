package top.yang.json;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import org.checkerframework.checker.units.qual.K;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.yang.collections.MapUtils;
import top.yang.string.StringUtils;

/**
 * @author PrideYang
 */
abstract class JSON {

    protected static final Logger logger = LoggerFactory.getLogger(JSONObject.class);

    protected static final ObjectMapper MAPPER;
    // 日起格式化
    protected static final String STANDARD_FORMAT = "yyyy-MM-dd HH:mm:ss";
    protected final static String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    protected final static String DATE_FORMAT = "yyyy-MM-dd";
    protected final static String TIME_FORMAT = "HH:mm:ss";
    /**
     * 默认日期格式化对象
     */
    protected static ThreadLocal<SimpleDateFormat> formatLocal = ThreadLocal.withInitial(() -> new SimpleDateFormat(STANDARD_FORMAT));

    /**
     * 设置一些通用的属性
     */
    static {
        MAPPER = new ObjectMapper();
        // 如果json中有新增的字段并且是实体类类中不存在的，不报错
        // mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        // 如果存在未知属性，则忽略不报错
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 允许key没有双引号
        MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        // 允许key有单引号
        MAPPER.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        //忽略空Bean转json的错误
        MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
//            // 允许整数以0开头
//            mapper.configure(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS, true);
//            // 允许字符串中存在回车换行控制符
//            mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        //所有的日期格式都统一为以下的样式，即yyyy-MM-dd HH:mm:ss
        MAPPER.setDateFormat(new SimpleDateFormat(STANDARD_FORMAT));
        //设置按照字段名进行序列化
        MAPPER.setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.NONE);
        MAPPER.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
        MAPPER.setVisibility(PropertyAccessor.IS_GETTER, JsonAutoDetect.Visibility.NONE);
        MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        //针对于JDK新时间类。序列化时带有T的问题，自定义格式化字符串
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(DATE_FORMAT)));
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern((TIME_FORMAT))));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern(DATE_FORMAT)));
        javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern(TIME_FORMAT)));
        MAPPER.registerModule(javaTimeModule);
    }

    /**
     * 获取objectMapper对象
     *
     * @return ObjectMapper
     */
    public static ObjectMapper getObjectMapper() {
        return MAPPER;
    }


    public static String toJSONString(Object obj, boolean format) {
        return obj != null ? toJSONString(obj, () -> "", format) : "";
    }

    public static String toJSONString(Object obj) {
        return obj != null ? toJSONString(obj, () -> "", false) : "";
    }

    public static String toFormatJSONString(Object obj) {
        return obj != null ? toJSONString(obj, () -> "", true) : "";
    }

    public static String toJSONString(Object obj, Supplier<String> defaultSupplier, boolean format) {
        try {
            if (obj == null) {
                return defaultSupplier.get();
            }
            if (obj instanceof String) {
                return obj.toString();
            }
            if (obj instanceof Number) {
                return obj.toString();
            }
            if (format) {
                return MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
            }
            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static Object parseObject(String value) {
        return parseObject(value, Object.class);
    }

    public static <T> T parseObject(String value, Class<T> tClass) {
        return StringUtils.isNotBlank(value) ? parseObject(value, tClass, () -> null) : null;
    }

    public static <T> T parseObject(Object obj, Class<T> tClass) {
        return obj != null ? parseObject(toJSONString(obj), tClass, () -> null) : null;
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
            MapType javaType = MAPPER.getTypeFactory().constructMapType(Map.class, keyType, valueType);
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

    /**
     * 创建一个JSON对象
     *
     * @return ObjectNode
     */
    public static ObjectNode newJSONObject() {
        return MAPPER.createObjectNode();
    }

    /**
     * 创建一个JSON数组
     *
     * @return ArrayNode
     */
    public static ArrayNode newJSONArray() {
        return MAPPER.createArrayNode();
    }

    //============================================== 获取value值方法封装 =============================================================

    /**
     * 获取JSON对象 ObjectNode
     *
     * @param value json对象
     * @param key   JSON key
     * @return ObjectNode
     */
    public static ObjectNode getJSONObject(JsonNode value, String key, Supplier<ObjectNode> defaultSupplier) {
        if (value == null) {
            return defaultSupplier.get();
        }
        return (ObjectNode) Optional.ofNullable(value.get(key)).orElse(defaultSupplier.get());
    }

    /**
     * 获取JSON数组 ArrayNode
     *
     * @param value json对象
     * @param key   JSON key
     * @return ArrayNode
     */
    public static ArrayNode getJSONArray(JsonNode value, String key, Supplier<ArrayNode> defaultSupplier) {
        if (value == null) {
            return defaultSupplier.get();
        }
        return (ArrayNode) Optional.ofNullable(value.get(key)).orElse(defaultSupplier.get());
    }

    /**
     * 获取String类型的value
     *
     * @param value        json对象
     * @param key          JSON key
     * @param defaultValue 默认value
     * @return String
     */
    public static String getString(JsonNode value, String key, String defaultValue) {
        if (value == null) {
            return null;
        }
        return Optional.ofNullable(value.get(key)).filter(x -> !x.isNull()).map(JSON::getJsonNodeString).orElse(defaultValue);
    }

    public static String getString(JsonNode value, String key) {
        return getString(value, key, null);
    }

    /**
     * 获取Byte类型的value
     *
     * @param jsonNode     json对象
     * @param key          JSON key
     * @param defaultValue 默认value
     * @return Byte
     */
    public static Byte getByte(JsonNode jsonNode, String key, Byte defaultValue) {
        if (jsonNode == null) {
            return null;
        }
        return Optional.ofNullable(jsonNode.get(key)).filter(x -> !x.isNull()).map(x -> Byte.valueOf(getJsonNodeString(x))).orElse(defaultValue);
    }

    public static Byte getByte(JsonNode jsonNode, String key) {
        return getByte(jsonNode, key, null);
    }

    /**
     * 获取Short类型的value
     *
     * @param jsonNode     json对象
     * @param key          JSON key
     * @param defaultValue 默认value
     * @return Short
     */
    public static Short getShort(JsonNode jsonNode, String key, Short defaultValue) {
        if (jsonNode == null) {
            return null;
        }
        return Optional.ofNullable(jsonNode.get(key)).filter(x -> !x.isNull()).filter(x -> !x.isNull()).map(x -> Short.valueOf(getJsonNodeString(x))).orElse(defaultValue);
    }

    public static Short getShort(JsonNode jsonNode, String key) {
        return getShort(jsonNode, key, null);
    }

    /**
     * 获取Integer类型的value
     *
     * @param jsonNode     json对象
     * @param key          JSON key
     * @param defaultValue 默认value
     * @return Integer
     */
    public static Integer getInteger(JsonNode jsonNode, String key, Integer defaultValue) {
        if (jsonNode == null) {
            return null;
        }
        return Optional.ofNullable(jsonNode.get(key)).filter(x -> !x.isNull()).map(x -> Integer.valueOf(getJsonNodeString(x))).orElse(defaultValue);
    }

    public static Integer getInteger(JsonNode jsonNode, String key) {
        return getInteger(jsonNode, key, null);
    }

    /**
     * 获取Long类型的value
     *
     * @param jsonNode     json对象
     * @param key          JSON key
     * @param defaultValue 默认value
     * @return Long
     */
    public static Long getLong(JsonNode jsonNode, String key, Long defaultValue) {
        if (jsonNode == null) {
            return null;
        }
        return Optional.ofNullable(jsonNode.get(key)).filter(x -> !x.isNull()).map(x -> Long.valueOf(Objects.requireNonNull(getJsonNodeString(x)))).orElse(defaultValue);
    }

    public static Long getLong(JsonNode jsonNode, String key) {
        return getLong(jsonNode, key, null);
    }

    /**
     * 获取Double类型的value
     *
     * @param jsonNode     json对象
     * @param key          JSON key
     * @param defaultValue 默认value
     * @return Double
     */
    public static Double getDouble(JsonNode jsonNode, String key, Double defaultValue) {
        if (jsonNode == null) {
            return null;
        }
        return Optional.ofNullable(jsonNode.get(key)).filter(x -> !x.isNull()).map(x -> Double.valueOf(getJsonNodeString(x))).orElse(defaultValue);
    }

    public static Double getDouble(JsonNode jsonNode, String key) {
        return getDouble(jsonNode, key, null);
    }

    /**
     * 获取Boolean类型的value
     *
     * @param jsonNode     json对象
     * @param key          JSON key
     * @param defaultValue 默认value
     * @return boolean
     */
    public static boolean getBoolean(JsonNode jsonNode, String key, boolean defaultValue) {
        if (jsonNode == null) {
            return false;
        }
        return Optional.ofNullable(jsonNode.get(key)).filter(x -> !x.isNull()).map(x -> Boolean.valueOf(getJsonNodeString(x))).orElse(defaultValue);
    }

    public static boolean getBoolean(JsonNode jsonNode, String key) {
        return getBoolean(jsonNode, key, false);
    }

    /**
     * 获取Date类型的value
     *
     * @param jsonNode json对象
     * @param key      JSON key
     * @param format   日期格式对象
     * @return Date
     */
    public static Date getDate(JsonNode jsonNode, String key, SimpleDateFormat format) {
        if (jsonNode == null || format == null) {
            return null;
        }
        String dateStr = Optional.ofNullable(jsonNode.get(key)).filter(x -> !x.isNull()).map(JSON::getJsonNodeString).orElse(null);
        if (dateStr == null) {
            return null;
        }
        try {
            return format.parse(dateStr);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static Date getDate(JsonNode jsonNode, String key) {
        return getDate(jsonNode, key, formatLocal.get());
    }

    /**
     * 去掉JsonNode toString后字符串的两端的双引号（直接用JsonNode的toString方法获取到的值做后续处理，Jackson去获取String类型的方法有坑）
     *
     * @param value JsonNode
     * @return String
     */
    private static String getJsonNodeString(JsonNode value) {
        if (value == null) {
            return null;
        }
        //去掉字符串左右的引号
        String quotesLeft = "^[\"]";
        String quotesRight = "[\"]$";
        return value.toString().replaceAll(quotesLeft, "").replaceAll(quotesRight, "");
    }
}
