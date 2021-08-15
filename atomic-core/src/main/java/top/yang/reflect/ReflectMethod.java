package top.yang.reflect;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import top.yang.exception.ReflectIllegalAccessException;
import top.yang.exception.ReflectInvocationTargetException;

/**
 * @className: ReflectMethod
 * @description: 反射方法对象
 */
public class ReflectMethod {

  private Object target;

  private Method method;

  public ReflectMethod(Object target, Method method) {
    this.target = target;
    this.method = method;
  }

  public String name() {
    return this.method.getName();
  }

  /**
   * @methodName: invoke
   * @description: 执行方法
   **/
  public Object invoke(Object... params) {
    Object result = null;
    try {
      result = method.invoke(target, params);
    } catch (IllegalAccessException e) {
      throw new ReflectIllegalAccessException();
    } catch (InvocationTargetException e) {
      throw new ReflectInvocationTargetException(e.getTargetException());
    }
    return result;
  }

  /**
   * @methodName: parameterTypes
   * @description: 获取参数个数以及类型
   **/
  public Class[] parameterTypes() {
    return this.method.getParameterTypes();
  }
}
