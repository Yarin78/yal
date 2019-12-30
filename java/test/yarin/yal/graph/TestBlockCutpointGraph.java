package yarin.yal.graph;

import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Scanner;

public class TestBlockCutpointGraph {

    @Test
    public void testBlockCutpointGraph() throws FileNotFoundException {
        InputStream inputStream = TestBlockCutpointGraph.class.getResourceAsStream("blockcutpointgraph.in");
        Scanner scanner = new Scanner(inputStream);

        int n = scanner.nextInt(), m = scanner.nextInt();
        BlockCutpointInputGraph g = new BlockCutpointInputGraph(n);
        for (int i = 0; i < m; i++) {
            int a = scanner.nextInt(), b = scanner.nextInt();
            Graph.Edge edge = g.new BlockCutpointSourceEdge(g.getNode(a), g.getNode(b));
            g.addEdge(edge);
        }

        BlockCutpointGraph.Node[] map = new BlockCutpointGraph.Node[g.getNodeCount()];
        BlockCutpointGraph h = BlockCutpointGraph.create(g, map);

        System.out.println("Mappings from G to block-cutpoint graph:");
        for (int i = 0; i < n; i++) {
            if (map[i] instanceof BlockCutpointGraph.CutVertexNode)
                System.out.println(i + " [cut-vertex] -> " + map[i].getIndex());
            else
                System.out.println(i + " [block]      -> " + map[i].getIndex());
        }

        System.out.println();

        System.out.println("Connections in block-cutpoint graph:\n");
        for (BlockCutpointGraph.Node node : h.getNodes()) {
            System.out.print(node.getIndex() + " " + (node instanceof BlockCutpointGraph.CutVertexNode ? "[cut-vertex]" : "[block]     "));
            for (Graph.Node e : node.getAdjacentNodes()) {
                System.out.print(" " + e.getIndex());
            }
            System.out.println();
        }

        System.out.println();

        System.out.println("Mappings from block-cutpoint graph to G:"); // Blocks will also contain their cut-vertices
        for (BlockCutpointGraph.Node node : h.getNodes()) {
            System.out.print(node.getIndex() + (node instanceof BlockCutpointGraph.CutVertexNode ? "C" : "B") + ":");
            if (node instanceof BlockCutpointGraph.BlockNode) {
                for (int v : ((BlockCutpointGraph.BlockNode) node).getVertices()) {
                    System.out.print(" " + v);
                }
                System.out.println();
            }
            else {
                System.out.println(" " + ((BlockCutpointGraph.CutVertexNode) node).getCutVertexMap());
            }
        }

        System.out.println();

        System.out.println("Mappings from edges in G to blocks in H:"); // Each edge in G belongs to exactly one block in H
        for (BlockCutpointInputGraph.BlockCutpointSourceEdge edge : g.getEdges()) {
            System.out.println(String.format("Edge %d-%d lies in block %s", edge.getA().getIndex(), edge.getB().getIndex(), edge.getBlock() == null ? "null" : Integer.toString(edge.getBlock().getIndex())));
        }
    }
}
