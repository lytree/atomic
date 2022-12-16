package top.lytree.redis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class Utils {
    static <T> List<T> buildList(Collection<T> collection) {
        if (null == collection || collection.size() == 0) {
            return new ArrayList<>();
        }
        return new ArrayList<>(collection);
    }
    static <T> List<T> isEmpty(Collection<T> collection) {
        if (null == collection || collection.size() == 0) {
            return new ArrayList<>();
        }
        return new ArrayList<>(collection);
    }
}
