package top.yang.exception;

import top.yang.string.StringUtils;

public class NetException extends RuntimeException {

    private static final long serialVersionUID = 8247610319171014183L;

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
