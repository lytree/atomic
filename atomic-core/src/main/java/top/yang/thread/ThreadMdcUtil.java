package top.yang.thread;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Supplier;
import org.slf4j.MDC;

/**
 * 日志 MDC TraceId
 * @author pride
 */
public class ThreadMdcUtil {

    public static void setTraceIdIfAbsent(String key, Supplier<String> id) {
        if (MDC.get(key) == null) {
            MDC.put(key, id.get());
        }
    }

    public static <T> Callable<T> wrap(final Callable<T> callable, final Map<String, String> context, String key, Supplier<String> id) {
        return () -> {
            if (context == null) {
                MDC.clear();
            } else {
                MDC.setContextMap(context);
            }
            setTraceIdIfAbsent(key, id);
            try {
                return callable.call();
            } finally {
                MDC.clear();
            }
        };
    }

    public static Runnable wrap(final Runnable runnable, final Map<String, String> context, String key, Supplier<String> id) {
        return () -> {
            if (context == null) {
                MDC.clear();
            } else {
                MDC.setContextMap(context);
            }
            setTraceIdIfAbsent(key, id);
            try {
                runnable.run();
            } finally {
                MDC.clear();
            }
        };

    }
}