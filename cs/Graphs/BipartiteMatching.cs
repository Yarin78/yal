using System;
using System.Collections.Generic;

namespace Algorithms.Graphs
{
	// Tested and much faster than Network

	public struct MatchingEdge
	{
		public int A, B;

		public MatchingEdge(int a, int b)
		{
			A = a;
			B = b;
		}
	}

	public static class BipartiteMatching
	{
		public static List<MatchingEdge> FindMaximumMatching(List<MatchingEdge> e)
		{
			int n = e.Count, aSize = 0, bSize = 0;
			bool augmented = true;
			for (int i = 0; i < n; i++)
			{
				aSize = Math.Max(aSize, e[i].A + 1);
				bSize = Math.Max(bSize, e[i].B + 1);
			}
			List<int>[] a = new List<int>[aSize];
			for (int i = 0; i < a.Length; i++)
				a[i] = new List<int>();
			int[] ba = new int[bSize], asat = new int[aSize], q = new int[aSize];
			for (int i = 0; i < bSize; i++)
				ba[i] = -1;
			for (int i = 0; i < n; i++)
				a[e[i].A].Add(e[i].B);
			while (augmented)
			{
				augmented = false;
				int head = 0, tail = 0;
				int[] aprev = new int[aSize], bprev = new int[bSize];
				for (int i = 0; i < aSize; i++)
					aprev[i] = -1;
				for (int i = 0; i < bSize; i++)
					bprev[i] = -1;
				for (int i = 0; i < aSize; i++)
					if (asat[i] == 0)
						q[tail++] = i;
				while (head < tail && !augmented)
				{
					int x = q[head++];
					for (int i = 0; i < a[x].Count && !augmented; i++)
					{
						int y = a[x][i], w = ba[y];
						if (x == w) 
							continue;
						if (w >= 0)
						{
							if (bprev[y] < 0) 
								bprev[y] = x;
							if (aprev[w] < 0) 
								aprev[q[tail++] = w] = y;
						}
						else
						{
							ba[y] = x;
							while ((y = aprev[x]) >= 0)
								ba[y] = x = bprev[y];
							asat[x] = 1;
							augmented = true;
						}
					}
				}
			}
			List<MatchingEdge> result = new List<MatchingEdge>();
			for (int i = 0; i < bSize; i++) 
				if (ba[i] >= 0)
					result.Add(new MatchingEdge(ba[i], i));
			return result;
		}
	}
}