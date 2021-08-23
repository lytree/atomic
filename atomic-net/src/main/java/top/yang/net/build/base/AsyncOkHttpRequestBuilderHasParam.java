package top.yang.net.build.base;

import java.util.LinkedHashMap;
import java.util.Map;
import okhttp3.OkHttpClient;

/**
 * 带有param的base request body Created by tsy on 2016/12/6.
 */

public abstract class AsyncOkHttpRequestBuilderHasParam<T extends AsyncOkHttpRequestBuilderHasParam> extends AsyncOkHttpRequestBuilder<T> {

  protected Map<String, String> params;

  public AsyncOkHttpRequestBuilderHasParam(OkHttpClient okHttpClient) {
    super(okHttpClient);
  }

  /**
   * set Map params
   *
   * @param params
   * @return
   */
  public T params(Map<String, String> params) {
    this.params = params;
    return (T) this;
  }

  /**
   * add param
   *
   * @param key param key
   * @param val param val
   * @return
   */
  public T addParam(String key, String val) {
    if (this.params == null) {
      params = new LinkedHashMap<>();
    }
    params.put(key, val);
    return (T) this;
  }
}
