package top.yang.exception;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;

public class ExceptionUtils extends org.apache.commons.lang3.exception.ExceptionUtils {

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
     * Gets a short message summarising the exception.
     * <p>
     * The message returned is of the form {ClassNameWithoutPackage}: {ThrowableMessage}
     *
     * @param th the throwable to get a message for, null returns empty string
     * @return the message, non-null
     * @since 2.2
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
     * <p>Introspects the {@code Throwable} to obtain the root cause.</p>
     *
     * <p>This method walks through the exception chain to the last element,
     * "root" of the tree, using {@link Throwable#getCause()}, and returns that exception.</p>
     *
     * <p>From version 2.2, this method handles recursive cause structures
     * that might otherwise cause infinite loops. If the throwable parameter has a cause of itself, then null will be returned. If the throwable parameter cause chain loops, the
     * last element in the chain before the loop is returned.</p>
     *
     * @param throwable the throwable to get the root cause for, may be null
     * @return the root cause of the {@code Throwable}, {@code null} if null throwable input
     */
    public static Throwable getRootCause(final Throwable throwable) {
        final List<Throwable> list = getThrowableList(throwable);
        return list.isEmpty() ? null : list.get(list.size() - 1);
    }

    //-----------------------------------------------------------------------

    /**
     * Gets a short message summarising the root cause exception.
     * <p>
     * The message returned is of the form {ClassNameWithoutPackage}: {ThrowableMessage}
     *
     * @param th the throwable to get a message for, null returns empty string
     * @return the message, non-null
     * @since 2.2
     */
    public static String getRootCauseMessage(final Throwable th) {
        Throwable root = getRootCause(th);
        root = root == null ? th : root;
        return getMessage(root);
    }

    //-----------------------------------------------------------------------

    /**
     * <p>Creates a compact stack trace for the root cause of the supplied
     * {@code Throwable}.</p>
     *
     * <p>The output of this method is consistent across JDK versions.
     * It consists of the root exception followed by each of its wrapping exceptions separated by '[wrapped]'. Note that this is the opposite order to the JDK1.4 display.</p>
     *
     * @param throwable the throwable to examine, may be null
     * @return an array of stack trace frames, never null
     * @since 2.0
     */
    public static String[] getRootCauseStackTrace(final Throwable throwable) {
        if (throwable == null) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        final Throwable[] throwables = getThrowables(throwable);
        final int count = throwables.length;
        final List<String> frames = new ArrayList<>();
        List<String> nextTrace = getStackFrameList(throwables[count - 1]);
        for (int i = count; --i >= 0; ) {
            final List<String> trace = nextTrace;
            if (i != 0) {
                nextTrace = getStackFrameList(throwables[i - 1]);
                removeCommonFrames(trace, nextTrace);
            }
            if (i == count - 1) {
                frames.add(throwables[i].toString());
            } else {
                frames.add(WRAPPED_MARKER + throwables[i].toString());
            }
            frames.addAll(trace);
        }
        return frames.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
    }

    /**
     * <p>Produces a {@code List} of stack frames - the message
     * is not included. Only the trace of the specified exception is returned, any caused by trace is stripped.</p>
     *
     * <p>This works in most cases - it will only fail if the exception
     * message contains a line that starts with: {@code &quot;&nbsp;&nbsp;&nbsp;at&quot;.}</p>
     *
     * @param t is any throwable
     * @return List of stack frames
     */
    static List<String> getStackFrameList(final Throwable t) {
        final String stackTrace = getStackTrace(t);
        final String linebreak = System.lineSeparator();
        final StringTokenizer frames = new StringTokenizer(stackTrace, linebreak);
        final List<String> list = new ArrayList<>();
        boolean traceStarted = false;
        while (frames.hasMoreTokens()) {
            final String token = frames.nextToken();
            // Determine if the line starts with <whitespace>at
            final int at = token.indexOf("at");
            if (at != NOT_FOUND && token.substring(0, at).trim().isEmpty()) {
                traceStarted = true;
                list.add(token);
            } else if (traceStarted) {
                break;
            }
        }
        return list;
    }

    //-----------------------------------------------------------------------

    /**
     * <p>Returns an array where each element is a line from the argument.</p>
     *
     * <p>The end of line is determined by the value of {@link System#lineSeparator()}.</p>
     *
     * @param stackTrace a stack trace String
     * @return an array where each element is a line from the argument
     */
    static String[] getStackFrames(final String stackTrace) {
        final String linebreak = System.lineSeparator();
        final StringTokenizer frames = new StringTokenizer(stackTrace, linebreak);
        final List<String> list = new ArrayList<>();
        while (frames.hasMoreTokens()) {
            list.add(frames.nextToken());
        }
        return list.toArray(ArrayUtils.EMPTY_STRING_ARRAY);
    }

    /**
     * <p>Captures the stack trace associated with the specified
     * {@code Throwable} object, decomposing it into a list of stack frames.</p>
     *
     * <p>The result of this method vary by JDK version as this method
     * uses {@link Throwable#printStackTrace(java.io.PrintWriter)}. On JDK1.3 and earlier, the cause exception will not be shown unless the specified throwable alters
     * printStackTrace.</p>
     *
     * @param throwable the {@code Throwable} to examine, may be null
     * @return an array of strings describing each stack frame, never null
     */
    public static String[] getStackFrames(final Throwable throwable) {
        if (throwable == null) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }
        return getStackFrames(getStackTrace(throwable));
    }

    //-----------------------------------------------------------------------

    /**
     * <p>Gets the stack trace from a Throwable as a String.</p>
     *
     * <p>The result of this method vary by JDK version as this method
     * uses {@link Throwable#printStackTrace(java.io.PrintWriter)}. On JDK1.3 and earlier, the cause exception will not be shown unless the specified throwable alters
     * printStackTrace.</p>
     *
     * @param throwable the {@code Throwable} to be examined
     * @return the stack trace as generated by the exception's {@code printStackTrace(PrintWriter)} method
     */
    public static String getStackTrace(final Throwable throwable) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }

    /**
     * Does the throwable's causal chain have an immediate or wrapped exception of the given type?
     *
     * @param chain The root of a Throwable causal chain.
     * @param type  The exception type to test.
     * @return true, if chain is an instance of type or is an UndeclaredThrowableException wrapping a cause.
     * @see #wrapAndThrow(Throwable)
     * @since 3.5
     */
    public static boolean hasCause(Throwable chain,
            final Class<? extends Throwable> type) {
        if (chain instanceof UndeclaredThrowableException) {
            chain = chain.getCause();
        }
        return type.isInstance(chain);
    }

    /**
     * Throw a checked exception without adding the exception to the throws clause of the calling method. For checked exceptions, this method throws an UndeclaredThrowableException
     * wrapping the checked exception. For Errors and RuntimeExceptions, the original exception is rethrown.
     * <p>
     * The downside to using this approach is that invoking code which needs to handle specific checked exceptions must sniff up the exception chain to determine if the caught
     * exception was caused by the checked exception.
     *
     * @param throwable The throwable to rethrow.
     * @param <R>       The type of the returned value.
     * @return Never actually returned, this generic type matches any type which the calling site requires. "Returning" the results of this method will satisfy the java compiler
     * requirement that all code paths return a value.
     * @see #rethrow(Throwable)
     * @see #hasCause(Throwable, Class)
     * @since 3.5
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
}
