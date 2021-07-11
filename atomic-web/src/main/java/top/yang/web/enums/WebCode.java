package top.yang.web.enums;

import top.yang.exception.ResultCode;

public enum WebCode implements ResultCode {
  INVALID_PARAM(false, "10003", "非法参数！"),
  UNAUTHENTICATED(false, "10001", "此操作需要登陆系统！"),
  UNAUTHORISE(false, "10002", "权限不足，无权操作！"),
  ;

  //操作是否成功
  boolean success;
  //操作代码
  String code;
  //提示信息
  String message;

  private WebCode(boolean success, String code, String message) {
    this.success = success;
    this.code = code;
    this.message = message;
  }


  @Override
  public Boolean isSuccess() {
    return success;
  }

  @Override
  public String getCode() {
    return code;
  }

  @Override
  public String getMessage() {
    return message;
  }
}

