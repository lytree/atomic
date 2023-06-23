package top.lytree.collections;
import top.lytree.collections.ReferenceConcurrentMap;
import top.lytree.utils.ReferenceUtils;

import java.lang.ref.Reference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
/**
 * 线程安全的WeakMap实现<br>
 * 参考：jdk.management.resource.internal.WeakKeyConcurrentHashMap
 *
 * @param <K> 键类型
 * @param <V> 值类型
 */
public class WeakConcurrentMap<K, V> extends ReferenceConcurrentMap<K, V> {
    private static final long serialVersionUID = 1L;

    /**
     * 构造
     */
    public WeakConcurrentMap() {
        this(new ConcurrentHashMap<>());
    }

    /**
     * 构造
     *
     * @param raw {@link ConcurrentMap}实现
     */
    public WeakConcurrentMap(final ConcurrentMap<Reference<K>, V> raw) {
        super(raw, ReferenceUtils.ReferenceType.WEAK);
    }
}
