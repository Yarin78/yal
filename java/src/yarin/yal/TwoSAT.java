package yarin.yal;

import java.util.List;

import yarin.yal.graph.DirectedGraph;
import yarin.yal.graph.StronglyConnectedComponents;
import yarin.yal.graph.TopologicalOrdering;

/**
 * Solves the 2-satisfiability problem
 *
 * Dependency: DirectedGraph, StronglyConnectedComponents, TopologicalOrdering
 */
public class TwoSAT {

    private final DirectedGraph dg;

    /**
     * @param noVariables the number of variables in the expression.
     */
    public TwoSAT(int noVariables) {
        this.dg = new DirectedGraph(noVariables * 2);
    }

    /**
     * Adds a new clause to the expression.
     * @param term1 the first variable in the clause.
     * @param neg1 true if the first variable is negated; otherwise false.
     * @param term2 the second variable in the clause.
     * @param neg2 true if the second variable is negated; otherwise false.
     */
    public void addClause(int term1, boolean neg1, int term2, boolean neg2) {
        int literal1 = term1*2 + (neg1 ? 1 : 0);
        int literal2 = term2*2 + (neg2 ? 1 : 0);
        this.dg.addEdge(literal1 ^ 1, literal2);
        this.dg.addEdge(literal2 ^ 1, literal1);
    }

    /**
     * Solves the 2-SAT problem and returns a possible variable assignment if it exits.
     * @return the variable assignment, or null if no solution exists.
     */
    public boolean[] solve() {
        StronglyConnectedComponents scc = new StronglyConnectedComponents();
        DirectedGraph.Node[] map = scc.findComponentsIterative(this.dg);

        // Verify that same term doesn't exist twice in same component
        for (int i = 0; i < map.length; i+=2) {
            if (map[i].equals(map[i+1])) return null;
        }

        DirectedGraph dag = scc.createCondensedGraph(this.dg, map);
        List<List<DirectedGraph.Node>> nodesInComponent = scc.getNodesInComponent(this.dg, map);

        boolean value[] = new boolean[map.length];
        boolean assigned[] = new boolean[map.length];

        List<DirectedGraph.Node> ordering = new TopologicalOrdering().findOrdering(dag);
        int comps = 0;
        for (DirectedGraph.Node node : ordering) {
            // Ignore nodes that are not representatives of the component
            if (map[node.getIndex()].getIndex() != node.getIndex()) continue;
            comps++;
            boolean setTrue = false, setFalse = false;
            for (DirectedGraph.Node nodes : nodesInComponent.get(node.getIndex())) {
                int ix = nodes.getIndex();
                if (assigned[ix]) {
                    if (value[ix]) setTrue = true;
                    if (!value[ix]) setFalse = true;
                }
            }

            assert !setTrue || !setFalse;

            for (DirectedGraph.Node nodes : nodesInComponent.get(node.getIndex())) {
                int ix = nodes.getIndex();
                assigned[ix] = true;
                assigned[ix ^ 1] = true;
                value[ix] = setTrue;
                value[ix ^ 1] = !setTrue;
            }
        }

        boolean[] result = new boolean[map.length / 2];
        for (int i = 0; i < result.length; i++) {
            result[i] = value[i*2];
        }
        return result;

    }
}
