using System;

namespace Algorithms
{
	public class Sequences
	{
		public static int LongestIncreasingSubsequence(int[] a)
		{
			int[] x = new int[a.Length + 1];
			int m = 0, n = a.Length;

			// x[i] == Smallest number that can be at position i in an increasing sequence of length i.
			x[0] = int.MinValue;
			for (int i = 0; i < n; i++)
			{
				int lo = 0, hi = m + 1;
				while (lo + 1 < hi)
				{
					int p = (lo + hi) / 2;
					if (x[p] >= a[i]) // Change to > for NonDecreasing
						hi = p;
					else
						lo = p;
				}
				x[lo + 1] = a[i];
				if (lo == m)
					m++;
			}
			return m;
			/*
						for (int i = 0; i <= m; i++)
							Console.Write(slast[i] + " ");
						Console.WriteLine();

						int[] ret = new int[m];
						for (int i = n - 1, j = m; i >= 0 && j > 0; i--)
						{
							if (a[i] >= slast[j] && (j == m || a[i] < ret[j])) // Change < to <= for NonDecreasing
								ret[--j] = a[i];
						}
						return ret;*/
		}

		public static bool NextPermutation<T>(T[] array)
			where T : IComparable<T>
		{
			return NextPermutation(array, 0, array.Length);
		}

		public static bool NextPermutation<T>(T[] array, int begin, int end)
			where T : IComparable<T>
		{
			if (begin == end)
				return false;

			int i = begin + 1;
			if (i == end)
				return false;
			i = end - 1;

			while (true)
			{
				int ii = i--;
				if (array[i].CompareTo(array[ii]) < 0)
				{
					int j = end;
					while (!(array[i].CompareTo(array[--j]) < 0));

					T tmp = array[i];
					array[i] = array[j];
					array[j] = tmp;
					Reverse(array, ii, end);
					return true;
				}
				if (i == 0)
				{
					Reverse(array, 0, end);
					return false;
				}
			}
		}

		public static void Reverse<T>(T[] array, int begin, int end)
		{
			for (int i = 0; i < (end-begin)/2; i++)
			{
				T tmp = array[begin + i];
				array[begin + i] = array[end - i - 1];
				array[end - i - 1] = tmp;
			}
		}
		
	}
}