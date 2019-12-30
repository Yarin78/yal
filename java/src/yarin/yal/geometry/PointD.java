package yarin.yal.geometry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PointD implements Comparable<PointD> {
    public double x, y;

    private static final double EPS = 1e-9;

    public PointD()  { this(0.0,0.0); }
    public PointD(double x, double y) { this.x = x; this.y = y; }
    public static PointD add(PointD p1, PointD p2) { return new PointD(p1.x+p2.x, p1.y+p2.y); }
    public static PointD sub(PointD p1, PointD p2) { return new PointD(p1.x-p2.x, p1.y-p2.y); }
    public static PointD mul(PointD p1, double scalar) { return new PointD(p1.x*scalar, p1.y*scalar); }
    public static PointD div(PointD p1, double scalar) { return new PointD(p1.x/scalar, p1.y/scalar); }
    public static double det(PointD a, PointD b) { return a.x*b.y-a.y*b.x; }
    public static double dot(PointD a, PointD b) { return a.x*b.x+a.y*b.y; }
    public static double cross(PointD a, PointD b, PointD c) { return det(sub(b, a), sub(c, a)); }
    public PointD rotate(double theta) {
        return new PointD(Math.cos(theta) * x - Math.sin(theta) * y, Math.sin(theta) * x + Math.cos(theta) * y);
    }
    public double getNormSqr() { return x*x+y*y; }
    public int compareTo(PointD other) {
        PointD p = other;
        return almostZero(x - p.x) ? Double.compare(y, p.y) : Double.compare(x, p.x);
    }

    @Override
    public boolean equals(Object obj) { return x==((PointD)obj).x && y==((PointD)obj).y; }

    public int hashCode() { return new Double(x).hashCode() ^ new Double(y).hashCode(); }

    public static boolean almostZero(double x) { return Math.abs(x) < EPS; }

    @Override
    public String toString() { return String.format("(%f, %f)", x, y); }

    public boolean liesOn(LineD line) {
        return PointD.almostZero(PointD.cross(line.a,line.b,this));
    }

    public boolean liesOnSegment(LineD segment) {
        return liesOn(segment) && PointD.dot(PointD.sub(segment.a, this), PointD.sub(this,segment.b))>=0;
    }

    /**
     * Gets the distance between the point and a line.
     */
    public double distance(LineD line)	{
        PointD v = PointD.sub(line.b, line.a), u = PointD.sub(this, line.a);
        double d = PointD.dot(v,u) / v.getNormSqr();
        return Math.sqrt(Math.pow(u.x-v.x*d,2) + Math.pow(u.y-v.y*d,2));
    }

    /**
     * Gets the distance between the point and a line segment.
     */
    public double distanceSegment(LineD segment) {
        PointD v = PointD.sub(segment.b, segment.a), u = PointD.sub(this, segment.a);
        double d = PointD.dot(v,u) / v.getNormSqr();
        if (d > 0 && d < 1) {
            return Math.sqrt(Math.pow(u.x-v.x*d,2) + Math.pow(u.y-v.y*d,2));
        }
        return Math.sqrt(Math.min(PointD.sub(segment.a,this).getNormSqr(), PointD.sub(segment.b,this).getNormSqr()));
    }

    /**
     * Projects the point onto a line
     */
    public PointD project(LineD line) {
        PointD b = PointD.sub(line.b, line.a), a = PointD.sub(this, line.a);
        double d = PointD.dot(b,a) / b.getNormSqr();
        PointD res = new PointD(line.a.x + b.x * d, line.a.y + b.y * d);
        return res;
    }

    /**
     * Moves a point along a line the given amount of distance.
     */
    public PointD walk(LineD line, double distance) {
        PointD v = line.getUnitVector();
        return PointD.add(this, PointD.mul(v, distance));
    }

    /**
     * Gets a line segment lying on the given line where all points are
     * within the specified distance from the point.
     */
    public LineD distanceRange(LineD line, double distance) {
        PointD p = project(line);
        double distSqr = distance * distance;
        double lineDistSqr = (p.x - x)*(p.x - x) + (p.y - y)*(p.y - y);
        if (lineDistSqr >= distSqr) return null;
        double walkDistance = Math.sqrt(distSqr - lineDistSqr);
        PointD a = p.walk(line, -walkDistance);
        PointD b = p.walk(line, walkDistance);
        LineD range = new LineD(a, b);
        return range;
    }

    // Transforms this point the same way point u has been transformed into point v
    public PointD vectorTransform(PointD u, PointD v) {
        double d0 = u.getNormSqr();
        PointD p = new PointD(PointD.det(u, this), PointD.dot(u, this));
        return new PointD(PointD.det(v, p) / d0, PointD.dot(v, p) / d0);
    }

    /**
     * Finds the closest pair of points
     * @param points the points
     * @param ret An array with 2 elements that will be populated with the closest pairs
     * @return the distance between the two closest points
     */
    public static double closestPair(List<PointD> points, PointD[] ret) {
        ArrayList<PointD> xa = new ArrayList<>(points), ya = new ArrayList<>(points);
        Collections.sort(xa);
        Collections.sort(ya, new Comparator<PointD>() {
            @Override
            public int compare(PointD o1, PointD o2) {
                return Double.compare(o1.y, o2.y);
            }
        });
        if (ret == null) {
            ret = new PointD[2];
        }

        return closestPairRec(ya, xa, ret);
    }

    private static double closestPairRec(List<PointD> ya, List<PointD> xa, PointD[] ret) {
        int n = ya.size(), split = n / 2;
        if (n <= 3) {
            // Base case
            double a = PointD.sub(ya.get(1), ya.get(0)).getNormSqr(), b = Double.MAX_VALUE, c = Double.MAX_VALUE;
            if (n == 3) {
                b = PointD.sub(ya.get(2), ya.get(0)).getNormSqr();
                c = PointD.sub(ya.get(2), ya.get(1)).getNormSqr();
            }
            if (a <= b) {
                ret[0] = ya.get(1);
                if (a <= c) {
                    ret[1] = ya.get(0);
                    return Math.sqrt(a);
                }
                ret[1] = ya.get(2);
                return Math.sqrt(c);
            } else {
                ret[0] = ya.get(2);
                if (b <= c) {
                    ret[1] = ya.get(0);
                    return Math.sqrt(b);
                }
                ret[1] = ya.get(1);
                return Math.sqrt(c);
            }
        }
        ArrayList<PointD> ly = new ArrayList<>(split), ry = new ArrayList<>(split+1), stripy = new ArrayList<>();
        PointD splitp = xa.get(split);
        for (PointD i : ya) {
            if (i != splitp && PointD.sub(i, splitp).getNormSqr() < 1e-12) {
                ret[0] = i;
                ret[1] = splitp;
                return 0.0;
            }
            if (i.compareTo(splitp) < 0) {
                ly.add(i);
            } else {
                ry.add(i);
            }
        }
        PointD[] ret2 = new PointD[2];
        double a = closestPairRec(ly, xa.subList(0, split), ret);
        double b = closestPairRec(ry, xa.subList(split, xa.size()), ret2);
        if (b < a) {
            a = b;
            ret[0] = ret2[0];
            ret[1] = ret2[1];
        }
        double a2 = a*a, splitx = splitp.x;
        for (PointD i : ya) { // Create strip (y-sorted)
            if (i.x >= splitx - a && i.x <= splitx + a) stripy.add(i);
        }
        for (int i = 0; i < stripy.size(); i++) {
            PointD p1 = stripy.get(i);
            for (int j = i + 1; j < stripy.size(); j++) {
                PointD p2 = stripy.get(j);
                if (p2.y - p1.y > a) break;
                double d2 = PointD.sub(p1, p2).getNormSqr();
                if (d2 < a2) {
                    ret[0] = p1;
                    ret[1] = p2;
                    a2 = d2;
                }
            }

        }
        return Math.sqrt(a2);
    }
}
