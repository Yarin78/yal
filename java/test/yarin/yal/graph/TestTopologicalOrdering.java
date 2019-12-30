package yarin.yal.graph;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class TestTopologicalOrdering {

    @Test
    public void simpleTest() {
        DirectedGraph dg = new DirectedGraph(5);
        dg.addEdge(3, 0);
        dg.addEdge(2, 3);
        dg.addEdge(1, 3);
        dg.addEdge(0, 4);

        List<DirectedGraph.Node> ordering = new TopologicalOrdering().findOrdering(dg);

        int expected[] = { 1, 2, 3, 0, 4 };
        for (int i = 0; i < expected.length; i++) {
            Assert.assertEquals(expected[i], ordering.get(i).getIndex());
        }
    }

    @Test
    public void testNonDag() {
        DirectedGraph dg = new DirectedGraph(4);
        dg.addEdge(0, 1);
        dg.addEdge(1, 2);
        dg.addEdge(2, 3);
        dg.addEdge(3, 1);

        List<DirectedGraph.Node> ordering = new TopologicalOrdering().findOrdering(dg);
        Assert.assertNull(ordering);
    }
}
