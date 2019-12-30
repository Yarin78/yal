package yarin.yal.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

/**
 * Class that contains an algorithm that will create the block-cutpoint graph from a simple graph.
 *
 * <p>
 * A block-cutpoint graph containing the biconnected components of a graph G and their connections.
 * The graph is bipartite (and a tree), where one partition contains blocks of nodes and the other partition contains
 * cut-vertices. Each vertex that, when removed from G, will disconnect the graph is a cut-vertex. There will
 * be an edge in the block-cutpoint graph between a block and a cutpoint-vertex iff the vertex is in the block.
 * </p>
 * <p>
 * The run-time complexity of the algorithm is O(|E| + |V|).
 * </p>
 */
public class BlockCutpointGraph extends Graph {

    /**
     * Algorithm that creates a block-cutpoint graph.
     */
    private static class Algorithm {
        private int label, root;
        private int[] high, dfsNumber;
        private Node[] vmap;
        private Stack<Integer> vertexStack;
        private Graph inputGraph;
        private BlockCutpointGraph outputGraph;

        public BlockCutpointGraph create(Graph graph, Node[] map) {
            inputGraph = graph;

            high = new int[graph.getNodeCount()];
            dfsNumber = new int[graph.getNodeCount()];
            vertexStack = new Stack<Integer>();
            vmap = new Node[graph.getNodeCount()];
            outputGraph = new BlockCutpointGraph();
            label = 0;

            for (root = 0; root < graph.getNodeCount(); root++) {
                if (vmap[root] == null) {
                    dfs(root);
                }
            }

            if (map != null) {
                for (int i = 0; i < map.length; i++) {
                    map[i] = vmap[i];
                }
            }

            // Assign edges to their corresponding blocks
            for (Graph.Edge edge : graph.getEdges()) {
                if (!(edge instanceof BlockCutpointInputGraph.BlockCutpointSourceEdge)) {
                    break;
                }
                BlockCutpointInputGraph.BlockCutpointSourceEdge sourceEdge = (BlockCutpointInputGraph.BlockCutpointSourceEdge) edge;
                Node nodeA = map[edge.getA().getIndex()];
                Node nodeB = map[edge.getB().getIndex()];

                if (nodeA instanceof BlockNode) {
                    sourceEdge.setBlock((BlockNode) nodeA);
                } else if (nodeB instanceof BlockNode) {
                    sourceEdge.setBlock((BlockNode) nodeB);
                } else {
                    for (Node node : ((Collection<Node>) nodeA.getAdjacentNodes())) {
                        if (((BlockNode)node).vertices.contains(edge.getB().getIndex())) {
                            sourceEdge.setBlock((BlockNode) node);
                            break;
                        }
                    }
                }
            }

            return outputGraph;
        }

        private void dfs(int v) {
            dfsNumber[v] = high[v] = ++label;
            vertexStack.push(v);

            int lastW = -1;
            for (Graph.Edge currentEdge : inputGraph.getNodeEdges().get(v)) {
                Graph.ExplorableEdge explorableEdge = (Graph.ExplorableEdge) currentEdge;

                int w = currentEdge.getA().getIndex() + currentEdge.getB().getIndex() - v; // Opposite vertex in edge

                if (explorableEdge.isExplored()) {
                    continue;
                }
                explorableEdge.setExplored(true);

                if (lastW >= 0 && high[lastW] >= dfsNumber[v]) {
                    getBlock(v, lastW, false);
                }

                if (high[w] == 0) {
                    dfs(w);
                    high[v] = Math.min(high[v], high[w]);
                    lastW = w;
                } else {
                    high[v] = Math.min(high[v], dfsNumber[w]);
                    lastW = -1;
                }
            }

            if (lastW >= 0 && high[lastW] >= dfsNumber[v]) {
                getBlock(v, lastW, v == root);
            }
        }

        private void getBlock(final int v, int w, boolean last) {
            Node vcut = null;
            final BlockNode blockNode = outputGraph.new BlockNode(outputGraph.getNodeCount(), v);

            outputGraph.addNode(blockNode);

            if (vmap[v] != null && vmap[v] instanceof CutVertexNode) {
                vcut = vmap[v]; // Old cut-vertex
            } else if (!last) {
                Node cutVertexNode = outputGraph.new CutVertexNode(outputGraph.getNodeCount(), v);

                vmap[v] = vcut = cutVertexNode; // New cut-vertex
                outputGraph.addNode(cutVertexNode);
            }
            else if (vmap[v] == null) {
                vmap[v] = blockNode;
            }
            if (vcut != null) {
                outputGraph.addEdge(blockNode.getIndex(), vcut.getIndex());
            }

            int blockVertex;
            do {
                blockVertex = vertexStack.pop();
                blockNode.getVertices().add(blockVertex);

                if (vmap[blockVertex] != null && vmap[blockVertex] instanceof CutVertexNode) {
                    outputGraph.addEdge(vmap[blockVertex].getIndex(), blockNode.getIndex());
                } else {
                    vmap[blockVertex] = blockNode;
                }
            } while (blockVertex != w);
        }
    }

    /**
     * The block-cutpoint graph can only be created with the static methods below.
     */
    private BlockCutpointGraph() { }

    /**
     * Creates the block-cutpoint graph for a simple graph.
     *
     * The edges in the graph must be {@link ExplorableEdge}.
     * Use {@link BlockCutpointInputGraph} and {@link yarin.yal.graph.BlockCutpointInputGraph.BlockCutpointSourceEdge}
     * for edges to be marked in which block they belong.
     *
     * @param graph the input graph.
     * @return the block-cutpoint graph.
     */
    public static BlockCutpointGraph create(Graph graph)
    {
        return create(graph, null);
    }

    /**
     * Creates the block-cutpoint graph for a simple graph.
     *
     * The edges in the graph must be {@link ExplorableEdge}.
     * Use {@link BlockCutpointInputGraph} and {@link yarin.yal.graph.BlockCutpointInputGraph.BlockCutpointSourceEdge}
     * for edges to be marked in which block they belong.
     *
     * @param graph the input graph.
     * @param map  if not null, a mapping from the nodes in the input graph to it's corresponding node
     * in the block-cutpoint graph will be stored here. Should contain the same number of elements as nodes in graph.
     * @return the block-cutpoint graph.
     */
    public static BlockCutpointGraph create(Graph graph, Node[] map) {
        if (map != null && map.length != graph.getNodeCount()) {
            throw new IllegalArgumentException();
        }
        Algorithm algorithm = new Algorithm();
        return algorithm.create(graph, map);
    }

    public Collection<Node> getNodes() {
        return (Collection<Node>) super.getNodes();
    }

    public class BlockNode extends Node {
        private List<Integer> vertices;

        public List<Integer> getVertices() {
            return vertices;
        }

        public BlockNode(int index, int vertex) {
            super(index);

            vertices = new ArrayList<Integer>();
            vertices.add(vertex);
        }
    }

    public class CutVertexNode extends Node {
        private int cutVertexMap;

        public int getCutVertexMap() {
            return cutVertexMap;
        }

        public CutVertexNode(int index, int cutVertexMap) {
            super(index);

            this.cutVertexMap = cutVertexMap;
        }
    }
}
