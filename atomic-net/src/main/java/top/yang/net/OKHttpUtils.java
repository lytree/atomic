package top.yang.net;


import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.yang.net.interceptor.HttpLoggingInterceptor;
import top.yang.net.request.HttpRequestCacheControl;
import top.yang.net.request.HttpRequestHeader;
import top.yang.net.request.HttpRequestUrl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
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

    public static void main(String[] args) {
        HashMap<String, Object> hashMap = new HashMap<>();
        Headers header = HttpRequestHeader.header(hashMap);
        Request build = new Request.Builder().url(Objects.requireNonNull(HttpRequestUrl.createUrl("https://www.mzitu.com"))).headers(header).cacheControl(HttpRequestCacheControl.noCache()).get().build();
        try {
            Response execute = okHttpClient.newCall(build).execute();
            boolean successful = execute.isSuccessful();
            if (successful == true){
                String bytes = execute.body().string();
                System.out.println(bytes);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
