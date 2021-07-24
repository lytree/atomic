package top.yang.web.enums;

import top.yang.exception.ResultCode;

public enum WebCode implements ResultCode {
  INVALID_PARAM( "10003", "非法参数！"),
  UNAUTHENTICATED( "10001", "此操作需要登陆系统！"),
  UNAUTHORISE( "10002", "权限不足，无权操作！"),
  ;
  //操作代码
  String code;
  //提示信息
  String message;

  private WebCode(String code, String message) {

    this.code = code;
    this.message = message;
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

