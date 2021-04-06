package top.yang.web.response;

import java.util.List;

public class PageResult<T> {

    private Long total;//总记录数
    private List<T> rows;//记录
    private Long currPage;
    private Long pageSize;

    public Long getCurrPage() {
        return currPage;
    }

    public void setCurrPage(Long currPage) {
        this.currPage = currPage;
    }

    public Long getPageSize() {
        return pageSize;
    }

    public void setPageSize(Long pageSize) {
        this.pageSize = pageSize;
    }

    public PageResult(Long total, List<T> rows, Long currPage, Long pageSize) {
        this.total = total;
        this.rows = rows;
        this.currPage = currPage;
        this.pageSize = pageSize;
    }

    public PageResult() {
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }
}