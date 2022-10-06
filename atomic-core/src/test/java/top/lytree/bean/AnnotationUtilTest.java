package top.lytree.bean;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import org.junit.Assert;
import org.junit.Test;

public class AnnotationUtilTest {

    @Test
    public void getCombinationAnnotationsTest() {
        Annotation[] annotations = AnnotationUtils.getAnnotations(ClassWithAnnotation.class);
        Assert.assertNotNull(annotations);
        Assert.assertEquals(3, annotations.length);
    }

//    @Test
//    public void getCombinationAnnotationsWithClassTest() {
//        AnnotationForTest[] annotations = AnnotationUtils.getCombinationAnnotations(ClassWithAnnotation.class, AnnotationForTest.class);
//        Assert.assertNotNull(annotations);
//        Assert.assertEquals(2, annotations.length);
//        Assert.assertEquals("测试", annotations[0].value());
//    }

    @Test
    public void getAnnotationValueTest() throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Object value = AnnotationUtils.getAnnotationValue(ClassWithAnnotation.class, AnnotationForTest.class);
        Assert.assertEquals("测试", value);

    }

//    @Test
//    public void getAnnotationSyncAlias() {
//        // 直接获取
//        Assert.assertEquals("", ClassWithAnnotation.class.getAnnotation(AnnotationForTest.class).retry());
//
//        // 加别名适配
//        AnnotationForTest annotation = AnnotationUtils.getAnnotationAlias(ClassWithAnnotation.class, AnnotationForTest.class);
//        Assert.assertEquals("测试", annotation.retry());
//    }

    @AnnotationForTest("测试")
    @RepeatAnnotationForTest
    static class ClassWithAnnotation {

        public void test() {

        }
    }
}
