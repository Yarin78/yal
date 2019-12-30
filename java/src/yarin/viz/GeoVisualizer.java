package yarin.viz;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.*;

import yarin.yal.geometry.Circle;
import yarin.yal.geometry.Line;
import yarin.yal.geometry.LineD;
import yarin.yal.geometry.Point;
import yarin.yal.geometry.PointD;
import yarin.yal.geometry.Polygon;
import yarin.yal.geometry.PolygonD;

public class GeoVisualizer {

    public static class Style {

        public Color color;
        public float width;
        public String label;
        public int arrowSize;

        public Style(Color color, float width) {
            this(color, width, null);
        }

        public Style(Color color, float width, String label) { this(color, width, label, 0); }

        public Style(Color color, float width, String label, int arrowSize) {
            this.color = color;
            this.width = width;
            this.label = label;
            this.arrowSize = arrowSize;
        }
    }

    private final boolean autoScale;
    private double xscale, yscale;
    private int xofs, yofs;

    private LineD visibilityArea = new LineD(0, 0, 400, 600);
    private Map<LineD, Style> lines = new LinkedHashMap<>();
    private Map<PolygonD, Style> polys = new LinkedHashMap<>();
    private Map<PointD, Style> points = new LinkedHashMap<>();
    private Map<Circle, Style> circles = new LinkedHashMap<>();
    private Map<LineD, Style> rects = new LinkedHashMap<>();

    public GeoVisualizer() {
        this(true);
    }

    public GeoVisualizer(boolean autoScale) {
        this.autoScale = autoScale;
    }

    public void setVisibilityArea(int minx, int miny, int maxx, int maxy) {
        if (autoScale) {
            throw new RuntimeException("Autoscale must be disabled");
        }
        visibilityArea = new LineD(minx, miny, maxx, maxy);
    }

    public void clearAll() {
        lines.clear();
        polys.clear();
        points.clear();
        circles.clear();
    }

    public void addLine(Line line) {
        addLine(line, Color.black, 1);
    }

    public void addLine(Line line, Color color, float width) {
        addLine(new LineD(line.a.x, line.a.y, line.b.x, line.b.y), color, width);
    }

    public void addLine(LineD line) {
        addLine(line, Color.black, 1);
    }

    public void addLine(LineD line, Color color, float width) {
        addLine(line, new Style(color, width));
    }

    public void addLine(LineD line, Style style) {
        lines.put(line, style);
    }

    public void addPoly(Polygon poly) {
        addPoly(poly, Color.black, 1);
    }

    public void addPoly(Polygon poly, Color color, float width) {
        ArrayList<PointD> points = new ArrayList<PointD>(poly.points.size());
        for (Point point : poly.points) {
            points.add(new PointD(point.x, point.y));
        }
        polys.put(new PolygonD(points), new Style(color, width));
    }

    public void addPoly(PolygonD poly) {
        addPoly(poly, Color.black, 1);
    }

    public void addPoly(PolygonD poly, Color color, float width) {
        polys.put(poly, new Style(color, width));
    }

    public void addPoint(Point point) {
        addPoint(point, Color.black, 1);
    }

    public void addPoint(Point point, Color color, float width) {
        addPoint(new PointD(point.x, point.y), color, width);
    }

    public void addPoint(PointD point) {
        addPoint(point, Color.black, 1);
    }

    public void addPoint(PointD point, Color color, float width) {
        points.put(point, new Style(color, width));
    }

    public void addCircle(Circle circle) {
        addCircle(circle, null);
    }

    public void addCircle(Circle circle, String label) {
        addCircle(circle, label, Color.black, 1);
    }

    public void addCircle(Circle circle, String label, Color color, float width) {
        circles.put(circle, new Style(color, width, label));
    }

    public void addRect(LineD line, Color fillColor) {
        rects.put(line, new Style(fillColor, 0, ""));
    }

    public void removeLine(Line line) {
        lines.remove(line);
    }

    public void removePoly(Polygon poly) {
        polys.remove(poly);
    }

    public void removePoint(Point point) { points.remove(point); }

    public void removeCircle(Circle circle) { circles.remove(circle); }

    public void show() {
        Canvas canvas = new Canvas();

        canvas.setModal(true);
        canvas.setVisible(true);
    }


    private class Canvas extends JDialog {
        public Canvas() {
            this((int) (visibilityArea.b.x - visibilityArea.a.x), (int)(visibilityArea.b.y - visibilityArea.a.y));
        }

