using System;

namespace Algorithms.Geometry
{
	public class Point : IComparable<Point>
	{
		private const double EPS = 1e-9;

		public double X, Y;
		public Point() : this(0, 0) { }
		public Point(double x, double y) { X = x; Y = y; }
		public static Point operator +(Point p1, Point p2) { return new Point(p1.X + p2.X, p1.Y + p2.Y); }
		public static Point operator -(Point p1, Point p2) { return new Point(p1.X - p2.X, p1.Y - p2.Y); }
		public static Point operator *(Point p1, double scalar) { return new Point(p1.X * scalar, p1.Y * scalar); }
		public static Point operator /(Point p1, double scalar) { return new Point(p1.X / scalar, p1.Y / scalar); }
		public static double Det(Point a, Point b) { return a.X * b.Y - a.Y * b.X; }
		public static double Dot(Point a, Point b) { return a.X * b.X + a.Y * b.Y; }
		public static double Cross(Point a, Point b, Point c) { return Det(b - a, c - a); }
		public Point Rotate(double theta) { return new Point(Math.Cos(theta) * X - Math.Sin(theta) * Y, Math.Sin(theta) * X + Math.Cos(theta) * Y); }
		public double NormSqr { get { return X * X + Y * Y; } }
		public int CompareTo(object obj) { return CompareTo((Point) obj); } 
		public int CompareTo(Point p) { return AlmostZero(X - p.X) ? Y.CompareTo(p.Y) : X.CompareTo(p.X); }
		public override bool Equals(object obj) { return AlmostZero(X -((Point)obj).X) && AlmostZero(Y - ((Point)obj).Y); }
		public override int GetHashCode() { return X.GetHashCode() ^ Y.GetHashCode(); }

		public static bool AlmostZero(double x) { return Math.Abs(x) < EPS; }
	}
}