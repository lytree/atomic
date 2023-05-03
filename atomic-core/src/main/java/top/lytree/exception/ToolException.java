package top.lytree.exception;

import top.lytree.text.StringFormatter;

import java.io.Serial;

public class ToolException extends
        RuntimeException {

    /**
     * Defines the serial version UID.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    public ToolException(final Throwable e) {
        super(ExceptionUtils.getMessage(e), e);
    }

    public ToolException(final String message) {
        super(message);
    }

    public ToolException(final String messageTemplate, final Object... params) {
        super(StringFormatter.format(messageTemplate, params));
    }

    public ToolException(final String message, final Throwable throwable) {
        super(message, throwable);
    }

    public ToolException(final Throwable throwable, final String messageTemplate, final Object... params) {
        super(StringFormatter.format(messageTemplate, params), throwable);
    }
}
