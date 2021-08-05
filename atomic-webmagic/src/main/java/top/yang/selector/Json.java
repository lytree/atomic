package top.yang.selector;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * parse json
 *
 * @author code4crafter@gmail.com
 * @since 0.5.0
 */
public class Json extends PlainText {

  private final static Logger logger = LoggerFactory.getLogger(Json.class);
  private final ObjectMapper objectMapper = new ObjectMapper();

  public Json(List<String> strings) {
    super(strings);
  }

  public Json(String text) {
    super(text);
  }

//    /**
//     * remove padding for JSONP
//     * @param padding padding
//     * @return json after padding removed
//     */
//    public Json removePadding(String padding) {
//        String text = getFirstSourceText();
//        XTokenQueue tokenQueue = new XTokenQueue(text);
//        tokenQueue.consumeWhitespace();
//        tokenQueue.consume(padding);
//        tokenQueue.consumeWhitespace();
//        String chompBalanced = tokenQueue.chompBalancedNotInQuotes('(', ')');
//        return new Json(chompBalanced);
//    }

  public <T> T toObject(Class<T> clazz) {
    if (getFirstSourceText() == null) {
      return null;
    }
    try {
      return objectMapper.readValue(getFirstSourceText(), clazz);
    } catch (JsonProcessingException e) {
      logger.error("{} 反序列化失败 错误信息：{}", clazz, e.getMessage());
      e.printStackTrace();
    }
    return null;
  }

  public <T> List toList(Class<T> clazz) {
    if (getFirstSourceText() == null) {
      return null;
    }
    try {
      return objectMapper.readValue(getFirstSourceText(), List.class);
    } catch (JsonProcessingException e) {
      logger.error("{} 反序列化失败 错误信息：{}", clazz, e.getMessage());
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public Selectable jsonPath(String jsonPath) {
    JsonPathSelector jsonPathSelector = new JsonPathSelector(jsonPath);
    return selectList(jsonPathSelector, getSourceTexts());
  }
}
