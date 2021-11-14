package top.yang.spring.exception;

/**
 *
 */
public class SystemException extends RuntimeException {

  protected String code;

  protected String msg;

  public SystemException(String code, String msg) {
    this.code = code;
    this.msg = msg;
  }

  public SystemException(ResultCode resultCode) {
    this.code = resultCode.getCode();
    this.msg = resultCode.getMessage();
  }

  public SystemException() {
  }

  public String getCode() {
    return code;
  }

  public String getMsg() {
    return msg;
  }

  @Override
  public String toString() {
    return "SystemException{" +
        "code='" + code + '\'' +
        ", msg='系统服务异常：" + msg + '\'' +
        "} " + super.toString();
  }
}

