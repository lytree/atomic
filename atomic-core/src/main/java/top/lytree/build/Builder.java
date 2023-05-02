package top.lytree.build;

@FunctionalInterface
public interface Builder<T> {

    /**
     * 返回对正在构造的对象或结果
     *
     * @return 构建器构造的对象或计算的结果
     */
    T build();
}
