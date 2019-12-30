using System;

namespace Algorithms.MathLib
{
	public struct Frac : IComparable<Frac>, IEquatable<Frac>
	{
		public long Nom, Denom;

		public Frac(long nominator, long denominator)
		{
			Nom = nominator;
			Denom = denominator;

			if (denominator != 1)
				Fix();
		}

		public override string ToString()
		{
			if (Denom == 1)
				return Nom.ToString();
			return Nom + "/" + Denom;
		}

		private void Fix()
		{
			if (Denom < 0)
			{
				Nom = -Nom;
				Denom = -Denom;
			}
			if (Nom == 0)
				Denom = 1;
			long d = gcd(Math.Abs(Nom), Denom);
			Nom /= d;
			Denom /= d;
		}

		private static long gcd(long a, long b)
		{
			return b == 0 ? a : gcd(b, a % b);
		}

		public static Frac operator +(Frac a, Frac b)
		{
			return new Frac(a.Nom * b.Denom + a.Denom * b.Nom, a.Denom * b.Denom);
		}

		public static Frac operator -(Frac a, Frac b)
		{
			return new Frac(a.Nom * b.Denom - a.Denom * b.Nom, a.Denom * b.Denom);
		}

		public static Frac operator *(Frac a, Frac b)
		{
			return new Frac(a.Nom * b.Nom, a.Denom * b.Denom);
		}

		public static Frac operator *(Frac a, long b)
		{
			return new Frac(a.Nom * b, a.Denom);
		}

		public static Frac operator /(Frac a, Frac b)
		{
			return new Frac(a.Nom * b.Denom, a.Denom * b.Nom);
		}

		public static Frac operator /(Frac a, long b)
		{
			return new Frac(a.Nom, a.Denom * b);
		}

		public static bool operator ==(Frac a, Frac b)
		{
			return a.Nom == b.Nom && a.Denom == b.Denom;
		}

		public static bool operator ==(Frac a, long b)
		{
			return a.Nom == b && a.Denom == 1;
		}

		public static Frac operator -(Frac a)
		{
			return new Frac(-a.Nom, a.Denom);
		}

		public static bool operator !=(Frac a, long b)
		{
			return !(a == b);
		}

		public static bool operator !=(Frac a, Frac b)
		{
			return !(a == b);
		}

		public static bool operator <(Frac a, Frac b)
		{
			return Compare(a, b) < 0;
		}

		public static bool operator <(Frac a, long b)
		{
			return Compare(a, new Frac(b, 1)) < 0;
		}

		public static bool operator >(Frac a, Frac b)
		{
			return Compare(a, b) > 0;
		}

		public static bool operator >(Frac a, long b)
		{
			return Compare(a, new Frac(b, 1)) > 0;
		}

		public static bool operator >=(Frac a, long b)
		{
			return Compare(a, new Frac(b, 1)) >= 0;
		}

		public static bool operator >=(Frac a, Frac b)
		{
			return Compare(a, b) >= 0;
		}

		public static bool operator <=(Frac a, Frac b)
		{
			return Compare(a, b) <= 0;
		}

		public static bool operator <=(Frac a, long b)
		{
			return Compare(a, new Frac(b, 1)) <= 0;
		}

		public bool Equals(Frac frac)
		{
			return Nom == frac.Nom && Denom == frac.Denom;
		}

		public override bool Equals(object obj)
		{
			if (!(obj is Frac)) return false;
			return Equals((Frac)obj);
		}

		public override int GetHashCode()
		{
			return (int)Nom + 29 * (int)Denom;
		}

		public static int Compare(Frac x, Frac y)
		{
			if (y.Denom == 1)
			{
				long b = y.Nom * x.Denom;
				if (x.Nom < b)
					return -1;
				if (x.Nom > b)
					return 1;
				return 0;
			}
			Frac t = x - y;
			if (t.Nom < 0)
				return -1;
			if (t.Nom == 0)
				return 0;
			return 1;
		}

		public int CompareTo(Frac other)
		{
			return Compare(this, other);
		}
	}
}
