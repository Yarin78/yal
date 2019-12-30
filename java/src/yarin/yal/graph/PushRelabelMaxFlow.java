package yarin.yal.graph;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Calculates maximum f in a directed graph
 * using the push-relabel algorithm with FIFO ordering
 * http://en.wikipedia.org/wiki/Push%E2%80%93relabel_maximum_flow_algorithm
 * http://community.topcoder.com/tc?module=Static&d1=tutorials&d2=maxflowPushRelabel
 */
public class PushRelabelMaxFlow {
  private int c[][], f[][], cf[][];
  private int n;
  private int h[], e[], noEdges[];
  private int edges[][];

  public PushRelabelMaxFlow(int nodes) {
    n = nodes;
    c = new int[n][n];
    f = new int[n][n];
    cf = new int[n][n];
    h = new int[n];
    e = new int[n];
  }

  public void addEdge(int u, int v, int capacity) {
    c[u][v] += capacity;
    edges = null;
  }

  public int getFlow(int u, int v) {
    return f[u][v];
  }

  private void push(int u, int v) {
    int delta = Math.min(e[u], cf[u][v]);
    f[u][v] += delta;
    f[v][u] = -f[u][v];
    e[u] -= delta;
    e[v] += delta;
    cf[u][v] = c[u][v] - f[u][v];
    cf[v][u] = c[v][u] - f[v][u];
  }

  public int maxFlow(int source, int sink) {
    if (edges == null) {
      setupEdges();
    }

    Queue<Integer> q = new LinkedList<>();
    boolean active[] = new boolean[n];

    h[source] = n;
    for (int i = 0; i < noEdges[source]; i++) {
      int v = edges[source][i];
      f[source][v] = c[source][v];
      f[v][source] = -c[source][v];
      e[v] = c[source][v];
      e[source] -= c[source][v];

      if (v != sink) {
        q.add(v);
        active[v] = true;
      }
    }
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        cf[i][j] = c[i][j] - f[i][j];
      }
    }

    while (q.size() > 0) {
      int u = q.peek(), min = -1;
//      System.out.println("Discharge " + u);
      // Discharge u
      for (int i = 0; i < noEdges[u] && e[u] > 0; i++) {
        int v = edges[u][i];
        if (cf[u][v] > 0) {
          if (h[u] > h[v]) {
//            System.out.println("Push " + u + " to " + v);
            push(u, v);
            if (!active[v] && v != source && v != sink) {
              active[v] = true;
              q.add(v);
            }
          } else if (min < 0 || h[v] < min) {
            min = h[v];
          }
        }
      }
      if (e[u] != 0) {
        // Relabel
        if (min < 0) throw new RuntimeException();
        h[u] = 1 + min;
//        System.out.println("Relabel " + u + " to " + h[u]);
      } else {
        active[u] = false;
        q.poll();
      }
    }

    return e[sink];
  }

  private void setupEdges() {
    edges = new int[n][n];
    noEdges = new int[n];
    for (int i = 0; i < n; i++) {
      int k = 0;
      for (int j = 0; j < n; j++) {
        if (c[i][j] != 0 || c[j][i] != 0)
          edges[i][k++] = j;
      }
      noEdges[i] = k;
    }
  }

  public void show() {
    for (int i = 0; i < n; i++) {
      System.out.println("Node " + i + ": h = " + h[i] + ", e = " + e[i]);
      for (int j = 0; j < n; j++) {
        if (c[i][j] > 0) {
          System.out.println("    -> node " + j + ": c = " + c[i][j] + ", f = " + f[i][j]);
        }
      }
    }
    System.out.println();
  }
}
