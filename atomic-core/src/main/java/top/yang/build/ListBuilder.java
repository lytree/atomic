package top.yang.build;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.builder.Builder;
import top.yang.collections.ListUtils;
import top.yang.collections.MapUtils;
import top.yang.lang.StringUtils;

/**
 * List 创建类
 *
 * @param <T> 泛型类型
 */
public class ListBuilder<T> implements Builder<List<T>> {

    private static final long serialVersionUID = 1L;

    private final List<T> list;

    /**
     * 创建Builder 默认ArrayList 实现
     *
     * @param <T> 泛型类型
     * @return ListBuilder
     */
    public static <T> ListBuilder<T> create() {
        return create(false);
    }

    /**
     * 创建Builder
     *
     * @param isLinked true创建LinkedList，false创建ArrayList
     * @param <T>      泛型类型
     * @return ListBuilder
     */
    public static <T> ListBuilder<T> create(boolean isLinked) {
        return create(isLinked ? new LinkedList<T>() : new ArrayList<T>());
    }

    /**
     * 创建Builder
     *
     * @param list List实体
     * @param <T>  泛型类型
     * @return ListBuilder
     */
    public static <T> ListBuilder<T> create(List<T> list) {
        return new ListBuilder<T>(list);
    }

    /**
     * 链式List
     *
     * @param list 要使用的List实现类
     */
    public ListBuilder(List<T> list) {
        this.list = list;
    }

    /**
     * @param t 添加元素
     * @return this
     */
    public ListBuilder<T> add(T t) {
        this.list.add(t);
        return this;
    }

    /**
     * @param list 添加多个元素
     * @return this
     */
    public ListBuilder<T> addAll(T... list) {
        this.list.addAll(Arrays.asList(list));
        return this;
    }

    /**
     * @param list 添加多个元素
     * @return this
     */
    public ListBuilder<T> addAll(List<T> list) {
        this.list.addAll(list);
        return this;
    }

    /**
     * @param t 删除某个元素
     * @return this
     */
    public ListBuilder<T> remove(T t) {
        this.list.remove(t);
        return this;
    }

    /**
     * @param list 删除多个元素
     * @return this
     */
    public ListBuilder<T> remove(List<T> list) {
        this.list.removeAll(list);
        return this;
    }

    /**
     * 清空数组
     *
     * @return this
     */
    public ListBuilder<T> clear() {
        this.list.clear();
        return this;
    }

    /**
     * 创建后的list
     *
     * @return 创建后的list
     */
    public List<T> list() {
        return this.list;
    }

    /**
     * 创建后的list
     *
     * @return 创建后的list
     */
    @Override
    public List<T> build() {
        return list();
    }

}
