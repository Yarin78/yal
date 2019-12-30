package yarin.yal.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Graph
{
    public class Node
    {
        private int index;

        Graph getGraph() {
            return Graph.this;
        }

        public int getIndex() {
            return index;
        }

        public int getDegree() {
            return Graph.this.nodeEdges.get(index).size();
        }

        public Node(int index) {
            this.index = index;
        }

        public Collection<? extends Node> getAdjacentNodes() {
            // TODO: yield?
            List<Node> nodes = new ArrayList<Node>();

            for (Edge edge : Graph.this.nodeEdges.get(index)) {
                if (edge.getA() != this) {
                    nodes.add(edge.getA());
                }
                if (edge.getB() != this) {
                    nodes.add(edge.getB());
                }
            }

            return nodes;
        }

        @Override
        public String toString() {
            return Integer.toString(index);
        }

        public Collection<? extends Edge> getAdjacentEdges() {
            return Collections.unmodifiableCollection(Graph.this.nodeEdges.get(this.index));
        }
    }

    public class Edge {
        private Node a, b;

        public Node getA() {
            return a;
        }

        public Node getB() {
            return b;
        }

        public Edge(Node a, Node b)	{

            if (a == null || b == null || a.getGraph() != b.getGraph()) {
                throw new IllegalArgumentException();
            }
            this.a = a;
            this.b = b;
        }
    }

    public class ExplorableEdge extends Edge {
        public ExplorableEdge(Node a, Node b) {
            super(a, b);
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
    private List<List<Edge>> nodeEdges;
    private NodeFactory nodeFactory;

    public NodeFactory getNodeFactory() {
        return nodeFactory;
    }

    public void setNodeFactory(NodeFactory nodeFactory) {
        this.nodeFactory = nodeFactory;
    }

    public List<List<Edge>> getNodeEdges() {
        return nodeEdges;
    }

    public Graph() {
        this(new SimpleNodeFactory(), 0);
    }

    public Graph(int nodes) {
        this(new SimpleNodeFactory(), nodes);
    }

    public Graph(NodeFactory nodeFactory, int noNodes) {
        this.nodeFactory = nodeFactory;

        edges = new ArrayList<>();
        nodes = new ArrayList<>(noNodes);
        nodeEdges = new ArrayList<>(noNodes);
        for (int i = 0; i < noNodes; i++) {
            addNode();
        }
    }

    public void addEdge(int a, int b) {
        addEdge(new Edge(getNode(a), getNode(b)));
    }

    public void addEdge(Edge e) {
        if (e == null || e.getA().getGraph() != this)
            throw new IllegalArgumentException();
        edges.add(e);
        this.nodeEdges.get(e.getA().getIndex()).add(e);
        this.nodeEdges.get(e.getB().getIndex()).add(e);
    }

    public Node getNode(int index) {
        return nodes.get(index);
    }

    public int getNodeCount() {
        return nodes.size();
    }

    public Node addNode() {
        Node node = this.nodeFactory.createNode(this, getNodeCount());
        nodes.add(node);
        this.nodeEdges.add(new ArrayList<Edge>());
        return node;

    }

    public void addNode(Node node) {
        if (node.getGraph() != this || node.getIndex() != getNodeCount())
            throw new IllegalStateException();
        nodes.add(node);
        nodeEdges.add(new ArrayList<Edge>());
    }

    public Collection<? extends Edge> getEdges() {
        return Collections.unmodifiableCollection(edges);
    }

    public Collection<? extends Node> getNodes() {
        return Collections.unmodifiableCollection(nodes);
    }

    public static abstract class NodeFactory {
        public abstract Node createNode(Graph graph, int index);
    }

    public static class SimpleNodeFactory extends NodeFactory {
        @Override
        public Node createNode(Graph graph, int index) {
            return graph.new Node(index);
        }
    }
}

