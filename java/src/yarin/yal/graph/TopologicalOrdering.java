package yarin.yal.graph;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Class that finds a topological ordering of an acyclic directed graph
 */
public class TopologicalOrdering {

    /**
     * @param dg A directed graph
     * @return an ordering of the nodes, or null if the graph is not a DAG
     */
    public List<DirectedGraph.Node> findOrdering(DirectedGraph dg) {
        ArrayList<DirectedGraph.Node> result = new ArrayList<>(dg.getNodeCount());

        int degree[] = new int[dg.getNodeCount()];
        Queue<DirectedGraph.Node> queue = new LinkedList<>();
        for (DirectedGraph.Node node : dg.getNodes()) {
            degree[node.getIndex()] = node.getInDegree();
            if (node.getInDegree() == 0) {
                queue.add(node);
            }
        }

        while (queue.size() > 0) {
            DirectedGraph.Node cur = queue.poll();
            result.add(cur);
            for (DirectedGraph.Edge edge : cur.getOutgoingEdges()) {
                if (--degree[edge.getDest().getIndex()] == 0) {
                    queue.add(edge.getDest());
                }
            }
        }

        if (result.size() < dg.getNodeCount()) {
            return null;
        }
        return result;
    }
}
