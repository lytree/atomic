package top.yang.exception;

/**
 * @author PrideYang
 */
public class AtomicException extends RuntimeException {

  protected Boolean success;
  protected String code;

  protected String message;

  public AtomicException() {
  }

  public AtomicException(Boolean success, String code, String msg) {
    this.success = success;
    this.code = code;
    this.message = msg;
  }

  public AtomicException(ResultCode code) {
    this.success = code.isSuccess();
    this.code = code.getCode();
    this.message = code.getMessage();
  }
}
