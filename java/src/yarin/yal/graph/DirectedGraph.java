package yarin.yal.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DirectedGraph {
    public class Node
    {
        private int index;

        DirectedGraph getGraph() {
            return DirectedGraph.this;
        }

        public int getIndex() {
            return index;
        }

        public int getOutDegree() {
            return DirectedGraph.this.nodeOutEdges.get(index).size();
        }
        public int getInDegree() { return DirectedGraph.this.nodeInEdges.get(index).size(); }

        public Node(int index) {
            this.index = index;
        }

        public Collection<? extends Node> getOutgoingNodes() {
            // TODO: yield?
            List<Node> nodes = new ArrayList<Node>();

            for (Edge edge : DirectedGraph.this.nodeOutEdges.get(index)) {
                nodes.add(edge.getDest());
            }

            return nodes;
        }

        public Collection<? extends Edge> getOutgoingEdges() {
            return Collections.unmodifiableCollection(DirectedGraph.this.nodeOutEdges.get(this.index));
        }

        public Collection<? extends Edge> getIncomingEdges() {
            return Collections.unmodifiableCollection(DirectedGraph.this.nodeInEdges.get(this.index));
        }

        @Override
        public String toString() {
            return Integer.toString(index);
        }
    }

    public class Edge {
        private Node src, dest;

        public Node getSrc() {
            return src;
        }

        public Node getDest() {
            return dest;
        }

        public Edge(Node a, Node b)	{
            assert a != null && b != null && a.getGraph() == b.getGraph();
            this.src = a;
            this.dest = b;
        }
    }

    public class WeightedEdge extends Edge {
        private int weight;

        public int getWeight() {
            return weight;
        }

        public WeightedEdge(Node a, Node b, int weight) {
            super(a, b);
            this.weight = weight;
        }
    }

    public class ExplorableEdge extends Edge {
        public ExplorableEdge(Node src, Node b) {
            super(src, b);
        }

        private boolean explored;

        public boolean isExplored() {
            return explored;
        }

        public void setExplored(boolean explored) {
            this.explored = explored;
        }
    }

    private final List<Node> nodes;
    private final List<Edge> edges;
    private List<List<Edge>> nodeOutEdges; // outgoing edges from a node
    private List<List<Edge>> nodeInEdges; // incoming edges from a node
    private NodeFactory nodeFactory;

    public NodeFactory getNodeFactory() {
        return nodeFactory;
    }

    public void setNodeFactory(NodeFactory nodeFactory) {
        this.nodeFactory = nodeFactory;
    }

    public List<List<Edge>> getNodeOutEdges() {
        return nodeOutEdges;
    }

    public List<List<Edge>> getNodeInEdges() {
        return nodeInEdges;
    }

    public DirectedGraph() {
        this(new SimpleNodeFactory(), 0);
    }

    public DirectedGraph(int nodes) {
        this(new SimpleNodeFactory(), nodes);
    }

    public DirectedGraph(NodeFactory nodeFactory, int noNodes) {
        this.nodeFactory = nodeFactory;

        edges = new ArrayList<>();
        nodes = new ArrayList<>(noNodes);
        nodeOutEdges = new ArrayList<>(noNodes);
        nodeInEdges = new ArrayList<>(noNodes);
        for (int i = 0; i < noNodes; i++) {
            addNode();
        }
    }

    public void addEdge(int a, int b) {
        addEdge(new Edge(getNode(a), getNode(b)));
    }

    public void addEdge(int a, int b, int weight) {
        addEdge(new WeightedEdge(getNode(a), getNode(b), weight));
    }

    public void addEdge(Edge e) {
        assert e != null && e.src.getGraph() == this && e.dest.getGraph() == this;
        edges.add(e);
        this.nodeOutEdges.get(e.getSrc().getIndex()).add(e);
        this.nodeInEdges.get(e.getDest().getIndex()).add(e);
    }

    public Node getNode(int index) {
        return nodes.get(index);
    }

    public int getNodeCount() {
        return nodes.size();
    }

    public void addNode() {
        nodes.add(this.nodeFactory.createNode(this, getNodeCount()));
        this.nodeOutEdges.add(new ArrayList<Edge>());
        this.nodeInEdges.add(new ArrayList<Edge>());
    }

    public void addNode(Node node) {
        assert node.getGraph() == this && node.getIndex() == getNodeCount();
        nodes.add(node);
        nodeOutEdges.add(new ArrayList<Edge>());
        nodeInEdges.add(new ArrayList<Edge>());
    }

    public Collection<? extends Edge> getEdges() {
        return Collections.unmodifiableCollection(edges);
    }

    public Collection<? extends Node> getNodes() {
        return Collections.unmodifiableCollection(nodes);
    }

    public static abstract class NodeFactory {
        public abstract Node createNode(DirectedGraph graph, int index);
    }

    public static class SimpleNodeFactory extends NodeFactory {
        @Override
        public Node createNode(DirectedGraph graph, int index) {
            return graph.new Node(index);
        }
    }
}
