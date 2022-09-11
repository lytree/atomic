package top.yang.model.exception.result;


import top.yang.model.exception.result.ResultCode;

/**
 * @date 2021/8/30 11:06
 */
public class CustomCode implements ResultCode {

    String code;
    String message;

    public CustomCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
