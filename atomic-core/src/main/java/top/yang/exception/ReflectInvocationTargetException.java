package top.yang.exception;

import java.io.PrintStream;
import java.io.PrintWriter;

public class ReflectInvocationTargetException extends RuntimeException {

    private Throwable targetException;

    public ReflectInvocationTargetException(Throwable targetException) {
        this.targetException = targetException;
    }

    public Throwable getTargetException() {
        return targetException;
    }

    @Override
    public String getMessage() {
        return targetException != null ?(targetException.getMessage()):(super.getMessage());
    }

    @Override
    public void printStackTrace() {
        if(targetException != null){
            targetException.getMessage();
        } else {
            super.getMessage();
        }
    }

    @Override
    public void printStackTrace(PrintStream s) {
        if(targetException != null){
            targetException.printStackTrace(s);
        } else {
            super.printStackTrace(s);
        }
    }

    @Override
    public void printStackTrace(PrintWriter s) {
        if(targetException != null){
            targetException.printStackTrace(s);
        } else {
            super.printStackTrace(s);
        }
    }

    @Override
    public StackTraceElement[] getStackTrace() {
        return targetException != null ?(targetException.getStackTrace()):(super.getStackTrace());
    }
}
