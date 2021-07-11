package top.yang.exception;

public interface ResultCode {

  Boolean isSuccess();

  String getCode();

  String getMessage();

}
