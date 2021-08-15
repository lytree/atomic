package top.yang.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import top.yang.exception.ReflectNoSuchMethodException;
import top.yang.string.StringUtils;

/**
 * @className: ReflectBean
 * @description: 反射bean对象
 */
public class ReflectBean {

  private Object source;

  public ReflectBean(Object obj) {
    this.source = obj;
  }

  public Object bean() {
    return this.source;
  }

  /**
   * @methodName: method
   * @description: 获取抽象类方法
   **/
  public ReflectMethod method(String name, Class<?>... params) {
    Method method = null;
    try {
      method = source.getClass().getMethod(name, params);
    } catch (NoSuchMethodException e) {
      throw new ReflectNoSuchMethodException(source.getClass(), name, params);
    }
    return new ReflectMethod(source, method);
  }

  /**
   * @methodName: methods
   * @description: 获取所有的方法
   **/
  public List<ReflectMethod> methods() {
    Method[] methods = source.getClass().getMethods();
    List<ReflectMethod> reflectMethods = new ArrayList<>();
    for (int i = 0; i < methods.length; i++) {
      reflectMethods.add(new ReflectMethod(source, methods[i]));
    }
    return reflectMethods;
  }

  /**
   * @methodName: getter
   * @description: 获取getter方法
   **/
  public ReflectMethod getter(String fieldName) {
    ReflectMethod method;
    if (field(fieldName).type().equals(boolean.class)) {
      method = method("is" + StringUtils.upperCase(fieldName));
    } else {
      method = method("get" + StringUtils.upperCase(fieldName));
    }

    return method;
  }

  /**
   * @methodName: setter
   * @description: 获取setter
   **/
  public ReflectMethod setter(String fieldName, Class<?>... paramTypes) {
    ReflectMethod method = method("set" + StringUtils.upperCase(fieldName), paramTypes);
    return method;
  }

  /**
   * @methodName: filterField
   * @description: 通过annotation过滤字段
   **/
  public List<ReflectField> filterField(Class<? extends Annotation> annotation) {
    List<ReflectField> fields = fields();
    List<ReflectField> result = fields.parallelStream()
        .filter(field -> field.isAnnotationPresent(annotation))
        .collect(Collectors.toList());
    return result;
  }

  /**
   * @methodName: getField
   * @description: 获取字段
   **/
  public ReflectField field(String fieldName) {
    List<ReflectField> fields = fields();
    for (ReflectField field : fields) {
      if (fieldName.equals(field.name())) {
        return field;
      }
    }

    return null;
  }

  /**
   * @methodName: getAllFields
   * @description: 获取全部字段
   **/
  public List<ReflectField> fields() {
    List<Field> fields = new ArrayList<>(Arrays.asList(source.getClass().getDeclaredFields()));

    Class superClazz = source.getClass().getSuperclass();
    while (!Object.class.equals(superClazz)) {
      fields.addAll(Arrays.asList(superClazz.getDeclaredFields()));
      superClazz = superClazz.getSuperclass();
    }

    List<ReflectField> reflectFields = fields.parallelStream()
        .filter(field -> !Modifier.isStatic(field.getModifiers()))
        .map(field -> new ReflectField(field, source))
        .collect(Collectors.toList());
    return reflectFields;
  }


  /**
   * @methodName: annotation
   * @description: 获取对象上的注解
   **/
  public Annotation annotation(Class<? extends Annotation> annotation) {
    if (source.getClass().isAnnotationPresent(annotation)) {
      return source.getClass().getAnnotation(annotation);
    }
    return null;
  }

  /**
   * @methodName: copyField
   * @description: 将bean中的字段拷贝到target对应的字段中，空值则不执行。已废弃建议使用copy方法
   **/
  @Deprecated
  public Object copyField(Object target) {

    // 普通的Field对象转换成反射属性对象
    List<ReflectField> sourceFields = fields();

    sourceFields.parallelStream().forEach(field -> {
      // 如果field中有值则继续执行ifPresent中的方法
      Optional.ofNullable(field.get()).ifPresent(value -> {

        try {
          // 如果
          Method method = target.getClass().getDeclaredMethod("set" + StringUtils.upperCase(field.name()), value.getClass());
          ReflectMethod reflectMethod = new ReflectMethod(target, method);
          reflectMethod.invoke(value);
        } catch (Exception e) {
          // 如果没有方法则不设置。
        }
      });

    });
    return target;

  }


  /**
   * @methodName: copy
   * @description: 将对象中的属性拷贝到target对象中，如果target对象中的field上有注解Mapper，则按Mapper的value去获取原属性
   **/
  public Object copy(Object target) {

    ReflectCopyBean reflectCopyBean = new ReflectCopyBean(this);

    return reflectCopyBean.copy(target);

  }

  /**
   * @methodName: toMap
   * @description: 转换成map对象
   **/
  public Map<String, Object> toMap() {

    Map<String, Object> resultMap = new HashMap<>();

    List<ReflectField> sourceFields = fields();

    sourceFields.stream().forEach(field -> {
      // 如果field中有值则继续执行ifPresent中的方法
      resultMap.put(field.name(), field.get());

    });
    return resultMap;

  }

  /**
   * @methodName: toList
   * @description: 将对象中的非空字段取出并转换成列表
   **/
  public List<Object> toList() {
    List<Object> result = new ArrayList<>();

    List<ReflectField> fields = fields();

    for (int i = 0; i < fields.size(); i++) {

      Object value = fields.get(i).get();
      if (null != value) {
        result.add(value);
      }

    }

    return result;

  }


}
