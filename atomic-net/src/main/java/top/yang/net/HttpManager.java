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
import top.yang.net.build.DeleteBuilder;
import top.yang.net.build.DownloadBuilder;
import top.yang.net.build.GetBuilder;
import top.yang.net.build.PatchBuilder;
import top.yang.net.build.PostBuilder;
import top.yang.net.build.PutBuilder;
import top.yang.net.build.UploadBuilder;
import top.yang.net.interceptor.HttpLoggingInterceptor;
import top.yang.net.interceptor.HttpLoggingInterceptor.Level;
import top.yang.net.utils.HttpsUtils.SSLParams;

public class HttpManager {

  private final static Logger logger = LoggerFactory.getLogger(HttpManager.class);

  public GetBuilder get(OkHttpClient okHttpClient) {
    return new GetBuilder(okHttpClient);
  }

  public PostBuilder post(OkHttpClient okHttpClient) {
    return new PostBuilder(okHttpClient);
  }

  public PutBuilder put(OkHttpClient okHttpClient) {
    return new PutBuilder(okHttpClient);
  }

  public PatchBuilder patch(OkHttpClient okHttpClient) {
    return new PatchBuilder(okHttpClient);
  }

  public DeleteBuilder delete(OkHttpClient okHttpClient) {
    return new DeleteBuilder(okHttpClient);
  }

  public UploadBuilder upload(OkHttpClient okHttpClient) {
    return new UploadBuilder(okHttpClient);
  }

  public DownloadBuilder download(OkHttpClient okHttpClient) {
    return new DownloadBuilder(okHttpClient);
  }

  /**
   * 根据tag 取消请求
   *
   * @param tag tag
   */
  public void cancel(OkHttpClient okHttpClient, Object tag) {
    if (okHttpClient == null) {
      return;
    }
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

  /**
   * 取消所有请求请求
   */
  public void cancelAll(OkHttpClient okHttpClient) {
    if (okHttpClient == null) {
      return;
    }
    Dispatcher dispatcher = okHttpClient.dispatcher();

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

  private HttpManager() {
  }
}
