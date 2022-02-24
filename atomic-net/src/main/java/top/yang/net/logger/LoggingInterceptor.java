package top.yang.net.logger;


import okhttp3.Interceptor;
import top.yang.net.Level;

public abstract class LoggingInterceptor implements Interceptor {


    protected volatile Level level;

    public LoggingInterceptor() {
        level = Level.NONE;
    }

    public LoggingInterceptor(Level level) {
        this.level = level;
    }
}
