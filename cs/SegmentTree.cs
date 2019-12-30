using System;
using System.Collections.Generic;
using System.Linq;

namespace Algorithms
{
	public class SegmentTree<T>
	{
		private readonly Comparison<T> comparison;
		private readonly T[] data;
		private readonly int[] tree;
		private readonly int n;
		private readonly Comparison<T> _comparison;

		private static int GetTreeSize(int n)
		{
			n--;
			while ((n & (n - 1)) > 0)
				n = n & (n - 1);
			return n*4;
		}

		public SegmentTree(int n, T defaultValue, Comparison<T> comparison)
		{
			this.n = n;
			this.comparison = comparison;
			data = new T[n];
			for (int i = 0; i < n; i++)
				data[i] = defaultValue;
			tree = new int[GetTreeSize(n)];
			Init(1, 0, n - 1);
		}

		public SegmentTree(IEnumerable<T> data, Comparison<T> comparison)
		{
			this.data = data.ToArray();
			this.comparison = comparison;
			n = this.data.Length;
			tree = new int[GetTreeSize(n)];
			Init(1, 0, n - 1);
		}

		private void Init(int node, int b, int e)
		{
			if (b == e)
				tree[node] = b;
			else
			{
				Init(2 * node, b, (b + e) / 2);
				Init(2 * node + 1, (b + e) / 2 + 1, e);
				tree[node] = comparison(data[tree[2*node]], data[tree[2*node + 1]]) <= 0
				             	? tree[2*node] : tree[2*node + 1];
			}
		}

		public int Query(int start, int end)
		{
			if (start < 0 || end > n)
				throw new ArgumentOutOfRangeException();
			if (start >= end)
				throw new ArgumentException();
			return Query(1, 0, n - 1, start, end - 1);
		}

		private int Query(int node, int b, int e, int i, int j)
		{
			if (i > e || j < b)
				return -1;
			if (b >= i && e <= j)
				return tree[node];
			
			int p1 = Query(2 * node, b, (b + e)/2, i, j);
			int p2 = Query(2 * node + 1, (b + e) / 2 + 1, e, i, j);
			if (p1 == -1)
				return p2;
			if (p2 == -1)
				return p1;
			return comparison(data[p1], data[p2]) <= 0 ? p1 : p2;
		}
		
		public void Set(int index, T value)
		{
			Set(1, 0, n - 1, index, value);
		}

		public T Get(int index)
		{
			return data[index];
		}

		private void Set(int node, int b, int e, int index, T value)
		{
			if (b == e)
			{
				data[index] = value;
				tree[node] = b;
			}
			else
			{
				if (index <= (b+e)/2)
					Set(2 * node, b, (b + e) / 2, index, value);
				else
					Set(2 * node + 1, (b + e) / 2 + 1, e, index, value);
				tree[node] = comparison(data[tree[2 * node]], data[tree[2 * node + 1]]) <= 0
								? tree[2 * node] : tree[2 * node + 1];
			}
		}
	}
}