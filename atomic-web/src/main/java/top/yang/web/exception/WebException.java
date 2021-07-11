package top.yang.web.exception;

import top.yang.exception.ResultCode;

public class WebException extends RuntimeException {

  //错误代码
  ResultCode resultCode;

  public WebException(ResultCode resultCode) {
    this.resultCode = resultCode;
  }

  public ResultCode getResultCode() {
    return resultCode;
  }


}
