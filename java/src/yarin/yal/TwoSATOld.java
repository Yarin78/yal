package yarin.yal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class TwoSATOld {
    private static class Graph {
        public List<List<Integer>> vin, vout;
        public int[] vcomp;

        public Graph(int n) {
            vin = new ArrayList<List<Integer>>();
            vout = new ArrayList<List<Integer>>();
            vcomp = new int[n];
            for (int i = 0; i < n; i++) {
                vin.add(new ArrayList<Integer>());
                vout.add(new ArrayList<Integer>());
            }
        }

        public void addEdge(int x, int y) {
            vout.get(x).add(y);
            vin.get(y).add(x);
        }
    }

    private int n;
    private Graph graph;
    private List<List<Integer>> literalMap, scGraph;
    private boolean[] visited, conflict, literalValue;

    /**
     * Initializes a new instance of a <see cref="yarin.algorithms.TwoSATOld"/> class.
     * @param noVariables the number of variables in the expression.
     */
    public TwoSATOld(int noVariables) {
        n = noVariables;
        graph = new Graph(n*2);
    }

    /**
     * Adds a new clause to the expression.
     * @param var1 the first variable in the clause.
     * @param neg1 true if the first variable is negated; otherwise false.
     * @param var2 the second variable in the clause.
     * @param neg2 true if the second variable is negated; otherwise false.
     */
    public void addClause(int var1, boolean neg1, int var2, boolean neg2) {
        int literal1 = var1*2 + (neg1 ? 1 : 0);
        int literal2 = var2*2 + (neg2 ? 1 : 0);
        graph.addEdge(literal1 ^ 1, literal2);
        graph.addEdge(literal2 ^ 1, literal1);
    }

    /**
     * Solves the 2-SAT problem and returns a possible variable assignment if it exits.
     * @return the variable assignment, or null if no solution exists.
     */
    public boolean[] solve() {
        StrongComponents sc = new StrongComponents(graph);
        scGraph = sc.createGraph();

        literalValue = new boolean[n*2];

        for (int i = 0; i < n; i++) {
            if (graph.vcomp[i*2] == graph.vcomp[i*2 + 1]) {
                return null;
            }
        }

        literalMap = new ArrayList<List<Integer>>();
        for (int i = 0; i < scGraph.size(); i++) {
            literalMap.add(new ArrayList<Integer>());
        }

        for (int i = 0; i < n*2; i++) {
            literalMap.get(graph.vcomp[i]).add(i);
        }

        visited = new boolean[scGraph.size()];
        conflict = new boolean[scGraph.size()];
        for (int i = 0; i < scGraph.size(); i++) {
            dfs(i);
        }

        boolean[] var = new boolean[n];

        for (int i = 0; i < n; i++) {
            if (!literalValue[i*2] && !literalValue[i*2 + 1]) {
                return null;
            }
            var[i] = literalValue[i*2];
        }
        return var;
    }


    private boolean dfs(int v) {
        boolean flag = conflict[v];

        if (!visited[v]) {
            visited[v] = true;
            for (int node : scGraph.get(v)) {
                flag |= dfs(node);
            }

            if (!flag) {
                for (int i : literalMap.get(v)) {
                    if (literalValue[i ^ 1]) {
                        flag = true;
                    }
                }

                if (!flag) {
                    for (int i : literalMap.get(v)) {
                        literalValue[i] = true;
                    }
                }
            }
        }

        return conflict[v] = flag;
    }


    private class StrongComponents {
        private int[] comp;
        private List<Integer> sorted;
        private boolean[] vis;
        private Graph g;
        private int noComp;

        public StrongComponents(Graph g) {
            this.g = g;
        }

        private void calc() {
            int n = g.vout.size();
            vis = new boolean[n];
            comp = new int[n];
            sorted = new ArrayList<Integer>(n);
            noComp = 0;
            for (int i = 0; i < n; i++) {
                dfs(i, g.vout);
            }
            vis = new boolean[n];

            for (int i = n - 1; i >= 0; i--) {
                if (!vis[sorted.get(i)]) {
                    dfs(sorted.get(i), g.vin);
                    noComp++;
                }
            }
            g.vcomp = comp;
        }

        private void dfs(int v, List<List<Integer>> edges) {
            if (!vis[v]) {
                vis[v] = true;
                comp[v] = noComp;
                for (int node : edges.get(v)) {
                    dfs(node, edges);
                }
                sorted.add(v);
            }
        }

        public List<List<Integer>> createGraph() {
            calc();

            List<HashSet<Integer>> h = new ArrayList<HashSet<Integer>>();
            for (int i = 0; i < noComp; i++) {
                h.add(new HashSet<Integer>());
            }

            for (int i = 0; i < g.vout.size(); i++) {
                for (Integer j : g.vout.get(i)) {
                    h.get(comp[i]).add(comp[j]);
                }
            }

            List<List<Integer>> h2 = new ArrayList<List<Integer>>();;

            for (int i = 0; i < noComp; i++) {
                h.get(i).remove(i);
                h2.add(new ArrayList<Integer>(h.get(i)));
            }

            return h2;
        }
    }
}
