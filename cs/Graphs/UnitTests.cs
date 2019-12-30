using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using Algorithms.Graphs.Decompositions;
using NUnit.Framework;

namespace Algorithms.Graphs
{
	[TestFixture]
	public class UnitTests
	{
		[Test]
		public void TestMaxFlowMinCost()
		{
			CostNetwork network = new CostNetwork();
			/*network.AddEdge(0, 1, 15, 4);
			network.AddEdge(0, 2, 8, 4);
			network.AddEdge(1, 2, 1000, 2);
			network.AddEdge(1, 3, 4, 2);
			network.AddEdge(1, 4, 10, 6);
			network.AddEdge(2, 3, 15, 1);
			network.AddEdge(3, 4, 5, 3);
			network.AddEdge(3, 4, 1000, 2);
			network.AddEdge(4, 2, 4, 1);
			network.AddEdge(3, 5, 1000, 0);
			network.AddEdge(4, 5, 1000, 0);*/
			/*
						network.AddEdge(0, 1, 3, 5);
						network.AddEdge(0, 2, 8, 6);
						network.AddEdge(1, 2, 4, 2);
						network.AddEdge(1, 3, 6, 70);
						network.AddEdge(2, 4, 7, 9);
						network.AddEdge(3, 5, 5, 20);
						network.AddEdge(4, 1, 5, 1);
						network.AddEdge(4, 3, 2, 9);
						network.AddEdge(4, 5, 8, 15);*/

			int n = 20;
			Random r = new Random();
			for (int i = 0; i < 100; i++)
			{
				int x = r.Next(0, n);
				int y = r.Next(0, n);
				int cap = r.Next(1, 100);
				int cost = 100 - i;// r.Next(1, 100);
				if (x != y)
				{
					network.AddEdge(x, y, cap, cost);
				}
			}
			network.MaxFlowMinCost(0, n - 1);
		}

		[Test]
		public void TestMaxFlowMinCostLarge()
		{
			var inputStream = new StreamReader(@"..\..\TestData\mincostmaxflow.in");
			var answerStream = new StreamReader(@"..\..\TestData\mincostmaxflow.ans");

			var a = Array.ConvertAll<string, int>(inputStream.ReadLine().Split(' '), int.Parse);
			while (a[0] > 0)
			{
				var b = Array.ConvertAll<string, int>(answerStream.ReadLine().Split(' '), int.Parse);
				int expectedMaxFlow = b[0], expectedMinCost = b[1];

				int n = a[0], m = a[1], source = a[2], sink = a[3];
				CostNetwork cn = new CostNetwork();
				for (int i = 0; i < m; i++)
				{
					a = Array.ConvertAll<string, int>(inputStream.ReadLine().Split(' '), int.Parse);
					int src = a[0], dest = a[1], cap = a[2], cost = a[3];
					if (src >= n || dest >= n)
						throw new Exception();
					cn.AddEdge(src, dest, cap, cost);
				}
				var ans = cn.MaxFlowMinCost(source, sink);
				var maxFlow = ans[0];
				var minCost = ans[1];

				Assert.AreEqual(expectedMaxFlow, maxFlow);
				Assert.AreEqual(expectedMinCost, minCost);

				a = Array.ConvertAll<string, int>(inputStream.ReadLine().Split(' '), int.Parse);
			}
		}
		
		[Test]
		public void TestTreeLCA()
		{
			Random r = new Random(0);
			for (int i = 0; i < 10; i++)
			{
				int N = r.Next(30000, 40000);
				int[] perm = new int[N];
				for (int j = 0; j < N; j++)
					perm[j] = j;
				for (int j = 0; j < N; j++)
				{
					int swap = j + r.Next(N-j);
					int tmp = perm[j];
					perm[j] = perm[swap];
					perm[swap] = tmp;
				}

				Graph g = new Graph(N);
				for (int j = 1; j < N; j++)
				{
					int a = r.Next(j);
					g.AddEdge(perm[a], perm[j]);
				}
				RootedTree tree = new RootedTree(g, g.GetNode(perm[0]));

				for (int j = 0; j < 100; j++)
				{
					int a = r.Next(N), b = r.Next(N);
					RootedTreeNode p = tree.GetNode(a), q = tree.GetNode(b);

					// Naive solution
					var pp = p;
					var qq = q;
					List<RootedTreeNode> ptrail = new List<RootedTreeNode>(), qtrail = new List<RootedTreeNode>();
					while (pp != null)
					{
						ptrail.Add(pp);
						pp = pp.Parent;
					}
					while (qq != null)
					{
						qtrail.Add(qq);
						qq = qq.Parent;
					}
					ptrail.Reverse();
					qtrail.Reverse();
					int k = 0;
					while (k+1 < ptrail.Count && k+1 < qtrail.Count && ptrail[k+1]==qtrail[k+1])
						k++;
					RootedTreeNode expected = ptrail[k];

					RootedTreeNode lca = tree.GetLowestCommonAncestor(p, q);

					Assert.AreSame(expected, lca);
				}
				
			}
			
		}

		[Test]
		public void TestBlockCutpointGraph()
		{
			var inputStream = new StreamReader(@"..\..\TestData\blockcutpointgraph.in");
			var parts = inputStream.ReadLine().Split(' ');
			int n = int.Parse(parts[0]), m = int.Parse(parts[1]);
			Graph g = new Graph(n);
			for (int i = 0; i < m; i++)
			{
				parts = inputStream.ReadLine().Split(' ');
				int a = int.Parse(parts[0]), b = int.Parse(parts[1]);
				var edge = new BlockCutpointSourceEdge(g.GetNode(a), g.GetNode(b));
				g.AddEdge(edge);
			}

			BlockCutpointNode[] map;
			var h = new BlockCutpointGraph().CreateBlockCutpointGraph(g, out map);

			Console.WriteLine("Mappings from G to block-cutpoint graph:");
			for (int i = 0; i < n; i++)
			{
				if (map[i].Type == VertexType.CutVertex)
					Console.WriteLine(i + " [cut-vertex] -> " + map[i].Index);
				else
					Console.WriteLine(i + " [block]      -> " + map[i].Index);
			}

			Console.WriteLine();

			Console.WriteLine("Connections in block-cutpoint graph:\n");
			foreach(BlockCutpointNode node in h.Nodes)
			{
				Console.Write(node.Index + " " + (node.Type == VertexType.CutVertex ? "[cut-vertex]" : "[block]     "));
				foreach (var e in node.GetAdjacentNodes())
				{
					Console.Write(" " + e.Index);
				}
				Console.WriteLine();
			}

			Console.WriteLine();

			Console.WriteLine("Mappings from block-cutpoint graph to G:"); // Blocks will also contain their cut-vertices
			foreach (BlockCutpointNode node in h.Nodes)
			{
				Console.Write(node.Index + (node.Type == VertexType.CutVertex ? "C" : "B") + ":");
				if (node.Type == VertexType.Block)
				{
					foreach(int v in node.Vertices)
					{
						Console.Write(" " + v);
					}
					Console.WriteLine();
				}
				else
				{
					Console.WriteLine(" " + node.CutVertexMap);
				}
			}

			Console.WriteLine();

			Console.WriteLine("Mappings from edges in G to blocks in H:"); // Each edge in G belongs to exactly one block in H
			foreach(BlockCutpointSourceEdge edge in g.Edges)
			{
				Console.WriteLine("Edge {0}-{1} lies in block {2}", edge.A.Index, edge.B.Index, edge.Block == null ? "null" : edge.Block.Index.ToString());
			}
		}
	}
}
