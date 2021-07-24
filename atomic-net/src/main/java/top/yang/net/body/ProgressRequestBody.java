package top.yang.net.body;

import java.io.IOException;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;
import org.jetbrains.annotations.NotNull;
import top.yang.net.response.IResponseHandler;

/**
 * 重写request body 设置上传进度监听 Created by tsy on 16/8/16.
 */
public class ProgressRequestBody extends RequestBody {

  private final IResponseHandler responseHandler;      //回调监听
  private final RequestBody requestBody;

  public ProgressRequestBody(RequestBody requestBody, IResponseHandler responseHandler) {
    this.responseHandler = responseHandler;
    this.requestBody = requestBody;
  }

  @Override
  public MediaType contentType() {
    return requestBody.contentType();
  }

  @Override
  public long contentLength() throws IOException {
    return requestBody.contentLength();
  }


  @Override
  public void writeTo(@NotNull BufferedSink sink) throws IOException {
    CountingSink countingSink = new CountingSink(sink);
    BufferedSink bufferedSink = Okio.buffer(countingSink);
    requestBody.writeTo(bufferedSink);
    bufferedSink.flush();
  }

  protected final class CountingSink extends ForwardingSink {

    private long bytesWritten = 0;
    private long contentLength = 0L;

    public CountingSink(Sink delegate) {
      super(delegate);
    }

    @Override
    public void write(Buffer source, long byteCount) throws IOException {
      super.write(source, byteCount);
      if (contentLength == 0) {
        //获得contentLength的值，后续不再调用
        contentLength = contentLength();
      }
      bytesWritten += byteCount;

      responseHandler.onProgress(bytesWritten, contentLength);
    }
  }
}
