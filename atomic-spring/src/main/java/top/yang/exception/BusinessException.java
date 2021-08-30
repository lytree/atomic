package top.yang.exception;

/**
 * @date 2021/8/30 10:25
 */
public class BusinessException extends AtomicException {

    public BusinessException(ResultCode resultCode) {
        super(resultCode);
    }

    public BusinessException(String code, String message) {
        super(code, message);
        this.resultCode = new CustomCode(code, message);
    }
}
