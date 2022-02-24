package top.yang.net.logger;

public class LoggingException extends RuntimeException {

    private static final long serialVersionUID = 3880206998166270511L;

    public LoggingException() {
        super();
    }

    public LoggingException(String message) {
        super(message);
    }

    public LoggingException(String message, Throwable cause) {
        super(message, cause);
    }

    public LoggingException(Throwable cause) {
        super(cause);
    }

}
