package top.yang;

import java.util.List;
import java.util.Map;
import top.yang.constant.HttpConstant;
import top.yang.selector.Html;
import top.yang.selector.Selectable;

/**
 *
 */
public class Page {

  private Html html;
  private Selectable selectable;
  private Map<String, List<String>> headers;
  private String rawText;
  private String url;
  private int statusCode = HttpConstant.StatusCode.CODE_200;

  private boolean downloadSuccess = true;

  public Page() {
  }

  /**
   * get html content of page
   *
   * @return html
   */
  public Html getHtml() {
    if (html == null) {
      html = new Html(rawText, url);
    }
    return html;
  }
}
