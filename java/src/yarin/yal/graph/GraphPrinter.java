package yarin.yal.graph;

import java.io.OutputStream;
import java.io.PrintWriter;

public class GraphPrinter {

    public static void show(OutputStream os, DirectedGraph dg) {
        PrintWriter pw = new PrintWriter(os);
        pw.println(dg.getNodeCount() + " nodes");
        for (DirectedGraph.Node node : dg.getNodes()) {
            pw.print(node.getIndex() + ": Edges to ");
            for (DirectedGraph.Edge edge : node.getOutgoingEdges()) {
                pw.print(edge.getDest().getIndex() + " ");
            }
            pw.println();
        }
        pw.flush();
    }
}
