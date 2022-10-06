package top.lytree.tree.parser;

import java.util.Map;
import top.lytree.collections.MapUtils;
import top.lytree.tree.Tree;
import top.lytree.tree.TreeNode;

/**
 * 默认的简单转换器
 *
 * @param <T> ID类型
 * @author liangbaikai
 */
public class DefaultNodeParser<T> implements NodeParser<TreeNode<T>, T> {

  @Override
  public void parse(TreeNode<T> treeNode, Tree<T> tree) {
    tree.setId(treeNode.getId());
    tree.setParentId(treeNode.getParentId());
    tree.setWeight(treeNode.getWeight());
    tree.setName(treeNode.getName());

    //扩展字段
    final Map<String, Object> extra = treeNode.getExtra();
    if (MapUtils.isNotEmpty(extra)) {
      extra.forEach(tree::putExtra);
    }
  }
}
