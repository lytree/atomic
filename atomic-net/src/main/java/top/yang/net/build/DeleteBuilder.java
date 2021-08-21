package top.yang.net.build;

import java.io.IOException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import top.yang.net.build.base.OkHttpRequestBuilder;

public class DeleteBuilder extends OkHttpRequestBuilder<DeleteBuilder> {

  public DeleteBuilder(OkHttpClient okHttpClient) {
    super(okHttpClient);
  }

  @Override
  public okhttp3.Response execute() {

    if (url == null || url.length() == 0) {
      throw new IllegalArgumentException("url can not be null !");
    }

    Request.Builder builder = new Request.Builder().url(url).delete();
    appendHeaders(builder, headers);

    if (tag != null) {
      builder.tag(tag);
    }

    Request request = builder.build();

    try {
      return okHttpClient
          .newCall(request)
          .execute();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
}