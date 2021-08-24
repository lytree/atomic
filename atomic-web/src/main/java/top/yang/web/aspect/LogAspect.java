package top.yang.web.aspect;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import top.yang.json.JSONUtil;
import top.yang.string.StringUtils;
import top.yang.web.annotation.ControllerLog;
import top.yang.web.domain.dto.SysLog;
import top.yang.web.enums.BusinessStatus;
import top.yang.web.utils.IPUtils;
import top.yang.web.utils.SecurityUtils;
import top.yang.web.utils.ServletUtils;

@Aspect
@Component
public class LogAspect {

  private static final Logger log = LoggerFactory.getLogger(LogAspect.class);

  // 配置织入点
  @Pointcut("@annotation(top.yang.web.annotation.ControllerLog)")
  public void logPointCut() {
  }

  /**
   * 处理完请求后执行
   *
   * @param joinPoint 切点
   */
  @AfterReturning(pointcut = "logPointCut()", returning = "jsonResult")
  public void doAfterReturning(JoinPoint joinPoint, Object jsonResult) {
    handleLog(joinPoint, null, jsonResult);
  }

  /**
   * 拦截异常操作
   *
   * @param joinPoint 切点
   * @param e         异常
   */
  @AfterThrowing(value = "logPointCut()", throwing = "e")
  public void doAfterThrowing(JoinPoint joinPoint, Exception e) {
    handleLog(joinPoint, e, null);
  }

  protected void handleLog(final JoinPoint joinPoint, final Exception e, Object jsonResult) {
    try {
      // 获得注解
      ControllerLog controllerLog = getAnnotationLog(joinPoint);
      if (controllerLog == null) {
        return;
      }

      // *========数据库日志=========*//
      SysLog log = new SysLog();
      log.setStatus(BusinessStatus.SUCCESS.ordinal());
      // 请求的地址
      String ip = IPUtils.getIpAddr(ServletUtils.getRequest());
      log.setOperIp(ip);
      // 返回参数
      log.setJsonResult(JSONUtil.toJSONString(jsonResult));

      log.setOperUrl(ServletUtils.getRequest().getRequestURI());
      String username = SecurityUtils.getUsername();
      if (StringUtils.isNotBlank(username)) {
        log.setOperName(username);
      }

      if (e != null) {
        log.setStatus(BusinessStatus.FAIL.ordinal());
        log.setErrorMsg(StringUtils.substring(e.getMessage(), 0, 2000));
      }
      // 设置方法名称
      String className = joinPoint.getTarget().getClass().getName();
      String methodName = joinPoint.getSignature().getName();
      log.setMethod(className + "." + methodName + "()");
      // 设置请求方式
      log.setRequestMethod(ServletUtils.getRequest().getMethod());
      // 处理设置注解上的参数
      getControllerMethodDescription(joinPoint, controllerLog, log);
    } catch (Exception exp) {
      // 记录本地异常日志
      log.error("==前置通知异常==");
      log.error("异常信息:{}", exp.getMessage());
      exp.printStackTrace();
    }
  }

  /**
   * 获取注解中对方法的描述信息 用于Controller层注解
   *
   * @param controllerLog    日志
   * @param sysLog 操作日志
   * @throws Exception
   */
  public void getControllerMethodDescription(JoinPoint joinPoint, ControllerLog controllerLog, SysLog sysLog) throws Exception {
    // 设置action动作
    sysLog.setBusinessType(controllerLog.businessType().ordinal());
    // 设置标题
    sysLog.setTitle(controllerLog.title());
    // 设置操作人类别
    sysLog.setOperatorType(controllerLog.operatorType().ordinal());
    // 是否需要保存request，参数和值
    if (controllerLog.isSaveRequestData()) {
      // 获取参数的信息，传入到数据库中。
      setRequestValue(joinPoint, sysLog);
    }
  }

  /**
   * 获取请求的参数，放到log中
   *
   * @param operLog 操作日志
   * @throws Exception 异常
   */
  private void setRequestValue(JoinPoint joinPoint, SysLog operLog) throws Exception {
    String requestMethod = operLog.getRequestMethod();
    if (HttpMethod.PUT.name().equals(requestMethod) || HttpMethod.POST.name().equals(requestMethod)) {
      String params = argsArrayToString(joinPoint.getArgs());
      operLog.setOperParam(StringUtils.substring(params, 0, 2000));
    }
  }

  /**
   * 是否存在注解，如果存在就获取
   */
  private ControllerLog getAnnotationLog(JoinPoint joinPoint) throws Exception {
    Signature signature = joinPoint.getSignature();
    MethodSignature methodSignature = (MethodSignature) signature;
    Method method = methodSignature.getMethod();

    if (method != null) {
      return method.getAnnotation(ControllerLog.class);
    }
    return null;
  }

  /**
   * 参数拼装
   */
  private String argsArrayToString(Object[] paramsArray) {
    String params = "";
    if (paramsArray != null && paramsArray.length > 0) {
      for (int i = 0; i < paramsArray.length; i++) {
        if (Objects.isNull(paramsArray[i]) && !isFilterObject(paramsArray[i])) {
          try {
            Object jsonObj = JSONUtil.toJSONString(paramsArray[i]);
            params += jsonObj.toString() + " ";
          } catch (Exception e) {
          }
        }
      }
    }
    return params.trim();
  }

  /**
   * 判断是否需要过滤的对象。
   *
   * @param o 对象信息。
   * @return 如果是需要过滤的对象，则返回true；否则返回false。
   */
  @SuppressWarnings("rawtypes")
  public boolean isFilterObject(final Object o) {
    Class<?> clazz = o.getClass();
    if (clazz.isArray()) {
      return clazz.getComponentType().isAssignableFrom(MultipartFile.class);
    } else if (Collection.class.isAssignableFrom(clazz)) {
      Collection collection = (Collection) o;
      for (Iterator iter = collection.iterator(); iter.hasNext(); ) {
        return iter.next() instanceof MultipartFile;
      }
    } else if (Map.class.isAssignableFrom(clazz)) {
      Map map = (Map) o;
      for (Iterator iter = map.entrySet().iterator(); iter.hasNext(); ) {
        Map.Entry entry = (Map.Entry) iter.next();
        return entry.getValue() instanceof MultipartFile;
      }
    }
    return o instanceof MultipartFile || o instanceof HttpServletRequest || o instanceof HttpServletResponse
        || o instanceof BindingResult;
  }
}
