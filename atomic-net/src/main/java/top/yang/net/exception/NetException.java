package top.yang.net.exception;

import java.io.IOException;
import okhttp3.Response;

/**
 * 七牛SDK异常封装类，封装了http响应数据
 */
public final class NetException extends IOException {

    public final Response response;
    private String error;
    private boolean isUnrecoverable = false;


    public NetException(Response response) {
        //super(response != null ? response.getInfo() : null);
        this.response = response;
        if (response != null) {
            response.close();
        }
    }

    public static NetException unrecoverable(Exception e) {
        NetException exception = new NetException(e);
        exception.isUnrecoverable = true;
        return exception;
    }

    public static NetException unrecoverable(String msg) {
        NetException exception = new NetException(null, msg);
        exception.isUnrecoverable = true;
        return exception;
    }

    public NetException(Exception e) {
        this(e, null);
    }

    public NetException(Exception e, String msg) {
        super(msg != null ? msg : (e != null ? e.getMessage() : null), e);
        this.response = null;
        this.error = msg;
    }
}
