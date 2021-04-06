package top.yang;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;

/**
 * 通过反射填充JavaBeans属性的实用程序方法。
 */
public class BeanUtil extends BeanUtils {
    /**
     * 从orig copy 到 dest
     * @param dest
     * @param orig
     */
    public static void copyPropertiesIgnoreNull(Object dest, Object orig){
        Field[] allFields = FieldUtils.getAllFields(dest.getClass());
        BeanUtilBean.getInstance().copyPropertyIgnoreNull();
    }
}
