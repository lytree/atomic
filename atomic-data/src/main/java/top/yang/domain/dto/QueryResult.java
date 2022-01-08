package top.yang.domain.dto;

import java.util.List;

public class QueryResult<T> extends BaseDto {

    private List<T> list;
    private Long length;

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public Long getLength() {
        return length;
    }

    public void setLength(Long length) {
        this.length = length;
    }
}
