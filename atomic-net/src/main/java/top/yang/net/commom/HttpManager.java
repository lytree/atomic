package top.yang.net.commom;


import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.EventListener;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.yang.net.build.DeleteBuilder;
import top.yang.net.build.DeleteBuilderAsync;
import top.yang.net.build.DownloadBuilderAsync;
import top.yang.net.build.GetBuilder;
import top.yang.net.build.GetBuilderAsync;
import top.yang.net.build.PatchBuilder;
import top.yang.net.build.PatchBuilderAsync;
import top.yang.net.build.PostBuilder;
import top.yang.net.build.PostBuilderAsync;
import top.yang.net.build.PutBuilder;
import top.yang.net.build.PutBuilderAsync;
import top.yang.net.build.UploadBuilderAsync;
import top.yang.net.interceptor.DefaultLoggingInterceptor;
import top.yang.net.interceptor.DefaultLoggingInterceptor.Level;

public class HttpManager {

    private final static Logger logger = LoggerFactory.getLogger(HttpManager.class);
    private final OkHttpClient okHttpClient;

    public GetBuilderAsync asyncGet() {
        return new GetBuilderAsync(this.okHttpClient);
    }

    public GetBuilder get() {
        return new GetBuilder(this.okHttpClient);
    }

    public PostBuilderAsync asyncPost() {
        return new PostBuilderAsync(this.okHttpClient);
    }

    public PostBuilder post() {
        return new PostBuilder(this.okHttpClient);
    }

    public PutBuilderAsync asyncPut() {
        return new PutBuilderAsync(this.okHttpClient);
    }

    public PutBuilder put() {
        return new PutBuilder(this.okHttpClient);
    }

    public PatchBuilderAsync asyncPatch() {
        return new PatchBuilderAsync(this.okHttpClient);
    }

    public PatchBuilder patch() {
        return new PatchBuilder(this.okHttpClient);
    }

    public DeleteBuilderAsync asyncDelete() {
        return new DeleteBuilderAsync(this.okHttpClient);
    }

    public DeleteBuilder delete() {
        return new DeleteBuilder(this.okHttpClient);
    }

    public UploadBuilderAsync asyncUpload() {
        return new UploadBuilderAsync(this.okHttpClient);
    }

    public DownloadBuilderAsync asyncDownload() {
        return new DownloadBuilderAsync(this.okHttpClient);
    }

    /**
     * 根据tag 取消请求
     *
     * @param tag tag
     */
    public void cancel(Object tag) {
        if (this.okHttpClient == null) {
            return;
        }
        Dispatcher dispatcher = this.okHttpClient.dispatcher();
        for (Call call : dispatcher.queuedCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
        for (Call call : dispatcher.runningCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
    }

    /**
     * 取消所有请求请求
     */
    public void cancelAll() {
        if (this.okHttpClient == null) {
            return;
        }
        Dispatcher dispatcher = this.okHttpClient.dispatcher();

        for (Call call : dispatcher.queuedCalls()) {
            call.cancel();
        }
        for (Call call : dispatcher.runningCalls()) {
            call.cancel();
        }
    }

    /**
     * 构建一个默认配置的 HTTP Client 类
     */
    private HttpManager() {
        this(null, null,
                Constants.CONNECT_TIMEOUT, Constants.READ_TIMEOUT, Constants.WRITE_TIMEOUT,
                Constants.DISPATCHER_MAX_REQUESTS, Constants.DISPATCHER_MAX_REQUESTS_PER_HOST,
                Constants.CONNECTION_POOL_MAX_IDLE_COUNT, Constants.CONNECTION_POOL_MAX_IDLE_MINUTES);
    }

    /**
     * 构建一个自定义配置的 HTTP Client 类
     */
    private HttpManager(ClientConfiguration cfg) {
        this(cfg.dns, cfg.proxy,
                cfg.connectTimeout, cfg.readTimeout, cfg.writeTimeout,
                cfg.dispatcherMaxRequests, cfg.dispatcherMaxRequestsPerHost,
                cfg.connectionPoolMaxIdleCount, cfg.connectionPoolMaxIdleMinutes);
    }

    /**
     * 构建一个自定义配置的 HTTP Client 类
     */
    private HttpManager(final Dns dns, final ProxyConfiguration proxy,
            int connTimeout, int readTimeout, int writeTimeout, int dispatcherMaxRequests,
            int dispatcherMaxRequestsPerHost, int connectionPoolMaxIdleCount,
            int connectionPoolMaxIdleMinutes) {
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(dispatcherMaxRequests);
        dispatcher.setMaxRequestsPerHost(dispatcherMaxRequestsPerHost);
        ConnectionPool connectionPool = new ConnectionPool(connectionPoolMaxIdleCount,
                connectionPoolMaxIdleMinutes, TimeUnit.MINUTES);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        builder.dispatcher(dispatcher);
        builder.connectionPool(connectionPool);
        builder.addInterceptor(new DefaultLoggingInterceptor(Level.HEADERS));
        if (dns != null) {
            builder.dns(hostname -> {
                try {
                    return dns.lookup(hostname);
                } catch (Exception ignored) {
                }
                return okhttp3.Dns.SYSTEM.lookup(hostname);
            });
        }
        if (proxy != null) {
            builder.proxy(proxy.proxy());
            if (proxy.user != null && proxy.password != null) {
                builder.proxyAuthenticator(proxy.authenticator());
            }
        }
        builder.connectTimeout(connTimeout, TimeUnit.SECONDS);
        builder.readTimeout(readTimeout, TimeUnit.SECONDS);
        builder.writeTimeout(writeTimeout, TimeUnit.SECONDS);
        builder.eventListener(new EventListener() {
            @Override
            public void connectStart(@NotNull Call call, @NotNull InetSocketAddress inetSocketAddress, @NotNull Proxy proxy) {
                Request req = call.request();
                IpTag tag = (IpTag) req.tag();
                tag.ip = inetSocketAddress + "";
            }
        });
        builder.addInterceptor(new DefaultLoggingInterceptor(Level.BASIC));
        builder.addNetworkInterceptor(chain -> {
            Request request = chain.request();
            okhttp3.Response response = chain.proceed(request);
            IpTag tag = (IpTag) request.tag();
            try {
                tag.ip = Objects.requireNonNull(chain.connection()).socket().getRemoteSocketAddress() + "";
            } catch (Exception e) {
                // ingore
            }
            return response;
        });
        this.okHttpClient = builder.build();
    }

    private static class IpTag {

        public String ip = "";
    }

    public static HttpManager httpFactory() {
        return new HttpManager();
    }

    public static HttpManager httpFactory(ClientConfiguration cfg) {
        return new HttpManager(cfg);
    }

    public static HttpManager httpFactory(final Dns dns, final ProxyConfiguration proxy,
            int connTimeout, int readTimeout, int writeTimeout, int dispatcherMaxRequests,
            int dispatcherMaxRequestsPerHost, int connectionPoolMaxIdleCount,
            int connectionPoolMaxIdleMinutes) {
        return new HttpManager(dns, proxy,
                connTimeout, readTimeout, writeTimeout, dispatcherMaxRequests,
                dispatcherMaxRequestsPerHost, connectionPoolMaxIdleCount,
                connectionPoolMaxIdleMinutes);
    }
}
