package yarin.yal.graph;

import java.util.ArrayList;
import java.util.Arrays;

public class RootedTree {
    private final Node root;
    private final Node[] rootedTreeNodes;
    private final Node[] topologicalOrder; // root = topologicalOrder[0]
    private Node[][] lcaPreprocess;

    public RootedTree(Graph.Node root) {
        if (root == null) {
            throw new IllegalArgumentException();
        }
        Graph graph = root.getGraph();
        if (graph.getEdges().size() != graph.getNodes().size() - 1) {
            throw new IllegalArgumentException();
        }
        int n = graph.getNodes().size();
        rootedTreeNodes = new Node[n];

        // Build up tree structure without using recursion and create a topological ordering
        int[] level = new int[n];
        int[] order = new int[n];
        int[] parent = new int[n];
        level[root.getIndex()] = 0;
        order[0] = root.getIndex();
        parent[root.getIndex()] = -1;
        for (int i = 0, j = 1; i < n; i++) {
            if (i == j) {
                throw new IllegalArgumentException();
            }
            for (Graph.Node node : graph.getNode(order[i]).getAdjacentNodes()) {
                int nodeIndex = node.getIndex();
                if (nodeIndex != root.getIndex() && level[nodeIndex] == 0) {
                    parent[nodeIndex] = order[i];
                    level[nodeIndex] = level[order[i]] + 1;
                    order[j++] = nodeIndex;
                }
            }
        }

        topologicalOrder = new Node[n];
        for (int i = n - 1; i >= 0; i--) {
            int nodeIndex = order[i];
            topologicalOrder[i] = new Node(graph.getNode(nodeIndex), parent[nodeIndex], level[nodeIndex]);
        }

        this.root = topologicalOrder[0];
    }

    public Node getRoot() {
        return this.root;
    }

    public Node getNode(int index) {
        return rootedTreeNodes[index];
    }

    private void setNode(Node treeNode) {
        rootedTreeNodes[treeNode.getNode().getIndex()] = treeNode;
    }

    private Node[][] preprocessLCA() {
        int N = rootedTreeNodes.length, M = 0;
        while ((1 << M) < N) {
            M++;
        }
        Node[][] p = new Node[N][M];

        for (int i = 0; i < N; i++) {
            p[i][0] = rootedTreeNodes[i].getParent();
        }

        for (int j = 1; 1 << j < N; j++) {
            for (int i = 0; i < N; i++) {
                if (p[i][j - 1] != null) {
                    p[i][j] = p[p[i][j - 1].getNode().getIndex()][j - 1];
                }
            }
        }

        return p;
    }

    public Node getLowestCommonAncestor(Node p, Node q) {
        if (lcaPreprocess == null) {
            lcaPreprocess = preprocessLCA();
        }

        if (p.getLevel() < q.getLevel()) {
            Node tmp = p;
            p = q;
            q = tmp;
        }

        int log;
        for (log = 1; 1 << log <= p.getLevel(); log++) ;
        log--;

        for (int i = log; i >= 0; i--) {
            if (p.getLevel() - (1 << i) >= q.getLevel()) {
                p = lcaPreprocess[p.getNode().getIndex()][i];
            }
        }

        if (p == q) {
            return p;
        }

        for (int i = log; i >= 0; i--) {
            if (lcaPreprocess[p.getNode().getIndex()][i] != null &&
                lcaPreprocess[p.getNode().getIndex()][i] != lcaPreprocess[q.getNode().getIndex()][i])
            {
                p = lcaPreprocess[p.getNode().getIndex()][i];
                q = lcaPreprocess[q.getNode().getIndex()][i];
            }
        }

        return p.getParent();
    }

    public class Node {
        private final Graph.Node node;
        private final Node[] children;
        private final int parentIndex;
        private final int level;

        public Graph.Node getNode() {
            return this.node;
        }

        public Node getParent() {
            return parentIndex < 0 ? null : RootedTree.this.getNode(parentIndex);
        }

        public int getLevel() {
            return this.level;
        }

        public Node[] getChildren() {
            return this.children;
        }

        public Node(Graph.Node node, int parentIndex, int level) {
            this.node = node;
            this.level = level;
            this.parentIndex = parentIndex;

            ArrayList<Node> children = new ArrayList<Node>(node.getDegree());
            for (Graph.Node child : node.getAdjacentNodes()) {
                if (child.getIndex() != parentIndex) {
                    children.add(RootedTree.this.getNode(child.getIndex()));
                }
            }
            this.children = children.toArray(new Node[0]);

            RootedTree.this.setNode(this);
        }

        @Override
        public String toString() {
            int[] children = new int[this.children.length];
            for (int i = 0; i < this.children.length; i++) {
                children[i] = this.children[i].getNode().getIndex();
            }
            return String.format("[index=%s; parent=%d; children={%s}]",
                                 node.getIndex(), parentIndex, Arrays.toString(children));
        }
    }
}
