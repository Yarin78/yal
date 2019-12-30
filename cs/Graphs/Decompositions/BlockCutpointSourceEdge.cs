namespace Algorithms.Graphs.Decompositions
{
	public class BlockCutpointSourceEdge : ExplorableEdge
	{
		public BlockCutpointSourceEdge(Node a, Node b)
			: base(a, b)
		{
		}

		public BlockCutpointNode Block { get; set; }
		
		public override string ToString()
		{
			return A.Index + "-" + B.Index;
		}
	}
}