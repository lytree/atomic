package top.yang.net.config;

public class ConnectionPoolConfiguration {

    private int maxIdleConnections;


    private long keepAliveSecond;

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
