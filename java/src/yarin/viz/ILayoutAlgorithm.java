package yarin.viz;

import java.util.Map;

import yarin.yal.geometry.Point;
import yarin.yal.graph.Graph;

public interface ILayoutAlgorithm {
  public Map<Graph.Node, Point> doLayout(Graph g, int width, int height);
}
