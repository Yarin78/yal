using System;
using System.Collections.Generic;

namespace Algorithms
{
	public class TwoSAT
	{
		private class Graph
		{
			public List<int>[] vin, vout;
			public int[] vcomp;

			public Graph(int n)
			{
				vin = new List<int>[n];
				vout = new List<int>[n];
				vcomp = new int[n];
				for (int i = 0; i < n; i++)
				{
					vin[i] = new List<int>();
					vout[i] = new List<int>();
				}
			}

			public void AddEdge(int x, int y)
			{
				vout[x].Add(y);
				vin[y].Add(x);
			}
		}

		private int n;
		private Graph graph;
		private List<int>[] literalMap, scGraph;
		private bool[] visited, conflict, literalValue;

		/// <summary>
		/// Initializes a new instance of a <see cref="TwoSAT"/> class.
		/// </summary>
		/// <param name="noVariables">The number of variables in the expression.</param>
		public TwoSAT(int noVariables)
		{
			n = noVariables;
			graph = new Graph(n*2);
		}

		/// <summary>
		/// Adds a new clause to the expression.
		/// </summary>
		/// <param name="var1">The first variable in the clause.</param>
		/// <param name="neg1">True if the first variable is negated; otherwise false.</param>
		/// <param name="var2">The second variable in the clause.</param>
		/// <param name="neg2">True if the second variable is negated; otherwise false.</param>
		public void AddClause(int var1, bool neg1, int var2, bool neg2)
		{
			int literal1 = var1*2 + (neg1 ? 1 : 0);
			int literal2 = var2*2 + (neg2 ? 1 : 0);
			graph.AddEdge(literal1 ^ 1, literal2);	
			graph.AddEdge(literal2 ^ 1, literal1);
		}

		/// <summary>
		/// Solves the 2-SAT problem and returns a possible variable assignment if it exits.
		/// </summary>
		/// <returns>The variable assignment, or null if no solution exists.</returns>
		public bool[] Solve()
		{
			var sc = new StrongComponents(graph);
			scGraph = sc.CreateGraph();

			literalValue = new bool[n*2];

			for (int i = 0; i < n; i++)
				if (graph.vcomp[i*2] == graph.vcomp[i*2 + 1])
					return null;
            
			literalMap = new List<int>[scGraph.Length];
			for (int i = 0; i < scGraph.Length; i++)
				literalMap[i] = new List<int>();

			for (int i = 0; i < n*2; i++)
				literalMap[graph.vcomp[i]].Add(i);

			visited = new bool[scGraph.Length];
			conflict = new bool[scGraph.Length];
			for (int i = 0; i < scGraph.Length; i++) 
				Dfs(i);
			
			var var = new bool[n];
			
			for (int i = 0; i < n; i++)
			{
				if (!literalValue[i*2] && !literalValue[i*2 + 1])
					return null;
				var[i] = literalValue[i*2];
			}
			return var;
		}


		private bool Dfs(int v)
		{
			bool flag = conflict[v];

			if (!visited[v])
			{
				visited[v] = true;
				foreach(int node in scGraph[v])
					flag |= Dfs(node);

				if (!flag)
				{
					foreach(int i in literalMap[v])
						if (literalValue[i ^ 1]) 
							flag = true;

					if (!flag)
						foreach(int i in literalMap[v])
							literalValue[i] = true;
				}
			}

			return conflict[v] = flag;

		}


		private class StrongComponents
		{
			private int[] comp;
			private List<int> sorted;
			private bool[] vis;
			private Graph g;
			private int noComp;

			public StrongComponents(Graph g)
			{
				this.g = g;
			}

			private void Calc()
			{
				int n = g.vout.Length;
				vis = new bool[n];
				comp = new int[n];
				sorted = new List<int>(n);
				noComp = 0;
				for (int i = 0; i < n; i++)
				{
					Dfs(i, g.vout);
				}
				vis = new bool[n];

				for (int i = n - 1; i >= 0; i--)
				{
					if (!vis[sorted[i]])
					{
						Dfs(sorted[i], g.vin);
						noComp++;
					}
				}
				g.vcomp = comp;
			}

			private void Dfs(int v, List<int>[] edges)
			{
				if (!vis[v])
				{
					vis[v] = true;
					comp[v] = noComp;
					foreach (int node in edges[v])
					{
						Dfs(node, edges);
					}
					sorted.Add(v);
				}
			}

			public List<int>[] CreateGraph()
			{
				Calc();

				var h = new HashSet<int>[noComp];
				for (int i = 0; i < noComp; i++)
				{
					h[i] = new HashSet<int>();
				}

				for (int i = 0; i < g.vout.Length; i++)
					foreach (int j in g.vout[i])
						h[comp[i]].Add(comp[j]);

				var h2 = new List<int>[noComp];

				for (int i = 0; i < noComp; i++)
				{
					h[i].Remove(i);
					h2[i] = new List<int>(h[i]);
				}

				return h2;
			}
		}
	}
}