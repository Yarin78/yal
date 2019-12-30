package yarin.yal.geometry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PolygonD {
    private static final double EPS = 1e-9;

    public List<PointD> points;

    public PolygonD(Collection<PointD> points) {
        this.points = new ArrayList<PointD>(points);
    }

    public double area() {
        double sum = 0;
        for (int i = 0; i < points.size(); i++) {
            sum += PointD.det(points.get(i), points.get((i + 1) % points.size()));
        }
        return Math.abs(sum/2.0);
    }

    public LineD[] getLineSegments()	{
        LineD[] segs = new LineD[points.size()];
        for (int i = 0; i < points.size(); i++) {
            segs[i] = new LineD(points.get(i), points.get((i+1) % points.size()));
        }
        return segs;
    }

    public boolean isPointOnBoundary(PointD p) {
        for (int i = 0; i < points.size(); i++) {
            PointD v1 = PointD.sub(points.get(i), p), v2 = PointD.sub(p, points.get((i+1) % points.size()));
            if (PointD.almostZero(PointD.det(v1, v2)) && PointD.dot(v1, v2) + EPS >= 0) {
                return true;
            }
        }
        return false;
    }

    public boolean isPointInside(PointD p) {
        // Return value is undefined if on boundary
        boolean flag = false;
        for (int i = 0; i < points.size(); i++)	{
            PointD a = points.get(i), b = PointD.sub(points.get((i + 1) % points.size()), a), q = PointD.sub(p, a);
            if (b.y > 0 ? q.y >= 0 && q.y < b.y && PointD.det(q, b) < 0 : b.y <= q.y && q.y < 0 && PointD.det(q, b) > 0) {
                flag = !flag;
            }
        }
        return flag;
    }

    public static PolygonD convexHull(PolygonD polygon) {
        ArrayList<PointD> p = new ArrayList<PointD>(polygon.points);
        Collections.sort(p);
        PointD adj = new PointD(p.get(0).x, p.get(0).y);
        for (int i = 0; i < p.size(); i++) {
            p.set(i, PointD.sub(p.get(i), adj));
        }
        Collections.sort(p, new PolarPointDComparer()); // Vector sort
        List<PointD> hull = new ArrayList<PointD>();
        for (int i = 0; i < p.size(); i++) {
            PointD cur = PointD.add(p.get(i), adj);
            if (hull.size() > 0 && hull.get(hull.size()-1).equals(cur)) continue;
            while (hull.size() > 1 && PointD.cross(hull.get(hull.size()-2), hull.get(hull.size()-1), cur) >= 0) {
                hull.remove(hull.size() - 1);
            }
            hull.add(cur);
        }
        return new PolygonD(hull);
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
    public static class PolarPointDComparer implements Comparator<PointD> {
        private static final double EPS = 1e-9;

        public int compare(PointD p1, PointD p2) {
            // All non-negative x-coordinates appear before all negative x-coordinates
            if (p1.x + EPS >= 0 && p2.x - EPS < 0) return -1;
            if (p1.x - EPS < 0 && p2.x + EPS >= 0) return 1;
            if (PointD.almostZero(p1.x) && PointD.almostZero(p2.x) && p1.y*p2.y - EPS < 0) {
                return Double.compare(p2.y, p1.y);
            }

            double r = PointD.det(p1, p2);
            return !PointD.almostZero(r) ? (r > 0 ? 1 : -1) : Double.compare(p1.getNormSqr(), p2.getNormSqr());
        }
    }

    public PolygonD cut(LineD line) {
        List<PointD> res = new ArrayList<>();
        for (int i = 0; i < this.points.size(); i++) {
            PointD cur = this.points.get(i), prev = i > 0 ? this.points.get(i-1) : this.points.get(this.points.size() - 1);
            boolean side = PointD.cross(line.a, line.b, cur) < 0;
            if (side != (PointD.cross(line.a, line.b, prev) < 0)) {
                PointD ipoint = new PointD();
                LineD.intersect(line, new LineD(cur, prev), ipoint);
                res.add(ipoint);
            }
            if (side)
                res.add(cur);
        }
        return new PolygonD(res);
    }
}
