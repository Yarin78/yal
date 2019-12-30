package yarin.viz;

import yarin.yal.graph.DirectedGraph;
import yarin.yal.graph.Graph;

public class GraphMain {
  public static void main(String args[]) {
    GraphVisualizer viz = new GraphVisualizer();

    Graph g = new Graph(10);
    g.addEdge(5, 2);
    g.addEdge(5, 8);
    g.addEdge(2, 0);
    g.addEdge(2, 4);
    g.addEdge(8, 6);
    g.addEdge(8, 9);
    g.addEdge(0, 1);
    g.addEdge(4, 3);
    g.addEdge(6, 7);

    viz.addGraph(g);
    viz.layout(new BinaryTreeLayoutAlgorithm(viz.getInternalNode(g.getNode(5))));
    viz.show();
  }

  public static void main2(String args[]) {
    GraphVisualizer viz = new GraphVisualizer();

    DirectedGraph g = new DirectedGraph(8);
    g.addEdge(5,6);
    g.addEdge(6,7);
    g.addEdge(5,1);
    g.addEdge(1,0);
    g.addEdge(1,3);
    g.addEdge(3,2);
    g.addEdge(3,4);

    viz.addGraph(g);
    viz.layout(new LayeredLayoutAlgorithm(viz.getInternalNode(g.getNode(6))));
    viz.show();


  }
}
