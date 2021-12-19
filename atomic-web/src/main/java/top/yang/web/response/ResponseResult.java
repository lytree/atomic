package top.yang.web.response;


import top.yang.spring.exception.ResultCode;
import top.yang.web.exception.CommonCode;


/**
 * @author PrideYang
 */
public class ResponseResult<T> implements Response {

  String requestId;
  //
  /**
   * 操作代码
   */
  private String code = SUCCESS_CODE;

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
    return new ResponseResult(CommonCode.SUCCESS, requestId);
  }

  public static <T> ResponseResult<T> success(T data, String requestId) {
    return new ResponseResult<>(CommonCode.SUCCESS, data, requestId);
  }

  public static ResponseResult<Void> fail(String requestId) {
    return new ResponseResult<Void>(CommonCode.FAIL, requestId);
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