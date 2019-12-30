using System;
using System.Collections;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using NUnit.Framework;

namespace Algorithms.Geometry.Integer
{
	[TestFixture]
	public class UnitTests
	{
		[Test]
		public void TestPointClass()
		{
			Point p1 = new Point(5, 7);
			Point p2 = new Point(5, 7);
			Point p3 = new Point(5, 2);
			Assert.IsFalse(p1 == p2);
			Assert.IsTrue(p1 != p2);
			Assert.IsTrue(p1.Equals(p2));
			Assert.IsFalse(p1.Equals(p3));
			Point p4 = p1 + p3;
			Assert.IsTrue(p4.Equals(new Point(10, 9)));
			Point p5 = new Point(2, 7);
			Assert.IsTrue((p4 - p5).Equals(new Point(8, 2)));
			Assert.IsTrue((p4 * 2).Equals(new Point(20, 18)));
			Assert.IsTrue((p3 / 3).Equals(new Point(1, 0)));

			Assert.IsTrue(Point.Det(p1, p4) == -25);
			Assert.IsTrue(Point.Dot(p1, p4) == 113);
			Assert.IsTrue(p1.NormSqr == 74);

			Assert.IsTrue(Point.Cross(p1, p2, p3) == 0);
			Assert.IsTrue(Point.Cross(new Point(2, 7), new Point(-13, 2), new Point(32, 17)) == 0);
			Assert.IsTrue(Point.Cross(new Point(2, 7), new Point(-13, 1), new Point(32, 17)) > 0);
			Assert.IsTrue(Point.Cross(new Point(2, 7), new Point(-13, 3), new Point(32, 17)) < 0);
		}

		[Test]
		public void TestLineClass()
		{
			Line l1 = new Line(2, 2, 11, 5);
			Line l2 = new Line(3, 7, 5, 3);
			Line l3 = new Line(6, 0, 9, 6);
			Line l4 = new Line(10, 2, 10, 8);
			Line l5 = new Line(12, 1, 13, 3);
			Line l6 = new Line(0, 7, 12, 7);
			Point p;

			Assert.IsTrue(Line.IsParallel(l3, l5));
			Assert.IsFalse(Line.IsParallel(l2, l4));
			Assert.IsTrue(Line.Intersect(l2, l6, out p));
			Assert.IsTrue(p.Equals(new Point(3, 7)));
			Assert.IsTrue(Line.Intersect(l3, l1, out p));
			Assert.IsTrue(p.Equals(new Point(8, 4)));
			Assert.IsTrue(Line.Intersect(l1, l4, out p));
			Assert.IsNull(p);
			Assert.IsTrue(Line.Intersect(l4, l6, out p));
			Assert.IsTrue(p.Equals(new Point(10, 7)));
			Assert.IsTrue(Line.Intersect(l5, l1));
			Assert.IsFalse(Line.Intersect(l3, l5));

			Assert.IsTrue(l2.LiesOn(new Point(4, 5)));
			Assert.IsTrue(l5.LiesOn(new Point(12, 1)));
			Assert.IsTrue(l1.LiesOn(new Point(-1, 1)));
			Assert.IsTrue(l1.LiesOn(new Point(14, 6)));
			Assert.IsTrue(l1.LiesOn(new Point(5, 3)));
			Assert.IsFalse(l1.LiesOn(new Point(5, 2)));
			double d1 = l1.Distance(new Point(4, 2));
			Assert.IsTrue(Math.Abs(d1 - 0.63245553203367588) < 1e-10);
			double d2 = l3.Distance(new Point(-23, -7));
			Assert.IsTrue(Math.Abs(d2 - 22.807893370497855) < 1e-10);
			Assert.IsTrue(l6.Distance(new Point(14832, 6)) == 1);
		}

