package yarin.yal.graph;

import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

public class TestStronglyConnectedComponents {

    @Test
    public void testStronglyConnectedComponentsLarge() {
        int n = 100000, m = 200000;

        DirectedGraph dg = new DirectedGraph(n);
        Random rnd = new Random(0);

        for (int i = 0; i < m; i++) {
            int a = rnd.nextInt(n), b = rnd.nextInt(n);
            dg.addEdge(dg.new ExplorableEdge(dg.getNode(a), dg.getNode(b)));
        }
        StronglyConnectedComponents algo = new StronglyConnectedComponents();

        DirectedGraph.Node[] map = algo.findComponentsIterative(dg);

        for (int i = 0; i < 100; i++) {
            System.out.println(i + ": " + map[i].getIndex());
        }
    }

    @Test
    public void testStronglyConnectedComponentsRandom() {
        // Compare iterative and recursive version

        Random rnd = new Random(0);
        int n = 1000, m = 1500;

        for (int cases = 0; cases < 10; cases++) {
            DirectedGraph dg = new DirectedGraph(n);
            for (int i = 0; i < m; i++) {
                int a = rnd.nextInt(n), b = rnd.nextInt(n);
                dg.addEdge(dg.new ExplorableEdge(dg.getNode(a), dg.getNode(b)));
            }
            StronglyConnectedComponents algo = new StronglyConnectedComponents();

            DirectedGraph.Node[] map1 = algo.findComponentsRecursive(dg);
            DirectedGraph.Node[] map2 = algo.findComponentsIterative(dg);
            for (int i = 0; i < n; i++) {
                Assert.assertEquals(map1[i], map2[i]);
            }
        }
    }

    @Test
    public void testStronglyConnectedComponentsRecursive() {
        int[][] edges = {{1, 2}, {5, 7}, {7, 5}, {2, 7}, {5, 1}, {6, 4}, {4, 3}, {3, 4}};

        DirectedGraph dg = new DirectedGraph(8);

        for (int[] edge : edges) {
            int a = edge[0], b = edge[1];
            dg.addEdge(dg.new ExplorableEdge(dg.getNode(a), dg.getNode(b)));
        }
        StronglyConnectedComponents algo = new StronglyConnectedComponents();
        DirectedGraph.Node[] map = algo.findComponentsRecursive(dg);

        int[] expected = {0, 1, 1, 3, 3, 1, 6, 1};

        for (int i = 1; i <= 7; i++) {
            Assert.assertEquals(map[i].getIndex(), expected[i]);
        }
    }

    @Test
    public void testStronglyConnectedComponentsIterative() {
        int[][] edges = {{1, 2}, {5, 7}, {7, 5}, {2, 7}, {5, 1}, {6, 4}, {4, 3}, {3, 4}};

        DirectedGraph dg = new DirectedGraph(8);

        for (int[] edge : edges) {
            int a = edge[0], b = edge[1];
            dg.addEdge(dg.new ExplorableEdge(dg.getNode(a), dg.getNode(b)));
        }
        StronglyConnectedComponents algo = new StronglyConnectedComponents();
        DirectedGraph.Node[] map = algo.findComponentsIterative(dg);

        int[] expected = {0, 1, 1, 3, 3, 1, 6, 1};

        for (int i = 1; i <= 7; i++) {
            Assert.assertEquals(map[i].getIndex(), expected[i]);
        }
    }

    @Test
    public void testCreateCondensedGraph() {
        int[][] edges = {{1,2},{2,5},{5,1},{4,6},{5,6},{6,3},{3,6},{3,0},{0,7},{7,9},{9,10},{7,8},{8,9},{10,7}};

        DirectedGraph dg = new DirectedGraph(11);

        for (int[] edge : edges) {
            int a = edge[0], b = edge[1];
            dg.addEdge(dg.new ExplorableEdge(dg.getNode(a), dg.getNode(b)));
        }
        StronglyConnectedComponents algo = new StronglyConnectedComponents();
        DirectedGraph.Node[] map = algo.findComponentsRecursive(dg);

        DirectedGraph dag = algo.createCondensedGraph(dg, map);

        Assert.assertEquals(4, dag.getEdges().size());
        Assert.assertEquals(dag.getNodeOutEdges().get(0).get(0).getDest().getIndex(), 7);
        Assert.assertEquals(dag.getNodeOutEdges().get(1).get(0).getDest().getIndex(), 6);
        Assert.assertEquals(dag.getNodeOutEdges().get(4).get(0).getDest().getIndex(), 6);
        Assert.assertEquals(dag.getNodeOutEdges().get(6).get(0).getDest().getIndex(), 0);
    }
}
