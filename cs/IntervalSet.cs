using System;
using System.Collections.Generic;
using System.Text;

namespace Algorithms
{
	/// <summary>
	/// Class that represents a set of non-overlapping half-open integer 
	/// intervals and supports adding/meringing intervals.
	/// </summary>
	/// <remarks>
	/// If the interval set contains the intervals 
	///		[0,5) [8,9) [20,23) [25,29)
	/// the operation Add([22,35]) would yield the set
	///		[0,5) [8,9) [20,35)
	/// and furthermore adding 5,10 would yield the set
	///		[0,10) [20,35)
	/// </remarks>
	public class IntervalSet<T>
		where T : IConvertible, IComparable<T>, IEquatable<T>
	{
		private readonly TreeSet<Interval<T>> intervals = new TreeSet<Interval<T>>();

		/// <summary>
		/// Gets the number of intervals in the set
		/// </summary>
		public int Count
		{
			get { return intervals.Count; }
		}

		public ICollection<Interval<T>> GetIntervals()
		{
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
			var probe = intervals.FindStartNode(interval);
			if (probe != null && probe.Item.Overlaps(interval))
				return probe.Item;
			probe = intervals.FindEndNode(interval);
			if (probe != null && probe.Item.Overlaps(interval))
				return probe.Item;
			return null;
		}

		public void Add(Interval<T> interval)
		{
			if (intervals.Count == 0)
			{
				intervals.Add(interval);
				return;
			}

			var startPoint = new Interval<T>(interval.Start, interval.Start);
			var endPoint = new Interval<T>(interval.End, interval.End);

			TreeSet<Interval<T>>.Node firstNode = intervals.FindEndNode(startPoint);
			TreeSet<Interval<T>>.Node lastNode = intervals.FindStartNode(endPoint);

			if (firstNode == null)
			{
				firstNode = intervals.FindNodeByIndex(0);
			}
			else if (firstNode.Item.Before(interval))
			{
				firstNode = firstNode.Next();
			}
			if (lastNode == null)
			{
				lastNode = intervals.FindNodeByIndex(intervals.Count - 1);
			} 
			else if (lastNode.Item.After(interval))
			{
				lastNode = lastNode.Previous();
			}
			
			// If no existing intervals overlap, firstOverlappingNode will now be greated than lastOverlappingNode
			if (firstNode == null || lastNode == null || firstNode.Item.CompareTo(lastNode.Item) > 0)
			{
				// Just add the interval
				intervals.Add(interval);
				return;
			}

			var mergedInterval = interval.Merge(firstNode.Item).Merge(lastNode.Item);
			
			// Remove all nodes in the interval
			var intervalsToRemove = new List<Interval<T>>();
			for(var node = firstNode; node != lastNode; node = node.Next())
			{
				intervalsToRemove.Add(node.Item);	
			}

			foreach (var toRemove in intervalsToRemove)
			{
				intervals.Remove(toRemove);
			}

			// Just change the last interval instead of deleting it and then inserting a new node
			// The change won't affects the position in the tree.

			lastNode.Item.Start = mergedInterval.Start;
			lastNode.Item.End = mergedInterval.End;
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
