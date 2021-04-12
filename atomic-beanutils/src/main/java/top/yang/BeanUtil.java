package top.yang;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.lang3.reflect.FieldUtils;
import top.yang.exception.UnsupportClassObjectException;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import static javafx.scene.input.KeyCode.M;

/**
 * 通过反射填充JavaBeans属性的实用程序方法。
 */
public class BeanUtil extends BeanUtils {
    public static void copyPropertiesIgnoreNull(Object dest, Object orig) throws IllegalAccessException, InvocationTargetException {
        BeanUtilBean.getInstance().copyPropertiesIgnoreNull(dest, orig);
    }

}
