package top.yang.web.annotation;


import top.yang.web.enums.BusinessType;
import top.yang.web.enums.OperatorType;

public @interface ControllerLog {

  /**
   * 模块
   */
  public String title() default "";

  /**
   * 功能
   */
  public BusinessType businessType() default BusinessType.OTHER;

  /**
   * 操作人类别
   */
  public OperatorType operatorType() default OperatorType.MANAGE;

  /**
   * 是否保存请求的参数
   */
  public boolean isSaveRequestData() default true;
}
