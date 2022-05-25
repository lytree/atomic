package top.yang.exception;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Try {

    private final static Logger logger = LoggerFactory.getLogger(Try.class);

    public static <T, R> Function<T, R> of(UncheckedFunction<T, R> mapper, R defaultR) {
        Objects.requireNonNull(mapper);
        return t -> {
            try {
                return mapper.apply(t);
            } catch (Exception ex) {
                logger.error(ExceptionUtils.getStackTrace(ex));
                return defaultR;
            }
        };
    }

    public static <T> Supplier<T> of(UncheckedSupplier<T> mapper, T defaultT) {
        Objects.requireNonNull(mapper);
        return () -> {
            try {
                return mapper.get();
            } catch (Exception ex) {
                logger.error(ExceptionUtils.getStackTrace(ex));
                return defaultT;
            }
        };
    }

    @FunctionalInterface
    public interface UncheckedFunction<T, R> {

        R apply(T t) throws Exception;
    }

    @FunctionalInterface
    public interface UncheckedSupplier<T> {

        T get() throws Exception;
    }
}
