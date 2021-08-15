package top.yang.reflect;


import top.yang.exception.NewInstanceFailException;
import top.yang.exception.UnsupportClassObjectException;

public class Reflect {

  public static ReflectBean on(Object obj) {
    if (obj instanceof Class) {
      throw new UnsupportClassObjectException();
    }
    return new ReflectBean(obj);
  }

  public static ReflectBean on(Class cla) {

    try {
      return new ReflectBean(cla.getDeclaredConstructor().newInstance());
    } catch (Exception e) {
      throw new NewInstanceFailException();
    }

  }

}
