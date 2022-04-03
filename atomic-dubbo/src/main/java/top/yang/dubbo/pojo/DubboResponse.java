package top.yang.dubbo.pojo;

public class DubboResponse  {

  private String interfaceClassName;
  private String methodName;
  private String result;
  private long spendTime;

  public String getInterfaceClassName() {
    return interfaceClassName;
  }

  public void setInterfaceClassName(String interfaceClassName) {
    this.interfaceClassName = interfaceClassName;
  }

  public String getMethodName() {
    return methodName;
  }

  public void setMethodName(String methodName) {
    this.methodName = methodName;
  }

  public String getResult() {
    return result;
  }

  public void setResult(String result) {
    this.result = result;
  }

  public long getSpendTime() {
    return spendTime;
  }

  public void setSpendTime(long spendTime) {
    this.spendTime = spendTime;
  }
}
