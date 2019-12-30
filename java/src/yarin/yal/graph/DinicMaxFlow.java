package yarin.yal.graph;

/**
 * Maximum flow using Dinics algorithm
 * Very fast in practice!
 * Tested on https://open.kattis.com/problems/maxflow
 *
 * http://en.wikipedia.org/wiki/Dinic%27s_algorithm
 * http://www.slideshare.net/KuoE0/acmicpc-dinics-algorithm
 */
public class DinicMaxFlow {

  private int n;
  private int c[][], f[][];
  private int lvl[], noEdges[];
  private int edges[][];

  public DinicMaxFlow(int nodes) {
    n = nodes;
    c = new int[n][n];
    f = new int[n][n];
    lvl = new int[n];
  }

  public void addEdge(int u, int v, int capacity) {
    c[u][v] += capacity;
    edges = null;
  }

  public int getFlow(int u, int v) {
    return f[u][v];
  }

  private int cf(int u, int v) {
    if (c[u][v] > 0) {
      return c[u][v] - f[u][v];
    }
    return f[v][u];
  }

  private boolean buildLevelGraph(int s, int t) {
    for (int i = 0; i < n; i++) {
      lvl[i] = 0;
    }
    lvl[s] = 1;
    int head = 0, tail = 0;
    int[] queue = new int[n];
    queue[head++] = s;
    while (tail < head) {
      int u = queue[tail++];
      for (int i = 0; i < noEdges[u]; i++) {
        int v = edges[u][i];
        if (cf(u, v) > 0 && lvl[v] == 0) {
          lvl[v] = lvl[u] + 1;
          queue[head++] = v;
        }
      }
    }
    return lvl[t] != 0;
  }

  private int constructBlockingFlow(int s, int t) {
    int top = 0, res = 0;
    int[] stk = new int[n];
    int[] path = new int[n];
    boolean[] vis = new boolean[n];
    stk[top++] = s;
    while (top > 0) {
      int now = stk[top - 1];
      if (now != t) {
        for (int i = 0; i < noEdges[now]; i++) {
          int v = edges[now][i];
          if (vis[v] || lvl[v] != lvl[now] + 1) continue;
          if (cf(now, v) > 0) {
            path[v] = now;
            stk[top++] = v;
            break;
          }
        }
        if (stk[top - 1] == now) {
          top--;
          vis[now] = true;
        }
      } else {
        int flow = Integer.MAX_VALUE, bottleneck = -1;
        for (int cur = t; cur != s; cur = path[cur])
          flow = Math.min(flow, cf(path[cur], cur));

//        System.out.println("Found augmenting path with flow " + flow + ": ");
//        for (int i = 0; i < top; i++) System.out.print(stk[i] + " ");
//        System.out.println();

        for (int cur = t; cur != s; cur = path[cur]) {
          int u = path[cur];
          f[u][cur] += flow;
          f[cur][u] -= flow;
          if (cf(u, cur) == 0) bottleneck = u;
        }
        if (bottleneck < 0) throw new RuntimeException();
//        System.out.println("Bottleneck at " + bottleneck);
        while (top > 0 && stk[top-1] != bottleneck) top--;
        res += flow;
      }
    }

    return res;
  }

  public int maxFlow(int s, int t) {
    if (edges == null) {
      setupEdges();
    }

    int res = 0;
    while (buildLevelGraph(s, t)) {
      res += constructBlockingFlow(s, t);
    }
    return res;
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

}
