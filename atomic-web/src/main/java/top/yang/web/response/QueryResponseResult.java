package top.yang.web.response;

import top.yang.web.response.base.QueryResult;

public class QueryResponseResult<T> extends ResponseResult {

    QueryResult<T> queryResult;

    public QueryResponseResult(ResultCode resultCode, QueryResult queryResult) {
        super(resultCode);
        this.queryResult = queryResult;
    }

    @Override
    public String toString() {
        return "QueryResponseResult{" +
                "queryResult=" + queryResult +
                ", success=" + success +
                ", code=" + code +
                ", message='" + message + '\'' +
                '}';
    }

    public QueryResult<T> getQueryResult() {
        return queryResult;
    }

    public void setQueryResult(QueryResult<T> queryResult) {
        this.queryResult = queryResult;
    }
}
