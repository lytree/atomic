package top.yang.net.build;


import java.util.Map;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import top.yang.net.HttpUtils;
import top.yang.net.callback.CustomCallback;
import top.yang.net.response.IResponseHandler;

/**
 * post builder Created by tsy on 16/9/18.
 */
public class PostBuilder extends OkHttpRequestBuilderHasParam<PostBuilder> {

  private String jsonParams = "";

  public PostBuilder(HttpUtils httpUtils) {
    super(httpUtils);
  }

  /**
   * json格式参数
   *
   * @param json
   * @return
   */
  public PostBuilder jsonParams(String json) {
    this.jsonParams = json;
    return this;
  }

  @Override
  public void enqueue(IResponseHandler responseHandler) {
    if (url == null || url.length() == 0) {
      throw new IllegalArgumentException("url can not be null !");
    }

    Request.Builder builder = new Request.Builder().url(url);
    appendHeaders(builder, headers);

    if (tag != null) {
      builder.tag(tag);
    }

    if (jsonParams.length() > 0) {
      //上传json格式参数
      RequestBody body = RequestBody.create(jsonParams, MediaType.parse("application/json; charset=utf-8"));
      builder.post(body);
    } else {        //普通kv参数
      FormBody.Builder encodingBuilder = new FormBody.Builder();
      appendParams(encodingBuilder, params);
      builder.post(encodingBuilder.build());
    }

    Request request = builder.build();

    httpUtils.create().
        newCall(request).
        enqueue(new CustomCallback(responseHandler));
  }

  //append params to form builder
  private void appendParams(FormBody.Builder builder, Map<String, String> params) {

    if (params != null && !params.isEmpty()) {
      for (String key : params.keySet()) {
        builder.add(key, params.get(key));
      }
    }
  }
}
