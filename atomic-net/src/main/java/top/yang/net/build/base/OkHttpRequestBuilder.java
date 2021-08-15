package top.yang.net.build.base;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import top.yang.net.response.IResponseHandler;

/**
 * 不带param的base request body Created by tsy on 16/9/14.
 */
public abstract class OkHttpRequestBuilder<T extends OkHttpRequestBuilder> {

  protected String url;
  protected Object tag;
  protected Map<String, String> headers = new HashMap<String, String>() {
    {
      put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
      put("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6");
      put("Cache-Control", "no-cache");
      put("Connection", "keep-alive");
      put("Accept-Encoding", "*");
      put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4456.0 Safari/537.36 Edg/91.0.845.2");
    }
  };

  protected OkHttpClient okHttpClient;

  /**
   * 异步执行
   *
   * @param responseHandler 自定义回调
   */
  protected abstract void enqueue(final IResponseHandler responseHandler);

  public OkHttpRequestBuilder(OkHttpClient okHttpClient) {
    this.okHttpClient = okHttpClient;
  }

  /**
   * set url
   *
   * @param url url
   * @return
   */
  public T url(String url) {
    this.url = url;
    return (T) this;
  }

  /**
   * set tag
   *
   * @param tag tag
   * @return
   */
  public T tag(Object tag) {
    this.tag = tag;
    return (T) this;
  }

  /**
   * set headers
   *
   * @param headers headers
   * @return
   */
  public T headers(Map<String, String> headers) {
    this.headers.putAll(headers);
    return (T) this;
  }

  /**
   * set one header
   *
   * @param key header key
   * @param val header val
   * @return
   */
  public T addHeader(String key, String val) {
    if (this.headers == null) {
      headers = new LinkedHashMap<>();
    }
    headers.put(key, val);
    return (T) this;
  }

  //append headers into builder
  protected void appendHeaders(Request.Builder builder, Map<String, String> headers) {
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
