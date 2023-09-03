package top.lytree.model.response.support;


import top.lytree.model.exception.result.ResultCode;
import top.lytree.model.exception.result.ServerCode;

import java.io.Serializable;


/**
 * @author yltree
 */
public class ResponseResult<T> implements Serializable {

    private String requestId;
    /**
     * 操作代码
     */
    private String code = "0000";

    //
    /**
     * 提示信息
     */
    private String message;
    /**
     * 数据
     */
    private T data;

    public ResponseResult(ResultCode resultCode, String requestId) {
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
        this.requestId = requestId;
    }

    public ResponseResult(ResultCode resultCode, T data, String requestId) {
        this.code = resultCode.getCode();
        this.message = resultCode.getMessage();
        this.data = data;
        this.requestId = requestId;
    }

    public static ResponseResult<Void> success(String requestId) {
        return new ResponseResult<Void>(ServerCode.SUCCESS, requestId);
    }

    public static <T> ResponseResult<T> success(T data, String requestId) {
        return new ResponseResult<>(ServerCode.SUCCESS, data, requestId);
    }

    public static ResponseResult<Void> fail(String requestId) {
        return new ResponseResult<Void>(ServerCode.FAIL, requestId);
    }

    public static ResponseResult<ResultCode> fail(ResultCode resultCode, String requestId) {
        return new ResponseResult<ResultCode>(resultCode, requestId);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}