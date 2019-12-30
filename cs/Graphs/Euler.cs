using System;
using System.Collections.Generic;

namespace Algorithms.Graphs
{
	// Not tested yet

	public class Euler
	{
		// Finds the lexicographically first euler cycle in an undirect graph.

		private readonly int n; // Number of nodes
		private int[] degree;
		private int[,] edgeCount; // Adjacency edge matrix

		public Euler(int n)
		{
			this.n = n;
		}

		public void AddEdge(int u, int v)
		{
			degree[u]++;
			degree[v]++;
			edgeCount[u, v]++;
			edgeCount[v, u]++;
		}

		public List<int> FindCycle()
		{
			List<int> result = new List<int>();
			int noOdd = 0, firstOdd = -1, firstNonZero = -1;
			for (int i = 0; i < n; i++)
			{
				if (degree[i] > 0 && firstNonZero < 0)
					firstNonZero = i;
				if (degree[i] % 2 == 1)
				{
					noOdd++;
					if (firstOdd < 0)
						firstOdd = i;
				}
			}
			if (noOdd > 2)
				return null;
			if (firstNonZero < 0)
				return result;
			if (noOdd > 0)
				Search(firstOdd, result);
			else
				Search(firstNonZero, result);
			result.Reverse();
			return result;
		}

		private void Search(int cur, ICollection<int> result)
		{
			for (int i = 0; i < n; i++)
				while (edgeCount[cur, i] > 0)
				{
					edgeCount[cur, i]--;
					edgeCount[i, cur]--;
					Search(i, result);
				}
			result.Add(cur);
		}
	}
}
