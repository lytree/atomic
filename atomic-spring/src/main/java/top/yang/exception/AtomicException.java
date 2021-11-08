package top.yang.exception;

/**
 *
 */
public class AtomicException extends RuntimeException {

  protected String code;

  protected String msg;

  public AtomicException(String code, String msg) {
    this.code = code;
    this.msg = msg;
  }

  public AtomicException() {
  }

  public String getCode() {
    return code;
  }

  public String getMsg() {
    return msg;
  }
}

