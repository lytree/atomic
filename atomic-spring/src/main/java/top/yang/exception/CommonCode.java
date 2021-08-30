package top.yang.exception;

/**
 * 抛出异常
 *
 * @author PrideYang
 */
public enum CommonCode implements ResultCode {
    SUCCESS("00000", "操作成功！"),
    INVALID_PARAM("99997", "参数错误"),
    SERVER_ERROR("99998", "服务异常"),
    FAIL("99999", "操作失败！"),
    ;

    private final String code;
    private final String message;

    CommonCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
