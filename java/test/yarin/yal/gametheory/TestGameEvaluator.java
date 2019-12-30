package yarin.yal.gametheory;

import org.junit.Test;

import yarin.yal.graph.DirectedGraph;

public class TestGameEvaluator {

  @Test
  public void simpleGameInGraph() {
    // Two players move in a DAG. First player to move on top of the other player wins.
    DirectedGraph g = new DirectedGraph(6);
    int[][] edges = {{0, 1}, {0, 4}, {1, 4}, {0, 3}, {3, 2}, {1, 2}, {3, 5}, {5, 2}};
    for (int[] edge : edges) {
      g.addEdge(edge[0], edge[1]);
      g.addEdge(edge[1], edge[0]);
    }

    DirectedGraph h = generateGameGraph(g);
    GameEvaluator ge = new GameEvaluator(h);
    for (int i = 0; i < g.getNodeCount() ; i++) {
      int t = getGameNode(g, i, i);
      ge.setResult(t, Result.Lose);
    }

    for (int i = 0; i < g.getNodeCount() ; i++) {
      for (int j = 0; j < g.getNodeCount(); j++) {
        int t = getGameNode(g, i, j);
        System.out.println(i + " " + j + ": " + ge.evaluate(t));
      }
    }
  }

  private int[] getPlayerPositions(DirectedGraph g, int node) {
    return new int[] { node % g.getNodeCount(), node / g.getNodeCount() };
  }

  private int getGameNode(DirectedGraph g, int... pos) {
    return pos[0] + pos[1] * g.getNodeCount();
  }

  private DirectedGraph generateGameGraph(DirectedGraph g) {
    DirectedGraph h = new DirectedGraph(g.getNodeCount() * g.getNodeCount());
    for (int i = 0; i < h.getNodeCount(); i++) {
      int[] pos = getPlayerPositions(g, i);
      for (DirectedGraph.Edge edge : g.getNode(pos[0]).getOutgoingEdges()) {
        int[] npos = new int[]{pos[1], edge.getDest().getIndex()};
        h.addEdge(i, getGameNode(g, npos));
      }
    }
    return h;
  }


}
