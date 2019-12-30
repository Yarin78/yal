package yarin.yal.graph;

import org.junit.Assert;
import org.junit.Test;

public class TestMaxFlow {

  @Test
  public void simpleGraph() {
    PushRelabelMaxFlow maxFlow = new PushRelabelMaxFlow(7);
    maxFlow.addEdge(0, 1, 5);
    maxFlow.addEdge(0, 2, 10);
    maxFlow.addEdge(0, 4, 4);
    maxFlow.addEdge(1, 3, 1);
    maxFlow.addEdge(1, 6, 3);
    maxFlow.addEdge(2, 3, 7);
    maxFlow.addEdge(3, 6, 5);
    maxFlow.addEdge(2, 4, 3);
    maxFlow.addEdge(2, 5, 7);
    maxFlow.addEdge(4, 5, 6);
    maxFlow.addEdge(5, 6, 4);

    int flow = maxFlow.maxFlow(0, 6);

    Assert.assertEquals(12, flow);
  }

  @Test
  public void singleEdge() {
    PushRelabelMaxFlow maxFlow = new PushRelabelMaxFlow(2);
    maxFlow.addEdge(0, 1, 3);
    Assert.assertEquals(3, maxFlow.maxFlow(0, 1));
  }

  @Test
  public void singleEdgeReverse() {
    PushRelabelMaxFlow maxFlow = new PushRelabelMaxFlow(2);
    maxFlow.addEdge(0, 1, 3);
    Assert.assertEquals(0, maxFlow.maxFlow(1, 0));
  }

  @Test
  public void multiEdges() {
    PushRelabelMaxFlow maxFlow = new PushRelabelMaxFlow(3);
    maxFlow.addEdge(0, 1, 2);
    maxFlow.addEdge(0, 1, 6);
    maxFlow.addEdge(1, 2, 3);
    maxFlow.addEdge(1, 2, 4);
    Assert.assertEquals(7, maxFlow.maxFlow(0, 2));
  }

  @Test
  public void bidirectionalEdges() {
    PushRelabelMaxFlow maxFlow = new PushRelabelMaxFlow(4);
    maxFlow.addEdge(0, 1, 2);
    maxFlow.addEdge(0, 2, 3);
    maxFlow.addEdge(1, 2, 3);
    maxFlow.addEdge(2, 1, 3);
    maxFlow.addEdge(1, 3, 4);
    maxFlow.addEdge(2, 3, 1);
    int flow = maxFlow.maxFlow(0, 3);
    Assert.assertEquals(5, flow);
    Assert.assertEquals(2, maxFlow.getFlow(2, 1));
    Assert.assertEquals(-2, maxFlow.getFlow(1, 2));
  }
}
