package top.yang.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface OrderBy {

    /**
     * 目标名称
     *
     * @return
     */
    String target();

    /**
     * 排序符号 ASC - 升序 DESC - 降序 详情请参见
     *
     * @return
     * @see OrderSign
     */
    String orderSign() default OrderSign.ASC;


    /**
     * 空值排序
     *
     * @return
     * @see NullHandler
     */
    String nullHandler() default NullHandler.NATIVE;


}
