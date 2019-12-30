package yarin.yal.graph;

import java.util.ArrayList;
import java.util.List;

public class NetworkEdmondKarp {

    // Network Flow
    // Using Edmonds-Karp algorithm (Ford-Fulkersson with BFS)
    // Complexity: O(V*E^2)

    private class Edge {
        int src, dest, flow, cap;

        public Edge(int a, int b, int c) {
            this.src = a;
            this.dest = b;
            this.flow = 0;
            this.cap = c;
        }
    }

    private class Vertex {
        List<Edge> e = new ArrayList<>();
        int slack;
        Edge used;
    }

    private Vertex vert[];

    /**
     * Create new network
     * @param maxNodes number of nodes, including source and sink
     */
    public NetworkEdmondKarp(int maxNodes) {
        this.vert = new Vertex[maxNodes];
        for (int i = 0; i < maxNodes; i++) {
            this.vert[i] = new Vertex();
        }
    }

    public void addEdge(int src, int dest, int cap) {
        if (src==dest) return;
        Edge e = new Edge(src,dest,cap);
        this.vert[src].e.add(e);
        this.vert[dest].e.add(e);
    }

    boolean findAugmentingPath(int source, int sink) {
        int q[] = new int[this.vert.length], head=0, tail=0;
        for (Vertex v : this.vert) {
            v.used = null;
        }
        this.vert[source].slack = 1<<30;
        q[tail++] = source;
        while ((head < tail) && this.vert[sink].used == null) {
            int x = q[head++];
            for (Edge e : this.vert[x].e) {
                int y = e.src + e.dest - x;
                if (y == source || this.vert[y].used != null) continue;
                int s = e.src == x ? (e.cap - e.flow) : e.flow;
                if (s != 0) {
                    this.vert[y].slack = Math.min(this.vert[x].slack, s);
                    this.vert[y].used = e;
                    q[tail++] = y;
                }
            }
        }
        return this.vert[sink].used != null;
    }

    void usePath(int source, int sink) {
        int x = sink, slack = this.vert[sink].slack;
        while (x != source) {
            if (this.vert[x].used.dest == x) {
                this.vert[x].used.flow += slack;
                x = this.vert[x].used.src;
            } else {
                this.vert[x].used.flow -= slack;
                x = this.vert[x].used.dest;
            }
        }
    }

    public int findMaxFlow(int source, int sink) {
        while (findAugmentingPath(source, sink))
            usePath(source, sink);
        int flow = 0;
        for (Edge edge : this.vert[source].e) {
            if (edge.src == source)
                flow += edge.flow;
        }
        return flow;
    }

    public List<Edge> findMinCut() {
        List<Edge> mincut = new ArrayList<>();
        for (int i = 0; i < this.vert.length; i++) {
            if (this.vert[i].used == null) continue;
            for (Edge e : this.vert[i].e) {
                if (e.src != i || this.vert[e.dest].used != null) continue;
                mincut.add(e);
            }
        }
        return mincut;
    }
}
