package top.yang.web.domain.request;


import javax.validation.constraints.NotNull;

/**
 * @author Pride_Yang
 */
public class PageReq extends BaseReq {
  @NotNull
  private Integer page = 1;
  @NotNull
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
