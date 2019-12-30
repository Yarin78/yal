using System;
using System.Collections.Generic;

namespace Algorithms.Graphs
{
	public class Network
	{
		private readonly List<int>[] _edges;
		private readonly int _source, _sink;
		private readonly int[,] _capacity, _flow;
		private readonly int[] _tag;
		private int _cookie = 1, _totalFlow;
	
		public Network(int maxNodes)
		{
			_source = maxNodes;
			_sink = maxNodes + 1;
			maxNodes += 2;
			_edges = new List<int>[maxNodes];
			for(int i=0; i<maxNodes; i++) _edges[i] = new List<int>();
			_capacity = new int[maxNodes,maxNodes];
			_flow = new int[maxNodes,maxNodes];
			_tag = new int[maxNodes];
		}

		public void AddSourceEdge(int dest, int capacity) { AddEdge(_source, dest, capacity); }
		public void AddSinkEdge(int src, int capacity) { AddEdge(src, _sink, capacity); }

		public void AddEdge(int src, int dest, int capacity)
		{
			if (_capacity[src,dest] == 0 && _capacity[dest,src] == 0)
			{
				if (src != _sink) _edges[src].Add(dest);
				if (src != _source) _edges[dest].Add(src);
			}
			_capacity[src,dest] += capacity;
		}

		private int Go(int cur, int flow)
		{
			if (cur == _source) _cookie++;
			if (cur == _sink) return flow;
			if (_tag[cur] == _cookie || flow == 0) return 0;
			_tag[cur] = _cookie;
			foreach(int v in _edges[cur])
			{
				int f = Go(v,Math.Min(flow, _capacity[cur,v] - _flow[cur,v] + _flow[v,cur]));
				if (f > 0) { _flow[cur,v] += f; return f; }
			}
			return 0;
		}

		public int Flow()
		{
			int f;
			while ((f = Go(_source, int.MaxValue)) > 0) _totalFlow += f;
			return _totalFlow;
		}
	}
}