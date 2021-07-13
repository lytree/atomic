package top.yang.web.response.base;


import java.util.List;

public class QueryResult<T> extends Result {

  //数据列表
  private List<T> result;
  //数据总数
  private long total;

  public QueryResult() {
  }

  public QueryResult(List<T> result, long total) {
    this.result = result;
    this.total = total;
  }

  public List<T> getResult() {
    return result;
  }

  public void setResult(List<T> result) {
    this.result = result;
  }

  public long getTotal() {
    return total;
  }

  public void setTotal(long total) {
    this.total = total;
  }

  @Override
  public String toString() {
    return "QueryResult{" +
        "list=" + result +
        ", total=" + total +
        '}';
  }
}
