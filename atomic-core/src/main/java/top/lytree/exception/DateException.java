package top.lytree.exception;

import top.lytree.lang.StringUtils;

public class DateException extends RuntimeException {

    /**
     * Defines the serial version UID.
     */
    private static final long serialVersionUID = 1L;

    public DateException(final Throwable e) {
        super(ExceptionUtils.getMessage(e), e);
    }

    public DateException(final String message) {
        super(message);
    }

    public DateException(final String messageTemplate, final Object... params) {
        super(StringUtils.format(messageTemplate, params));
    }

    public DateException(final String message, final Throwable throwable) {
        super(message, throwable);
    }

    public DateException(final Throwable throwable, final String messageTemplate, final Object... params) {
        super(StringUtils.format(messageTemplate, params), throwable);
    }

}
