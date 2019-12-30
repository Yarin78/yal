package yarin.viz;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import yarin.yal.geometry.Point;
import yarin.yal.graph.Graph;

public class LayeredLayoutAlgorithm extends TreeLayoutAlgorithm {

  private final int MARGIN = GraphVisualizer.NODE_RADIUS;
  private Graph.Node root;

  public LayeredLayoutAlgorithm() { }

  public LayeredLayoutAlgorithm(Graph.Node root) {
    this.root = root;
  }

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

    int totWidth = getWidth(node, parent);
    int curLeft = left + MARGIN;
    for (Graph.Node child : children) {
      int childWidth = (width - MARGIN * (children.size() + 1)) * getWidth(child, node) / totWidth;
      embed(map, child, node, curLeft, top - deltaHeight, childWidth, deltaHeight);
      curLeft += childWidth + MARGIN;
    }
  }


}
