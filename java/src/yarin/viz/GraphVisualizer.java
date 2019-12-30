package yarin.viz;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import yarin.yal.geometry.Circle;
import yarin.yal.geometry.LineD;
import yarin.yal.geometry.Point;
import yarin.yal.geometry.PointD;
import yarin.yal.graph.DirectedGraph;
import yarin.yal.graph.Graph;

public class GraphVisualizer {

  public static final int CANVAS_WIDTH = 800, CANVAS_HEIGHT = 600;
  public static final int NODE_RADIUS = 20, ARROW_SIZE = 5;

  private Random random = new Random(0);

  private Map<Object, Point> embedding = new HashMap<>();
  private List<Graph.Edge> undirected = new ArrayList<>();
  private List<DirectedGraph.Edge> directedEdges = new ArrayList<>();

  private Graph intGraph = new Graph();
  private Map<Object, Graph.Node> extToInt = new HashMap<>();
  private Map<Graph.Node, Object> intToExt = new HashMap<>();

  public void clearAll() {
    embedding.clear();
    undirected.clear();
    directedEdges.clear();
  }

  private Point newPosition() {
    return new Point(random.nextInt(CANVAS_WIDTH - 2 * NODE_RADIUS) + NODE_RADIUS,
                     random.nextInt(CANVAS_HEIGHT - 2 * NODE_RADIUS) + NODE_RADIUS);
  }

  public void addGraph(Graph g) {
    for (Graph.Node node : g.getNodes()) {
      embedding.put(node, newPosition());

      Graph.Node addedNode = intGraph.addNode();
      extToInt.put(node, addedNode);
      intToExt.put(addedNode, node);
    }

    undirected.addAll(g.getEdges());
    for (Graph.Edge edge : g.getEdges()) {
      intGraph.addEdge(intGraph.new Edge(extToInt.get(edge.getA()), extToInt.get(edge.getB())));
    }
  }

  public void addGraph(DirectedGraph g) {
    for (DirectedGraph.Node node : g.getNodes()) {
      embedding.put(node, newPosition());

      Graph.Node addedNode = intGraph.addNode();
      extToInt.put(node, addedNode);
      intToExt.put(addedNode, node);
    }

    directedEdges.addAll(g.getEdges());
    for (DirectedGraph.Edge edge : g.getEdges()) {
      intGraph.addEdge(intGraph.new Edge(extToInt.get(edge.getSrc()), extToInt.get(edge.getDest())));
    }
  }

  public void show() {
    GeoVisualizer viz = new GeoVisualizer(false);
    viz.setVisibilityArea(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);
    for (Map.Entry<Object, Point> entry : embedding.entrySet()) {
      viz.addCircle(new Circle(entry.getValue(), NODE_RADIUS), entry.getKey().toString());
    }
    for (Graph.Edge edge : undirected) {
      Point p1 = embedding.get(edge.getA());
      Point p2 = embedding.get(edge.getB());
      LineD line = new LineD(p1.x, p1.y, p2.x, p2.y);
      PointD ofs = PointD.mul(line.getUnitVector(), NODE_RADIUS);
      PointD p1d = PointD.add(new PointD(p1.x, p1.y), ofs);
      PointD p2d = PointD.sub(new PointD(p2.x, p2.y), ofs);
      viz.addLine(new LineD(p1d, p2d));
    }

    for (DirectedGraph.Edge edge : directedEdges) {
      Point p1 = embedding.get(edge.getSrc());
      Point p2 = embedding.get(edge.getDest());
      LineD line = new LineD(p1.x, p1.y, p2.x, p2.y);
      PointD ofs1 = PointD.mul(line.getUnitVector(), NODE_RADIUS);
      PointD ofs2 = PointD.mul(line.getUnitVector(), NODE_RADIUS + ARROW_SIZE * 0.65);
      PointD p1d = PointD.add(new PointD(p1.x, p1.y), ofs1);
      PointD p2d = PointD.sub(new PointD(p2.x, p2.y), ofs2);
      viz.addLine(new LineD(p1d, p2d), new GeoVisualizer.Style(Color.black, 1f, null, ARROW_SIZE));
    }

//    viz.addLine(new LineD(0, 0, CANVAS_WIDTH, 0));
//    viz.addLine(new LineD(0, CANVAS_HEIGHT, CANVAS_WIDTH, CANVAS_HEIGHT));
//    viz.addLine(new LineD(0, 0, 0, CANVAS_HEIGHT));
//    viz.addLine(new LineD(CANVAS_WIDTH, 0, CANVAS_WIDTH, CANVAS_HEIGHT));
    viz.show();
  }

  public void layout(ILayoutAlgorithm layoutAlgorithm) {
    Map<Graph.Node, Point> nodePointMap = layoutAlgorithm.doLayout(intGraph, CANVAS_WIDTH, CANVAS_HEIGHT);
    for (Map.Entry<Graph.Node, Point> entry : nodePointMap.entrySet()) {
      embedding.put(intToExt.get(entry.getKey()), entry.getValue());
    }
  }

  public Graph.Node getInternalNode(Object node) {
    return extToInt.get(node);
  }
}
