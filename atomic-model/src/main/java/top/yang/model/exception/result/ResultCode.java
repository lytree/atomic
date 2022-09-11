package top.yang.model.exception.result;

/**
 * @author PrideYang
 */
public interface ResultCode {

    /**
     * 操作代码
     **/

    String getCode();

    /**
     * 提示信息
     *
     * @return
     */
    String getMessage();
}
