package top.lytree.model.exception.exception;


import top.lytree.model.exception.result.ResultCode;

public class ServerException extends RuntimeException {

    private final String devMsg;
    //错误代码
    ResultCode resultCode;

    public ServerException(ResultCode resultCode) {
        super(resultCode.getMessage(), null, true, false);
        this.devMsg = resultCode.getMessage();
        this.resultCode = resultCode;

    }

    public ServerException(ResultCode resultCode, String devMsg) {
        super(resultCode.getMessage(), null, true, false);
        this.devMsg = devMsg;
        this.resultCode = resultCode;

    }

    public ServerException(ResultCode resultCode, String devMsg, Throwable throwable) {
        super(resultCode.getMessage(), throwable, true, false);
        this.resultCode = resultCode;
        this.devMsg = devMsg;
    }

    public ServerException(ResultCode resultCode, Throwable throwable) {
        super(resultCode.getMessage(), throwable, true, false);
        this.resultCode = resultCode;
        this.devMsg = resultCode.getMessage();
    }

    public ResultCode getResultCode() {
        return resultCode;
    }

    public String getDevMsg() {
        return devMsg;
    }
}
