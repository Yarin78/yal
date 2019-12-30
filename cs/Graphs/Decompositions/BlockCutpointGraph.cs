using System;
using System.Collections.Generic;

namespace Algorithms.Graphs.Decompositions
{
	/// <summary>
	/// Class that contains an algorithm that will create the block-cutpoint graph from a simple graph.
	/// </summary>
	/// <remarks>
	/// <para>
	/// A block-cutpoint graph containing the biconnected components of a graph G and their connections.
	/// The graph is bipartite (and a tree), where one partition contains blocks of nodes and the other partition contains
	/// cut-vertices. Each vertex that, when removed from G, will disconnect the graph is a cut-vertex. There will
	/// be an edge in the block-cutpoint graph between a block and a cutpoint-vertex iff the vertex is in the block.
	/// </para>
	/// <para>
	/// The run-time complexity of the algorithm is O(|E| + |V|).
	/// </para>
	/// </remarks>
	public class BlockCutpointGraph
	{
		private class BlockCutpointNodeFactory : NodeFactory
		{
			public override Node CreateNode(Graph graph, int index)
			{
				return new BlockCutpointNode(graph, index);
			}
		}

		private int _label, _root;
		private int[] _high, _dfsNumber;
		private BlockCutpointNode[] _vmap;
		private Stack<int> _vertexStack;
		private Graph _inputGraph;
		private Graph _outputGraph;

		/// <summary>
		/// Creates the block-cutpoint graph for a simple graph.
		/// </summary>
		/// <param name="graph">The input graph.</param>
		/// <returns>The block-cutpoint graph.</returns>
		/// <remarks>
		/// <para>
		/// The edges in the graph must be <see cref="ExplorableEdge"/>. 
		/// Use <see cref="BlockCutpointSourceEdge"/> for edges to be marked in which block the belong.
		/// </para>
		/// </remarks>
		public Graph CreateBlockCutpointGraph(Graph graph)
		{
			BlockCutpointNode[] map;
			return CreateBlockCutpointGraph(graph, out map);
		}

		/// <summary>
		/// Creates the block-cutpoint graph for a simple graph.
		/// </summary>
		/// <param name="graph">The input graph.</param>
		/// <param name="map">A mapping from the nodes in the input graph to it's corresponding node in the block-cutpoint graph.</param>
		/// <returns>The block-cutpoint graph.</returns>
		/// <remarks>
		/// <para>
		/// The edges in the graph must be <see cref="ExplorableEdge"/>. 
		/// Use <see cref="BlockCutpointSourceEdge"/> for edges to be marked in which block the belong.
		/// </para>
		/// <para>
		/// Vertices in <paramref name="graph"/> that are cut-vertices will <paramref name="map"/> to their cut-vertex node.
		/// </para>
		/// </remarks>
		public Graph CreateBlockCutpointGraph(Graph graph, out BlockCutpointNode[] map)
		{
			_inputGraph = graph;

			_high = new int[graph.NodeCount];
			_dfsNumber = new int[graph.NodeCount];
			_vertexStack = new Stack<int>();
			_vmap = new BlockCutpointNode[graph.NodeCount];
			_outputGraph = new Graph(new BlockCutpointNodeFactory(), 0);
			_label = 0;

			for (_root = 0; _root < graph.NodeCount; _root++)
			{
				if (_vmap[_root] == null)
				{
					Dfs(_root);
				}
			}

			map = _vmap;

			// Assign edges to their corresponding blocks
			foreach (var edge in graph.Edges)
			{
				var sourceEdge = edge as BlockCutpointSourceEdge;
				if (sourceEdge == null)
					break;
				var nodeA = map[edge.A.Index];
				var nodeB = map[edge.B.Index];

				if (nodeA.Type == VertexType.Block)
				{
					sourceEdge.Block = nodeA;
				}
				else if (nodeB.Type == VertexType.Block)
				{
					sourceEdge.Block = nodeB;
				}
				else
				{
					foreach (BlockCutpointNode node in nodeA.GetAdjacentNodes())
					{
						if (node.Vertices.Contains(edge.B.Index))
						{
							sourceEdge.Block = node;
							break;
						}
					}
				}
			}

			return _outputGraph;
		}

		private void Dfs(int v)
		{
			_dfsNumber[v] = _high[v] = ++_label;
			_vertexStack.Push(v);

			int lastW = -1;
			foreach(ExplorableEdge currentEdge in _inputGraph.NodeEdges[v])
			{
				int w = currentEdge.A.Index + currentEdge.B.Index - v; // Opposite vertex in edge

				if (currentEdge.Explored)
					continue;
				currentEdge.Explored = true;

				if (lastW >= 0 && _high[lastW] >= _dfsNumber[v])
				{
					GetBlock(v, lastW, false);
				}
				
				if (_high[w] == 0)
				{
					Dfs(w);
					_high[v] = Math.Min(_high[v], _high[w]);
					lastW = w;
				}
				else
				{
					_high[v] = Math.Min(_high[v], _dfsNumber[w]);
					lastW = -1;
				}
			}
			if (lastW >= 0 && _high[lastW] >= _dfsNumber[v])
			{
				GetBlock(v, lastW, v == _root);
			}
		}

		private void GetBlock(int v, int w, bool last)
		{
			BlockCutpointNode vcut = null;

			var blockNode = new BlockCutpointNode(_outputGraph, _outputGraph.NodeCount)
			                	{
			                		Type = VertexType.Block,
			                		CutVertexMap = -1
			                	};
			_outputGraph.AddNode(blockNode);

			if (_vmap[v] != null && _vmap[v].Type == VertexType.CutVertex)
			{
				vcut = _vmap[v]; // Old cut-vertex
			}
			else if (!last)
			{
				var cutVertexNode = new BlockCutpointNode(_outputGraph, _outputGraph.NodeCount)
				                    	{
				                    		Type = VertexType.CutVertex,
				                    		CutVertexMap = v
				                    	};
				
				_vmap[v] = vcut = cutVertexNode; // New cut-vertex
				_outputGraph.AddNode(cutVertexNode);
			}
			else if (_vmap[v] == null)
			{
				_vmap[v] = blockNode;
			}
			if (vcut != null)
			{
				_outputGraph.AddEdge(blockNode.Index, vcut.Index);
			}

			blockNode.Vertices = new List<int> { v };
			int blockVertex;
			do
			{
				blockVertex = _vertexStack.Pop();
				blockNode.Vertices.Add(blockVertex);

				if (_vmap[blockVertex] != null && _vmap[blockVertex].Type == VertexType.CutVertex)
				{
					_outputGraph.AddEdge(_vmap[blockVertex].Index, blockNode.Index);
				}
				else
				{
					_vmap[blockVertex] = blockNode;
				}
			} while (blockVertex != w);
		}
	}
}