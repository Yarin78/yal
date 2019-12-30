package yarin.yal.geometry;

import java.util.Arrays;

public class LineD {
    public PointD a, b;
    public LineD() { this(new PointD(), new PointD()); }
    public LineD(PointD a, PointD b) { this.a = a; this.b = b; }
    public LineD(double x1, double y1, double x2, double y2)  { this(new PointD(x1,y1), new PointD(x2,y2)); }

    public double segmentLength() {
        return Math.sqrt(PointD.sub(a, b).getNormSqr());
    }

    public static boolean isParallel(LineD a, LineD b) {
        return PointD.almostZero(PointD.det(PointD.sub(a.b, a.a), PointD.sub(b.b, b.a)));
    }

    /**
     * Returns true if the two lines intersect (and are not parallel)
     * @param a a line
     * @param b a line
     * @param res will contain the intersection point
     * @return true if the lines intersect
     */
    public static boolean intersect(LineD a, LineD b, PointD res) {
        PointD difv = PointD.sub(b.a, a.a), av = PointD.sub(a.b, a.a), bv = PointD.sub(b.a, b.b);
        double d = PointD.det(av,bv), fa = PointD.det(difv, bv), fb = PointD.det(av, difv);
        if (PointD.almostZero(d)) return false;
        if (d < 0) { d = -d; fa = -fa; fb = -fb; }
        PointD t = PointD.add(a.a, PointD.div(PointD.mul(av, fa), d));
        if (res != null) {
            res.x = t.x;
            res.y = t.y;
        }
        return true;
    }

    public static boolean intersect(LineD a, LineD b) {
        return intersect(a,b,null);
    }

    public static boolean intersectSegment(LineD a, LineD b, PointD res) {PointD difv = PointD.sub(b.a, a.a), av = PointD.sub(a.b, a.a), bv = PointD.sub(b.a, b.b);
        double d = PointD.det(av,bv), fa = PointD.det(difv, bv), fb = PointD.det(av, difv);
        if (PointD.almostZero(d)) return false;
        if (d < 0) { d = -d; fa = -fa; fb = -fb; }
        if (fa<0 || fa>d || fb<0 || fb>d) return false;
        PointD t = PointD.add(a.a, PointD.div(PointD.mul(av, fa), d));
        if (res != null) {
            res.x = t.x;
            res.y = t.y;
        }
        return true;
    }

    public static boolean intersectSegment(LineD a, LineD b) {
        return intersectSegment(a,b,null);
    }

    /**
     * If a and b are parallel lines that share at least one point, return the merge of these lines.
     * Otherwise return null. If a LineSeg is returned, it will be a new instance.
     */
    public static LineD mergeSegments(LineD a, LineD b) {
        if (!isParallel(a,b)) return null;
        if (!b.a.liesOnSegment(a) && !b.b.liesOnSegment(a) && !a.a.liesOnSegment(b) && !a.b.liesOnSegment(b)) return null;
        PointD[] p = new PointD[] { a.a, a.b, b.a, b.b };
        Arrays.sort(p);
        return new LineD(p[0], p[3]);
    }

    /**
     * Find the intersection of two parallel segments.
     * Returns null if the lines don't share more than one point.
     */
    public static LineD parallelIntersectSegment(LineD a, LineD b) {
        if (!isParallel(a,b)) return null;
        if (!b.a.liesOnSegment(a) && !b.b.liesOnSegment(a) && !a.a.liesOnSegment(b) && !a.b.liesOnSegment(b)) return null;
        PointD[] p = new PointD[] { a.a, a.b, b.a, b.b };
        Arrays.sort(p);
        if (p[1].equals(p[2])) return null;
        return new LineD(p[1], p[2]);
    }

    /**
     * Gets the unit vector of this line segment.
     */
    public PointD getUnitVector() {
        double len = segmentLength();
        return new PointD((b.x - a.x) / len, (b.y - a.y) / len);
    }

    public double getRelativePosition(PointD p) {
        double d = Math.sqrt(PointD.sub(p, a).getNormSqr());
        return d / segmentLength();
    }

    /**
     * Gets an equation on the form ax+by=c for this line
     * where a^2+b^2 = 1
     */
    public double[] getEquation() {
        double ea, eb, ec;
        if (Math.abs(a.x-b.x) > Math.abs(a.y-b.y)) {
            ea = (a.y-b.y)/(b.x-a.x);
            eb = 1;
        } else {
            ea = 1;
            eb = (a.x-b.x)/(b.y-a.y);
        }
        double d = Math.sqrt(ea*ea+eb*eb);
        ea /= d;
        eb /= d;
        ec = ea * a.x + eb * a.y;
        return new double[] { ea, eb, ec };
    }

    @Override
    public String toString() { return String.format("%s-%s", a.toString(), b.toString()); }

    public boolean equals(Object obj) {
        if (!(obj instanceof LineD)) return false;
        LineD ls = (LineD)obj;
        return (a.equals(ls.a) && b.equals(ls.b)) || (a.equals(ls.b) && b.equals(ls.a));
    }

    public int hashCode() {
        return a.hashCode() ^ b.hashCode();
    }
}
