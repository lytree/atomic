package top.yang.web.response.base;

import top.yang.admin.pojo.vo.base.Result;

import java.util.List;

public class PageResult<T> extends Result {

    private Long total;//总记录数
    private List<T> result;//记录
    private Integer currPage;
    private Integer pageSize;

    public Integer getCurrPage() {
        return currPage;
    }

    public void setCurrPage(Integer currPage) {
        this.currPage = currPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public PageResult(Long total, List<T> result, Integer currPage, Integer pageSize) {
        this.total = total;
        this.result = result;
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

    public List<T> getResult() {
        return result;
    }

    public void setResult(List<T> result) {
        this.result = result;
    }

}