package top.yang.net.logger;

import java.lang.reflect.Constructor;

public class LoggingFactory {

    private static Constructor<? extends LoggingInterceptor> logConstructor;

    static {
        tryImplementation(LoggingFactory::useDefaultLogging);
    }

    private LoggingFactory() {
    }

    public static LoggingInterceptor getLoggingInterceptor(Class<?> clazz) {
        return getLoggingInterceptor(clazz.getName());
    }

    public static LoggingInterceptor getLoggingInterceptor(String logger) {
        try {
            return logConstructor.newInstance(logger);
        } catch (Throwable t) {
            throw new LoggingException("Error creating logger for logger " + logger + ".  Cause: " + t, t);
        }
    }

    public static synchronized void useCustomLogging(Class<? extends LoggingInterceptor> clazz) {
        setImplementation(clazz);
    }

    public static synchronized void useDefaultLogging() {
        setImplementation(DefaultLoggingInterceptor.class);
    }

    private static void tryImplementation(Runnable runnable) {
        if (logConstructor == null) {
            try {
                runnable.run();
            } catch (Throwable t) {
                // ignore
            }
        }
    }

    private static void setImplementation(Class<? extends LoggingInterceptor> implClass) {
        try {
            Constructor<? extends LoggingInterceptor> candidate = implClass.getConstructor(String.class);
            LoggingInterceptor log = candidate.newInstance(LoggingFactory.class.getName());
            logConstructor = candidate;
        } catch (Throwable t) {
            throw new LoggingException("Error setting Log implementation.  Cause: " + t, t);
        }
    }
}
