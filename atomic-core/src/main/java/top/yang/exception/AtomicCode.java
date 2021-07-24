package top.yang.exception;

/**
 * 抛出异常
 *
 * @author PrideYang
 */
public enum AtomicCode implements ResultCode {
  SUCCESS("0000", "操作成功！"),
  FAIL("9999", "操作失败！"),
  ;

  private final String code;
  private final String message;

  AtomicCode(String code, String message) {
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
