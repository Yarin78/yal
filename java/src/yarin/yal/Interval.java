package yarin.yal;

/**
 * Class that represents a half-open integer interval, for instance [5,12)
 */
public class Interval<T extends Comparable<T>> implements Comparable<Interval<T>> {
    // The startpoint (inclusive) and endpoint (exclusive) of the interval.
    public T start, end;

    public Interval(T start, T end) {
        this.start = start;
        this.end = end;
    }

    /**
     * Gets a value indicating if the two intervals are mergable.
     * They must then either overlap or share an endpoint.
     */
    public boolean mergable(Interval<T> other) {
        return other.end.compareTo(start) >= 0 && end.compareTo(other.start) >= 0;
    }

    /**
     * Gets a value indicating if two intervals overlap.
     * The overlapping part must be strictly greater than zero.
     */
    public boolean overlaps(Interval<T> other) {
        return other.end.compareTo(start) > 0 && end.compareTo(other.start) > 0;
    }

    /**
     * Gets a value indicating if the interval lies entirely after another interval
     * (not touching at any endpoints).
     */
    public boolean after(Interval<T> other) {
        return start.compareTo(other.end) > 0;
    }

    /**
     * Gets a value indicating if the interval lies entirely before another interval
     * (not touching at any endpoints).
     */
    public boolean before(Interval<T> other) {
        return end.compareTo(other.start) < 0;
    }

    /**
     * Creates a new interval that is the union of two intervals.
     * The method assumes that the interval overlaps or touches at the endpoints.
     */
    public Interval<T> merge(Interval<T> other)	{
        T newStart = start.compareTo(other.start) < 0 ? start : other.start;
        T newEnd = end.compareTo(other.end) > 0 ? end : other.end;
        return new Interval<T>(newStart, newEnd);
    }

    public int compareTo(Interval<T> other) {
        return start.compareTo(other.start);
    }

    public boolean equals(Interval<T> other) {
        return start.equals(other.start) && end.equals(other.end);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Interval))
            return false;
        return equals((Interval<T>) obj);
    }

    public int hashCode() {
        return start.hashCode()*37 + end.hashCode();
    }

    public String toString() {
        return String.format("[%d, %d]", start, end);
    }
}