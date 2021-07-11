package top.yang.web.response;

import top.yang.exception.AtomicCode;
import top.yang.exception.ResultCode;

public class ResponseResult implements Response {

  //操作是否成功
  boolean success = SUCCESS;

  //操作代码
  String code = SUCCESS_CODE;

  //提示信息
  String message;

  public ResponseResult(ResultCode resultCode) {
    this.success = resultCode.isSuccess();
    this.code = resultCode.getCode();
    this.message = resultCode.getMessage();
  }

  public static ResponseResult success() {
    return new ResponseResult(AtomicCode.SUCCESS);
  }

  public static ResponseResult fail() {
    return new ResponseResult(AtomicCode.FAIL);
  }

}