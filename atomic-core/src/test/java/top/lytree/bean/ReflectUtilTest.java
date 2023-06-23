package top.lytree.bean;

import lombok.Data;
import org.junit.Assert;
import org.junit.Test;

public class ReflectUtilTest {
    @Test
    public void setFieldTest() {
        final TestClass testClass = new TestClass();
        ReflectUtils.setFieldValue(testClass, "a", "111");
        Assert.assertEquals(111, testClass.getA());
    }

    @Data
    static class TestClass {
        private int a;
    }
}
