package top.lytree.build;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Supplier;
import top.lytree.collections.SetUtils;
import top.lytree.lang.StringUtils;

public record SetBuilder<E>(Set<E> set) implements Builder<Set<E>>, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 创建Builder，默认HashMap实现
     *
     * @param <E> Value类型
     * @return MapBuilder
     */
    public static <E> SetBuilder<E> create() {
        return create(false);
    }

    /**
     * 创建Builder
     *
     * @param <E>      Value类型
     * @param isLinked true创建LinkedHashMap，false创建HashMap
     * @return MapBuilder
     */
    public static <E> SetBuilder<E> create(boolean isLinked) {
        return create(isLinked ? new HashSet<E>() : new LinkedHashSet<>());
    }

    /**
     * 创建Builder
     *
     * @param <E> Value类型
     * @param set Map实体类
     * @return MapBuilder
     */
    public static <E> SetBuilder<E> create(Set<E> set) {
        return new SetBuilder<>(set);
    }

    /**
     * 链式Map创建类
     *
     * @param set 要使用的Map实现类
     */
    public SetBuilder {
    }

    /**
     * 链式Map创建
     *
     * @param e Value类型
     * @return 当前类
     */
    public SetBuilder<E> add(E e) {
        set.add(e);
        return this;
    }

    /**
     * 链式Map创建
     *
     * @param condition put条件
     * @param e         Value类型
     * @return 当前类
     */
    public SetBuilder<E> add(boolean condition, E e) {
        if (condition) {
            add(e);
        }
        return this;
    }

    /**
     * 链式Map创建
     *
     * @param condition put条件
     * @param supplier  Value类型结果提供方
     * @return 当前类
     */
    public SetBuilder<E> add(boolean condition, Supplier<E> supplier) {
        if (condition) {
            add(supplier.get());
        }
        return this;
    }

    /**
     * 链式Map创建
     *
     * @param set 合并map
     * @return 当前类
     */
    public SetBuilder<E> addAll(Set<E> set) {
        this.set.addAll(set);
        return this;
    }

    /**
     * 清空Map
     *
     * @return this
     */
    public SetBuilder<E> clear() {
        this.set.clear();
        return this;
    }

    /**
     * 创建后的map
     *
     * @return 创建后的map
     */
    @Override
    public Set<E> set() {
        return set;
    }

    /**
     * 创建后的map
     *
     * @return 创建后的map
     */
    @Override
    public Set<E> build() {
        return set();
    }

    /**
     * 将map转成字符串
     *
     * @param separator         entry之间的连接符
     * @param keyValueSeparator kv之间的连接符
     * @return 连接字符串
     */
    public String join(String separator, final String keyValueSeparator) {
        return StringUtils.join(this.set, separator, keyValueSeparator);
    }

    /**
     * 将map转成字符串
     *
     * @param separator         entry之间的连接符
     * @param keyValueSeparator kv之间的连接符
     * @return 连接后的字符串
     */
    public String joinIgnoreNull(String separator) {
        return join(separator, true);
    }

    /**
     * 将map转成字符串
     *
     * @param separator         entry之间的连接符
     * @param keyValueSeparator kv之间的连接符
     * @param isIgnoreNull      是否忽略null的键和值
     * @return 连接后的字符串
     */
    public String join(String separator, boolean isIgnoreNull) {
        return SetUtils.join(this.set, separator, isIgnoreNull);
    }


}
