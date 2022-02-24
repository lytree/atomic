package top.yang.net.spring;

import java.util.concurrent.TimeUnit;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import top.yang.net.config.ConnectionPoolConfiguration;
import top.yang.net.config.OkHttpClientConfiguration;
import top.yang.net.logger.LoggingFactory;

public class OkHttpClientFactoryBean implements FactoryBean<OkHttpClient>, InitializingBean {

    private final static Logger logger = LoggerFactory.getLogger(OkHttpClientFactoryBean.class);
    private OkHttpClient client;
    private OkHttpClientConfiguration okHttpClientConfiguration;
//    private LoggingInterceptor logImpl;

    @Override

    public OkHttpClient getObject() throws Exception {
        if (this.client == null) {
            afterPropertiesSet();
        }
        return this.client;
    }

    @Override
    public Class<?> getObjectType() {
        return this.client == null ? OkHttpClient.class : this.client.getClass();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.client = buildOkHttpClientFactory();
    }


    protected OkHttpClient buildOkHttpClientFactory() {
        final OkHttpClientConfiguration okHttpClientConfiguration;
        if (this.okHttpClientConfiguration != null) {
            okHttpClientConfiguration = this.okHttpClientConfiguration;
        } else {
            logger.debug("Property 'configuration'  not specified, using default OKHttp Configuration");
            okHttpClientConfiguration = new OkHttpClientConfiguration();
        }
        Builder builder = new Builder();
        builder.callTimeout(okHttpClientConfiguration.getCallTimeout(), TimeUnit.SECONDS);
        builder.writeTimeout(okHttpClientConfiguration.getCallTimeout(), TimeUnit.SECONDS);
        builder.readTimeout(okHttpClientConfiguration.getCallTimeout(), TimeUnit.SECONDS);
        builder.connectTimeout(okHttpClientConfiguration.getCallTimeout(), TimeUnit.SECONDS);
        builder.pingInterval(okHttpClientConfiguration.getPingInterval(), TimeUnit.SECONDS);
        ConnectionPoolConfiguration connectionPool = okHttpClientConfiguration.getConnectionPool();
        builder.connectionPool(new ConnectionPool(connectionPool.getMaxIdleConnections(), connectionPool.getKeepAliveSecond(), TimeUnit.SECONDS));
        builder.followRedirects(okHttpClientConfiguration.getFollowRedirects());
        builder.followSslRedirects(okHttpClientConfiguration.getFollowSslRedirects());
        builder.addNetworkInterceptor(LoggingFactory.getLoggingInterceptor(okHttpClientConfiguration.getLogImpl()));
        builder.retryOnConnectionFailure(okHttpClientConfiguration.getRetryOnConnectionFailure());
        return builder.build();
    }
}
