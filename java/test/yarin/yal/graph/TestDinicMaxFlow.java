package yarin.yal.graph;

import org.junit.Assert;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Random;
import java.util.Scanner;

public class TestDinicMaxFlow {

  @Test
  public void exampleGraph() {
    DinicMaxFlow g = new DinicMaxFlow(10);
    g.addEdge(0, 1, 30);
    g.addEdge(1, 2, 15);
    g.addEdge(2, 3, 10);
    g.addEdge(3, 4, 15);
    g.addEdge(1, 5, 5);
    g.addEdge(2, 6, 10);
    g.addEdge(6, 4, 7);
    g.addEdge(5, 6, 5);
    g.addEdge(0, 7, 10);
    g.addEdge(7, 5, 5);
    g.addEdge(5, 9, 5);
    g.addEdge(9, 4, 10);
    g.addEdge(7, 8, 5);
    g.addEdge(8, 9, 6);

    int maxFlow = g.maxFlow(0, 4);

    Assert.assertEquals(27, maxFlow);
  }

  @Test
  public void simpleGraph() {
    DinicMaxFlow g = new DinicMaxFlow(7);
    g.addEdge(0, 1, 5);
    g.addEdge(0, 2, 10);
    g.addEdge(0, 4, 4);
    g.addEdge(1, 3, 1);
    g.addEdge(1, 6, 3);
    g.addEdge(2, 3, 7);
    g.addEdge(3, 6, 5);
    g.addEdge(2, 4, 3);
    g.addEdge(2, 5, 7);
    g.addEdge(4, 5, 6);
    g.addEdge(5, 6, 4);

    int flow = g.maxFlow(0, 6);

    Assert.assertEquals(12, flow);
  }

  @Test
  public void singleEdge() {
    DinicMaxFlow g = new DinicMaxFlow(2);
    g.addEdge(0, 1, 3);
    Assert.assertEquals(3, g.maxFlow(0, 1));
  }

  @Test
  public void singleEdgeReverse() {
    DinicMaxFlow g = new DinicMaxFlow(2);
    g.addEdge(0, 1, 3);
    Assert.assertEquals(0, g.maxFlow(1, 0));
  }

  @Test
  public void multiEdges() {
    DinicMaxFlow g = new DinicMaxFlow(3);
    g.addEdge(0, 1, 2);
    g.addEdge(0, 1, 6);
    g.addEdge(1, 2, 3);
    g.addEdge(1, 2, 4);
    Assert.assertEquals(7, g.maxFlow(0, 2));
  }

  @Test
  public void bidirectionalEdges() {
    DinicMaxFlow g = new DinicMaxFlow(4);
    g.addEdge(0, 1, 2);
    g.addEdge(0, 2, 3);
    g.addEdge(1, 2, 3);
    g.addEdge(2, 1, 3);
    g.addEdge(1, 3, 4);
    g.addEdge(2, 3, 1);
    int flow = g.maxFlow(0, 3);
    Assert.assertEquals(5, flow);
    Assert.assertEquals(2, g.getFlow(2, 1));
    Assert.assertEquals(-2, g.getFlow(1, 2));
  }

  @Test
  public void manyBiDirectionalEdges() {
    DinicMaxFlow g = new DinicMaxFlow(10);
    for (int i = 0; i < 9; i++) {
      g.addEdge(i, i+1, 2);
      g.addEdge(i+1, i, 3);
    }
    int flow = g.maxFlow(0, 9);
    Assert.assertEquals(2, flow);
  }

  @Test
  public void testRandom() {
    Random r = new Random(0);

    for (int cases = 0; cases < 1000; cases++) {
      int n = 50, edges = 500;

      DinicMaxFlow g = new DinicMaxFlow(n);
      PushRelabelMaxFlow h = new PushRelabelMaxFlow(n);

      for (int i = 0; i < edges; i++)
      {
        int x = r.nextInt(n);
        int y = r.nextInt(n);
        int cap = 1 + r.nextInt(99);
        if (x != y) {
          g.addEdge(x, y, cap);
          h.addEdge(x, y, cap);
//          if (cases == 163) {
//            System.out.println(x + " " + y + " " + cap);
//          }
        }
      }

      int src = 0, sink = 0;

      while (src == sink) {
        src = r.nextInt(n);
        sink = r.nextInt(n);
      }
//      if (cases == 163) {
//        System.out.println(src + " " + sink);
//      }

      int gflow = g.maxFlow(src, sink), hflow = h.maxFlow(src, sink);
//      if (gflow != hflow) {
//        System.out.println("Case #" + cases + ": " + gflow + " " + hflow);
//      }
      Assert.assertEquals(hflow, gflow);
    }
  }

  @Test
  public void testBug() throws FileNotFoundException {
    InputStream inputStream = TestDinicMaxFlow.class.getResourceAsStream("network1.in");
    InputStream answerStream = TestDinicMaxFlow.class.getResourceAsStream("network1.ans");

    Scanner inputScanner = new Scanner(inputStream);
    Scanner answerScanner = new Scanner(answerStream);

    int casesTested = 0;

    while (true) {
      int n = inputScanner.nextInt(), m = inputScanner.nextInt(), source = inputScanner.nextInt(), sink = inputScanner.nextInt();
      if (n == 0) {
        break;
      }
      int expectedMaxFlow = answerScanner.nextInt();

      DinicMaxFlow cn = new DinicMaxFlow(n);
      for (int i = 0; i < m; i++)
      {
        int src = inputScanner.nextInt(), dest = inputScanner.nextInt(), cap = inputScanner.nextInt();
        if (src >= n || dest >= n) {
          throw new RuntimeException();
        }
        cn.addEdge(src, dest, cap);
      }
      int maxFlow = cn.maxFlow(source, sink);

      Assert.assertEquals(maxFlow, expectedMaxFlow);

      casesTested++;
    }

  }

  @Test
  public void testLarge() throws FileNotFoundException {
    InputStream inputStream = TestCostNetwork.class.getResourceAsStream("mincostmaxflow.in");
    InputStream answerStream = TestCostNetwork.class.getResourceAsStream("mincostmaxflow.ans");

    Scanner inputScanner = new Scanner(inputStream);
    Scanner answerScanner = new Scanner(answerStream);

    int casesTested = 0;

    while (true) {
      int n = inputScanner.nextInt(), m = inputScanner.nextInt(), source = inputScanner.nextInt(), sink = inputScanner.nextInt();
      if (n == 0) {
        break;
      }
      int expectedMaxFlow = answerScanner.nextInt(), expectedMinCost = answerScanner.nextInt();

      DinicMaxFlow cn = new DinicMaxFlow(n);
      for (int i = 0; i < m; i++)
      {
        int src = inputScanner.nextInt(), dest = inputScanner.nextInt(), cap = inputScanner.nextInt(), cost = inputScanner.nextInt();
        if (src >= n || dest >= n) {
          throw new RuntimeException();
        }
        cn.addEdge(src, dest, cap);
      }
      int maxFlow = cn.maxFlow(source, sink);

      Assert.assertEquals(maxFlow, expectedMaxFlow);

      casesTested++;
    }

    Assert.assertTrue(casesTested >= 10);
  }
}
