package top.lytree.exception;

import top.lytree.text.StringFormatter;

public class BeanException extends RuntimeException {

    /**
     * Defines the serial version UID.
     */
    private static final long serialVersionUID = 1L;

    public BeanException(final Throwable e) {
        super(ExceptionUtils.getMessage(e), e);
    }

    public BeanException(final String message) {
        super(message);
    }

    public BeanException(final String messageTemplate, final Object... params) {
        super(StringFormatter.format(messageTemplate, params));
    }

    public BeanException(final String message, final Throwable throwable) {
        super(message, throwable);
    }

    public BeanException(final Throwable throwable, final String messageTemplate, final Object... params) {
        super(StringFormatter.format(messageTemplate, params), throwable);
    }
}
