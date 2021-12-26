package top.yang.domain.query;

import org.springframework.data.relational.core.sql.In;

public abstract class PageQuery extends BaseQuery {

    private Integer page = 1;
    private Integer pageSize = 10;
    private Integer startIndex;

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

    public Integer getStartIndex() {
        return (page - 1) * pageSize;
    }
}
