package top.yang.exception;

/**
 * @author PrideYang
 */
public class AtomicException extends RuntimeException {

    //错误代码
    protected ResultCode resultCode;

    public AtomicException(ResultCode resultCode) {
        super(resultCode.getMessage(), null, true, false);
        this.resultCode = resultCode;

    }

    public AtomicException(String code, String message) {
        super(message, null, true, false);
        this.resultCode = new CustomCode(code, message);

    }

    public ResultCode getResultCode() {
        return resultCode;
    }
}
