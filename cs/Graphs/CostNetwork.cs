using System;
using System.Collections.Generic;

namespace Algorithms.Graphs
{
	public class CostNetwork
	{
		private class Edge
		{
			public readonly int Source, Dest, Capacity, Cost;
			public Edge Other;
			public int Flow;

			public Edge(int from, int to, int cost, int capacity)
			{
				Source = from;
				Dest = to;
				Capacity = capacity;
				Cost = cost;
			}
		}

		private int noNodes = 0;
		private readonly List<Edge> edges = new List<Edge>();
		
		public void AddEdge(int source, int dest, int capacity, int cost)
		{
			if (source + 1 > noNodes) 
				noNodes = source + 1;
			if (dest + 1 > noNodes) 
				noNodes = dest + 1;
			Edge e1 = new Edge(source, dest, cost, capacity);
			Edge e2 = new Edge(dest, source, -cost, 0);
			e1.Other = e2;
			e2.Other = e1;
			edges.Add(e1);
			edges.Add(e2);
		}

		private int BellmanFord(int source, out Edge[] p)
		{
			p = new Edge[noNodes];
			int[] d = new int[noNodes];
			for (int i = 0; i < noNodes; i++)
				d[i] = int.MaxValue;
			d[source] = 0;
			bool change = true;
			for (int m = 1; change && m < noNodes - 1; m++)
			{
				change = false;
				foreach(Edge e in edges)
					if (e.Capacity > e.Flow && d[e.Source] != int.MaxValue && d[e.Dest] > d[e.Source] + e.Cost)
					{
						change = true;
						d[e.Dest] = d[e.Source] + e.Cost;
						p[e.Dest] = e;
					}
			}
			foreach(Edge e in edges)
				if (e.Capacity > e.Flow && d[e.Source] != int.MaxValue && d[e.Dest] > d[e.Source] + e.Cost)
				{
					p[e.Dest] = e;
					return e.Dest;
				}
			return -1;
		}

		private static void IncrementFlow(int source, int sink, Edge[] p)
		{
			int minCapacity = int.MaxValue;
			for (int u = sink; u != source; u = p[u].Source)
				minCapacity = Math.Min(minCapacity, p[u].Capacity - p[u].Flow);
			
//			string s = sink.ToString();
			for (int u = sink; u != source; u = p[u].Source)
			{
//				s = p[u].Source + " -> " + s;
				p[u].Flow += minCapacity;
				p[u].Other.Flow -= minCapacity;
			}
//			s += ": Increase flow with " + minCapacity;
//			Console.WriteLine(s);
		}

		/*private static void AdjustCycle(int u, Edge[] p)
		{
//			int dst = p[u].Cost;
			int minCapacity = p[u].Capacity - p[u].Flow;
			for (int v = p[u].Source; v != u; v = p[v].Source)
			{
				minCapacity = Math.Min(minCapacity, p[v].Capacity - p[v].Flow);
//				dst += p[v].Cost;
			}
			p[u].Flow += minCapacity;
			p[u].Other.Flow -= minCapacity;
			string s = p[u].Source.ToString();
			for (int v = p[u].Source; v != u; v = p[v].Source)
			{
				s = p[v].Source + " -> " + s;
				p[v].Flow += minCapacity;
				p[v].Other.Flow -= minCapacity;
			}
			s += ": Adjusting flow with " + minCapacity;
			Console.WriteLine(s);
		}*/

		public int[] MaxFlowMinCost(int source, int sink)
		{
			while (true)
			{
				Edge[] p;
				int u = BellmanFord(source, out p);
				if (u == -1)
				{
					if (p[sink] == null)
						break;
					IncrementFlow(source, sink, p);
				}
				else
					throw new Exception(); // Shouldn't happen
//					AdjustCycle(u, p);
			}

			int flow = 0, cost = 0;
			foreach (Edge edge in edges)
			{
				if (edge.Source == source)
					flow += edge.Flow;

				if (edge.Capacity > 0)
					cost += edge.Cost * edge.Flow;
			}
			return new int[] { flow, cost };
		}
	}
}
