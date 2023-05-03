package top.lytree.exception;

import java.io.IOException;
import java.io.Serial;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import top.lytree.lang.StringUtils;

/**
 * 基于可抛出原因的List IOException。
 * <p>
 * The first exception in the list is used as this exception's cause and is accessible with the usual {@link #getCause()} while the complete list is accessible with {@link
 * #getCauseList()}.
 * </p>
 */
public class IOListException extends IOException {

    /**
     * Defines the serial version UID.
     */
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 如果列表不为空或空，则抛出此异常。
     *
     * @param causeList The list to test.
     * @param message   The detail message, see {@link #getMessage()}.
     * @throws IOListException if the list is not null or empty.
     */
    public static void checkEmpty(final List<? extends Throwable> causeList, final Object message) throws IOListException {
        if (!isEmpty(causeList)) {
            throw new IOListException(Objects.toString(message, null), causeList);
        }
    }

    private static boolean isEmpty(final List<? extends Throwable> causeList) {
        return causeList == null || causeList.isEmpty();
    }

    private static String toMessage(final List<? extends Throwable> causeList) {
        return StringUtils.format("{} exception(s): {}", causeList == null ? 0 : causeList.size(), causeList);
    }

    private final List<? extends Throwable> causeList;

    /**
     * 创建由异常列表引起的新异常。
     *
     * @param causeList a list of cause exceptions.
     */
    public IOListException(final List<? extends Throwable> causeList) {
        this(toMessage(causeList), causeList);
    }

    /**
     * Creates a new exception caused by a list of exceptions.
     *
     * @param message   The detail message, see {@link #getMessage()}.
     * @param causeList a list of cause exceptions.
     */
    public IOListException(final String message, final List<? extends Throwable> causeList) {
        super(message != null ? message : toMessage(causeList), isEmpty(causeList) ? null : causeList.get(0));
        this.causeList = causeList == null ? Collections.emptyList() : causeList;
    }

    /**
     * Gets the cause exception at the given index.
     *
     * @param <T>   type of exception to return.
     * @param index index in the cause list.
     * @return The list of causes.
     */
    public <T extends Throwable> T getCause(final int index) {
        return (T) causeList.get(index);
    }

    /**
     * Gets the cause exception at the given index.
     *
     * @param <T>   type of exception to return.
     * @param index index in the cause list.
     * @param clazz type of exception to return.
     * @return The list of causes.
     */
    public <T extends Throwable> T getCause(final int index, final Class<T> clazz) {
        return clazz.cast(getCause(index));
    }

    /**
     * Gets the cause list.
     *
     * @param <T> type of exception to return.
     * @return The list of causes.
     */
    public <T extends Throwable> List<T> getCauseList() {
        return (List<T>) causeList;
    }

    /**
     * Works around Throwable and Generics, may fail at runtime depending on the argument value.
     *
     * @param <T>   type of exception to return.
     * @param clazz the target type
     * @return The list of causes.
     */
    public <T extends Throwable> List<T> getCauseList(final Class<T> clazz) {
        return (List<T>) causeList;
    }

}
