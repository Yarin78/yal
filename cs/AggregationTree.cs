using System;
using System.Collections.Generic;
using System.Linq;

namespace Algorithms
{
	public class AggregationTree<T>
	{
		private readonly Func<T, T, T> aggregateFunc;
		private readonly T[] tree;
		private readonly int n;

		private static int GetTreeSize(int n)
		{
			n--;
			while ((n & (n - 1)) > 0)
				n = n & (n - 1);
			return n*4;
		}

		public AggregationTree(int n, T defaultValue, Func<T, T, T> aggregateFunc)
		{
			this.aggregateFunc = aggregateFunc;
			this.n = n;
			var data = new T[n];
			for (int i = 0; i < n; i++)
				data[i] = defaultValue;
			tree = new T[GetTreeSize(n)];
			Init(1, 0, n - 1, data);
		}

		public AggregationTree(IEnumerable<T> data, Func<T, T, T> aggregateFunc)
		{
			this.aggregateFunc = aggregateFunc;
			n = data.Count();
			tree = new T[GetTreeSize(n)];
			Init(1, 0, n - 1, data.ToArray());
		}

		private void Init(int node, int b, int e, T[] data)
		{
			if (b == e)
				tree[node] = data[b];
			else
			{
				Init(2*node, b, (b + e)/2, data);
				Init(2*node+1, (b + e) / 2 + 1, e, data);
				tree[node] = aggregateFunc(tree[2*node], tree[2*node + 1]);
			}
		}

		public T Query(int start, int end)
		{
			if (start < 0 || end > n)
				throw new ArgumentOutOfRangeException();
			if (start >= end)
				throw new ArgumentException();
			return Query(1, 0, n - 1, start, end - 1);
		}

		private T Query(int node, int b, int e, int i, int j)
		{
			if (i > e || j < b)
				throw new ArgumentException();
			if (b >= i && e <= j)
				return tree[node];
			
			T aggregate = default(T);
			bool set = false;

			if (i <= (b+e)/2)
			{
				aggregate = Query(2 * node, b, (b + e)/2, i, j);
				set = true;
			}
			if (j >= (b+e) / 2 + 1)
			{
				T p = Query(2 * node + 1, (b + e) / 2 + 1, e, i, j);
				aggregate = set ? aggregateFunc(aggregate, p) : p;
			}
			return aggregate;
		}
		
		public void Set(int index, T value)
		{
			Set(1, 0, n - 1, index, value);
		}

		private void Set(int node, int b, int e, int index, T value)
		{
			if (b == e)
				tree[node] = value;
			else
			{
				if (index <= (b+e)/2)
					Set(2 * node, b, (b + e) / 2, index, value);
				else
					Set(2 * node + 1, (b + e) / 2 + 1, e, index, value);
				tree[node] = aggregateFunc(tree[2 * node], tree[2 * node + 1]);
			}
		}
	}
}