package top.yang.exception;

/**
 * @author PrideYang
 */
public class AtomicException extends RuntimeException {

  protected String code;

  protected String message;

  public AtomicException() {
  }

  public AtomicException(String code, String msg) {
    this.code = code;
    this.message = msg;
  }

  public AtomicException(ResultCode code) {
    this.code = code.getCode();
    this.message = code.getMessage();
  }
}
