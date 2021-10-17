package top.yang.pojo;


import java.util.List;

public class Page<T> {

  public Page(Integer totalPage, Long totalElement, List<T> content, Integer currentPage) {
    this.totalPage = totalPage;
    this.totalElement = totalElement;
    this.content = content;
    this.currentPage = currentPage;
  }

  public Page() {
  }

  /**
   * 总页数
   */
  private Integer totalPage;

  /**
   * 总记录书
   */
  private Long totalElement;

  /**
   * 内容
   */
  private List<T> content;

  /**
   * 当前page
   */
  private Integer currentPage;

  public Integer getTotalPage() {
    return totalPage;
  }

  public void setTotalPage(Integer totalPage) {
    this.totalPage = totalPage;
  }

  public Long getTotalElement() {
    return totalElement;
  }

  public void setTotalElement(Long totalElement) {
    this.totalElement = totalElement;
  }

  public List<T> getContent() {
    return content;
  }

  public void setContent(List<T> content) {
    this.content = content;
  }

  public Integer getCurrentPage() {
    return currentPage;
  }

  public void setCurrentPage(Integer currentPage) {
    this.currentPage = currentPage;
  }
}
