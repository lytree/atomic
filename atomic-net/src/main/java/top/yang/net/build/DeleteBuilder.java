package top.yang.net.build;


import okhttp3.Request;
import top.yang.net.HttpManager;
import top.yang.net.build.base.OkHttpRequestBuilder;
import top.yang.net.callback.CustomCallback;
import top.yang.net.response.IResponseHandler;

/**
 * delete builder Created by tsy on 2016/12/6.
 */

public class DeleteBuilder extends OkHttpRequestBuilder<DeleteBuilder> {

  public DeleteBuilder(HttpManager httpManager) {
    super(httpManager);
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

    httpManager.getOkHttpClient()
        .newCall(request)
        .enqueue(new CustomCallback(responseHandler));
  }
}

