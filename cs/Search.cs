using System.Collections;

namespace Algorithms.Search
{
	public class BFS
	{
		public static int[,] simpleBFS(string[] map, int startX, int startY, string allowedSquares, bool allowDiagonal)
		{
			int[] dx = {0,1,0,-1,1,1,-1,-1}, dy = {1,0,-1,0,-1,1,-1,1};
			int xSize = map[0].Length, ySize = map.Length, maxDir = allowDiagonal ? 8 : 4;
			int[,] dist = new int[ySize,xSize];
			for(int y = 0; y < ySize; y++) for(int x = 0; x < xSize; x++) dist[y,x] = -1;
			Queue q = new Queue();
			q.Enqueue(startY * xSize + startX);
			dist[startY, startX] = 0;
			while (q.Count > 0)
			{
				int e = (int)q.Dequeue();
				int curX = e % xSize, curY = e / xSize;
				for(int d = 0; d < maxDir; d++)
				{
					int nx = curX + dx[d], ny = curY + dy[d];
					if (nx >= 0 && ny >= 0 && nx < xSize && ny < ySize &&
						allowedSquares.IndexOf(map[ny][nx]) >= 0 && dist[ny,nx] < 0)
					{
						dist[ny, nx] = dist[curY, curX] + 1;
						q.Enqueue(ny * xSize + nx);
					}
				}
			}
			return dist;
		}
	}
}

