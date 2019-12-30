package yarin.viz;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import yarin.yal.geometry.Point;
import yarin.yal.graph.Graph;

public class BinaryTreeLayoutAlgorithm extends TreeLayoutAlgorithm {

  private final int MARGIN = GraphVisualizer.NODE_RADIUS;
  private Graph.Node root;

  public BinaryTreeLayoutAlgorithm() { }

  public BinaryTreeLayoutAlgorithm(Graph.Node root) {
    this.root = root;
  }

  @Override
  public Map<Graph.Node, Point> doLayout(Graph g, int width, int height) {
    HashMap<Graph.Node, Point> map = new HashMap<>();

    Graph.Node root = this.root == null ? g.getNode(0) : this.root;
    embed(map, root, null, 0, height - MARGIN, width, (height - 2 * MARGIN) / (getDepth(root, null) - 1));

    return map;
  }

  private void embed(HashMap<Graph.Node, Point> map,
                     Graph.Node node, Graph.Node parent, int left,
                     int top, int width, int deltaHeight) {
    Point p = new Point(left + width / 2, top);
//    System.out.println("Putting node " + node.getIndex() + " at " + p);
    map.put(node, p);

    List<Graph.Node> children = getChildren(node, parent);

    for (Graph.Node child : children) {
      if (child.getIndex() < node.getIndex()) {
        embed(map, child, node, left, top - deltaHeight, width / 2, deltaHeight);
      } else {
        embed(map, child, node, left + width / 2, top - deltaHeight, width / 2, deltaHeight);
      }
    }
  }
}
