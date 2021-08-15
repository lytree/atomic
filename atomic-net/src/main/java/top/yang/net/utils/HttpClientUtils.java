package top.yang.net.utils;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HostnameVerifier;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import top.yang.net.interceptor.HttpLoggingInterceptor;
import top.yang.net.interceptor.HttpLoggingInterceptor.Level;
import top.yang.net.utils.HttpsUtils.SSLParams;

public class HttpClientUtils {

  public static OkHttpClient getOkHttpClient(SSLParams sslParams) {
    Builder builder = new OkHttpClient().newBuilder().addNetworkInterceptor(new HttpLoggingInterceptor(Level.HEADERS))
        .retryOnConnectionFailure(false).connectionPool(new ConnectionPool(20, 5, TimeUnit.MINUTES)).
            connectTimeout(5, TimeUnit.SECONDS).
            readTimeout(5, TimeUnit.SECONDS).
            writeTimeout(5, TimeUnit.SECONDS);
    if (!Objects.isNull(sslParams)) {
      builder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
    }
    return builder.build();
  }


  public static OkHttpClient getOkHttpClient() {
    Builder builder = new OkHttpClient().newBuilder().addNetworkInterceptor(new HttpLoggingInterceptor(Level.HEADERS))
        .retryOnConnectionFailure(false).connectionPool(new ConnectionPool(20, 5, TimeUnit.MINUTES)).
            connectTimeout(5, TimeUnit.SECONDS).
            readTimeout(5, TimeUnit.SECONDS).
            writeTimeout(5, TimeUnit.SECONDS);
    return builder.build();
  }

  public static OkHttpClient getOkHttpClient(HostnameVerifier hostnameVerifier) {
    Builder builder = new OkHttpClient().newBuilder().addNetworkInterceptor(new HttpLoggingInterceptor(Level.HEADERS))
        .retryOnConnectionFailure(false).connectionPool(new ConnectionPool(20, 5, TimeUnit.MINUTES)).
            connectTimeout(5, TimeUnit.SECONDS).
            readTimeout(5, TimeUnit.SECONDS).
            writeTimeout(5, TimeUnit.SECONDS);
    if (!Objects.isNull(hostnameVerifier)) {
      builder.hostnameVerifier(hostnameVerifier);
    }
    return builder.
        build();
  }

  public static OkHttpClient getOkHttpClient(HostnameVerifier hostnameVerifier, SSLParams sslParams) {
    Builder builder = new OkHttpClient().newBuilder().addNetworkInterceptor(new HttpLoggingInterceptor(Level.HEADERS))
        .retryOnConnectionFailure(false).connectionPool(new ConnectionPool(20, 5, TimeUnit.MINUTES)).
            connectTimeout(5, TimeUnit.SECONDS).
            readTimeout(5, TimeUnit.SECONDS).
            writeTimeout(5, TimeUnit.SECONDS);
    if (!Objects.isNull(hostnameVerifier)) {
      builder.hostnameVerifier(hostnameVerifier);
    }
    if (!Objects.isNull(sslParams)) {
      builder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
    }
    return builder.
        build();
  }

  public static OkHttpClient getOkHttpClient(String hostname, int port, Proxy.Type type) {
    InetSocketAddress inetSocketAddress = new InetSocketAddress(hostname, port);
    Builder builder = new OkHttpClient().newBuilder().addNetworkInterceptor(new HttpLoggingInterceptor(Level.HEADERS))
        .proxy(new Proxy(type, new InetSocketAddress(hostname, 8080)))
        .retryOnConnectionFailure(false).connectionPool(new ConnectionPool(20, 5, TimeUnit.MINUTES)).
            connectTimeout(5, TimeUnit.SECONDS).
            readTimeout(5, TimeUnit.SECONDS).
            writeTimeout(5, TimeUnit.SECONDS);
    return builder.
        build();
  }
}
