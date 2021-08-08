package top.yang;

import java.util.LinkedHashMap;
import java.util.Map;

public class ResultItems {

  private final Map<String, Object> fields = new LinkedHashMap<String, Object>();

  private boolean skip;

  @SuppressWarnings("unchecked")
  public <T> T get(String key) {
    Object o = fields.get(key);
    if (o == null) {
      return null;
    }
    return (T) fields.get(key);
  }

  public Map<String, Object> getAll() {
    return fields;
  }

  public <T> ResultItems put(String key, T value) {
    fields.put(key, value);
    return this;
  }

}
