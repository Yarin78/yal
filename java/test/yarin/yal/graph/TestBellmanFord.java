package yarin.yal.graph;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class TestBellmanFord {

  @Test
  public void simpleTest() {
    DirectedGraph dg = new DirectedGraph(5);
    dg.addEdge(0, 1, 5);
    dg.addEdge(1, 2, 3);
    dg.addEdge(2, 3, 5);
    dg.addEdge(3, 4, -3);
    int[] dist = new BellmanFord().shortestPaths(dg, dg.getNode(0));
    Assert.assertEquals(10, dist[4]);

    dg.addEdge(0, 2, -1);
    dist = new BellmanFord().shortestPaths(dg, dg.getNode(0));
    Assert.assertEquals(1, dist[4]);

    dg.addEdge(2, 1, -4);
    dist = new BellmanFord().shortestPaths(dg, dg.getNode(0));
    Assert.assertEquals(Integer.MIN_VALUE, dist[4]);

    System.out.println(Arrays.toString(dist));
  }

  @Test
  public void negativeCycle() {
    DirectedGraph dg = new DirectedGraph(4);
    dg.addEdge(0, 1, 5);
    dg.addEdge(1, 2, 3);
    dg.addEdge(2, 1, -4);
    dg.addEdge(0, 3, 9);

    int[] dist = new BellmanFord().shortestPaths(dg, dg.getNode(0));
    Assert.assertEquals(9, dist[3]);
    Assert.assertEquals(Integer.MIN_VALUE, dist[1]);
    Assert.assertEquals(Integer.MIN_VALUE, dist[2]);
    Assert.assertEquals(0, dist[0]);
  }

}
