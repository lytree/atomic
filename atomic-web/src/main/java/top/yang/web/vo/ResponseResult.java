package top.yang.web.vo;


import top.yang.exception.AtomicCode;
import top.yang.exception.ResultCode;

/**
 * @author PrideYang
 */
public class ResponseResult<T> implements Response {

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

  public ResponseResult(ResultCode resultCode) {
    this.code = resultCode.getCode();
    this.message = resultCode.getMessage();
  }

  public ResponseResult(ResultCode resultCode, T data) {
    this.code = resultCode.getCode();
    this.message = resultCode.getMessage();
    this.data = data;
  }

  public static ResponseResult<Void> success() {
    return new ResponseResult(AtomicCode.SUCCESS);
  }

  public static <T> ResponseResult<T> success(T data) {
    return new ResponseResult<>(AtomicCode.SUCCESS, data);
  }

  public static ResponseResult<Void> fail() {
    return new ResponseResult<Void>(AtomicCode.FAIL);
  }

  public static ResponseResult<ResultCode> fail(ResultCode resultCode) {
    return new ResponseResult<ResultCode>(resultCode);
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
}