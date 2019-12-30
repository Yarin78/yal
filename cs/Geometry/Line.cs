using System;

namespace Algorithms.Geometry
{
	public class Line
	{
		private const double EPS = 1e-9;

		public Point a, b;
		public Line() : this(new Point(), new Point()) { }
		public Line(Point a, Point b) { this.a = a; this.b = b; }
		public Line(int x1, int y1, int x2, int y2) : this(new Point(x1, y1), new Point(x2, y2)) { }
		public bool Contains(Point p) { return Point.AlmostZero(Point.Cross(a, b, p)); }
		public bool SegmentContains(Point p) { return Contains(p) && Point.Dot(a - p, p - b) + EPS >= 0; }
		public static bool IsParallel(Line a, Line b) { return Point.AlmostZero(Point.Det(a.b - a.a, b.b - b.a)); }

		// Returns true if the two line/linesegments intersect (and are not parallel)
		// If the intersection point has integer coordinates, it will be returned in p (otherwise p will be null)
		public static bool LinesIntersect(Line a, Line b, out Point p, bool lineSegments, bool onlyInterior)
		{
			p = null;
			Point difv = b.a - a.a, av = a.b - a.a, bv = b.a - b.b;
			double d = Point.Det(av, bv), fa = Point.Det(difv, bv), fb = Point.Det(av, difv);
			if (Point.AlmostZero(d)) return false;
			if (d < 0) { d = -d; fa = -fa; fb = -fb; }
			if ((fa * av.X) % d == 0 && (fa * av.Y) % d == 0)
				p = a.a + (av * fa / d);
			if (!lineSegments)
				return true;
			if ((fa < -EPS || fa > d + EPS)) return false;
			if ((fb < -EPS || fb > d + EPS)) return false;
			bool interiorA = fa > EPS && fa < d - EPS, interiorB = fb > EPS && fb < d - EPS;
			if (onlyInterior && !interiorA && !interiorB)
				return false;
			return true;
		}

		public static bool LinesIntersect(Line a, Line b, bool lineSegments, bool onlyInterior)
		{
			Point dummy;
			return LinesIntersect(a, b, out dummy, lineSegments, onlyInterior);
		}

		public double Distance(Point p)
		{
			Point v = b - a, u = p - a;
			double d = Point.Dot(v, u) / v.NormSqr;
			return Math.Sqrt(Math.Pow(u.X - v.X * d, 2) + Math.Pow(u.Y - v.Y * d, 2));
		}

		public override bool Equals(object obj)
		{
			Line ls = (Line)obj;
			return (a.Equals(ls.a) && b.Equals(ls.b)) || (a.Equals(ls.b) && b.Equals(ls.a));
		}

		public override int GetHashCode()
		{
			return a.GetHashCode() ^ b.GetHashCode();
		}

		// If a and b are parallel line segments that share at least one point, return the merge of these lines; otherwise null.
		public static Line MergeSegments(Line a, Line b)
		{
			if (!IsParallel(a, b)) return null;
			if (!a.SegmentContains(b.a) && !a.SegmentContains(b.b) && !b.SegmentContains(a.a) && !b.SegmentContains(a.b)) return null;
			Point[] p = new Point[] { a.a, a.b, b.a, b.b };
			Array.Sort(p);
			return new Line(p[0], p[3]);
		}
	}
}