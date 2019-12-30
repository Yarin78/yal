using System;
using System.Collections.Generic;
using System.Globalization;
using System.Text;

namespace Algorithms.MathLib
{
	public class Polynomial {
		private readonly List<double> a;
		
		public Polynomial(params double[] coeff)
		{
			a = new List<double>(coeff);
			while (a.Count > 1 && a[a.Count - 1] == 0)
				a.RemoveAt(a.Count - 1);
		}

		public Polynomial(IEnumerable<double> coeff)
		{
			a = new List<double>(coeff);
			while (a.Count > 1 && a[a.Count - 1] == 0)
				a.RemoveAt(a.Count - 1);
		}

		public double this[int ix]
		{
			get { return a[ix]; }
		}

		public int Degree
		{
			get { return a.Count - 1; }
		}

		public double Eval(double x) {
			double val = 0;
			for (int i = a.Count - 1; i >= 0; --i)
				val = (val*x) + a[i];
			return val;
		}
	
		public Polynomial Diff()
		{
			var p = new double[a.Count - 1];
			for (int i = 1; i < a.Count; i++)
				p[i - 1] = i*a[i];
			return new Polynomial(p);
		}

		public Polynomial DivRoot(double x0)
		{
			// divide by (x-x0), ignore remainder
			var p = new double[a.Count - 1];
			for (int i = a.Count - 2; i >= 0; i--)
				p[i] = (i == a.Count - 2 ? 0 : p[i + 1]) * x0 + a[i+1];
			return new Polynomial(p);
		}

		public List<double> FindRoots(double xmin, double xmax)
		{
			if (Degree == 1)
				return new List<double> {-a[0]/a[1]};
			Polynomial d = Diff();
			var droots = d.FindRoots(xmin, xmax);
			var roots = new List<double>();
			for (int i = -1; i < droots.Count; i++)
			{
				double lo = i < 0 ? xmin - 1 : droots[i];
				double hi = i + 1 == droots.Count ? xmax + 1 : droots[i + 1];
				bool loSign = Eval(lo) > 0, hiSign = Eval(hi) > 0;
				if (loSign != hiSign)
				{
					while (hi-lo > 1e-9)
					{
						double m = (lo + hi)/2, f = Eval(m);
						if ((f <= 0) != loSign)
							lo = m;
						else
							hi = m;
					}
					roots.Add((lo + hi)/2);
				}
			}
			return roots;
		}
		
		public override string ToString()
		{
			var sb = new StringBuilder();
			for (int i = a.Count - 1; i >= 0; i--)
			{
				if (a[i] == 0)
					continue;
				if (sb.Length > 0)
					sb.Append(a[i] > 0 ? " + " : " - ");
				else if (a[i] < 0)
					sb.Append("-");
				
				if (i == 0 || Math.Abs(a[i]) != 1)
					sb.Append(Math.Abs(a[i]).ToString(CultureInfo.InvariantCulture));
				if (i > 0)
					sb.Append("x");
				if (i > 1)
					sb.Append("^" + i);
			}
			return sb.ToString();
		}
	}
}
