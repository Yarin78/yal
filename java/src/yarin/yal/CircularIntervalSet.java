package yarin.yal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CircularIntervalSet<T extends Comparable<T>> {
    private final IntervalSet<T> internalSet;
    private final T zero, modulo;

    public CircularIntervalSet(T zero, T modulo) {
        this.zero = zero;
        this.modulo = modulo;
        internalSet = new IntervalSet<T>();
    }

    /**
     * Adds an interval to the circular set.
     * @param interval  the interval to add. May have the {@link Interval#end} value less
     * than or equal to {@link Interval#start} if wrapping and/or covering the entire range.
     */
    public void add(Interval<T> interval) {
        if (interval.start.compareTo(modulo) >= 0 || interval.end.compareTo(modulo) >= 0)
            throw new IllegalArgumentException();
        if (interval.start.compareTo(interval.end) < 0)	{
            internalSet.add(interval);
        }
        else {
            internalSet.add(new Interval<T>(interval.start, modulo));
            internalSet.add(new Interval<T>(zero, interval.end));
        }
    }

    /**
     * Gets the current set of intervals. The first interval may have {@link Interval#start}
     * greater than {@link Interval#end} if it wraps around.
     * @return
     */
    public Collection<Interval<T>> getIntervals() {
        List<Interval<T>> intervals = new ArrayList<Interval<T>>(internalSet.getIntervals());
        if (intervals.size() > 1) {
            if (intervals.get(0).start.equals(zero) && intervals.get(intervals.size()-1).end.equals(modulo)) {
                intervals.set(0, new Interval<T>(intervals.get(intervals.size() - 1).start, intervals.get(0).end));
                intervals.remove(intervals.size() - 1);
            }
        }
        return intervals;
    }

    /**
     * Finds an interval in the set that {@link Interval#overlaps(Interval)}
     * with interval. Returns null if no such interval is found.
     * The given interval must be non-empty for a match to be found.
     */
    public Interval<T> findOverlappingInterval(Interval<T> interval) {
        if (interval.start.compareTo(modulo) >= 0 || interval.end.compareTo(modulo) >= 0)
            throw new IllegalArgumentException();
        if (interval.start.compareTo(interval.end) < 0)	{
            return internalSet.findOverlappingInterval(interval);
        }

        Interval<T> overlappingSet = internalSet.findOverlappingInterval(new Interval<T>(interval.start, modulo));
        if (overlappingSet != null)
            return overlappingSet;
        overlappingSet = internalSet.findOverlappingInterval(new Interval<T>(zero, interval.end));
        return overlappingSet;
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
