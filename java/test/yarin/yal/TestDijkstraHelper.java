package yarin.yal;

import org.junit.Test;

public class TestDijkstraHelper {

    @Test
    public void testDijkstraHelper() {
        int n = 6;
        int[][] edges = new int[n][n];
        DijkstraHelper<Integer> dh = new DijkstraHelper<Integer>(n, 0);
        edges[0][1] = 3;
        edges[0][2] = 8;
        edges[1][2] = 4;
        edges[1][3] = 6;
        edges[2][4] = 7;
        edges[3][5] = 5;
        edges[4][1] = 5;
        edges[4][3] = 2;
        edges[4][5] = 8;

        dh.add(0, 0);
        int cur = dh.getNext();
        while (cur >= 0) {
            int curDist = dh.getDistance(cur);
            System.out.println(String.format("Node %d, distance = %d", cur, curDist));
            for (int i = 0; i < n; i++) {
                if (edges[i][cur] > 0)
                    dh.add(i, curDist + edges[i][cur]);
                if (edges[cur][i] > 0)
                    dh.add(i, curDist + edges[cur][i]);
            }
            cur = dh.getNext();
        }
    }

}
