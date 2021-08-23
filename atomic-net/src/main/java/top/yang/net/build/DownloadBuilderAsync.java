package top.yang.net.build;


import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.yang.net.body.ProgressResponseBody;
import top.yang.net.callback.DownloadCallback;
import top.yang.net.response.DownloadResponseHandler;

/**
 * download builder Created by tsy on 16/9/18.
 */
public class DownloadBuilderAsync {

  private final static Logger logger = LoggerFactory.getLogger(DownloadBuilderAsync.class);
  private final OkHttpClient okHttpClient;

  private String url = "";
  private Object tag;
  private Map<String, String> header;

  private String fileDir = "";        //文件dir
  private String fileName = "";       //文件名
  private String filePath = "";       //文件路径 （如果设置该字段则上面2个就不需要）

  private Long completeBytes = 0L;    //已经完成的字节数 用于断点续传

  public DownloadBuilderAsync(OkHttpClient okHttpClient) {
    this.okHttpClient = okHttpClient;
  }

  public DownloadBuilderAsync url(String url) {
    this.url = url;
    return this;
  }

  /**
   * set file storage dir
   *
   * @param fileDir file directory
   * @return
   */
  public DownloadBuilderAsync fileDir(String fileDir) {
    this.fileDir = fileDir;
    return this;
  }

  /**
   * set file storage name
   *
   * @param fileName file name
   * @return
   */
  public DownloadBuilderAsync fileName(String fileName) {
    this.fileName = fileName;
    return this;
  }

  /**
   * set file path
   *
   * @param filePath file path
   * @return
   */
  public DownloadBuilderAsync filePath(String filePath) {
    this.filePath = filePath;
    return this;
  }

  /**
   * set tag
   *
   * @param tag tag
   * @return
   */
  public DownloadBuilderAsync tag(Object tag) {
    this.tag = tag;
    return this;
  }

  /**
   * set headers
   *
   * @param headers headers
   * @return
   */
  public DownloadBuilderAsync headers(Map<String, String> headers) {
    this.header = headers;
    return this;
  }

  /**
   * set one header
   *
   * @param key header key
   * @param val header val
   * @return
   */
  public DownloadBuilderAsync addHeader(String key, String val) {
    if (this.header == null) {
      header = new LinkedHashMap<>();
    }
    header.put(key, val);
    return this;
  }

  /**
   * set completed bytes (BreakPoints)
   *
   * @param completeBytes 已经完成的字节数
   * @return
   */
  public DownloadBuilderAsync setCompleteBytes(Long completeBytes) {
    if (completeBytes > 0L) {
      this.completeBytes = completeBytes;
      addHeader("RANGE", "bytes=" + completeBytes + "-");     //添加断点续传header
    }
    return this;
  }

  /**
   * 异步执行
   *
   * @param downloadResponseHandler 下载回调
   */
  public Call enqueue(final DownloadResponseHandler downloadResponseHandler) {
    if (url.length() == 0) {
      throw new IllegalArgumentException("Url can not be null !");
    }

    if (filePath.length() == 0) {
      if (fileDir.length() == 0 || fileName.length() == 0) {
        throw new IllegalArgumentException("FilePath can not be null !");
      } else {
        filePath = fileDir + fileName;
      }
    }
    try {
      checkFilePath(filePath, completeBytes);
    } catch (IOException e) {
      e.printStackTrace();
      logger.error(e.getMessage());
    }

    Request.Builder builder = new Request.Builder().url(url);
    appendHeaders(builder, header);

    if (tag != null) {
      builder.tag(tag);
    }

    Request request = builder.build();

    //设置拦截器
    Call call = okHttpClient.newBuilder()
        .addNetworkInterceptor(chain -> {
          Response originalResponse = chain.proceed(chain.request());
          return originalResponse.newBuilder()
              .body(new ProgressResponseBody(originalResponse.body(), downloadResponseHandler))
              .build();
        })
        .build()
        .newCall(request);
    call.enqueue(new DownloadCallback(downloadResponseHandler, filePath, completeBytes));

    return call;
  }

  //检查filePath有效性
  private void checkFilePath(String filePath, Long completeBytes) throws IOException {
    File file = new File(filePath);
    if (file.exists()) {
      return;
    }

    if (completeBytes > 0L) {       //如果设置了断点续传 则必须文件存在
      throw new IOException("断点续传文件" + filePath + "不存在！");
    }

    if (filePath.endsWith(File.separator)) {
      throw new IOException("创建文件" + filePath + "失败，目标文件不能为目录！");
    }

    //判断目标文件所在的目录是否存在
    if (!file.getParentFile().exists()) {
      if (!file.getParentFile().mkdirs()) {
        throw new IOException("创建目标文件所在目录失败！");
      }
    }
  }

  //append headers into builder
  private void appendHeaders(Request.Builder builder, Map<String, String> headers) {
    Headers.Builder headerBuilder = new Headers.Builder();
    if (headers == null || headers.isEmpty()) {
      return;
    }
    for (String key : headers.keySet()) {
      headerBuilder.add(key, headers.get(key));
    }
    builder.headers(headerBuilder.build());
  }
}
