package top.lytree.oss.model.exception.exception;


import top.lytree.oss.model.exception.result.ResultCode;

/**
 * @author Pride_Yang
 */
public class BusinessException extends RuntimeException {

    //错误代码
    ResultCode resultCode;
    private final String devMsg;

    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.resultCode = resultCode;
        this.devMsg = resultCode.getMessage();
    }

    public BusinessException(ResultCode resultCode, String devMsg) {
        super(resultCode.getMessage(), null, true, false);
        this.resultCode = resultCode;
        this.devMsg = devMsg;
    }

    public BusinessException(ResultCode resultCode, String devMsg, Throwable throwable) {
        super(resultCode.getMessage(), throwable, true, false);
        this.resultCode = resultCode;
        this.devMsg = devMsg;
    }

    public ResultCode getResultCode() {
        return resultCode;
    }

    public String getDevMsg() {
        return devMsg;
    }
}
