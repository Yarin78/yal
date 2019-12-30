package yarin.yal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeSet;

public class IntervalSet<T extends Comparable<T>> {
    private final TreeSet<Interval<T>> intervals = new TreeSet<Interval<T>>();

    /**
     * Gets the number of intervals in the set
     */
    public int getCount() {
        return intervals.size();
    }

    public Collection<Interval<T>> getIntervals() {
        return intervals;
    }

    /**
     * Finds an interval in the set that {@link Interval#overlaps(Interval)}
     * with interval. Returns null if no such interval is found.
     * The given interval must be non-empty for a match to be found.
     */
    public Interval<T> findOverlappingInterval(Interval<T> interval) {
        Interval<T> probe = intervals.floor(interval);
        if (probe != null && probe.overlaps(interval))
            return probe;
        probe = intervals.ceiling(interval);
        if (probe != null && probe.overlaps(interval))
            return probe;
        return null;
    }

    public void add(Interval<T> interval) {
        if (intervals.size() == 0) {
            intervals.add(interval);
            return;
        }

        Interval<T> startPoint = new Interval<T>(interval.start, interval.start);
        Interval<T> endPoint = new Interval<T>(interval.end, interval.end);

        Interval<T> first = intervals.floor(startPoint);
        Interval<T> last = intervals.ceiling(endPoint);

        if (first == null) {
            first = intervals.first();
        } else if (first.before(interval)) {
            first = intervals.higher(first);
        }

        if (last == null) {
            last = intervals.last();
        } else if (last.after(interval)) {
            last = intervals.lower(last);
        }

        // If no existing intervals overlap, firstOverlappingNode will now be greater than lastOverlappingNode
        if (first == null || last == null || first.compareTo(last) > 0)	{
            // Just add the interval
            intervals.add(interval);
            return;
        }

        Interval<T> mergedInterval = interval.merge(first).merge(last);


        // Remove all nodes in the interval
        List<Interval<T>> intervalsToRemove = new ArrayList<Interval<T>>();
        while (!first.equals(last)) {
            intervalsToRemove.add(first);
            first = intervals.higher(first);
        }

        intervals.removeAll(intervalsToRemove);

        // Just change the last interval instead of deleting it and then inserting a new node
        // The change won't affects the position in the tree.

        last.start = mergedInterval.start;
        last.end = mergedInterval.end;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Interval<T> interval : getIntervals()) {
            if (sb.length() > 0)
                sb.append(' ');
            sb.append(interval.toString());
        }
        return sb.toString();
    }
}
