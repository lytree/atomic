package top.yang.selector;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JsonPath selector.<br> Used to extract content from JSON.<br>
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.2.1
 */
public class JsonPathSelector implements Selector {

  private static final Logger logger = LoggerFactory.getLogger(JsonPathSelector.class);
  private final ObjectMapper objectMapper = new ObjectMapper();

  private final JsonPath jsonPath;

  public JsonPathSelector(String jsonPathStr) {
    this.jsonPath = JsonPath.compile(jsonPathStr);
  }

  @Override
  public String select(String text) {
    Object object = jsonPath.read(text);
    if (object == null) {
      return null;
    }
    if (object instanceof List) {
      List list = (List) object;
      if (list != null && list.size() > 0) {
        return toString(list.iterator().next());
      }
    }
    return object.toString();
  }

  private String toString(Object object) {
    try {
      return objectMapper.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      logger.error("序列化失败 错误信息{}", e.getMessage());
      e.printStackTrace();
    }
    return "";
  }

  @Override
  public List<String> selectList(String text) {
    List<String> list = new ArrayList<String>();
    Object object = jsonPath.read(text);
    if (object == null) {
      return list;
    }
    if (object instanceof List) {
      List<Object> items = (List<Object>) object;
      for (Object item : items) {
        list.add(toString(item));
      }
    } else {
      list.add(toString(object));
    }
    return list;
  }
}
