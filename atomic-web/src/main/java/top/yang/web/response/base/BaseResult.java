package top.yang.web.response.base;




public class BaseResult<T> extends Result {

    private T result;

    public BaseResult(T result) {
        this.result = result;
    }
    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
