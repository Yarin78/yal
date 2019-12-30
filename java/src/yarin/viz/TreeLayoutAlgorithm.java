package yarin.viz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import yarin.yal.graph.Graph;

public abstract class TreeLayoutAlgorithm implements ILayoutAlgorithm {

  private Map<Graph.Node, Integer> widthMemo;

  public TreeLayoutAlgorithm() {
    widthMemo = new HashMap<>();
  }

  protected int getDepth(Graph.Node node, Graph.Node parent) {
    int max = 0;
    for (Graph.Node other : getChildren(node, parent)) {
      max = Math.max(max, getDepth(other, node));
    }
    return max + 1;
  }

  protected int getWidth(Graph.Node node, Graph.Node parent) {
    if (widthMemo.containsKey(node)) return widthMemo.get(node);
    int sum = 0;
    for (Graph.Node other : getChildren(node, parent)) {
      sum += getWidth(other, node);
    }
    int res = Math.max(sum, 1);
    widthMemo.put(node, res);
    return res;
  }

  protected List<Graph.Node> getChildren(Graph.Node node, Graph.Node parent) {
    ArrayList<Graph.Node> children = new ArrayList<>();
    for (Graph.Edge edge : node.getAdjacentEdges()) {
      Graph.Node other = edge.getA() == node ? edge.getB() : edge.getA();
      if (other != parent)
        children.add(other);
    }
    return children;
  }

}
