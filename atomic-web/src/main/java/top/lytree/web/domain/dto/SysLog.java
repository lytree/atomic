package top.lytree.web.domain.dto;

public class SysLog {


  /**
   * 操作模块
   */

  private String title;

  /**
   * 业务类型（0其它 1新增 2修改 3删除）
   */

  private Integer businessType;

  /**
   * 业务类型数组
   */
  private Integer[] businessTypes;

  /**
   * 请求方法
   */

  private String method;

  /**
   * 请求方式
   */

  private String requestMethod;

  /**
   * 操作类别（0其它 1后台用户 2手机端用户）
   */

  private Integer operatorType;

  /**
   * 操作人员
   */

  private String operName;

  /**
   * 部门名称
   */

  private String deptName;

  /**
   * 请求url
   */

  private String operUrl;

  /**
   * 操作地址
   */

  private String operIp;

  /**
   * 请求参数
   */

  private String operParam;

  /**
   * 返回参数
   */

  private String jsonResult;

  /**
   * 操作状态（0正常 1异常）
   */

  private Integer status;

  /**
   * 错误消息
   */

  private String errorMsg;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Integer getBusinessType() {
    return businessType;
  }

  public void setBusinessType(Integer businessType) {
    this.businessType = businessType;
  }

  public Integer[] getBusinessTypes() {
    return businessTypes;
  }

  public void setBusinessTypes(Integer[] businessTypes) {
    this.businessTypes = businessTypes;
  }

  public String getMethod() {
    return method;
  }

  public void setMethod(String method) {
    this.method = method;
  }

  public String getRequestMethod() {
    return requestMethod;
  }

  public void setRequestMethod(String requestMethod) {
    this.requestMethod = requestMethod;
  }

  public Integer getOperatorType() {
    return operatorType;
  }

  public void setOperatorType(Integer operatorType) {
    this.operatorType = operatorType;
  }

  public String getOperName() {
    return operName;
  }

  public void setOperName(String operName) {
    this.operName = operName;
  }

  public String getDeptName() {
    return deptName;
  }

  public void setDeptName(String deptName) {
    this.deptName = deptName;
  }

  public String getOperUrl() {
    return operUrl;
  }

  public void setOperUrl(String operUrl) {
    this.operUrl = operUrl;
  }

  public String getOperIp() {
    return operIp;
  }

  public void setOperIp(String operIp) {
    this.operIp = operIp;
  }

  public String getOperParam() {
    return operParam;
  }

  public void setOperParam(String operParam) {
    this.operParam = operParam;
  }

  public String getJsonResult() {
    return jsonResult;
  }

  public void setJsonResult(String jsonResult) {
    this.jsonResult = jsonResult;
  }

  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public String getErrorMsg() {
    return errorMsg;
  }

  public void setErrorMsg(String errorMsg) {
    this.errorMsg = errorMsg;
  }
}
