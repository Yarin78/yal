using System;
using System.Collections.Generic;
using System.Text;

namespace Algorithms
{
	public class CircularIntervalSet<T>
		where T : IConvertible, IComparable<T>, IEquatable<T>
	{
		private readonly IntervalSet<T> internalSet;
		private readonly T zero, modulo;

		public CircularIntervalSet(T zero, T modulo)
		{
			this.zero = zero;
			this.modulo = modulo;
			internalSet = new IntervalSet<T>();
		}

		/// <summary>
		/// Adds an interval to the circular set.
		/// </summary>
		/// <param name="interval">The interval to add. May have the <see cref="Interval{T}.End"/> value less
		/// than or equal to <see cref="Interval{T}.Start"/> if wrapping and/or covering the entire range.</param>
		public void Add(Interval<T> interval)
		{
			if (interval.Start.CompareTo(modulo) >= 0 || interval.End.CompareTo(modulo) >= 0)
				throw new ArgumentException();
			if (interval.Start.CompareTo(interval.End) < 0)
			{
				internalSet.Add(interval);
			}
			else
			{
				internalSet.Add(new Interval<T>(interval.Start, modulo));
				internalSet.Add(new Interval<T>(zero, interval.End));
			}
		}

		/// <summary>
		/// Gets the current set of intervals. The first interval may have <see cref="Interval{T}.Start"/>
		/// greater than <see cref="Interval{T}.End"/> if it wraps around.
		/// </summary>
		public IEnumerable<Interval<T>> GetIntervals()
		{
			int start = -1;
			var intervals = new List<Interval<T>>(internalSet.GetIntervals());
			if (intervals.Count > 1)
			{
				if (intervals[0].Start.Equals(zero) && intervals[intervals.Count-1].End.Equals(modulo))
				{
					intervals[0] = new Interval<T>(intervals[intervals.Count - 1].Start, intervals[0].End);
					intervals.RemoveAt(intervals.Count - 1);
				}
			}
			return intervals;
		}

		/// <summary>
		/// Finds an interval in the set that <see cref="Interval{T}.Overlaps"/>
		/// with <paramref name="interval"/>. Returns null if no such interval is found.
		/// </summary>
		/// <remarks>
		/// The given interval must be non-empty for a match to be found.
		/// </remarks>
		public Interval<T> FindOverlappingInterval(Interval<T> interval)
		{
			if (interval.Start.CompareTo(modulo) >= 0 || interval.End.CompareTo(modulo) >= 0)
				throw new ArgumentException();
			if (interval.Start.CompareTo(interval.End) < 0)
			{
				return internalSet.FindOverlappingInterval(interval);
			}
			
			var overlappingSet = internalSet.FindOverlappingInterval(new Interval<T>(interval.Start, modulo));
			if (overlappingSet != null)
				return overlappingSet;
			overlappingSet = internalSet.FindOverlappingInterval(new Interval<T>(zero, interval.End));
			return overlappingSet;
		}

		public override string ToString()
		{
			var sb = new StringBuilder();
			foreach (var interval in GetIntervals())
			{
				if (sb.Length > 0)
					sb.Append(' ');
				sb.Append(interval.ToString());
			}
			return sb.ToString();
		}
	}
}