package top.lytree.oss.model.response.support;


import java.io.Serial;
import java.io.Serializable;

/**
 * @author PrideYang
 */
public abstract class BaseResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    String SUCCESS_CODE = "0000";

    private String requestId;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public BaseResponse(String requestId) {
        this.requestId = requestId;
    }
}
