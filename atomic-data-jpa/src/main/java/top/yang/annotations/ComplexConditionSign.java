package top.yang.annotations;

/**
 * @author xiangfeng@yintong.com.cn
 * @className: ComplexConditionSign
 * @description: 复杂条件符号
 * @date 2018/2/24
 */
public interface ComplexConditionSign {

    // 相等
    String EQ = "EQ";


    // 相等
    String NOTEQ = "NOTEQ";
    // 小于
    String LT = "LT";
    // 小于等于
    String LTEQ = "LTEQ";
    // 大于
    String GT = "GT";
    // 大于等于
    String GTEQ = "GTEQ";
    // 使用IN符号
    String IN = "IN";
    // 使用LIKE覆盖
    String LIKE = "LIKE";
    // 不等于NULL
    String NOTNULL = "NOTNULL";
}
