package top.lytree.collections;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ArrayUtilsTest {
    @Test
    public void isEmptyTest() {
        final int[] a = {};
        Assertions.assertTrue(ArrayUtils.isEmpty(a));
        Assertions.assertTrue(ArrayUtils.isEmpty((Object) a));
        final int[] b = null;
        Assertions.assertTrue(ArrayUtils.isEmpty(b));
        final Object c = null;
        Assertions.assertTrue(ArrayUtils.isEmpty(c));

        Object d = new Object[]{"1", "2", 3, 4D};
        boolean isEmpty = ArrayUtils.isEmpty(d);
        Assertions.assertFalse(isEmpty);
        d = new Object[0];
        isEmpty = ArrayUtils.isEmpty(d);
        Assertions.assertTrue(isEmpty);
        d = null;
        isEmpty = ArrayUtils.isEmpty(d);
        Assertions.assertTrue(isEmpty);

        // Object数组
        final Object[] e = new Object[]{"1", "2", 3, 4D};
        final boolean empty = ArrayUtils.isEmpty(e);
        Assertions.assertFalse(empty);
    }
}
