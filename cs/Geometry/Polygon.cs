using System;
using System.Collections.Generic;

namespace Algorithms.Geometry
{
	public class Polygon
	{
		private const double EPS = 1e-9;

		public Point[] Points;

		public Polygon(ICollection<Point> points)
		{
			Points = new Point[points.Count];
			points.CopyTo(Points, 0);
		}
		
		public double GetArea()
		{
			double sum = 0;
			for (int i = 0; i < Points.Length; i++)
				sum += Point.Det(Points[i], Points[(i + 1) % Points.Length]);
			return Math.Abs(sum/2.0);
		}

		public Line[] GetLineSegments()
		{
			Line[] segs = new Line[Points.Length];
			for (int i = 0; i < Points.Length; i++)
				segs[i] = new Line(Points[i], Points[(i + 1) % Points.Length]);
			return segs;
		}

		public bool IsOnBoundary(Point p)
		{
			for (int i = 0; i < Points.Length; i++)
			{
				Point v1 = Points[i] - p, v2 = p - Points[(i + 1) % Points.Length];
				if (Point.AlmostZero(Point.Det(v1, v2)) && Point.Dot(v1, v2) + EPS >= 0)
					return true;
			}
			return false;
		}

		public bool IsInside(Point p)
		{
			// Return value is undefined if on boundary
			bool flag = false;
			for (int i = 0; i < Points.Length; i++)
			{
				Point a = Points[i], b = Points[(i + 1) % Points.Length] - a, q = p - a;
				if (b.Y > 0 ? q.Y >= 0 && q.Y < b.Y && Point.Det(q, b) < 0 : b.Y <= q.Y && q.Y < 0 && Point.Det(q, b) > 0)
					flag = !flag;
			}
			return flag;
		}

		public Polygon GetConvexHull()
		{
			Point[] points = (Point[]) Points.Clone();
			Array.Sort(points);
			Point adj = new Point(points[0].X, points[0].Y);
			for (int i = 0; i < points.Length; i++)
				points[i] -= adj;
			Array.Sort(points, new PointAngleComparer());
			List<Point> hull = new List<Point>();
			for (int i = 0; i < points.Length; i++)
			{
				Point cur = points[i] + adj;
				if (hull.Count > 0 && hull[hull.Count - 1].Equals(cur)) 
					continue;
				while (hull.Count > 1 && Point.Cross(hull[hull.Count - 2], hull[hull.Count - 1], cur) + EPS >= 0)
					hull.RemoveAt(hull.Count - 1);
				hull.Add(cur);
			}
			return new Polygon(hull);
		}

		public class PointAngleComparer : IComparer<Point>
		{
			public int Compare(Point p1, Point p2)
			{
				double r = Point.Det(p1, p2);
				if (!Point.AlmostZero(r))
					return r > 0 ? 1 : -1;
				return p1.NormSqr.CompareTo(p2.NormSqr);
			}
		}
	}
}