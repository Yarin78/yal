package yarin.yal.gametheory;

import java.util.LinkedList;
import java.util.Queue;

import yarin.yal.graph.DirectedGraph;

/**
 * Evaluator for two player games that can be described as a directed graph
 * where each edge is a move.
 * Assumes the player to move alternates after each move.
 * A state (node) is a Win if the player to move has a winning strategy.
 * A state (node) is a Lose if the player to move can't avoid losing.
 */
public class GameEvaluator {

  private DirectedGraph graph;
  private Result[] evaluation;
  private int[] movesLeft;
  private boolean[] visited; // Has a result assigned or is in the processQueue
  private Queue<DirectedGraph.Node> processQueue;

  public GameEvaluator(DirectedGraph graph) {
    this(graph, Result.Lose);
  }

  public GameEvaluator(DirectedGraph graph, Result noMovesResult) {
    this.graph = graph;
    this.evaluation = new Result[graph.getNodeCount()];
    this.movesLeft = new int[graph.getNodeCount()];
    this.processQueue = new LinkedList<>();
    this.visited = new boolean[graph.getNodeCount()];
    for (DirectedGraph.Node node : graph.getNodes()) {
      this.movesLeft[node.getIndex()] = node.getOutgoingEdges().size();
      if (node.getOutgoingEdges().size() == 0) {
        setResult(node.getIndex(), noMovesResult);
      }
    }
  }

  public void setResult(int nodeIndex, Result result) {
    Result oldRes = evaluation[nodeIndex];
    if (oldRes != null && oldRes != result) throw new IllegalStateException();

    evaluation[nodeIndex] = result;
    visited[nodeIndex] = true;

    switch (result) {
      case Lose:
        for (DirectedGraph.Edge edge : graph.getNode(nodeIndex).getIncomingEdges()) {
          int ix = edge.getSrc().getIndex();
          if (evaluation[ix] == null && !visited[ix]) {
            visited[ix] = true;
            processQueue.add(edge.getSrc());
          }
        }
        break;
      case Win:
      case Draw:
        for (DirectedGraph.Edge edge : graph.getNode(nodeIndex).getIncomingEdges()) {
          int ix = edge.getSrc().getIndex();
          if (--movesLeft[ix] == 0) {
            if (!visited[ix]) {
              visited[ix] = true;
              processQueue.add(edge.getSrc());
            }
          }
        }
        break;
    }
  }

  public Result evaluate(int nodeIndex) {
    while (processQueue.size() > 0 && evaluation[nodeIndex] == null) {
      DirectedGraph.Node next = processQueue.poll();
      int cur = next.getIndex();
      if (evaluation[cur] != null) throw new RuntimeException("Node already evaluated");
      boolean hasDraw = false, hasUnknown = false;
      for (DirectedGraph.Edge edge : next.getOutgoingEdges()) {
        Result r = evaluation[edge.getDest().getIndex()];
        if (r == null) {
          hasUnknown = true;
        } else if (r == Result.Lose) {
          setResult(cur, Result.Win);
          break;
        } else if (r == Result.Draw) {
          hasDraw = true;
        }
      }
      if (evaluation[cur] == null) {
        if (hasUnknown) throw new RuntimeException("Couldn't evaluate node");
        setResult(cur, hasDraw ? Result.Draw : Result.Lose);
      }
    }

    // Game completely evaluated. All null results are draws.
    if (evaluation[nodeIndex] == null) {
      setResult(nodeIndex, Result.Draw);
    }
    return evaluation[nodeIndex];
  }
}
