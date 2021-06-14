package top.yang.exception;

/**
 * 抛出异常
 */
public enum CommentExceptionCode implements ExceptionCode {
    JSON_EXCEPTION("", "");

    private String code;

    private String message;

    CommentExceptionCode(String code, String message) {
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
