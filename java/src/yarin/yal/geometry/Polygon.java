package yarin.yal.geometry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Polygon {
    public List<Point> points;

    public Polygon(Collection<Point> points) {
        this.points = new ArrayList<Point>(points);
    }

    public double area() {
        return Math.abs(signedDoubledArea()) / 2.0;
    }

    public int signedDoubledArea() {
        int sum = 0;
        for (int i = 0; i < points.size(); i++)
            sum += Point.det(points.get(i), points.get((i + 1) % points.size()));
        return sum;
    }

    public Line[] getLineSegments()	{
        Line[] segs = new Line[points.size()];
        for (int i = 0; i < points.size(); i++) {
            segs[i] = new Line(points.get(i), points.get((i+1) % points.size()));
        }
        return segs;
    }

    public boolean isPointOnBoundary(Point p) {
        for (int i = 0; i < points.size(); i++) {
            Point v1 = Point.sub(points.get(i), p), v2 = Point.sub(p, points.get((i+1) % points.size()));
            if (Point.det(v1, v2) == 0 && Point.dot(v1, v2) >= 0) {
                return true;
            }
        }
        return false;
    }

    public boolean isPointInside(Point p) {
        boolean flag = false;
        for (int i = 0; i < points.size(); i++)	{
            Point a = points.get(i), b = Point.sub(points.get((i+1) % points.size()), a), q = Point.sub(p, a);
            if (b.y > 0 ? q.y >= 0 && q.y < b.y && Point.det(q, b) < 0 : b.y <= q.y && q.y < 0 && Point.det(q, b) > 0) {
                flag = !flag;
            }
        }
        return flag;
    }

    public static Polygon convexHull(Polygon polygon) {
        ArrayList<Point> p = new ArrayList<Point>(polygon.points);
        Collections.sort(p);
        Point adj = new Point(p.get(0).x, p.get(0).y);
        for (int i = 0; i < p.size(); i++) {
            p.set(i, Point.sub(p.get(i), adj));
        }
        Collections.sort(p, new PolarPointComparer()); // Vector sort
        List<Point> hull = new ArrayList<Point>();
        for (int i = 0; i < p.size(); i++) {
            Point cur = Point.add(p.get(i), adj);
            if (hull.size() > 0 && hull.get(hull.size()-1).equals(cur)) continue;
            while (hull.size() > 1 && Point.cross(hull.get(hull.size()-2), hull.get(hull.size()-1), cur) >= 0) {
                hull.remove(hull.size() - 1);
            }
            hull.add(cur);
        }
        return new Polygon(hull);
    }

    private int gcd(int a, int b) {
        return b == 0 ? a : gcd(b, a % b);
    }

    public int countExteriorIntegerCoordinates() {
        int cnt = 0;
        for (int i = 0; i < points.size(); i++) {
            Point p = Point.sub(points.get(i), points.get((i + 1) % points.size()));
            cnt += gcd(Math.abs(p.x), Math.abs(p.y));
        }
        return cnt;
    }

    public int countInteriorIntegerCoordinates() {
        return 1 + (Math.abs(signedDoubledArea()) - countExteriorIntegerCoordinates()) / 2;
    }

    /**
     * Sorts a set of point by treating them as vectors and sorting by the angle.
     * <p>
     * Starts at angle 90 degrees (x=0, y=-9999), goes to 0 (x=9999, y=0),
     * -90 (x=0, y=9999) to 180 (x=-9999, y=0). Points at 0,0 are undefined.
     * </p>
     * <p>
     * Points with the same angle are sorted according to the length from origo, closest first.</para>
     * </p>
     */
    public static class PolarPointComparer implements Comparator<Point> {
        public int compare(Point p1, Point p2) {
            // All non-negative x-coordinates appear before all negative x-coordinates
            if (p1.x >= 0 && p2.x < 0) return -1;
            if (p1.x < 0 && p2.x >= 0) return 1;
            if (p1.x==0 && p2.x==0 && p1.y*p2.y < 0)
                return p2.y - p1.y;

            int r = Point.det(p1, p2);
            return r != 0 ? r : p1.getNormSqr() - p2.getNormSqr();
        }
    }
}

