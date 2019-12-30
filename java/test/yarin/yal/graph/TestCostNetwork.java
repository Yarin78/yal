package yarin.yal.graph;

import org.junit.Assert;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Random;
import java.util.Scanner;

public class TestCostNetwork {
    @Test
    public void testMaxFlowMinCostSmall() {
        CostNetwork network = new CostNetwork();
        network.addEdge(0, 1, 100, 1000);
        long[] res = network.maxFlowMinCost(0, 1);
        Assert.assertEquals(100, res[0]);
        Assert.assertEquals(100000, res[1]);
    }

    @Test
    public void testMaxFlowMinCost() {
        CostNetwork network = new CostNetwork();

        int n = 20;
        Random r = new Random();
        for (int i = 0; i < 100; i++)
        {
            int x = r.nextInt(n);
            int y = r.nextInt(n);
            int cap = 1 + r.nextInt(99);
            int cost = 100 - i;
            if (x != y) {
                network.addEdge(x, y, cap, cost);
            }
        }
        network.maxFlowMinCost(0, n - 1);
    }

    @Test
    public void testMaxFlowMinCostLarge() throws FileNotFoundException {
        InputStream inputStream = TestCostNetwork.class.getResourceAsStream("mincostmaxflow.in");
        InputStream answerStream = TestCostNetwork.class.getResourceAsStream("mincostmaxflow.ans");

        Scanner inputScanner = new Scanner(inputStream);
        Scanner answerScanner = new Scanner(answerStream);

        int casesTested = 0;

        while (true) {
            int n = inputScanner.nextInt(), m = inputScanner.nextInt(), source = inputScanner.nextInt(), sink = inputScanner.nextInt();
            if (n == 0) {
                break;
            }
            int expectedMaxFlow = answerScanner.nextInt(), expectedMinCost = answerScanner.nextInt();

            CostNetwork cn = new CostNetwork();
            for (int i = 0; i < m; i++)
            {
                int src = inputScanner.nextInt(), dest = inputScanner.nextInt(), cap = inputScanner.nextInt(), cost = inputScanner.nextInt();
                if (src >= n || dest >= n) {
                    throw new RuntimeException();
                }
                cn.addEdge(src, dest, cap, cost);
            }
            long[] ans = cn.maxFlowMinCost(source, sink);
            long maxFlow = ans[0];
            long minCost = ans[1];

            Assert.assertEquals(maxFlow, expectedMaxFlow);
            Assert.assertEquals(minCost, expectedMinCost);

            casesTested++;
        }

        Assert.assertTrue(casesTested >= 10);
    }
}
