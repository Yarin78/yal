package yarin.yal.geometry;

import java.util.Arrays;

public class Line {
    public Point a, b;
    public Line() { this(new Point(), new Point()); }
    public Line(Point a, Point b) { this.a = a; this.b = b; }
    public Line(int x1, int y1, int x2, int y2)  { this(new Point(x1,y1), new Point(x2,y2)); }

    @Override
    public String toString() { return String.format("%s-%s", a, b); }
    public double segmentLength() {
        return Math.sqrt(Point.sub(a, b).getNormSqr());
    }

    /**
     * Gets a canonical version of the line with the endpoints sorted
     */
    public Line canonical() { return a.compareTo(b) <= 0 ? this : new Line(b, a); }

    /**
     * Gets the midpoint of this line. Will be rounded to intger coordinates.
     */
    public Point midPoint() { return new Point((a.x + b.x) / 2, (a.y + b.y) / 2); }

    public static boolean isParallel(Line a, Line b) {
        return Point.det(Point.sub(a.b, a.a), Point.sub(b.b, b.a)) == 0;
    }

    /**
     * Returns true if the two lines intersect (and are not parallel)
     * @param a a line
     * @param b a line
     * @param res will contain the intersection point if it's on integer coordinates
     * @return true if the lines intersect
     */
    public static boolean intersect(Line a, Line b, Point res) {
        Point difv = Point.sub(b.a, a.a), av = Point.sub(a.b, a.a), bv = Point.sub(b.a, b.b);
        int d = Point.det(av,bv), fa = Point.det(difv, bv), fb = Point.det(av, difv);
        if (d == 0) return false;
        if (d < 0) { d = -d; fa = -fa; fb = -fb; }
        if ((fa*av.x) % d == 0 && (fa*av.y) % d == 0) {
            Point t = Point.add(a.a, (Point.div(Point.mul(av, fa), d)));
            if (res != null) {
                res.x = t.x;
                res.y = t.y;
            }
        }
        return true;
    }

    public static boolean intersect(Line a, Line b) {
        return intersect(a,b,null);
    }

    /**
     * Determines if two line segments intersect
     * @param a the first line segment
     * @param b the second line segment
     * @param res will contain the intersection point if it's on integer coordinates, otherwise undefined
     * @return true if the two segment intersect or share one common point.
     * false if there is no intersection or if the lines are parallel
     */
    public static boolean intersectSegment(Line a, Line b, Point res) {
        Point difv = Point.sub(b.a, a.a), av = Point.sub(a.b, a.a), bv = Point.sub(b.a, b.b);
        int d = Point.det(av,bv), fa = Point.det(difv, bv), fb = Point.det(av, difv);
        if (d == 0) return false;
        if (d < 0) { d = -d; fa = -fa; fb = -fb; }
        if (fa<0 || fa>d || fb<0 || fb>d) return false;
        if ((fa*av.x) % d == 0 && (fa*av.y) % d == 0) {
            Point t = Point.add(a.a, (Point.div(Point.mul(av, fa), d)));
            if (res != null) {
                res.x = t.x;
                res.y = t.y;
            }
        }
        return true;
    }

    public static boolean intersectSegment(Line a, Line b) {
        return intersectSegment(a,b,null);
    }

    /**
     * Determines if two line segments are parallel and share an infinite many points
     * @param a the first line segment
     * @param b the second line segment
     * @return true if the two line segments share an infinite many points
     */
    public static boolean overlapSegments(Line a, Line b) {
        Line overlap = parallelIntersectSegments(a, b);
        return overlap != null && !overlap.a.equals(overlap.b);
    }

    // If a and b are parallel lines that share at least one point, return the merge of these lines.
    // Otherwise return null.
    public static Line mergeSegments(Line a, Line b) {
        if (!isParallel(a,b)) return null;
        if (!b.a.liesOnSegment(a) && !b.b.liesOnSegment(a) && !a.a.liesOnSegment(b) && !a.b.liesOnSegment(b)) return null;
        Point[] p = new Point[] { a.a, a.b, b.a, b.b };
        Arrays.sort(p);
        return new Line(p[0], p[3]);
    }

    // Return the parallel overlapping part of two line segments (intersection)
    // If the lines are not parallel or overlapping, null is returned.
    // If only a single point is shared, a 0 length line will be returned
    public static Line parallelIntersectSegments(Line a, Line b) {
        if (!isParallel(a,b)) return null;
        if (!b.a.liesOnSegment(a) && !b.b.liesOnSegment(a) && !a.a.liesOnSegment(b) && !a.b.liesOnSegment(b)) return null;

        a = a.canonical();
        b = b.canonical();
        if (a.a.compareTo(b.a) > 0) {
            Line tmp = a; a = b; b = tmp;
        }
        Line res = new Line(Point.max(a.a, b.a), Point.min(a.b, b.b));
        if (res.a.compareTo(res.b) > 0) return null;
        return res;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Line)) return false;
        Line ls = (Line)obj;
        return (a.equals(ls.a) && b.equals(ls.b)) || (a.equals(ls.b) && b.equals(ls.a));
    }

    public int hashCode() {
        return a.hashCode() ^ b.hashCode();
    }
}
