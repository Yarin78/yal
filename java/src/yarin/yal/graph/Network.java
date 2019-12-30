package yarin.yal.graph;

import java.util.ArrayList;
import java.util.List;

public class Network {
    private final List<List<Integer>> edges;
    private final int source, sink;
    private final int[][] capacity, flow;
    private final int[] tag;

    private int cookie = 1, totalFlow;

    /**
     * Create new network
     * @param n number of nodes, excluding source and sink
     */
    public Network(int maxNodes)
    {
        source = maxNodes;
        sink = maxNodes + 1;
        maxNodes += 2;
        edges = new ArrayList<List<Integer>>(maxNodes);
        for(int i=0; i<maxNodes; i++) {
            edges.add(new ArrayList<Integer>());
        }
        capacity = new int[maxNodes][maxNodes];
        flow = new int[maxNodes][maxNodes];
        tag = new int[maxNodes];
    }

    public void addSourceEdge(int dest, int capacity) {
        addEdge(source, dest, capacity);
    }

    public void addSinkEdge(int src, int capacity) {
        addEdge(src, sink, capacity);
    }

    public void addEdge(int src, int dest, int capacity) {
        if (this.capacity[src][dest] == 0 && this.capacity[dest][src] == 0) {
            if (src != sink) {
                edges.get(src).add(dest);
            }
            if (src != source) {
                edges.get(dest).add(src);
            }
        }
        this.capacity[src][dest] += capacity;
    }

    private int go(int cur, int flow) {
        if (cur == source) {
            cookie++;
        }
        if (cur == sink) {
            return flow;
        }
        if (tag[cur] == cookie || flow == 0) {
            return 0;
        }
        tag[cur] = cookie;
        for (int v : edges.get(cur)) {
            int f = go(v, Math.min(flow, capacity[cur][v] - this.flow[cur][v] + this.flow[v][cur]));
            if (f > 0) {
                this.flow[cur][v] += f;
                return f;
            }
        }
        return 0;
    }

    public int flow() {
        int f;
        while ((f = go(source, Integer.MAX_VALUE)) > 0) {
            totalFlow += f;
        }
        return totalFlow;
    }
}
