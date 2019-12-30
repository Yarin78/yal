package yarin.yal.graph;

import java.util.Collection;

public class BlockCutpointInputGraph extends Graph {
    public BlockCutpointInputGraph() {
    }

    public BlockCutpointInputGraph(int nodes) {
        super(nodes);
    }

    public BlockCutpointInputGraph(NodeFactory nodeFactory, int noNodes) {
        super(nodeFactory, noNodes);
    }

    @Override
    public Collection<BlockCutpointSourceEdge> getEdges() {
        return (Collection<BlockCutpointSourceEdge>) super.getEdges();
    }

    public class BlockCutpointSourceEdge extends Graph.ExplorableEdge {
        public BlockCutpointSourceEdge(Node a, Node b) {
            super(a, b);
        }

        private BlockCutpointGraph.BlockNode block;

        public BlockCutpointGraph.BlockNode getBlock() {
            return block;
        }

        public void setBlock(BlockCutpointGraph.BlockNode block) {
            this.block = block;
        }

        @Override
        public String toString() {
            return getA().getIndex() + "-" + getB().getIndex();
        }
    }
}
