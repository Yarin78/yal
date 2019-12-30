using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;

namespace Algorithms.Graphs
{
	public abstract class NodeFactory
	{
		public abstract Node CreateNode(Graph graph, int index);
	}
	
	public class SimpleNodeFactory : NodeFactory
	{
		public override Node CreateNode(Graph graph, int index)
		{
			return new Node(graph, index);
		}
	}
	
	public class Graph
	{
		private readonly List<Node> _nodes;
		private readonly List<Edge> _edges;
		internal List<List<Edge>> NodeEdges { get; private set; }

		private NodeFactory NodeFactory { get; set; }
        
		public Graph() : this(new SimpleNodeFactory(), 0)
		{
		}

		public Graph(int nodes) : this(new SimpleNodeFactory(), nodes)
		{
		}

		public Graph(NodeFactory nodeFactory, int noNodes)
		{
			NodeFactory = nodeFactory;

			_edges = new List<Edge>();
			_nodes = new List<Node>(noNodes);
			NodeEdges = new List<List<Edge>>(noNodes);
			for (int i = 0; i < noNodes; i++)
			{
				AddNode();
			}
		}

		public void AddEdge(int a, int b)
		{
			AddEdge(new Edge(GetNode(a), GetNode(b)));
		}

		public void AddEdge(Edge e)
		{
			if (e == null || e.A.Graph != this)
				throw new ArgumentException();
			_edges.Add(e);
			NodeEdges[e.A.Index].Add(e);
			NodeEdges[e.B.Index].Add(e);
		}

		public Node GetNode(int index)
		{
			return _nodes[index];
		}

		public int NodeCount
		{
			get { return _nodes.Count; }
		}

		public void AddNode()
		{
			_nodes.Add(NodeFactory.CreateNode(this, NodeCount));
			NodeEdges.Add(new List<Edge>());
		}

		public void AddNode(Node node)
		{
			if (node.Graph != this || node.Index != NodeCount)
				throw new InvalidOperationException();
			_nodes.Add(node);
			NodeEdges.Add(new List<Edge>());
		}

		public ICollection<Edge> Edges
		{
			get { return new ReadOnlyCollection<Edge>(_edges); }
		}

		public ICollection<Node> Nodes
		{
			get { return new ReadOnlyCollection<Node>(_nodes); }
		}
	}

	public class Node
	{
		public Graph Graph { get; private set; }
		public int Index { get; private set; }
		public int Degree
		{
			get { return Graph.NodeEdges[Index].Count; }
		}

		public Node(Graph graph, int index)
		{
			Graph = graph;
			Index = index;
		}

		public IEnumerable<Node> GetAdjacentNodes()
		{
			foreach (Edge edge in Graph.NodeEdges[Index])
			{
				if (edge.A != this)
					yield return edge.A;
				if (edge.B != this)
					yield return edge.B;
			}
		}

		public IEnumerable<Edge> GetAdjacentEdges()
		{
			return new ReadOnlyCollection<Edge>(Graph.NodeEdges[Index]);
		}
	}

	public class Edge
	{
		public Node A { get; set; }
		public Node B { get; set; }
		
		public Edge(Node a, Node b)
		{
			if (a == null || b == null || a.Graph != b.Graph)
				throw new ArgumentException();
			A = a;
			B = b;
		}
	}

	public class ExplorableEdge : Edge
	{
		public ExplorableEdge(Node a, Node b) : base(a, b)
		{
		}

		public bool Explored { get; set; }
	}
}