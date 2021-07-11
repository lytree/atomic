package top.yang.exception;

/**
 * 抛出异常
 *
 * @author PrideYang
 */
public enum AtomicCode implements ResultCode {
  SUCCESS(true, "0000", "操作成功！"),
  FAIL(false, "9999", "操作失败！"),
  ;

  private final Boolean success;
  private final String code;
  private final String message;

  AtomicCode(Boolean success, String code, String message) {
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
