package top.lytree.http.exception;

public class OkHttpsException extends RuntimeException {

    public OkHttpsException(String message) {
        super(message);
    }

    public OkHttpsException(String message, Throwable cause) {
        super(message, cause);
    }

    public OkHttpsException(Throwable cause) {
        super(cause);
    }

    public OkHttpsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public OkHttpsException() {
    }
}
