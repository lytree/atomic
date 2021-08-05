package top.yang.selector;

import java.util.List;
import java.util.stream.Collectors;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.JXNode;


/**
 * XPath selector based on Xsoup.<br>
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.3.0
 */
public class XpathSelector extends BaseElementSelector {

  private final String xpath;

  public XpathSelector(String xpath) {
    this.xpath = xpath;
  }

  @Override
  public JXNode selectElement(Element element) {
    Elements elements = new Elements();
    elements.add(element);
    JXDocument jxDocument = new JXDocument(elements);
    return jxDocument.selNOne(xpath);
  }

  @Override
  public List<JXNode> selectElements(Element element) {
    Elements elements = new Elements();
    elements.add(element);
    JXDocument jxDocument = new JXDocument(elements);
    return jxDocument.selN(xpath);
  }

  @Override
  public boolean hasAttribute() {
    return false;
  }

  @Override
  public String select(Element element) {
    Elements elements = new Elements();
    elements.add(element);
    JXDocument jxDocument = new JXDocument(elements);
    return jxDocument.selNOne(xpath).asString();
  }

  @Override
  public List<String> selectList(Element element) {
    Elements elements = new Elements();
    elements.add(element);
    JXDocument jxDocument = new JXDocument(elements);
    List<JXNode> nodeList = jxDocument.selN(xpath);

    return nodeList.stream().map(JXNode::asString).collect(Collectors.toList());
  }
}