		[Test]
		public void TestLineSegClass()
		{
			LineSeg l1 = new LineSeg(2, 2, 11, 5);
			LineSeg l2 = new LineSeg(3, 7, 5, 3);
			LineSeg l3 = new LineSeg(6, 0, 9, 6);
			LineSeg l4 = new LineSeg(10, 2, 10, 8);
			LineSeg l5 = new LineSeg(12, 1, 13, 3);
			LineSeg l6 = new LineSeg(0, 7, 12, 7);
			Point p;

			Assert.IsTrue(Line.IsParallel(l3, l5));
			Assert.IsFalse(Line.IsParallel(l2, l4));
			Assert.IsTrue(Line.Intersect(l2, l6, out p));
			Assert.IsTrue(p.Equals(new Point(3, 7)));
			Assert.IsTrue(Line.Intersect(l3, l1, out p));
			Assert.IsTrue(p.Equals(new Point(8, 4)));
			Assert.IsTrue(Line.Intersect(l1, l4, out p));
			Assert.IsNull(p);
			Assert.IsTrue(Line.Intersect(l4, l6, out p));
			Assert.IsTrue(p.Equals(new Point(10, 7)));
			Assert.IsFalse(Line.Intersect(l5, l1));
			Assert.IsFalse(Line.Intersect(l3, l5));
			Assert.IsFalse(Line.Intersect(l3, l6));
			Assert.IsFalse(Line.Intersect(l6, l3));

			Assert.IsTrue(l2.LiesOn(new Point(4, 5)));
			Assert.IsTrue(l5.LiesOn(new Point(12, 1)));
			Assert.IsFalse(l1.LiesOn(new Point(-1, 1)));
			Assert.IsFalse(l1.LiesOn(new Point(14, 6)));
			Assert.IsTrue(l1.LiesOn(new Point(5, 3)));
			Assert.IsFalse(l1.LiesOn(new Point(5, 2)));
			double d1 = l1.Distance(new Point(4, 2));
			Assert.IsTrue(Math.Abs(d1 - 0.63245553203367588) < 1e-10);
			double d2 = l3.Distance(new Point(-23, -7));
			Assert.IsTrue(Math.Abs(d2 - 29.832867780352597) < 1e-10);
			double d3 = l6.Distance(new Point(14832, 7));
			Assert.IsTrue(Math.Abs(d3 - 14820) < 1e-10);

			Assert.IsTrue(Math.Abs(l5.Length - Math.Sqrt(5)) < 1e-10);

			Assert.IsNull(LineSeg.Merge(l3, l5));
			LineSeg l7 = new LineSeg(8, 4, 7, 2);
			Assert.IsTrue(LineSeg.Merge(l3, l7).Equals(l3));
			LineSeg l8 = new LineSeg(4, -4, 7, 2);
			Assert.IsTrue(LineSeg.Merge(l8, l3).Equals(new LineSeg(9, 6, 4, -4)));
			Assert.IsTrue(LineSeg.Merge(new LineSeg(9, 6, 10, 8), l3).Equals(new LineSeg(10, 8, 6, 0)));
		}

		[Test]
		public void TestLineLineSegMix()
		{
			Line l1 = new Line(2, 3, 8, 9);
			Line l2 = new Line(8, 9, 2, 3);
			LineSeg ls1 = new LineSeg(2, 3, 8, 9);
			LineSeg ls2 = new LineSeg(8, 9, 2, 3);
			Assert.IsTrue(l1.Equals(l2));
			Assert.IsTrue(l2.Equals(l1));
			Assert.IsTrue(ls1.Equals(ls2));
			Assert.IsTrue(ls2.Equals(ls1));
			Assert.IsFalse(l1.Equals(ls2));
			Assert.IsFalse(l2.Equals(ls1));
			Assert.IsFalse(ls1.Equals(l2));
			Assert.IsFalse(ls2.Equals(l1));

			// TODO: Test Intersect
		}

		[Test]
		public void TestConvexHull1()
		{
			List<Point> al = new List<Point>();
			for (int i = 0; i < 5; i++)
				for (int j = 0; j < 5; j++)
					al.Add(new Point(i, j));
			Poly p = Poly.ConvexHull(new Poly(al.ToArray()));
			Assert.AreEqual(4, p.point.Length);
		}

