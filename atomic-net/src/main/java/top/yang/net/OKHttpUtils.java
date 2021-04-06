package top.yang.net;


import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.yang.net.interceptor.HttpLoggingInterceptor;

import java.util.concurrent.TimeUnit;

public class OKHttpUtils {
    private final static Logger logger = LoggerFactory.getLogger(OKHttpUtils.class);
    private static final OkHttpClient okHttpClient;

    static {
        okHttpClient = new OkHttpClient().newBuilder().addNetworkInterceptor(new HttpLoggingInterceptor(HttpLoggingInterceptor.Level.BODY))
                .retryOnConnectionFailure(false).connectionPool(new ConnectionPool(50, 5, TimeUnit.MINUTES)).
                        connectTimeout(5, TimeUnit.SECONDS).
                        readTimeout(5, TimeUnit.SECONDS).
                        writeTimeout(5, TimeUnit.SECONDS).
                        build();
    }
}
