using System.Collections.Generic;

namespace Algorithms.Graphs.Decompositions
{
	public class BlockCutpointNode : Node
	{
		public VertexType Type { get; set; }
		public List<int> Vertices { get; set; } // Vertice in this block (if Type == Block)
		public int CutVertexMap { get; set; }

		public BlockCutpointNode(Graph graph, int index)
			: base(graph, index)
		{
		}
	}
}