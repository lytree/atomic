package top.yang.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ComplexCondition {

    /**
     * 目标名称
     *
     * @return
     */
    String target();

    /**
     * 符号 EQ - 相等 详情请参见
     *
     * @return
     * @see ComplexConditionSign
     */
    String sign() default "EQ";


}
