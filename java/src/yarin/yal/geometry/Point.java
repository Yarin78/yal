package yarin.yal.geometry;

public class Point implements Comparable<Point> {
    public int x, y;

    public Point()  { this(0,0); }
    public Point(int x, int y) { this.x = x; this.y = y; }
    public static Point add(Point p1, Point p2) { return new Point(p1.x+p2.x, p1.y+p2.y); }
    public static Point sub(Point p1, Point p2) { return new Point(p1.x-p2.x, p1.y-p2.y); }
    public static Point mul(Point p1, int scalar) { return new Point(p1.x*scalar, p1.y*scalar); }
    public static Point div(Point p1, int scalar) { return new Point(p1.x/scalar, p1.y/scalar); }
    public static int det(Point a, Point b) { return a.x*b.y-a.y*b.x; }
    public static int dot(Point a, Point b) { return a.x*b.x+a.y*b.y; }
    public static int cross(Point a, Point b, Point c) { return det(sub(b, a), sub(c, a)); }
    public int getNormSqr() { return x*x+y*y; }
    public int compareTo(Point other) { Point p=other; return x==p.x ? y-p.y : x-p.x; }

    @Override
    public boolean equals(Object obj) { return x==((Point)obj).x && y==((Point)obj).y; }

    @Override
    public int hashCode() { return x * 37 + y; }

    @Override
    public String toString() { return String.format("(%d,%d)", x, y); }

    public static Point min(Point... p) {
        if (p.length == 0) return null;
        Point res = p[0];
        for (int i = 1; i < p.length; i++) {
            if (p[i].compareTo(res) < 0) res = p[i];
        }
        return res;
    }

    public static Point max(Point... p) {
        if (p.length == 0) return null;
        Point res = p[0];
        for (int i = 1; i < p.length; i++) {
            if (p[i].compareTo(res) > 0) res = p[i];
        }
        return res;
    }

    public boolean liesOn(Line line) {
        return Point.cross(line.a,line.b,this) == 0;
    }

    public boolean liesOnSegment(Line segment) {
        return liesOn(segment) && Point.dot(Point.sub(segment.a, this), Point.sub(this,segment.b))>=0;
    }

    public double distance(Line line)	{
        Point v = Point.sub(line.b, line.a), u = Point.sub(this, line.a);
        double d = (double) Point.dot(v,u)/v.getNormSqr();
        return Math.sqrt(Math.pow(u.x-v.x*d,2) + Math.pow(u.y-v.y*d,2));
    }

    public double distanceSegment(Line segment)	{
        Point v = Point.sub(segment.b, segment.a), u = Point.sub(this, segment.a);
        double d = (double) Point.dot(v,u)/v.getNormSqr();
        if (d > 0 && d < 1) {
            return Math.sqrt(Math.pow(u.x-v.x*d,2) + Math.pow(u.y-v.y*d,2));
        }
        return Math.sqrt(Math.min(Point.sub(segment.a,this).getNormSqr(),Point.sub(segment.b,this).getNormSqr()));
    }
}

