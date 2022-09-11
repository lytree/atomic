package top.yang.model.response.support;


import java.io.Serializable;
import top.yang.model.constants.Globals;

/**
 * @author PrideYang
 */
public abstract class BaseResponse implements Serializable {

    private static final long serialVersionUID = Globals.serialVersionUID;

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
