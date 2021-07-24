package top.yang.net.build;

import java.util.LinkedHashMap;
import java.util.Map;
import top.yang.net.HttpUtils;

/**
 * 带有param的base request body Created by tsy on 2016/12/6.
 */

public abstract class OkHttpRequestBuilderHasParam<T extends OkHttpRequestBuilderHasParam> extends OkHttpRequestBuilder<T> {

  protected Map<String, String> params;

  public OkHttpRequestBuilderHasParam(HttpUtils httpUtils) {
    super(httpUtils);
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
