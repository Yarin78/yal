using System;
using System.Collections.Generic;
using System.Text;

namespace Algorithms.MathLib
{
	public class BigInt
	{
		private const long RADIX = 1000;
		private const int RADIX_NODECIMALDIGS = 3;

		private readonly long[] data; // Reverse order (least significant part first)
		private readonly bool negative;

		public BigInt() : this("0") {}
		public BigInt(long value) : this(value.ToString()) { }

		public BigInt(string value)
		{
			if (string.IsNullOrEmpty(value))
				throw new ArgumentException("value");

			negative = false;
			int start = 0;
			if (value[0] == '-')
			{
				negative = true;
				start = 1;
			}

			int pos = value.Length;
			
			List<long> d = new List<long>();
			while (pos > start)
			{
				int t = pos - RADIX_NODECIMALDIGS, len = RADIX_NODECIMALDIGS;
				if (t < start)
				{
					len = pos - start;
					t = start;
				}
				d.Add(long.Parse(value.Substring(t, len)));
				pos = t;
			}
			if (d.Count == 0)
				throw new ArgumentException("value");
			data = d.ToArray();
		}

		public BigInt(BigInt value)
		{
			data = (long[]) value.data.Clone();
			negative = value.negative;
		}

		private BigInt(bool neg, List<long> d)
		{
			int last = d.Count;
			while (last > 1 && d[last-1] == 0)
				last--;

			data = new long[last];
			d.CopyTo(0, data, 0, last);
			negative = neg;
			if (last == 1 && data[0] == 0)
				negative = false;
		}

		public int Length
		{
			get { return data.Length; }
		}

		public bool IsZero
		{
			get { return data.Length == 1 && data[0] == 0; }
		}
        
		public static BigInt operator +(BigInt a, BigInt b)
		{
			if (a.negative == b.negative)
				return new BigInt(a.negative, UnsignedAdd(a.data, b.data));
			if (UnsignedCompare(a.data, b.data) > 0)
				return new BigInt(a.negative, UnsignedSubtract(a.data, b.data));
			return new BigInt(b.negative, UnsignedSubtract(b.data, a.data));
		}

		public static BigInt operator -(BigInt a, BigInt b)
		{
			if (a.negative != b.negative)
				return new BigInt(a.negative, UnsignedAdd(a.data, b.data));
			if (UnsignedCompare(a.data, b.data) > 0)
				return new BigInt(a.negative, UnsignedSubtract(a.data, b.data));
			return new BigInt(!a.negative, UnsignedSubtract(b.data, a.data));
		}

		public static BigInt operator *(BigInt a, BigInt b)
		{
			List<long> result = new List<long>(a.Length + b.Length);
			for (int i = 0; i < a.Length + b.Length; i++)
				result.Add(0);

			for (int i = 0; i < b.Length; i++)
				AddTo(result, a.data, i, b.data[i]);

			return new BigInt(a.negative != b.negative, result);
		}

		public static BigInt operator /(BigInt a, long b)
		{
			if (b == 0)
				throw new DivideByZeroException();
			List<long> res = new List<long>(a.Length);
			for (int i = 0; i < a.Length; i++)
				res.Add(0);

			long cf = 0;
			bool resNeg = b > 0 ? a.negative : !a.negative;
			if (b<0)
				b = - b;
			for (int i = a.Length - 1; i >= 0; i--)
			{
				long v = a.data[i] + cf * RADIX;
				res[i] = v/b;
				cf = v%b;
			}
			return new BigInt(resNeg, res);
		}

		public static long operator %(BigInt a, long b)
		{
			if (b <= 0 || a.negative)
				throw new ArgumentOutOfRangeException();
			long cf = 0;
			for (int i = a.Length - 1; i >= 0; i--)
				cf = (a.data[i] + cf * RADIX)%b;
			return cf;
		}

