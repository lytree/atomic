package top.yang.net.config;

public class ConnectionPoolConfiguration {

    private static final int MAX_IDLE_CONNECTIONS = 10;
    private static final long KEEP_ALIVE_SECOND = 5;

    private int maxIdleConnections;

    private long keepAliveSecond;

    public ConnectionPoolConfiguration() {
        this.maxIdleConnections = MAX_IDLE_CONNECTIONS;
        this.keepAliveSecond = KEEP_ALIVE_SECOND;
    }

    public int getMaxIdleConnections() {
        return maxIdleConnections;
    }

    public void setMaxIdleConnections(int maxIdleConnections) {
        this.maxIdleConnections = maxIdleConnections;
    }

    public long getKeepAliveSecond() {
        return keepAliveSecond;
    }

    public void setKeepAliveSecond(long keepAliveSecond) {
        this.keepAliveSecond = keepAliveSecond;
    }
}
