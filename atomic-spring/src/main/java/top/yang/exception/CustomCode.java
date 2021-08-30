package top.yang.exception;

/**
 * @date 2021/8/30 11:06
 */
class CustomCode implements ResultCode {

    String code;
    String message;

    CustomCode(String code, String message) {
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
