using System;
using System.Collections.Generic;
using System.Linq;

namespace Algorithms.Graphs
{
	public class RootedTree
	{
		public RootedTreeNode Root { get; private set; }
		private readonly RootedTreeNode[] RootedTreeNodes;
		private RootedTreeNode[,] lcaPreprocess;
		private readonly RootedTreeNode[] topologicalOrder; // root = topologicalOrder[0]

		public RootedTree(Graph graph, Node root)
		{
			if (graph == null || root == null || root.Graph != graph || graph.Edges.Count != graph.Nodes.Count - 1)
				throw new ArgumentException();
			var n = graph.Nodes.Count;
			RootedTreeNodes = new RootedTreeNode[n];

			// Build up tree structure without using recursion and create a topological ordering
			var level = new int[n];
			var order = new int[n];
			var parent = new int[n];
			level[root.Index] = 0;
			order[0] = root.Index;
			parent[root.Index] = -1;
			for (int i = 0, j = 1; i < n; i++)
			{
				if (i == j)
					throw new ArgumentException();
				foreach (var node in graph.GetNode(order[i]).GetAdjacentNodes())
				{
					var nodeIndex = node.Index;
					if (nodeIndex != root.Index && level[nodeIndex] == 0)
					{
						parent[nodeIndex] = order[i];
						level[nodeIndex] = level[order[i]] + 1;
						order[j++] = nodeIndex;
					}
				}
			}

			topologicalOrder = new RootedTreeNode[n];
			for (int i = n - 1; i >= 0; i--)
			{
				var nodeIndex = order[i];
				topologicalOrder[i] = new RootedTreeNode(this, graph.GetNode(nodeIndex), parent[nodeIndex], level[nodeIndex]);
			}

			Root = topologicalOrder[0];
		}

		public RootedTreeNode GetNode(int index)
		{
			return RootedTreeNodes[index];
		}

		internal void SetNode(RootedTreeNode treeNode)
		{
			RootedTreeNodes[treeNode.Node.Index] = treeNode;
		}

		private RootedTreeNode[,] PreprocessLCA()
		{
			int N = RootedTreeNodes.Length, M = 0;
			while ((1 << M) < N)
				M++;
			var p = new RootedTreeNode[N, M];

			for (int i = 0; i < N; i++)
				p[i, 0] = RootedTreeNodes[i].Parent;

			for (int j = 1; 1 << j < N; j++)
				for (int i = 0; i < N; i++)
					if (p[i, j - 1] != null)
						p[i, j] = p[p[i, j - 1].Node.Index, j - 1];

			return p;
		}

		public RootedTreeNode GetLowestCommonAncestor(RootedTreeNode p, RootedTreeNode q)
		{
			if (lcaPreprocess == null)
				lcaPreprocess = PreprocessLCA();

			if (p.Level < q.Level)
			{
				RootedTreeNode tmp = p;
				p = q;
				q = tmp;
			}

			int log;
			for (log = 1; 1 << log <= p.Level; log++) ;
			log--;

			for (int i = log; i >= 0; i--)
				if (p.Level - (1 << i) >= q.Level)
					p = lcaPreprocess[p.Node.Index, i];

			if (p == q)
				return p;

			for (int i = log; i >= 0; i--)
				if (lcaPreprocess[p.Node.Index, i] != null &&
					lcaPreprocess[p.Node.Index, i] != lcaPreprocess[q.Node.Index, i])
				{
					p = lcaPreprocess[p.Node.Index, i];
					q = lcaPreprocess[q.Node.Index, i];
				}

			return p.Parent;
		}
	}

	public class RootedTreeNode
	{
		private readonly int _parentIndex;

		public RootedTree Tree { get; private set; }
		public Node Node { get; private set; }
		public RootedTreeNode Parent { get { return _parentIndex < 0 ? null : Tree.GetNode(_parentIndex); } }
		public int Level { get; private set; }

		public RootedTreeNode[] Children { get; private set; }

		public RootedTreeNode(RootedTree tree, Node node, int parentIndex, int level)
		{
			Tree = tree;
			Node = node;
			Level = level;
			_parentIndex = parentIndex;

			var children = new List<RootedTreeNode>(node.Degree);
			foreach (Node child in Node.GetAdjacentNodes().Where(x => x.Index != parentIndex))
				children.Add(tree.GetNode(child.Index));
			Children = children.ToArray();

			tree.SetNode(this);
		}
	}
}
