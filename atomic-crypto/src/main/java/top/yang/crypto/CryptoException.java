package top.yang.crypto;


import top.yang.lang.StringUtils;

/**
 * 加密异常
 *
 * @author Looly
 */
public class CryptoException extends RuntimeException {

    private static final long serialVersionUID = 8068509879445395353L;

    public CryptoException(Throwable e) {
        super(e.getMessage(), e);
    }

    public CryptoException(String message) {
        super(message);
    }

    public CryptoException(String messageTemplate, Object... params) {
        super(StringUtils.format(messageTemplate, params));
    }

    public CryptoException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public CryptoException(String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
        super(message, throwable, enableSuppression, writableStackTrace);
    }

    public CryptoException(Throwable throwable, String messageTemplate, Object... params) {
        super(StringUtils.format(messageTemplate, params), throwable);
    }
}
