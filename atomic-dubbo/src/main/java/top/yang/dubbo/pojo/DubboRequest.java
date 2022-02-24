package top.yang.dubbo.pojo;


import top.yang.domain.dto.BaseDto;

public class DubboRequest extends BaseDto {

  private String interfaceClass;
  private String methodName;
  private Object[] args;

  public String getInterfaceClass() {
    return interfaceClass;
  }

  public void setInterfaceClass(String interfaceClass) {
    this.interfaceClass = interfaceClass;
  }

  public String getMethodName() {
    return methodName;
  }

  public void setMethodName(String methodName) {
    this.methodName = methodName;
  }

  public Object[] getArgs() {
    return args;
  }

  public void setArgs(Object[] args) {
    this.args = args;
  }
}
