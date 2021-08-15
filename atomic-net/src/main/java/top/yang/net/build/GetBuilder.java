package top.yang.net.build;

import java.util.Map;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import top.yang.net.build.base.OkHttpRequestBuilderHasParam;
import top.yang.net.callback.CustomCallback;
import top.yang.net.response.IResponseHandler;

/**
 * Get Builder Created by tsy on 16/9/18.
 */
public class GetBuilder extends OkHttpRequestBuilderHasParam<GetBuilder> {

  public GetBuilder(OkHttpClient okHttpClient) {
    super(okHttpClient);
  }

  @Override
  public void enqueue(final IResponseHandler responseHandler) {

    if (url == null || url.length() == 0) {
      throw new IllegalArgumentException("url can not be null !");
    }

    if (params != null && params.size() > 0) {
      url = appendParams(url, params);
    }

    Request.Builder builder = new Request.Builder().url(url).get();
    appendHeaders(builder, headers);

    if (tag != null) {
      builder.tag(tag);
    }

    Request request = builder.build();

    okHttpClient.
        newCall(request).
        enqueue(new CustomCallback(responseHandler));
  }

  //append params to url
  private String appendParams(String url, Map<String, String> params) {
    StringBuilder sb = new StringBuilder();
    sb.append(url + "?");
    if (params != null && !params.isEmpty()) {
      for (String key : params.keySet()) {
        sb.append(key).append("=").append(params.get(key)).append("&");
      }
    }

    sb = sb.deleteCharAt(sb.length() - 1);
    return sb.toString();
  }
}
