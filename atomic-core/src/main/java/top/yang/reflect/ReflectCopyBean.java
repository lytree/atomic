package top.yang.reflect;


import java.util.List;
import java.util.Optional;
import top.yang.string.StringUtils;

public class ReflectCopyBean {

  private ReflectBean sourceBean;

  private String SEPARATOR = ".";

  public ReflectCopyBean(ReflectBean sourceBean) {
    this.sourceBean = sourceBean;
  }

  public Object copy(Object target) {

    ReflectBean targetBean = Reflect.on(target);

    List<ReflectField> fields = targetBean.fields();

    fields.parallelStream().forEach(targetField -> {
      Mapper mapper = (Mapper) targetField.annotation(Mapper.class);
      String fieldName = targetField.name();
      if (null != mapper && !StringUtils.isEmpty(mapper.value())) {
        mapperNameCopy(mapper.value(), this.sourceBean, targetField);
      } else {
        smpleFieldCopy(fieldName, this.sourceBean, targetField);
      }
    });
    return target;

  }

  /**
   * @methodName: smpleFieldCopy
   * @description: 简单字段复制，根据字段名直接赋值值
   **/
  private void smpleFieldCopy(String fieldName, ReflectBean sourceBean, ReflectField targetField) {

    Optional<ReflectField> sourceField = Optional.ofNullable(sourceBean.field(fieldName));

    if (sourceField.isPresent() && targetField.type().equals(sourceField.get().type())) {
      Object value = sourceField.get().get();
      if (null != value) {
        targetField.set(sourceField.get().get());
      } else {
        if (targetField.isAnnotationPresent(SetNull.class)
            && sourceField.get().isAnnotationPresent(SetNull.class)) {
          targetField.set(null);
        }

      }
    }
  }

  /**
   * @methodName: mapperNameCopy
   * @description: 通过映射名称拷贝对象
   **/
  private void mapperNameCopy(String mapperName, ReflectBean sourceBean, ReflectField targetField) {

    StringBeanExpression expression = StringBeanExpression.from(mapperName);

    // 如果是存在对象嵌套的情况，则采用字符串分隔的情况进行处理，否则采用简单拷贝
    if (expression.hasSeparator(SEPARATOR)) {

      StringBeanExpression prevBoby = expression.cutFirstBy(SEPARATOR);
      ReflectField field = sourceBean.field(prevBoby.field());

      copyField(field, prevBoby, expression, targetField);

    } else {
      smpleFieldCopy(mapperName, sourceBean, targetField);
    }

  }

  /**
   * @methodName: copyField
   * @description: 拷贝字段
   **/
  private void copyField(ReflectField field, StringBeanExpression prevBoby, StringBeanExpression expression,
      ReflectField targetField) {
    if (null != field) {
      Object target = field.get();
      // 如果获取的对象不为空则判断类型
      if (null != target) {
        if (target instanceof List) {
          copyListField(target, prevBoby, expression, targetField);
        } else {
          mapperNameCopy(expression.cutLastBy(SEPARATOR).get(), new ReflectBean(target), targetField);
        }
      }

    }
  }

  /**
   * @methodName: copyListField
   * @description: 拷贝列表中的值对象
   **/
  private void copyListField(Object target, StringBeanExpression prevBoby, StringBeanExpression expression,
      ReflectField targetField) {

    Optional<Object> objectOptional = Optional.ofNullable(((List) target).get(prevBoby.index()));
    objectOptional.ifPresent(targetBean -> mapperNameCopy(expression.cutLastBy(SEPARATOR).get(), new ReflectBean(
        targetBean), targetField));

  }

}
