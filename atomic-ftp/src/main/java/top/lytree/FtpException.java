package top.lytree;

import top.lytree.exception.ExceptionUtils;
import top.lytree.lang.StringUtils;

public class FtpException extends RuntimeException {
    private static final long serialVersionUID = -8490149159895201756L;

    public FtpException(final Throwable e) {
        super(ExceptionUtils.getMessage(e), e);
    }

    public FtpException(final String message) {
        super(message);
    }

    public FtpException(final String messageTemplate, final Object... params) {
        super(StringUtils.format(messageTemplate, params));
    }

    public FtpException(final String message, final Throwable throwable) {
        super(message, throwable);
    }

    public FtpException(final String message, final Throwable throwable, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, throwable, enableSuppression, writableStackTrace);
    }

    public FtpException(final Throwable throwable, final String messageTemplate, final Object... params) {
        super(StringUtils.format(messageTemplate, params), throwable);
    }
}
