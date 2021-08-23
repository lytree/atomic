package top.yang.net.build;


import okhttp3.OkHttpClient;
import okhttp3.Request;
import top.yang.net.build.base.AsyncOkHttpRequestBuilder;
import top.yang.net.callback.CustomCallback;
import top.yang.net.response.IResponseHandler;

/**
 * delete builder Created by tsy on 2016/12/6.
 */

public class DeleteBuilderAsync extends AsyncOkHttpRequestBuilder<DeleteBuilderAsync> {

  public DeleteBuilderAsync(OkHttpClient okHttpClient) {
    super(okHttpClient);
  }

  @Override
  public void enqueue(final IResponseHandler responseHandler) {

    if (url == null || url.length() == 0) {
      throw new IllegalArgumentException("url can not be null !");
    }

    Request.Builder builder = new Request.Builder().url(url).delete();
    appendHeaders(builder, headers);

    if (tag != null) {
      builder.tag(tag);
    }

    Request request = builder.build();

    okHttpClient
        .newCall(request)
        .enqueue(new CustomCallback(responseHandler));
  }
}
