package top.yang.web.vo;

import top.yang.exception.AtomicCode;
import top.yang.exception.ResultCode;

public class ResponseResult<T> implements Response {

  //操作是否成功
  boolean success = SUCCESS;

  //操作代码
  String code = SUCCESS_CODE;

  //提示信息
  String message;
  T data;

  public ResponseResult(ResultCode resultCode) {
    this.success = resultCode.isSuccess();
    this.code = resultCode.getCode();
    this.message = resultCode.getMessage();
  }

  public ResponseResult(ResultCode resultCode, T data) {
    this.success = resultCode.isSuccess();
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
}