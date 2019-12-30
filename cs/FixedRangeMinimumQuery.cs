using System;

namespace Algorithms
{
	public class FixedRangeMinimumQuery<T>
		where T : IComparable<T>
	{
		public T[] FindMinimum(T[] data, int range)
		{
			if (range > data.Length)
				throw new ArgumentException();
			T[] result = new T[data.Length - range + 1];
			for (int i = 0; i + range - 1 < data.Length; i += range)
			{
				T[] minLeft = new T[range];
				minLeft[0] = data[i + range - 1];
				for (int j = 1; j < range; j++)
				{
					int dif = minLeft[j - 1].CompareTo(data[i + range - 1 - j]);
					minLeft[j] = dif < 0 ? minLeft[j - 1] : data[i + range - 1 - j];
				}
				T minRight = data[i + range - 1];
				for (int j = 0; j < range && i + j < result.Length; j++)
				{
					int dif = minLeft[range - j - 1].CompareTo(minRight);
					result[i + j] = dif < 0 ? minLeft[range - j - 1] : minRight;
					if (i + range + j < data.Length)
					{
						dif = minRight.CompareTo(data[i + range + j]);
						if (dif > 0)
							minRight = data[i + range + j];
					}
				}
			}

			return result;
		}
	}
}