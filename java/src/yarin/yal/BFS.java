package yarin.yal;

import java.util.LinkedList;
import java.util.Queue;

public class BFS {
    public static int[][] simpleBFS(String[] map, int startX, int startY, String allowedSquares, boolean allowDiagonal) {
        int[] dx = {0,1,0,-1,1,1,-1,-1}, dy = {1,0,-1,0,-1,1,-1,1};
        int xSize = map[0].length(), ySize = map.length, maxDir = allowDiagonal ? 8 : 4;
        int[][] dist = new int[ySize][xSize];
        for(int y = 0; y < ySize; y++) for(int x = 0; x < xSize; x++) dist[y][x] = -1;
        Queue<Integer> q = new LinkedList<Integer>();
        q.add(startY * xSize + startX);
        dist[startY][startX] = 0;
        while (q.size() > 0) {
            int e = q.poll();
            int curX = e % xSize, curY = e / xSize;
            for(int d = 0; d < maxDir; d++) {
                int nx = curX + dx[d], ny = curY + dy[d];
                if (nx >= 0 && ny >= 0 && nx < xSize && ny < ySize &&
                    allowedSquares.indexOf(map[ny].charAt(nx)) >= 0 && dist[ny][nx] < 0) {
                    dist[ny][nx] = dist[curY][curX] + 1;
                    q.add(ny * xSize + nx);
                }
            }
        }
        return dist;
    }
}
