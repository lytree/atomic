package top.yang.selector;

import java.util.List;
import java.util.stream.Collectors;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.JXNode;

public class XpathSelector extends BaseElementSelector {

  private final String xpathStr;

  public XpathSelector(String xpathStr) {
    this.xpathStr = xpathStr;
  }

  @Override
  public Element selectElement(Element element) {
    Elements elements = new Elements(element);
    JXDocument jxDocument = new JXDocument(elements);
    return jxDocument.selNOne(this.xpathStr).asElement();
  }

  @Override
  public List<Element> selectElements(Element element) {
    Elements elements = new Elements(element);
    JXDocument jxDocument = new JXDocument(elements);
    List<JXNode> jxNodes = jxDocument.selN(this.xpathStr);
    return jxNodes.stream().map(JXNode::asElement).collect(Collectors.toList());
  }

  @Override
  public boolean hasAttribute() {
    return true;
  }

  @Override
  public String select(Element element) {
    Elements elements = new Elements(element);
    JXDocument jxDocument = new JXDocument(elements);
    return jxDocument.selNOne(this.xpathStr).asString();
  }

  @Override
  public List<String> selectList(Element element) {
    Elements elements = new Elements(element);
    JXDocument jxDocument = new JXDocument(elements);
    List<JXNode> jxNodes = jxDocument.selN(this.xpathStr);
    return jxNodes.stream().map(JXNode::asString).collect(Collectors.toList());
  }
}
