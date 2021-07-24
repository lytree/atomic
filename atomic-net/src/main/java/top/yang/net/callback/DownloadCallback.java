package top.yang.net.callback;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Objects;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.yang.net.response.DownloadResponseHandler;

public class DownloadCallback implements Callback {

  private static final Logger logger = LoggerFactory.getLogger(DownloadCallback.class);
  private final DownloadResponseHandler downloadResponseHandler;
  private final String filePath;
  private Long completeBytes;


  public DownloadCallback(DownloadResponseHandler downloadResponseHandler, String filePath, Long completeBytes) {
    this.downloadResponseHandler = downloadResponseHandler;
    this.filePath = filePath;
    this.completeBytes = completeBytes;
  }

  @Override
  public void onFailure(Call call, final IOException e) {
    downloadResponseHandler.onFailure(call, e.getMessage());
  }

  @Override
  public void onResponse(Call call, final Response response) throws IOException {
    ResponseBody body = response.body();
    if (response.isSuccessful()) {
      //开始
      if (response.header("Content-Range") == null || response.header("Content-Range").length() == 0) {
        //返回的没有Content-Range 不支持断点下载 需要重新下载
        completeBytes = 0L;
      }
      try {
        saveFile(response, filePath, completeBytes);
      } catch (IOException e) {
        e.printStackTrace();
        logger.error("保存文件失败");
      }
      final File file = new File(filePath);
      if (call.isCanceled()) {
        //判断是主动取消还是别动出错
        downloadResponseHandler.onStart(Objects.requireNonNull(response.body()).contentLength());
      }
    } else {
      logger.error("onResponse fail status=" + response.code());
    }

  }

  //保存文件
  private void saveFile(Response response, String filePath, Long completeBytes) throws IOException {
    InputStream is = null;
    byte[] buf = new byte[4 * 1024];           //每次读取4kb
    int len;
    RandomAccessFile file = null;

    try {
      is = Objects.requireNonNull(response.body()).byteStream();

      file = new RandomAccessFile(filePath, "rwd");
      if (completeBytes > 0L) {
        file.seek(completeBytes);
      }

      long completeLen = 0;
      final long totalLen = Objects.requireNonNull(response.body()).contentLength();
      while ((len = is.read(buf)) != -1) {
        file.write(buf, 0, len);
        completeLen += len;

        //已经下载完成写入文件的进度
        final long finalCompleteLen = completeLen;
        downloadResponseHandler.onProgress(finalCompleteLen, totalLen);
      }
    } finally {
      try {
        if (is != null) {
          is.close();
        }
      } catch (IOException ignored) {
      }
      try {
        if (file != null) {
          file.close();
        }
      } catch (IOException ignored) {
      }
    }
  }
}
