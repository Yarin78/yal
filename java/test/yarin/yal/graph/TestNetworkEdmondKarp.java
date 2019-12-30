package yarin.yal.graph;


import org.junit.Assert;
import org.junit.Test;

public class TestNetworkEdmondKarp {

    @Test
    public void simpleGraph() throws Exception {
        NetworkEdmondKarp network = new NetworkEdmondKarp(11);

        network.addEdge(0, 1, 3);
        network.addEdge(0, 5, 7);
        network.addEdge(0, 8, 4);
        network.addEdge(1, 2, 6);
        network.addEdge(1, 8, 5);
        network.addEdge(2, 3, 7);
        network.addEdge(3, 4, 4);
        network.addEdge(5, 6, 6);
        network.addEdge(6, 7, 9);
        network.addEdge(7, 4, 2);
        network.addEdge(7, 2, 5);
        network.addEdge(8, 9, 9);
        network.addEdge(9, 10, 7);
        network.addEdge(10, 4, 8);

        int maxFlow = network.findMaxFlow(0, 4);
        Assert.assertEquals(13, maxFlow);

    }
}
