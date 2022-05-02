package top.yang.annotations;

/**
 * @author xiangfeng@biyouxinli.com.cn
 * @className: NullHandler
 * @description: 空值处理
 * @date 2018/7/30
 */
public interface NullHandler {

    // null值排首部
    String FIRST = "first";
    // null值排末尾
    String LAST = "last";

    // null值按照数据自己排序
    String NATIVE = "native";


}
