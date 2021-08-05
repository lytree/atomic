package top.yang.net;


import java.util.Objects;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.HostnameVerifier;
import okhttp3.Call;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.yang.net.utils.HttpsUtils.SSLParams;
import top.yang.net.build.DeleteBuilder;
import top.yang.net.build.DownloadBuilder;
import top.yang.net.build.GetBuilder;
import top.yang.net.build.PatchBuilder;
import top.yang.net.build.PostBuilder;
import top.yang.net.build.PutBuilder;
import top.yang.net.build.UploadBuilder;
import top.yang.net.interceptor.HttpLoggingInterceptor;
import top.yang.net.interceptor.HttpLoggingInterceptor.Level;

public class HttpManager {

  private final static Logger logger = LoggerFactory.getLogger(HttpManager.class);
  private final OkHttpClient okHttpClient;

  /**
   * construct
   */
  protected HttpManager(SSLParams sslParams) {
    Builder builder = new OkHttpClient().newBuilder().addNetworkInterceptor(new HttpLoggingInterceptor(Level.BODY))
        .retryOnConnectionFailure(false).connectionPool(new ConnectionPool(50, 5, TimeUnit.MINUTES)).
            connectTimeout(5, TimeUnit.SECONDS).
            readTimeout(5, TimeUnit.SECONDS).
            writeTimeout(5, TimeUnit.SECONDS);
    if (!Objects.isNull(sslParams)) {
      builder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
    }
    okHttpClient = builder.
        build();
  }


  private HttpManager() {
    Builder builder = new OkHttpClient().newBuilder().addNetworkInterceptor(new HttpLoggingInterceptor(Level.BODY))
        .retryOnConnectionFailure(false).connectionPool(new ConnectionPool(50, 5, TimeUnit.MINUTES)).
            connectTimeout(5, TimeUnit.SECONDS).
            readTimeout(5, TimeUnit.SECONDS).
            writeTimeout(5, TimeUnit.SECONDS);
    okHttpClient = builder.
        build();
  }


  private HttpManager(OkHttpClient okHttpClient) {
    this.okHttpClient = okHttpClient;
  }

  private HttpManager(HostnameVerifier hostnameVerifier) {
    Builder builder = new OkHttpClient().newBuilder().addNetworkInterceptor(new HttpLoggingInterceptor(Level.BODY))
        .retryOnConnectionFailure(false).connectionPool(new ConnectionPool(50, 5, TimeUnit.MINUTES)).
            connectTimeout(5, TimeUnit.SECONDS).
            readTimeout(5, TimeUnit.SECONDS).
            writeTimeout(5, TimeUnit.SECONDS);
    if (!Objects.isNull(hostnameVerifier)) {
      builder.hostnameVerifier(hostnameVerifier);
    }
    okHttpClient = builder.
        build();
  }

  private HttpManager(HostnameVerifier hostnameVerifier, SSLParams sslParams) {
    Builder builder = new OkHttpClient().newBuilder().addNetworkInterceptor(new HttpLoggingInterceptor(Level.BODY))
        .retryOnConnectionFailure(false).connectionPool(new ConnectionPool(50, 5, TimeUnit.MINUTES)).
            connectTimeout(5, TimeUnit.SECONDS).
            readTimeout(5, TimeUnit.SECONDS).
            writeTimeout(5, TimeUnit.SECONDS);
    if (!Objects.isNull(hostnameVerifier)) {
      builder.hostnameVerifier(hostnameVerifier);
    }
    if (!Objects.isNull(sslParams)) {
      builder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
    }
    okHttpClient = builder.
        build();
  }

  public GetBuilder get() {
    return new GetBuilder(this);
  }

  public PostBuilder post() {
    return new PostBuilder(this);
  }

  public PutBuilder put() {
    return new PutBuilder(this);
  }

  public PatchBuilder patch() {
    return new PatchBuilder(this);
  }

  public DeleteBuilder delete() {
    return new DeleteBuilder(this);
  }

  public UploadBuilder upload() {
    return new UploadBuilder(this);
  }

  public DownloadBuilder download() {
    return new DownloadBuilder(this);
  }
  public  OkHttpClient getOkHttpClient(){
    return this.okHttpClient;
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

  public static HttpManager httpFactory() {
    return new HttpManager();
  }

  public static HttpManager httpFactory(OkHttpClient okHttpClient) {
    return new HttpManager(okHttpClient);
  }

  public static HttpManager httpFactory(SSLParams sslParams) {
    return new HttpManager(sslParams);
  }

  public static HttpManager httpFactory(HostnameVerifier hostnameVerifier) {
    return new HttpManager(hostnameVerifier);
  }

  public static HttpManager httpFactory(SSLParams sslParams, HostnameVerifier hostnameVerifier) {
    return new HttpManager(hostnameVerifier, sslParams);
  }

}