		/*public static BigInt operator /(BigInt a, BigInt b)
		{
			if (b.IsZero)
				throw new DivideByZeroException();
			if (b.Length > a.Length)
				return new BigInt();
			
			long[] remainder = new long[a.Length];
			Array.Copy(a.data, remainder, a.Length);
			for (int ofs = a.Length - b.Length; ofs >= 0; ofs--)
			{
				long[] backup;
				long cf, cnt = 0;
				do
				{
					backup = (long[]) remainder.Clone();
					cf = 0;
					for (int j = 0; j < b.Length && cf > 0 & ofs + j < remainder.Length; j++)
					{
						remainder[ofs + j] -= ((j < b.data.Length ? b.data[j] : 0) + cf);
						if (remainder[ofs + j] < 0)
						{
							remainder[ofs + j] += RADIX;
							cf = 1;
						}
						else
						{
							cf = 0;
						}
					}
				} while (cf > 0);
				remainder = backup;
			}
			return null;
		}*/

		private static List<long> UnsignedAdd(long[] a, long[] b)
		{
			List<long> result = new List<long>(Math.Max(a.Length, b.Length) + 1);
			int cf = 0;
			for (int i = 0; i < Math.Max(a.Length, b.Length); i++)
			{
				long v = (i < a.Length ? a[i] : 0) + (i < b.Length ? b[i] : 0) + cf;
				if (v >= RADIX)
				{
					cf = 1;
					v -= RADIX;
				}
				else
				{
					cf = 0;
				}
				result.Add(v);
			}
			if (cf > 0)
				result.Add(1);
			return result;
		}

		private static List<long> UnsignedSubtract(long[] a, long[] b)
		{
			List<long> result = new List<long>(a.Length);
			int cf = 0;
			for (int i = 0; i < a.Length; i++)
			{
				long v = a[i] - (i < b.Length ? b[i] : 0) - cf;
				if (v < 0)
				{
					cf = 1;
					v += RADIX;
				}
				else
				{
					cf = 0;
				}
				result.Add(v);
			}
			if (cf > 0 || b.Length > a.Length)
				throw new ArgumentException("a must be greater than or equal to b");
			// Remove leading zeros
			while (result.Count > 1 && result[result.Count - 1] == 0)
				result.RemoveAt(result.Count - 1);
			return result;
		}

		private static int UnsignedCompare(long[] a, long[] b)
		{
			if (a.Length != b.Length)
				return a.Length - b.Length;
			for (int i = a.Length - 1; i >= 0; i--)
			{
				long dif = a[i] - b[i];
				if (dif != 0)
					return dif > 0 ? 1 : -1;
			}
			return 0;
		}

		private static void AddTo(IList<long> result, IEnumerable<long> source, int offset, long multiplier)
		{
			long cf = 0;
			foreach(long x in source)
			{
				long y = x*multiplier;
				long z = result[offset] + y + cf;
				result[offset++] = z%RADIX;
				cf = z/RADIX;
			}
			while (cf > 0)
			{
				long z = result[offset] + cf;
				result[offset++] = z%RADIX;
				cf = z/RADIX;
			}
		}

		public override string ToString()
		{
			StringBuilder sb = new StringBuilder();
			if (negative)
				sb.Append('-');
			for (int i = data.Length - 1; i >= 0; i--)
			{
				if (i == data.Length - 1)
					sb.Append(data[i]);
				else
					sb.AppendFormat("{0:D" + RADIX_NODECIMALDIGS + "}", data[i]);
			}
			return sb.ToString();
		}

		public override bool Equals(object obj)
		{
			BigInt bi = obj as BigInt;
			if (bi == null || negative != bi.negative || Length != bi.Length)
				return false;
			for (int i = 0; i < Length; i++)
				if (data[i] != bi.data[i])
					return false;
			return true;
		}

		public override int GetHashCode()
		{
			int hc = negative ? 1 : 0;
			foreach (long x in data)
				hc = hc*27 + ((int) x ^ (int) (x >> 32));
			return hc;
		}
	}
}
