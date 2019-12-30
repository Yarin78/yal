package yarin.yal.graph;

import java.util.ArrayList;
import java.util.List;

// Test at Kattis, https://open.kattis.com/problems/mincostmaxflow
public class CostNetwork {
    private class Edge {
        private final int source, dest, capacity, cost;
        private Edge other;
        private int flow;

        public Edge(int from, int to, int cost, int capacity) {
            this.source = from;
            this.dest = to;
            this.capacity = capacity;
            this.cost = cost;
        }
    }

    private int noNodes = 0;
    private final List<Edge> edges = new ArrayList<Edge>();

    public void addEdge(int source, int dest, int capacity, int cost) {
        if (source + 1 > noNodes) {
            noNodes = source + 1;
        }
        if (dest + 1 > noNodes) {
            noNodes = dest + 1;
        }
        Edge e1 = new Edge(source, dest, cost, capacity);
        Edge e2 = new Edge(dest, source, -cost, 0);
        e1.other = e2;
        e2.other = e1;
        edges.add(e1);
        edges.add(e2);
    }

    private int bellmanFord(int source, Edge[] p) {
        int[] d = new int[noNodes];
        for (int i = 0; i < noNodes; i++) {
            d[i] = Integer.MAX_VALUE;
        }
        d[source] = 0;
        boolean change = true;
        for (int m = 0; change && m < noNodes - 1; m++) {
            change = false;
            for (Edge e : edges) {
                if (e.capacity > e.flow && d[e.source] != Integer.MAX_VALUE && d[e.dest] > d[e.source] + e.cost) {
                    change = true;
                    d[e.dest] = d[e.source] + e.cost;
                    p[e.dest] = e;
                }
            }
        }
        for(Edge e : edges) {
            if (e.capacity > e.flow && d[e.source] != Integer.MAX_VALUE && d[e.dest] > d[e.source] + e.cost) {
                p[e.dest] = e;
                return e.dest;
            }
        }
        return -1;
    }

    private static void incrementFlow(int source, int sink, Edge[] p) {
        int minCapacity = Integer.MAX_VALUE;
        for (int u = sink; u != source; u = p[u].source) {
            minCapacity = Math.min(minCapacity, p[u].capacity - p[u].flow);
        }

//			string s = sink.ToString();
        for (int u = sink; u != source; u = p[u].source) {
//				s = p[u].Source + " -> " + s;
            p[u].flow += minCapacity;
            p[u].other.flow -= minCapacity;
        }
//			s += ": Increase f with " + minCapacity;
//			Console.WriteLine(s);
    }

    public long[] maxFlowMinCost(int source, int sink) {
        while (true) {
            Edge[] p = new Edge[noNodes];;
            int u = bellmanFord(source, p);
            if (u == -1) {
                if (p[sink] == null) {
                    break;
                }
                incrementFlow(source, sink, p);
            }
            else {
                throw new RuntimeException(); // Shouldn't happen
            }
        }

        long flow = 0, cost = 0;
        for (Edge edge : edges) {
            if (edge.source == source) {
                flow += edge.flow;
            }

            if (edge.capacity > 0) {
                cost += (long) edge.cost * edge.flow;
            }
        }
        return new long[] { flow, cost };
    }
}
