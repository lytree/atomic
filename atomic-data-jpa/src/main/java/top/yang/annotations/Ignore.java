package top.yang.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
/**
 * @className: Ignore
 * @description: 复杂查询条件请求对象忽略字段
 * @author xiangfeng@biyouxinli.com.cn
 * @date 2018/8/9
*/
public @interface Ignore {



}
