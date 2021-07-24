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
import top.yang.net.HttpsUtils.SSLParams;
import top.yang.net.build.DeleteBuilder;
import top.yang.net.build.DownloadBuilder;
import top.yang.net.build.GetBuilder;
import top.yang.net.build.PatchBuilder;
import top.yang.net.build.PostBuilder;
import top.yang.net.build.PutBuilder;
import top.yang.net.build.UploadBuilder;
import top.yang.net.interceptor.HttpLoggingInterceptor;
import top.yang.net.interceptor.HttpLoggingInterceptor.Level;

public class HttpUtils {

  private final static Logger logger = LoggerFactory.getLogger(HttpUtils.class);
  private static OkHttpClient okHttpClient;

  /**
   * construct
   */
  protected HttpUtils(SSLParams sslParams) {
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


  protected HttpUtils() {
    Builder builder = new OkHttpClient().newBuilder().addNetworkInterceptor(new HttpLoggingInterceptor(Level.BODY))
        .retryOnConnectionFailure(false).connectionPool(new ConnectionPool(50, 5, TimeUnit.MINUTES)).
            connectTimeout(5, TimeUnit.SECONDS).
            readTimeout(5, TimeUnit.SECONDS).
            writeTimeout(5, TimeUnit.SECONDS);
    okHttpClient = builder.
        build();
  }


  protected HttpUtils(OkHttpClient okHttpClient) {
    HttpUtils.okHttpClient = okHttpClient;
  }

  protected HttpUtils(HostnameVerifier hostnameVerifier) {
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

  public HttpUtils(HostnameVerifier hostnameVerifier, SSLParams sslParams) {
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

  public OkHttpClient create() {
    return okHttpClient;
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

  /**
   * do cacel by tag
   *
   * @param tag tag
   */
  public void cancel(Object tag) {
    Dispatcher dispatcher = okHttpClient.dispatcher();
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

  public static HttpUtils createFactory() {
    return new HttpUtils();
  }

  public static HttpUtils createFactory(OkHttpClient okHttpClient) {
    return new HttpUtils(okHttpClient);
  }

  public static HttpUtils createFactory(SSLParams sslParams) {
    return new HttpUtils(sslParams);
  }

  public static HttpUtils createFactory(HostnameVerifier hostnameVerifier) {
    return new HttpUtils(hostnameVerifier);
  }

  public static HttpUtils createFactory(SSLParams sslParams, HostnameVerifier hostnameVerifier) {
    return new HttpUtils(hostnameVerifier, sslParams);
  }

  public static void main(String[] args) {
    HttpUtils httpUtils = HttpUtils.createFactory();
    OkHttpClient okHttpClient = httpUtils.create();
    OkHttpClient okHttpClient1 = httpUtils.create();
  }
}
