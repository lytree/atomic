package top.lytree.exception;

import top.lytree.collections.ArrayUtils;
import top.lytree.bean.ClassUtils;
import top.lytree.lang.StringUtils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.List;

public class ExceptionUtils {

    private static final int NOT_FOUND = -1;
    /**
     * <p>Used when printing stack frames to denote the start of a
     * wrapped exception.</p>
     *
     * <p>Package private for accessibility by test suite.</p>
     */
    static final String WRAPPED_MARKER = " [wrapped] ";
//-----------------------------------------------------------------------

    /**
     * 获得完整消息，包括异常名，消息格式为：{SimpleClassName}: {ThrowableMessage}
     *
     * @param th 异常
     *
     * @return 完整消息
     */
    public static String getMessage(final Throwable th) {
        if (th == null) {
            return StringUtils.EMPTY;
        }
        final String clsName = ClassUtils.getShortClassName(th, null);
        final String msg = th.getMessage();
        return clsName + ": " + StringUtils.defaultString(msg);
    }

    /**
     * 获取异常链中最尾端的异常，即异常最早发生的异常对象。<br>
     * 此方法通过调用{@link Throwable#getCause()} 直到没有cause为止，如果异常本身没有cause，返回异常本身<br>
     * 传入null返回也为null
     *
     * <p>
     * 此方法来自Apache-Commons-Lang3
     * </p>
     *
     * @param throwable 异常对象，可能为null
     * @return 最尾端异常，传入null参数返回也为null
     */
    public static Throwable getRootCause(final Throwable throwable) {
        final List<Throwable> list = getThrowableList(throwable);
        return list.isEmpty() ? null : list.get(list.size() - 1);
    }

    //-----------------------------------------------------------------------

    /**
     * 获取异常链中最尾端的异常的消息，消息格式为：{SimpleClassName}: {ThrowableMessage}
     *
     * @param th 异常
     * @return 消息
     */
    public static String getRootCauseMessage(final Throwable th) {
        Throwable root = getRootCause(th);
        root = root == null ? th : root;
        return getMessage(root);
    }

    //-----------------------------------------------------------------------

    /**
     * 堆栈转为完整字符串
     *
     * @param throwable 异常对象
     * @return 堆栈转为的字符串
     */
    public static String getStackTrace(final Throwable throwable) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }

    public static boolean hasCause(Throwable chain,
            final Class<? extends Throwable> type) {
        if (chain instanceof UndeclaredThrowableException) {
            chain = chain.getCause();
        }
        return type.isInstance(chain);
    }

    /**
     * 包装异常并重新抛出此异常<br>
     * {@link RuntimeException} 和{@link Error} 直接抛出，其它检查异常包装为{@link UndeclaredThrowableException} 后抛出
     *
     * @param throwable 异常
     */
    public static <R> R wrapAndThrow(final Throwable throwable) {
        if (throwable instanceof RuntimeException) {
            throw (RuntimeException) throwable;
        }
        if (throwable instanceof Error) {
            throw (Error) throwable;
        }
        throw new UndeclaredThrowableException(throwable);
    }

    /**
     * 获取异常链上所有异常的集合，如果{@link Throwable} 对象没有cause，返回只有一个节点的List<br>
     * 如果传入null，返回空集合
     *
     * <p>
     * 此方法来自Apache-Commons-Lang3
     * </p>
     *
     * @param throwable 异常对象，可以为null
     * @return 异常链中所有异常集合
     *
     */
    public static List<Throwable> getThrowableList(Throwable throwable) {
        final List<Throwable> list = new ArrayList<>();
        while (throwable != null && !list.contains(throwable)) {
            list.add(throwable);
            throwable = throwable.getCause();
        }
        return list;
    }
    /**
     * 获取异常链上所有异常的集合，如果{@link Throwable} 对象没有cause，返回只有一个节点的List<br>
     * 如果传入null，返回空集合
     *
     * <p>
     * 此方法来自Apache-Commons-Lang3
     * </p>
     *
     * @param throwable 异常对象，可以为null
     * @return 异常链中所有异常数组
     */
    public static Throwable[] getThrowables(final Throwable throwable) {
        final List<Throwable> list = getThrowableList(throwable);
        return list.toArray(ArrayUtils.EMPTY_THROWABLE_ARRAY);
    }


    public static <R> R rethrow(final Throwable throwable) {
        // claim that the typeErasure invocation throws a RuntimeException
        return ExceptionUtils.typeErasure(throwable);
    }

    @SuppressWarnings("unchecked")
    private static <R, T extends Throwable> R typeErasure(final Throwable throwable) throws T {
        throw (T) throwable;
    }

}
