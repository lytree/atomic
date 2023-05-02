package top.lytree.build;


import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

import top.lytree.collections.MapUtils;
import top.lytree.lang.StringUtils;

/**
 * Map创建类
 *
 * @param <K> Key类型
 * @param <V> Value类型
 */
public record MapBuilder<K, V>(Map<K, V> map) implements Builder<Map<K, V>>, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 创建Builder，默认HashMap实现
     *
     * @param <K> Key类型
     * @param <V> Value类型
     * @return MapBuilder
     */
    public static <K, V> MapBuilder<K, V> create() {
        return create(false);
    }

    /**
     * 创建Builder
     *
     * @param <K>      Key类型
     * @param <V>      Value类型
     * @param isLinked true创建LinkedHashMap，false创建HashMap
     * @return MapBuilder
     */
    public static <K, V> MapBuilder<K, V> create(boolean isLinked) {
        return create(isLinked ? new LinkedHashMap<>() : new HashMap<>());
    }

    /**
     * 创建Builder
     *
     * @param <K> Key类型
     * @param <V> Value类型
     * @param map Map实体类
     * @return MapBuilder
     */
    public static <K, V> MapBuilder<K, V> create(Map<K, V> map) {
        return new MapBuilder<>(map);
    }

    /**
     * 链式Map创建类
     *
     * @param map 要使用的Map实现类
     */
    public MapBuilder {
    }

    /**
     * 链式Map创建
     *
     * @param k Key类型
     * @param v Value类型
     * @return 当前类
     */
    public MapBuilder<K, V> put(K k, V v) {
        map.put(k, v);
        return this;
    }

    /**
     * 链式Map创建
     *
     * @param condition put条件
     * @param k         Key类型
     * @param v         Value类型
     * @return 当前类
     */
    public MapBuilder<K, V> put(boolean condition, K k, V v) {
        if (condition) {
            put(k, v);
        }
        return this;
    }

    /**
     * 链式Map创建
     *
     * @param condition put条件
     * @param k         Key类型
     * @param supplier  Value类型结果提供方
     * @return 当前类
     */
    public MapBuilder<K, V> put(boolean condition, K k, Supplier<V> supplier) {
        if (condition) {
            put(k, supplier.get());
        }
        return this;
    }

    /**
     * 链式Map创建
     *
     * @param map 合并map
     * @return 当前类
     */
    public MapBuilder<K, V> putAll(Map<K, V> map) {
        this.map.putAll(map);
        return this;
    }

    /**
     * 清空Map
     *
     * @return this
     */
    public MapBuilder<K, V> clear() {
        this.map.clear();
        return this;
    }

    /**
     * 创建后的map
     *
     * @return 创建后的map
     */
    @Override
    public Map<K, V> map() {
        return map;
    }

    /**
     * 创建后的map
     *
     * @return 创建后的map
     */
    @Override
    public Map<K, V> build() {
        return map();
    }

    /**
     * 将map转成字符串
     *
     * @param separator         entry之间的连接符
     * @param keyValueSeparator kv之间的连接符
     * @return 连接字符串
     */
    public String join(String separator, final String keyValueSeparator) {
        return StringUtils.join(this.map, separator, keyValueSeparator);
    }

    /**
     * 将map转成字符串
     *
     * @param separator         entry之间的连接符
     * @param keyValueSeparator kv之间的连接符
     * @return 连接后的字符串
     */
    public String joinIgnoreNull(String separator, final String keyValueSeparator) {
        return MapUtils.joinIgnoreNull(this.map, separator, keyValueSeparator);
    }

    /**
     * 将map转成字符串
     *
     * @param separator         entry之间的连接符
     * @param keyValueSeparator kv之间的连接符
     * @param isIgnoreNull      是否忽略null的键和值
     * @return 连接后的字符串
     */
    public String join(String separator, final String keyValueSeparator, boolean isIgnoreNull) {
        return MapUtils.join(this.map, separator, keyValueSeparator, isIgnoreNull);
    }

}
