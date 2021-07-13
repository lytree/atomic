package top.yang.web.response;


import top.yang.exception.AtomicCode;
import top.yang.exception.ResultCode;
import top.yang.web.response.base.BaseResult;

/**
 * @author Y
 */
public class BaseResponseResult<T> extends ResponseResult {
    private BaseResult<T> data;

    public BaseResponseResult(ResultCode resultCode,BaseResult<T> data) {
        super(resultCode);
        this.data = data;
    }

    public BaseResponseResult(BaseResult<T> data) {
        super(AtomicCode.SUCCESS);
        this.data = data;
    }

    public BaseResult<T> getData() {
        return data;
    }
}
