package top.yang.web.aspect;

import com.google.common.collect.ImmutableMap;
import javax.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import top.yang.constants.Globals;
import top.yang.web.domain.response.ResponseResult;
import top.yang.web.exception.BusinessException;
import top.yang.web.exception.CommonCode;
import top.yang.web.exception.ResultCode;

/**
 * @date 2021/8/30 10:16
 */
public class GlobalExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
  //定义map，配置异常类型所对应的错误代码
  private static ImmutableMap<Class<? extends Throwable>, ResultCode> EXCEPTIONS;
  //定义map的builder对象，去构建ImmutableMap
  protected static ImmutableMap.Builder<Class<? extends Throwable>, ResultCode> builder = ImmutableMap.builder();

  //捕获CustomException此类异常
  @ExceptionHandler(BusinessException.class)
  @ResponseBody
  public ResponseResult customException(BusinessException businessException) {
    String requestId = MDC.get(Globals.REQUEST_ID);
    businessException.printStackTrace();
    //记录日志
    logger.error("catch exception:{}", businessException.getMessage());
    ResultCode resultCode = businessException.getResultCode();
    return new ResponseResult(resultCode, requestId);
  }

  //捕获ValidationException此类异常
  @ExceptionHandler(ValidationException.class)
  @ResponseBody
  public ResponseResult validationException(ValidationException validationException) {
    String requestId = MDC.get(Globals.REQUEST_ID);
    validationException.printStackTrace();
    //记录日志
    logger.error("catch exception:{}", validationException.getMessage());
    return new ResponseResult(CommonCode.INVALID_PARAM, requestId);
  }

  //捕获Exception此类异常
  @ExceptionHandler(Exception.class)
  @ResponseBody
  public ResponseResult exception(Exception exception) {
    String requestId = MDC.get(Globals.REQUEST_ID);
    exception.printStackTrace();
    //记录日志
    logger.error("catch exception:{}", exception.getMessage());
    if (EXCEPTIONS == null) {
      EXCEPTIONS = builder.build();//EXCEPTIONS构建成功
    }
    //从EXCEPTIONS中找异常类型所对应的错误代码，如果找到了将错误代码响应给用户，如果找不到给用户响应99999异常
    ResultCode resultCode = EXCEPTIONS.get(exception.getClass());
    if (resultCode != null) {
      return new ResponseResult(resultCode, requestId);
    } else {
      //返回9999异常
      return new ResponseResult(CommonCode.FAIL, requestId);
    }
  }


  static {
    //定义异常类型所对应的错误代码
    builder.put(HttpMessageNotReadableException.class, CommonCode.SERVER_ERROR);
  }
}
