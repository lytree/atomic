package top.yang.net.config;

import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

import org.springframework.core.env.Environment;
import top.yang.net.Level;
import top.yang.net.logger.LoggingFactory;
import top.yang.net.logger.LoggingInterceptor;

public class OkHttpClientConfiguration {

    private static final long CALL_TIMEOUT = SECONDS.toMillis(30);
    private static final long READ_TIMEOUT = SECONDS.toMillis(5);
    private static final long WRITE_TIMEOUT = MINUTES.toMillis(10);
    private static final long CONNECT_TIMEOUT = MINUTES.toMillis(30);
    private static final long PING_INTERVAL = MINUTES.toMillis(30);
    private static final boolean RETRY_ON_CONNECTION_FAILURE = true;
    private static final boolean FOLLOW_REDIRECTS = true;
    private static final boolean FOLLOW_SSL_REDIRECTS = true;
    private static final int DEFAULT_POOL_SIZE = 10;
    private Long callTimeout;
    private Long readTimeout;
    private Long writeTimeout;
    private Long connectTimeout;
    private Long pingInterval;
    private Boolean retryOnConnectionFailure;
    private Boolean followRedirects;
    private Boolean followSslRedirects;
    private Class<? extends LoggingInterceptor> logImpl;
    private Level level;
    private ConnectionPoolConfiguration connectionPool;
    protected Environment environment;

    public OkHttpClientConfiguration() {
        callTimeout = CALL_TIMEOUT;
        readTimeout = READ_TIMEOUT;
        writeTimeout = WRITE_TIMEOUT;
        connectTimeout = CONNECT_TIMEOUT;
        pingInterval = PING_INTERVAL;
        retryOnConnectionFailure = RETRY_ON_CONNECTION_FAILURE;
        followRedirects = FOLLOW_REDIRECTS;
        followSslRedirects = FOLLOW_SSL_REDIRECTS;
    }

    public OkHttpClientConfiguration(Environment environment) {
        this();
        this.environment = environment;
    }

    public Long getPingInterval() {
        return pingInterval;
    }

    public void setPingInterval(Long pingInterval) {
        this.pingInterval = pingInterval;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    public Long getCallTimeout() {
        return callTimeout;
    }

    public void setCallTimeout(Long callTimeout) {
        this.callTimeout = callTimeout;
    }

    public Long getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(Long readTimeout) {
        this.readTimeout = readTimeout;
    }

    public Long getWriteTimeout() {
        return writeTimeout;
    }

    public void setWriteTimeout(Long writeTimeout) {
        this.writeTimeout = writeTimeout;
    }

    public Long getConnectTimeout() {
        return connectTimeout;
    }

    public Boolean getRetryOnConnectionFailure() {
        return retryOnConnectionFailure;
    }

    public void setRetryOnConnectionFailure(Boolean retryOnConnectionFailure) {
        this.retryOnConnectionFailure = retryOnConnectionFailure;
    }

    public Boolean getFollowRedirects() {
        return followRedirects;
    }

    public void setFollowRedirects(Boolean followRedirects) {
        this.followRedirects = followRedirects;
    }

    public Boolean getFollowSslRedirects() {
        return followSslRedirects;
    }

    public void setFollowSslRedirects(Boolean followSslRedirects) {
        this.followSslRedirects = followSslRedirects;
    }

    public Class<? extends LoggingInterceptor> getLogImpl() {
        return logImpl;
    }

    public void setLogImpl(Class<? extends LoggingInterceptor> logImpl) {
        if (logImpl != null) {
            this.logImpl = logImpl;
            LoggingFactory.useCustomLogging(this.logImpl);
        }
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public void setConnectTimeout(Long connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public ConnectionPoolConfiguration getConnectionPool() {
        return this.connectionPool;
    }

    public void setConnectionPool(ConnectionPoolConfiguration connectionPool) {
        this.connectionPool = connectionPool;
    }

}
