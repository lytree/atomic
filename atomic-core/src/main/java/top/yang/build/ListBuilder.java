package top.yang.build;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.builder.Builder;
import top.yang.collections.ListUtils;
import top.yang.collections.MapUtils;
import top.yang.lang.StringUtils;

public class ListBuilder<T> implements Builder<List<T>> {

    private static final long serialVersionUID = 1L;

    private final List<T> list;


    public static <T> ListBuilder<T> create() {
        return create(false);
    }


    public static <T> ListBuilder<T> create(boolean isLinked) {
        return create(isLinked ? new LinkedList<T>() : new ArrayList<T>());
    }

    public static <T> ListBuilder<T> create(List<T> list) {
        return new ListBuilder<T>(list);
    }

    public ListBuilder(List<T> list) {
        this.list = list;
    }

    public ListBuilder<T> add(T t) {
        this.list.add(t);
        return this;
    }

    public ListBuilder<T> addAll(List<T> t) {
        this.list.addAll(t);
        return this;
    }

    public ListBuilder<T> clear() {
        this.list.clear();
        return this;
    }

    /**
     * 创建后的map
     *
     * @return 创建后的map
     */
    public List<T> list() {
        return this.list;
    }

    @Override
    public List<T> build() {
        return list();
    }

}
