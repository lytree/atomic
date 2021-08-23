package top.yang.net.build;


import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import top.yang.net.build.base.AsyncOkHttpRequestBuilder;
import top.yang.net.callback.CustomCallback;
import top.yang.net.response.IResponseHandler;

/**
 * patch builder Created by tsy on 16/12/06.
 */
public class PatchBuilderAsync extends AsyncOkHttpRequestBuilder<PatchBuilderAsync> {

  public PatchBuilderAsync(OkHttpClient okHttpClient) {
    super(okHttpClient);
  }

  @Override
  public void enqueue(final IResponseHandler responseHandler) {
    if (url == null || url.length() == 0) {
      throw new IllegalArgumentException("url can not be null !");
    }

    Request.Builder builder = new Request.Builder().url(url);
    appendHeaders(builder, headers);

    if (tag != null) {
      builder.tag(tag);
    }

    builder.patch(RequestBody.create("", MediaType.parse("text/plain;charset=utf-8")));
    Request request = builder.build();

    okHttpClient.
        newCall(request).
        enqueue(new CustomCallback(responseHandler));
  }
}
