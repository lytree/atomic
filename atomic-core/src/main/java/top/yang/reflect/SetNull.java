package top.yang.reflect;

import java.lang.annotation.*;


/**
* 允许数据库修改当前字段为空
* @author Songzh
* @date 2019年11月29日 上午11:21:30
*/
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface SetNull {


}
