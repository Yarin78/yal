using System;

namespace Algorithms
{
	/// <summary>
	/// Class that represents a half-open integer interval, for instance [5,12)
	/// </summary>
	public class Interval<T> : IEquatable<Interval<T>>, IComparable<Interval<T>>
		where T : IConvertible, IComparable<T>, IEquatable<T>
	{
		/// <summary>
		/// Gets the startpoint (inclusive) of the interval.
		/// </summary>
		public T Start { get; set; }	

		/// <summary>
		/// Gets the endpoint (exclusive) of the interval.
		/// </summary>
		public T End { get; set; }

		/// <summary>
		/// Initializes a new instance of an <see cref="Interval{T}"/>.
		/// </summary>
		public Interval(T start, T end)
		{
			Start = start;
			End = end;
		}

		/// <summary>
		/// Gets a value indicating if the two intervals are mergable.
		/// They must then either overlap or share an endpoint.
		/// </summary>
		public bool Mergable(Interval<T> other)
		{
			return other.End.CompareTo(Start) >= 0 && End.CompareTo(other.Start) >= 0;
		}

		/// <summary>
		/// Gets a value indicating if two intervals overlap.
		/// The overlapping part must be strictly greater than zero.
		/// </summary>
		public bool Overlaps(Interval<T> other)
		{
			return other.End.CompareTo(Start) > 0 && End.CompareTo(other.Start) > 0;
		}

		/// <summary>
		/// Gets a value indicating if the interval lies entirely after another interval
		/// (not touching at any endpoints).
		/// </summary>
		public bool After(Interval<T> other)
		{
			return Start.CompareTo(other.End) > 0;
		}

		/// <summary>
		/// Gets a value indicating if the interval lies entirely before another interval
		/// (not touching at any endpoints).
		/// </summary>
		public bool Before(Interval<T> other)
		{
			return End.CompareTo(other.Start) < 0;
		}

		/// <summary>
		/// Creates a new interval that is the union of two intervals.
		/// </summary>
		/// <remarks>
		/// The method assumes that the interval overlaps or touches at the endpoints.
		/// </remarks>
		public Interval<T> Merge(Interval<T> other)
		{
			T newStart = Start.CompareTo(other.Start) < 0 ? Start : other.Start;
			T newEnd = End.CompareTo(other.End) > 0 ? End : other.End;
			return new Interval<T>(newStart, newEnd);
		}

		public int CompareTo(object obj)
		{
			return CompareTo((Interval<T>) obj);
		}

		public int CompareTo(Interval<T> other)
		{
			return Start.CompareTo(other.Start);
		}

		public bool Equals(Interval<T> other)
		{
			return Start.Equals(other.Start) && End.Equals(other.End);
		}

		public override bool Equals(object obj)
		{
			if (!(obj is Interval<T>))
				return false;
			return Equals((Interval<T>) obj);
		}

		public override int GetHashCode()
		{
			return Start.GetHashCode()*37 + End.GetHashCode();
		}

		public override string ToString()
		{
			return string.Format("[{0}, {1}]", Start, End);
		}
	}
}