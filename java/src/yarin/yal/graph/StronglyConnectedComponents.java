package yarin.yal.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

/**
 * Class with methods that finds all strongly connected components in graph.
 * Provides both a recursive and an iterative implementation.
 *
 * Uses Kosarajus algorithm (http://en.wikipedia.org/wiki/Kosaraju%27s_algorithm)
 */
public class StronglyConnectedComponents {

    /**
     * @param dg the input graph
     * @return a map of nodes to a representative node in the strong component
     */
    public DirectedGraph.Node[] findComponentsRecursive(DirectedGraph dg) {
        int n = dg.getNodeCount();
        boolean visited[] = new boolean[n];
        DirectedGraph.Node components[] = new DirectedGraph.Node[n];
        Stack<DirectedGraph.Node> stack = new Stack<DirectedGraph.Node>();
        for (DirectedGraph.Node node : dg.getNodes()) {
            dfs1(node, visited, stack);
        }
        while (!stack.empty()) {
            DirectedGraph.Node node = stack.pop();
            dfs2(node, components, node);
        }
        return components;
    }

    private void dfs1(DirectedGraph.Node node, boolean[] visited, Stack<DirectedGraph.Node> stack) {
        if (!visited[node.getIndex()]) {
            visited[node.getIndex()] = true;
            for (DirectedGraph.Edge edge : node.getOutgoingEdges()) {
                dfs1(edge.getDest(), visited, stack);
            }
            stack.add(node);
        }
    }

    private void dfs2(DirectedGraph.Node node, DirectedGraph.Node[] components, DirectedGraph.Node rep) {
        if (components[node.getIndex()] == null) {
            components[node.getIndex()] = rep;
            for (DirectedGraph.Edge edge : node.getIncomingEdges()  ) {
                dfs2(edge.getSrc(), components, rep);
            }
        }
    }


    /**
     * @param dg the input graph
     * @return a map of nodes to a representative node in the strong component
     */
    public DirectedGraph.Node[] findComponentsIterative(DirectedGraph dg) {
        int n = dg.getNodeCount();
        boolean visited[] = new boolean[n];
        DirectedGraph.Node components[] = new DirectedGraph.Node[n];
        Stack<DirectedGraph.Node> stack = new Stack<>();
        for (DirectedGraph.Node node : dg.getNodes()) {
            if (visited[node.getIndex()]) continue;

            // positive values are nodes to expand to,
            // negative values means the node has no more neighbours
            Stack<Integer> dfsStack = new Stack<>();
            dfsStack.add(node.getIndex());

            while (dfsStack.size() > 0) {
                int cur = dfsStack.pop();
                if (cur < 0) {
                    stack.add(dg.getNode(-cur-1));
                } else {
                    if (!visited[cur]) {
                        visited[cur] = true;
                        dfsStack.add(-cur-1);

                        ArrayList<DirectedGraph.Edge> edges = new ArrayList<>(dg.getNode(cur).getOutgoingEdges());
                        // Reverse so visited order becomes the same as in the recursive version
                        Collections.reverse(edges);
                        for (DirectedGraph.Edge edge : edges) {
                            dfsStack.add(edge.getDest().getIndex());
                        }
                    }
                }
            }
        }
        
        while (!stack.empty()) {
            DirectedGraph.Node node = stack.pop();
            DirectedGraph.Node rep = node;
            Queue<DirectedGraph.Node> queue = new LinkedList<>();
            queue.add(node);
            while (queue.size() > 0) {
                DirectedGraph.Node cur = queue.poll();
                if (components[cur.getIndex()] == null) {
                    components[cur.getIndex()] = rep;
                    for (DirectedGraph.Edge edge : cur.getIncomingEdges()  ) {
                        queue.add(edge.getSrc());
                    }
                }
            }
        }

        return components;
    }

    /**
     * Create a DAG by joining all nodes in a strongly connected component into one node
     * The new graph will contain the same number of nodes, but the nodes that are not
     * the representative nodes of a component will be isolated
     *
     * @param dg the graph
     * @return a DAG
     */
    public DirectedGraph createCondensedGraph(DirectedGraph dg, DirectedGraph.Node components[]) {
        DirectedGraph dag = new DirectedGraph(dg.getNodeCount());

        for (DirectedGraph.Edge edge : dg.getEdges()) {
            int a = components[edge.getSrc().getIndex()].getIndex();
            int b = components[edge.getDest().getIndex()].getIndex();
            if (a != b) {
                dag.addEdge(a, b);
            }
        }

        return dag;
    }

    public List<List<DirectedGraph.Node>> getNodesInComponent(DirectedGraph dg,
                                                              DirectedGraph.Node[] map) {
        ArrayList<List<DirectedGraph.Node>> result = new ArrayList<>(map.length);
        for (int i = 0; i < map.length; i++) {
            result.add(new ArrayList<DirectedGraph.Node>());
        }
        for (int i = 0; i < map.length; i++) {
            result.get(map[i].getIndex()).add(dg.getNode(i));
        }
        return result;
    }
}
