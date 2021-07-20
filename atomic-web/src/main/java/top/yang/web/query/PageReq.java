package top.yang.web.query;


/**
 * @author Pride_Yang
 */
public class PageReq extends BaseReq {

  private Integer page = 1;
  private Integer pageSize = 10;

  public Integer getPage() {
    return page;
  }

  public void setPage(Integer page) {
    this.page = page;
  }

  public Integer getPageSize() {
    return pageSize;
  }

  public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
  }
}
