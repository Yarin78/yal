using System;
using System.Collections.Generic;
using System.Text;
using Algorithms.MathLib;

namespace Algorithms.Geometry.Fraction
{
	public class Point : IComparable
	{
		public Frac x, y;
		public Point() : this(new Frac(0, 1), new Frac(0, 1)) { }
		public Point(Frac x, Frac y) { this.x = x; this.y = y; }
		public static Point operator +(Point p1, Point p2) { return new Point(p1.x + p2.x, p1.y + p2.y); }
		public static Point operator -(Point p1, Point p2) { return new Point(p1.x - p2.x, p1.y - p2.y); }
		public static Point operator *(Point p1, Frac scalar) { return new Point(p1.x * scalar, p1.y * scalar); }
		public static Point operator /(Point p1, Frac scalar) { return new Point(p1.x / scalar, p1.y / scalar); }
		public static Frac Det(Point a, Point b) { return a.x * b.y - a.y * b.x; }
		public static Frac Dot(Point a, Point b) { return a.x * b.x + a.y * b.y; }
		public static Frac Cross(Point a, Point b, Point c) { return Det(b - a, c - a); }
		public Frac NormSqr { get { return x * x + y * y; } }
		public int CompareTo(object obj) { Point p = (Point)obj; return x == p.x ? y.CompareTo(p.y) : x.CompareTo(p.x); }

		public override bool Equals(object obj) { return x == ((Point)obj).x && y == ((Point)obj).y; }
		public override int GetHashCode() { return x.GetHashCode() ^ y.GetHashCode(); }

		public override string ToString()
		{
			return string.Format("({0}, {1})", x, y);
		}
	}

	public class Line
	{
		public Point a, b;
		public Line() : this(new Point(), new Point()) { }
		public Line(Point a, Point b) { this.a = a; this.b = b; }
		public Line(Frac x1, Frac y1, Frac x2, Frac y2) : this(new Point(x1, y1), new Point(x2, y2)) { }
		public virtual bool LiesOn(Point p) { return Point.Cross(a, b, p) == 0; }
		public static bool IsParallel(Line a, Line b) { return Point.Det(a.b - a.a, b.b - b.a) == 0; }

		// Returns true if the two line/linesegments intersect (and are not parallel)
		// If the intersection point has integer coordinates, it will be returned in p (otherwise p will be null)
		public static bool Intersect(Line a, Line b, out Point p)
		{
			p = null;
			Point difv = b.a - a.a, av = a.b - a.a, bv = b.a - b.b;
			Frac d = Point.Det(av, bv), fa = Point.Det(difv, bv), fb = Point.Det(av, difv);
			if (d == 0) return false;
			if (d < 0) { d = -d; fa = -fa; fb = -fb; }
			p = a.a + (av * fa / d);
			if (a is LineSeg && (fa <= 0 || fa >= d)) return false;
			if (b is LineSeg && (fb <= 0 || fb >= d)) return false;
			return true;
		}

		public static bool Intersect(Line a, Line b)
		{
			Point dummy;
			return Intersect(a, b, out dummy);
		}

		public override bool Equals(object obj)
		{
			if (GetType() != obj.GetType()) return false;
			Line ls = (Line)obj;
			return (a.Equals(ls.a) && b.Equals(ls.b)) || (a.Equals(ls.b) && b.Equals(ls.a));
		}

		public override int GetHashCode()
		{
			return a.GetHashCode() ^ b.GetHashCode();
		}
	}

	public class LineSeg : Line
	{
		public LineSeg(Point a, Point b) : base(a, b) { }
		public LineSeg(Frac x1, Frac y1, Frac x2, Frac y2) : base(x1, y1, x2, y2) { }
		public LineSeg(long x1, long y1, long x2, long y2) : base(new Frac(x1, 1), new Frac(y1, 1), new Frac(x2, 1), new Frac(y2, 1)) { }
		public override bool LiesOn(Point p) { return base.LiesOn(p) && Point.Dot(a - p, p - b) >= 0; }

		// If a and b are parallel lines that share at least one point, return the merge of these lines.
		// Otherwise return null. If a LineSeg is returned, it will be a new instance.
		public static LineSeg Merge(LineSeg a, LineSeg b)
		{
			if (!IsParallel(a, b)) return null;
			if (!a.LiesOn(b.a) && !a.LiesOn(b.b) && !b.LiesOn(a.a) && !b.LiesOn(a.b)) return null;
			Point[] p = new Point[] { a.a, a.b, b.a, b.b };
			Array.Sort(p);
			return new LineSeg(p[0], p[3]);
		}
	}

	public class Circle
	{
		private Point center;
		private int radius;

		public Point Center
		{
			get { return center; }
			set { center = value; }
		}

		public int Radius
		{
			get { return radius; }
			set { radius = value; }
		}

		public Circle(Point center, int radius)
		{
			Center = center;
			Radius = radius;
		}

		/// <summary>
		/// Finds the center of a circle with the three given points on the circumference
		/// </summary>
		/// <exception cref="ArgumentException">Thrown if the three points are colinear.</exception>
		public static Point FindCenter(Point a, Point b, Point c)
		{
			Point ab = b - a, ac = c - a;
			Frac v = Point.Det(ab, c - b)*2;
			// If isnull(v), points are colinear!
			Point p = new Point(Point.Dot(ab, a + b), Point.Dot(ac, a + c));
			Point circleCenter = new Point(
				Point.Det(p, new Point(ab.y, ac.y))/v,
				-Point.Det(p, new Point(ab.x, ac.x))/v);
			return circleCenter;
		}
	}
}
