using System;
using System.Collections.Generic;
using System.Linq;

namespace Algorithms.Geometry
{
	public class Shape3D : IEquatable<Shape3D>
	{
		public class Point : IEquatable<Point>, IComparable<Point>
		{
			public int X { get; private set; }
			public int Y { get; private set; }
			public int Z { get; private set; }

			public Point(int x, int y, int z) { X = x; Y = y; Z = z; }

			public bool Equals(Point obj)
			{
				return obj.X == X && obj.Y == Y && obj.Z == Z;
			}

			public int CompareTo(Point other)
			{
				if (X != other.X) return X - other.X;
				if (Y != other.Y) return Y - other.Y;
				return Z - other.Z;
			}

			public override int GetHashCode()
			{
				return (((X*397) ^ Y)*397) ^ Z;
			}

			public Point RotateX() { return new Point(X, Z, -Y); }
			public Point RotateY() { return new Point(Z, Y, -X); }
			public Point RotateZ() { return new Point(Y, -X, Z); }
		}

		public List<Point> Points { get; private set; }

		public bool Equals(Shape3D other)
		{
			if (Points.Count != other.Points.Count)
				return false;
			for (int i = 0; i < Points.Count; i++)
				if (!Points[i].Equals(other.Points[i]))
					return false;
			return true;
		}

		public override int GetHashCode()
		{
			int hc = 0;
			foreach (Point p in Points)
			{
				hc ^= p.GetHashCode();
				hc *= 37;
			}
			return hc;
		}

		private static IEnumerable<Point> Normalize(IEnumerable<Point> points)
		{
			int minX = points.Min(p => p.X), minY = points.Min(p => p.Y), minZ = points.Min(p => p.Z);
			points = points.Select(p => new Point(p.X - minX, p.Y - minY, p.Z - minZ));
			return points;
		}

		public Shape3D(IEnumerable<Point> points, bool normalize)
		{
			if (normalize)
				points = Normalize(points);
			Points = new List<Point>(points);
			Points.Sort();
		}

		public Shape3D(string[,] shape, bool normalize)
		{
			Points = new List<Point>();
			for (int z = 0; z < shape.GetLength(0); z++)
				for (int y = 0; y < shape.GetLength(1); y++)
					for (int x = 0; x < shape[z, y].Length; x++)
						if (shape[z, y][x] != '.')
							Points.Add(new Point(x, y, z));
			if (normalize)
				Points = new List<Point>(Normalize(Points));
			Points.Sort();
		}

		public Shape3D Transform(Func<Point, Point> pointTransformation, bool normalize)
		{
			return new Shape3D(Points.Select(pointTransformation), normalize);
		}

		public Shape3D[] GenerateRotations()
		{
			var shapes = new HashSet<Shape3D>();

			Shape3D current = this;
			for (int xRots = 0; xRots < 4; xRots++)
			{
				current = current.Transform(p => p.RotateX(), true);
				for (int yRots = 0; yRots < 4; yRots++)
				{
					current = current.Transform(p => p.RotateY(), true);
					for (int zRots = 0; zRots < 4; zRots++)
					{
						current = current.Transform(p => p.RotateZ(), true);
						shapes.Add(current);
					}
				}
			}
			return shapes.ToArray();
		}

		public Shape3D[] GenerateTranslations(int xsize, int ysize, int zsize)
		{
			var shapes = new List<Shape3D>();
			int maxX = Points.Max(p => p.X), maxY = Points.Max(p => p.Y), maxZ = Points.Max(p => p.Z);
			for (int z = 0; z < zsize - maxZ; z++)
				for (int y = 0; y < ysize - maxY; y++)
					for (int x = 0; x < xsize - maxX; x++)
						shapes.Add(Transform(p => new Point(p.X + x, p.Y + y, p.Z + z), false));
			return shapes.ToArray();
		}

		public long GetBitMask(int xsize, int ysize, int zsize)
		{
			if (xsize * ysize * zsize > 64)
				throw new ArgumentException();
			long mask = 0;
			foreach (Point p in Points)
			{
				int v = (p.Z*ysize + p.Y)*xsize + p.X;
				mask |= 1L << v;
			}
			return mask;
		}
	}

}