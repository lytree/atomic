package top.lytree.exception;

import top.lytree.lang.StringUtils;

import java.io.Serial;

/**
 * 网络异常
 */
public class NetException extends RuntimeException {

    /**
     * Defines the serial version UID.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    public NetException(Throwable e) {
        super(ExceptionUtils.getMessage(e), e);
    }

    public NetException(String message) {
        super(message);
    }

    public NetException(String messageTemplate, Object... params) {
        super(StringUtils.format(messageTemplate, params));
    }

    public NetException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public NetException(String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
        super(message, throwable, enableSuppression, writableStackTrace);
    }

    public NetException(Throwable throwable, String messageTemplate, Object... params) {
        super(StringUtils.format(messageTemplate, params), throwable);
    }

}