        public Canvas(int width, int height) {
            initUI();

            setSize(width, height);
            setLocationRelativeTo(null);

            addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {
                    setVisible(false);
                    dispose();
                }

                @Override
                public void keyPressed(KeyEvent e) {

                }

                @Override
                public void keyReleased(KeyEvent e) {

                }
            });
        }

        class Surface extends JPanel {

            private void doDrawing(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

                if (autoScale) {
                    findScale();
                } else {
                    setScale(visibilityArea.a.x, visibilityArea.a.y, visibilityArea.b.x, visibilityArea.b.y);
                }

                for (Map.Entry<LineD, Style> entry : rects.entrySet()) {
                    LineD rect = entry.getKey();
                    Style style = entry.getValue();

                    int x1 = (int) Math.round(rect.a.x * xscale + xofs);
                    int y1 = (int) Math.round(rect.a.y * yscale + yofs);
                    int x2 = (int) Math.round(rect.b.x * xscale + xofs);
                    int y2 = (int) Math.round(rect.b.y * yscale + yofs);
                    g2d.setPaint(style.color);
                    g2d.fillRect(Math.min(x1, x2), Math.min(y1, y2), Math.abs(x2 - x1), Math.abs(y2 - y1));
                }

                for (Map.Entry<PolygonD, Style> entry : polys.entrySet()) {
                    PolygonD poly = entry.getKey();
                    Style style = entry.getValue();
                    g2d.setColor(style.color);
                    g2d.setStroke(new BasicStroke(style.width));
                    int[] polyx = new int[poly.points.size()];
                    int[] polyy = new int[poly.points.size()];
                    for (int i = 0; i < polyx.length; i++) {
                        polyx[i] = (int) Math.round(poly.points.get(i).x * xscale + xofs);
                        polyy[i] = (int) Math.round(poly.points.get(i).y * yscale + yofs);
                    }

                    g2d.drawPolygon(polyx, polyy, polyx.length);
                }

                for (Map.Entry<LineD, Style> entry : lines.entrySet()) {
                    LineD line = entry.getKey();
                    Style style = entry.getValue();
                    g2d.setColor(style.color);
                    g2d.setStroke(new BasicStroke(style.width));
                    int x1 = (int) Math.round(line.a.x * xscale + xofs);
                    int y1 = (int) Math.round(line.a.y * yscale + yofs);
                    int x2 = (int) Math.round(line.b.x * xscale + xofs);
                    int y2 = (int) Math.round(line.b.y * yscale + yofs);
                    g2d.drawLine(x1, y1, x2, y2);
                    if (style.arrowSize > 0) {
                        AffineTransform tx = new AffineTransform();
                        tx.setToIdentity();
                        double angle = Math.atan2(y2-y1, x2-x1);
                        tx.translate(x2, y2);
                        tx.rotate((angle - Math.PI / 2d));
                        g2d.setTransform(tx);
                        java.awt.Polygon arrowHead = new java.awt.Polygon(
                            new int[]{0, -style.arrowSize, style.arrowSize},
                            new int[]{style.arrowSize, -style.arrowSize, -style.arrowSize},
                            3);

                        g2d.fill(arrowHead);
                        g2d.setTransform(new AffineTransform());
                    }
                }

                for (Map.Entry<PointD, Style> entry : points.entrySet()) {
                    PointD point = entry.getKey();
                    Style style = entry.getValue();
                    g2d.setColor(style.color);
                    g2d.setStroke(new BasicStroke(style.width));
                    g2d.drawLine(
                        (int) Math.round(point.x * xscale + xofs),
                        (int) Math.round(point.y * yscale + yofs),
                        (int) Math.round(point.x * xscale + xofs),
                        (int) Math.round(point.y * yscale + yofs));
                }

                for (Map.Entry<Circle, Style> entry : circles.entrySet()) {
                    Circle circle = entry.getKey();
                    Style style = entry.getValue();
                    double x = circle.getCenter().x, y = circle.getCenter().y, r = circle.getRadius();

                    g2d.setColor(style.color);
                    g2d.setStroke(new BasicStroke(style.width));
                    g2d.drawOval(
                        (int) Math.round((x - r) * xscale + xofs),
                        (int) Math.round((y + r) * yscale + yofs),
                        (int) Math.round(2 * r * xscale),
                        (int) Math.round(2 * r * Math.abs(yscale)));
                    g2d.setFont(new Font("Serif", Font.BOLD, 16));
                    FontMetrics fm = g2d.getFontMetrics();
                    int w = fm.stringWidth(style.label), h = fm.getAscent();
                    g2d.drawString(style.label,
                                   (int) Math.round(x * xscale + xofs - w / 2),
                                   (int) Math.round(y * yscale + yofs + h / 2));
                }
            }

            private void findScale() {
                ArrayList<Double> xlist = new ArrayList<>();
                ArrayList<Double> ylist = new ArrayList<>();
                for (LineD line : lines.keySet()) {
                    xlist.add(line.a.x);
                    xlist.add(line.b.x);
                    ylist.add(line.a.y);
                    ylist.add(line.b.y);
                }
                for (PolygonD poly : polys.keySet()) {
                    for (PointD p : poly.points) {
                        xlist.add(p.x);
                        ylist.add(p.y);
                    }
                }
                for (PointD point : points.keySet()) {
                    xlist.add(point.x);
                    ylist.add(point.y);
                }
                for (Circle circle : circles.keySet()) {
                    double x = circle.getCenter().x, y = circle.getCenter().y, r = circle.getRadius();
                    xlist.add(x - r);
                    xlist.add(x + r);
                    ylist.add(y - r);
                    ylist.add(y + r);
                }

                if (xlist.size() == 0) {
                    return;
                }

                double xmin = xlist.get(0), xmax = xlist.get(0);
                double ymin = ylist.get(0), ymax = ylist.get(0);

                for (double x : xlist) {
                    xmin = Math.min(xmin, x);
                    xmax = Math.max(xmax, x);
                }
                for (double y : ylist) {
                    ymin = Math.min(ymin, y);
                    ymax = Math.max(ymax, y);
                }

                setScale(xmin, ymin, xmax, ymax);
            }

            private void setScale(double xmin, double ymin, double xmax, double ymax) {
                Dimension size = getSize();
                Insets insets = getInsets();

                int margin = 30;
                int w = size.width - insets.left - insets.right - margin;
                int h = size.height - insets.top - insets.bottom - margin;

                xscale = yscale = Math.min(w / (xmax - xmin), h / (ymax - ymin));
                yscale = -yscale;

                xofs = (int) (Math.round(-xmin * xscale) + margin / 2);
                yofs = (int) (Math.round(-ymax * yscale) + margin / 2);
            }

            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                doDrawing(g);
            }
        }

        public void initUI() {
            setTitle("Visualization");
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);


            add(new Surface());
        }
    }


}
