package top.yang.net.body;

import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;
import top.yang.net.response.DownloadResponseHandler;

/**
 * 重写responsebody 设置下载进度监听 Created by tsy on 16/8/16.
 */
public class ProgressResponseBody extends ResponseBody {

  private ResponseBody responseBody;
  private DownloadResponseHandler downloadResponseHandler;
  private BufferedSource bufferedSource;

  public ProgressResponseBody(ResponseBody responseBody, DownloadResponseHandler downloadResponseHandler) {
    this.responseBody = responseBody;
    this.downloadResponseHandler = downloadResponseHandler;
  }

  @Override
  public MediaType contentType() {
    return responseBody.contentType();
  }

  @Override
  public long contentLength() {
    return responseBody.contentLength();
  }

  @Override
  public BufferedSource source() {
    if (bufferedSource == null) {
      bufferedSource = Okio.buffer(source(responseBody.source()));
    }
    return bufferedSource;
  }

  private Source source(Source source) {

    return new ForwardingSource(source) {

      long totalBytesRead;

      @Override
      public long read(Buffer sink, long byteCount) throws IOException {
        //这个的进度应该是读取response每次内容的进度，在写文件进度之前 所以暂时以写完文件的进度为准
        long bytesRead = super.read(sink, byteCount);
        totalBytesRead += ((bytesRead != -1) ? bytesRead : 0);
//                if (mDownloadResponseHandler != null) {
//                    mDownloadResponseHandler.onProgress(totalBytesRead, mResponseBody.contentLength());
//                }
        return bytesRead;
      }
    };
  }
}