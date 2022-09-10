package top.yang.web.exception;

import top.yang.spring.exception.CustomCode;
import top.yang.spring.exception.ResultCode;

public class ServerException extends RuntimeException {

    private static final long serialVersionUID = 2359767895161832954L;


    private final String code;

    private final String msg;

    public ServerException(ResultCode resultCode) {
        this.code = resultCode.getCode();
        this.msg = resultCode.getMessage();
    }

    public ServerException(String code, String msg) {
        this.code = code;
        this.msg = msg;

    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return super.fillInStackTrace();
    }

    public Throwable doFillInStackTrace() {
        return super.fillInStackTrace();
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public ResultCode getResult() {
        return new CustomCode(this.code, this.msg);
    }

    @Override
    public String toString() {
        return "BusinessException{" +
                "code='" + code + '\'' +
                ", msg=' 服务端错误" + msg + '\'' +
                "} " + super.toString();
    }
}
