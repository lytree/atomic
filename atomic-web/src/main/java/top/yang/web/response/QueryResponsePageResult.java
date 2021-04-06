package top.yang.web.response;

import top.yang.web.enums.ResultCode;

public class QueryResponsePageResult<T> extends ResponseResult {
    private PageResult<T> pageResult;

    public PageResult<T> getPageResult() {
        return pageResult;
    }

    public void setPageResult(PageResult<T> pageResult) {
        this.pageResult = pageResult;
    }

    public QueryResponsePageResult(ResultCode resultCode, PageResult<T> pageResult) {
        super(resultCode);
        this.pageResult = pageResult;
    }
}
