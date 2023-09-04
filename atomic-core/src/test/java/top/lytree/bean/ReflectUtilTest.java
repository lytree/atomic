package top.lytree.bean;

import lombok.Data;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.Optional;

public class ReflectUtilTest {
    @Test
    public void setFieldTest() {
        final TestClass testClass = new TestClass();
        ReflectUtils.setFieldValue(testClass, "a", "111");
        Assertions.assertEquals(111, testClass.getA());
    }

    @Data
    static class TestClass {
        private int a;
    }
}
