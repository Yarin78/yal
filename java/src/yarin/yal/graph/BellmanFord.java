package yarin.yal.graph;

public class BellmanFord {

  /**
   * Finds the shortest path from the source to the other nodes
   * Integer.MIN_VALUE is used to denote an infinitely negative value (negative cycles)
   * Integer.MAX_VALUE is used to denote nodes that can't be reached
   */
  public int[] shortestPaths(DirectedGraph dg, DirectedGraph.Node source) {
    int[] dist = new int[dg.getNodeCount()];

    for (int i = 0; i < dg.getNodeCount(); i++) {
      dist[i] = Integer.MAX_VALUE;
    }
    dist[source.getIndex()] = 0;

    boolean updated = false;
    for (int i = 0; i < dg.getNodeCount() || updated; i++) {
      updated = false;
      boolean cycles = i + 1 >= dg.getNodeCount();
      for (DirectedGraph.Edge edge : dg.getEdges()) {
        int srcDist = dist[edge.getSrc().getIndex()];
        if (srcDist == Integer.MAX_VALUE) continue;
        int weight = (edge instanceof DirectedGraph.WeightedEdge) ?
                     ((DirectedGraph.WeightedEdge) edge).getWeight() : 1;
        int targetDist = dist[edge.getDest().getIndex()];

        if (srcDist == Integer.MIN_VALUE && targetDist > Integer.MIN_VALUE) {
          dist[edge.getDest().getIndex()] = Integer.MIN_VALUE;
          updated = true;
        } else if (srcDist > Integer.MIN_VALUE && srcDist + weight < targetDist) {
          dist[edge.getDest().getIndex()] = cycles ? Integer.MIN_VALUE : srcDist + weight;
          updated = true;
        }
      }
    }

    return dist;
  }
}