		[Test]
		public void TestConvexHull2()
		{
			string input = "1,2 4,1 6,1 5,3 3,3 7,3 8,4 6,5 4,5 2,5 3,7 5,6";
			string expected = "1,2 2,5 3,7 5,6 8,4 6,1 4,1";

			var inputPoly = input.Split(' ').Select(x => new Point(x[0] - '0', x[2] - '0')).ToArray();
			var expectedPoly = expected.Split(' ').Select(x => new Point(x[0] - '0', x[2] - '0')).ToArray();
			var actual = Poly.ConvexHull(new Poly(inputPoly.ToArray()));

			Assert.AreEqual(expectedPoly.Length, actual.point.Length);
			for (int i = 0; i < actual.point.Length; i++)
			{
				Assert.AreEqual(expectedPoly[i], actual.point[i]);
			}
		}

		[Test]
		public void TestSortingPoints_1()
		{
			var points = new List<Point>();
			for (int y = -4; y <= 4; y++)
			{
				for(int x=-4;x <=4;x++)
				{
					if (Math.Abs(x)+Math.Abs(y)==4)
					{
						points.Add(new Point(x, y));
					}
				}
			}
			
			points.Add(new Point(6, 2));
			points.Add(new Point(-4, 4));
			var points2 = new List<Point>(points);
			points.Sort(new PolarPointComparer());
			points2.Sort(new AnglePointComparer());
			for (int i = 0; i < points.Count; i++)
			{
				var point = points[i];
				var point2 = points2[i];
				Assert.AreEqual(point, point2);
			}

			// Make sure the special case when x=0 is working properly
			points = new List<Point>
			         	{
			         		new Point(0, -5), new Point(0, -2), new Point(0, 5),
			         		new Point(0, 2), new Point(0, 8), new Point(0, -8)
			         	};

			var expectedPoints = new List<Point>
			                     	{
			                     		new Point(0, 2), new Point(0, 5), new Point(0, 8),
										new Point(0, -2), new Point(0, -5),  new Point(0, -8)
			                     	};
			points.Sort(new PolarPointComparer());
			for (int i = 0; i < points.Count; i++)
			{
				Assert.AreEqual(expectedPoints[i], points[i]);
			}
		}

		[Test]
		public void TestSortingPoints2()
		{
			var random = new Random(0);
			var polyComparer = new PolarPointComparer();
			var angleComparer = new AnglePointComparer();
			for (int test = 0; test < 100; test++)
			{
				Console.WriteLine("Case #" + (test + 1));
				var points1 = new List<Point>();
				var points2 = new List<Point>();
				for (int i = 0; i < 100; i++)
				{
					int x = random.Next(-10, 10);
					int y = random.Next(-10, 10);
					if (x == 0 && y == 0)
					{
						i--;
						continue;
					}
					var point = new Point(x,y);
					points1.Add(point);
					points2.Add(point);
				}

				for (int i = 0; i < 100; i++)
				{
					for (int j = 0; j < 100; j++)
					{
						int d1 = angleComparer.Compare(points1[i], points1[j]);
						int d2 = polyComparer.Compare(points1[i], points1[j]);
						if (d1 < 0) d1 = -1;
						if (d1 > 0) d1 = 1;
						if (d2 < 0) d2 = -1;
						if (d2 > 0) d2 = 1;
						if (d1 != d2)
							Console.WriteLine(points1[i] + " - " + points1[j] + " " + d1 + " " + d2);
						Assert.AreEqual(d1, d2);
					}
				}

				points1.Sort(angleComparer);
				points2.Sort(polyComparer);

				for (int i = 0; i < 100; i++)
				{
					Assert.AreEqual(points1[i], points2[i]);
				}
			}
		}

		public class AnglePointComparer : IComparer<Point>
		{
			public int Compare(Point x, Point y)
			{
				double a1 = Math.Atan2(x.y, x.x), a2 = Math.Atan2(y.y, y.x);
				if (Math.Abs(a1 - a2) < 1e-9)
					return x.NormSqr - y.NormSqr;
				a1 = Math.PI/2 - a1;
				a2 = Math.PI/2 - a2;
				if (a1 < 0) a1 += 2*Math.PI;
				if (a2 < 0) a2 += 2*Math.PI;
				if (a1 < a2)
					return -1;
				return 1;
			}
		}

	}
}
