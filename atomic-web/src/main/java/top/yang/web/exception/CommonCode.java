package top.yang.web.exception;

/**
 * 抛出异常
 *
 * @author PrideYang
 */
public enum CommonCode implements ResultCode {
  SUCCESS("00000", "操作成功！"),
  INVALID_PARAM("99997", "非法参数！"),
  SERVER_ERROR("99998", "服务器通讯异常"),
  FAIL("99999", "操作失败"),
  ;

  private final String code;
  private final String message;

  CommonCode(String code, String message) {
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
