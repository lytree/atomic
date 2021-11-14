package top.yang.spring.exception;

/**
 * @author PrideYang
 */
public interface ResultCode {

  /**
   * 返回编码
   *
   * @return
   */
  String getCode();

  /**
   * 返回提示
   * @return
   */

  String getMessage();

}
