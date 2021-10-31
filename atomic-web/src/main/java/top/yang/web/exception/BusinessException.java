package top.yang.web.exception;


/**
 * @author PrideYang
 */
public class BusinessException extends RuntimeException {

  //错误代码
  protected ResultCode resultCode;

  public BusinessException(ResultCode resultCode) {
    super(resultCode.getMessage(), null, true, false);
    this.resultCode = resultCode;

  }

  public BusinessException(String code, String message) {
    super(message, null, true, false);
    this.resultCode = new CustomCode(code, message);

  }

  public ResultCode getResultCode() {
    return resultCode;
  }
}
