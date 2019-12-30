package yarin.yal;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class TestIntervalSet {

    @Test
    public void testIntervalSet() {
        // Test various "suffix" extensions
        IntervalSet<Integer> set = new IntervalSet<Integer>();
        set.add(new Interval<Integer>(5, 7));
        Assert.assertEquals("[5, 7]", set.toString());
        set.add(new Interval<Integer>(6, 12));
        Assert.assertEquals("[5, 12]", set.toString());
        set.add(new Interval<Integer>(12, 14));
        Assert.assertEquals("[5, 14]", set.toString());
        set.add(new Interval<Integer>(15, 20));
        Assert.assertEquals("[5, 14] [15, 20]", set.toString());
        set.add(new Interval<Integer>(30, 40));
        Assert.assertEquals("[5, 14] [15, 20] [30, 40]", set.toString());
        set.add(new Interval<Integer>(6, 23));
        Assert.assertEquals("[5, 23] [30, 40]", set.toString());

        // Test various "prefix" extensions
        set = new IntervalSet<Integer>();
        set.add(new Interval<Integer>(30, 35));
        Assert.assertEquals("[30, 35]", set.toString());
        set.add(new Interval<Integer>(27, 32));
        Assert.assertEquals("[27, 35]", set.toString());
        set.add(new Interval<Integer>(24, 27));
        Assert.assertEquals("[24, 35]", set.toString());
        set.add(new Interval<Integer>(19, 23));
        Assert.assertEquals("[19, 23] [24, 35]", set.toString());
        set.add(new Interval<Integer>(2, 7));
        Assert.assertEquals("[2, 7] [19, 23] [24, 35]", set.toString());
        set.add(new Interval<Integer>(9, 25));
        Assert.assertEquals("[2, 7] [9, 35]", set.toString());

        // Test extensions in both directions
        set = new IntervalSet<Integer>();
        set.add(new Interval<Integer>(30, 35));
        Assert.assertEquals("[30, 35]", set.toString());
        set.add(new Interval<Integer>(27, 38));
        Assert.assertEquals("[27, 38]", set.toString());

        // Test merge multiple subintervals
        set = new IntervalSet<Integer>();
        set.add(new Interval<Integer>(30, 35));
        Assert.assertEquals("[30, 35]", set.toString());
        set.add(new Interval<Integer>(10, 15));
        Assert.assertEquals("[10, 15] [30, 35]", set.toString());
        set.add(new Interval<Integer>(20, 25));
        Assert.assertEquals("[10, 15] [20, 25] [30, 35]", set.toString());
        set.add(new Interval<Integer>(13, 32));
        Assert.assertEquals("[10, 35]", set.toString());
    }

    private class IntervalSetDummy {
        private final int Max = 10000;
        private boolean[] covered = new boolean[Max];

        public void add(Interval<Integer> interval)	{
            for (int i = interval.start; i < interval.end; i++)	{
                covered[i] = true;
            }
        }

        public Collection<Interval<Integer>> getIntervals() {
            List<Interval<Integer>> intervals = new ArrayList<Interval<Integer>>();
            int x = 0;
            while (x < Max && !covered[x]) x++;
            while (x < Max) {
                int start = x;
                while (covered[x]) x++;
                intervals.add(new Interval<Integer>(start, x));
                while (x < Max && !covered[x]) x++;
            }
            return intervals;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (Interval<Integer> interval : getIntervals()) {
                if (sb.length() > 0)
                    sb.append(' ');
                sb.append(interval.toString());
            }
            return sb.toString();
        }

        public Interval<Integer> findOverlappingInterval(Interval<Integer> interval) {
            int x = interval.start;
            while (x < interval.end && !covered[x]) x++;
            if (x == interval.end)
                return null;
            int start = x, end = x;
            while (start > 0 && covered[start - 1])
                start--;
            while (end < Max && covered[end])
                end++;
            if (start == end) throw new RuntimeException();
            return new Interval<Integer>(start, end);
        }
    }

    @Test
    public void testIntervalSetRandom() {
        Random random = new Random(0);

        for (int i = 0; i < 1000; i++) {
            IntervalSetDummy dummySet = new IntervalSetDummy();
            IntervalSet<Integer> fastSet = new IntervalSet<Integer>();

            for (int j = 0; j < 50; j++) {
                int start = random.nextInt(9000);
                int end = start + random.nextInt(299) + 1;
                Interval<Integer> interval = new Interval<Integer>(start, end);

                dummySet.add(interval);
                fastSet.add(interval);

                int chkStart = random.nextInt(9000);
                Interval<Integer> chk = new Interval<Integer>(chkStart, chkStart + 1);
                Interval<Integer> dummyOverlap = dummySet.findOverlappingInterval(chk);
                Interval<Integer> fastOverlap = fastSet.findOverlappingInterval(chk);
                Assert.assertEquals(dummyOverlap, fastOverlap);
            }

            Assert.assertEquals(fastSet.toString(), dummySet.toString());
        }
    }

    @Test
    public void testCircularIntervalSet() {
        CircularIntervalSet<Integer> set = new CircularIntervalSet<Integer>(0, 100);
        set.add(new Interval<Integer>(12, 17));
        set.add(new Interval<Integer>(77, 95));
        set.add(new Interval<Integer>(92, 7));
        Assert.assertEquals("[77, 7] [12, 17]", set.toString());
        set.add(new Interval<Integer>(8, 15));
        Assert.assertEquals("[77, 7] [8, 17]", set.toString());
        set.add(new Interval<Integer>(17, 77));
        Assert.assertEquals("[8, 7]", set.toString());
        set.add(new Interval<Integer>(7, 8));
        Assert.assertEquals("[0, 100]", set.toString());
        set.add(new Interval<Integer>(70, 30));
        Assert.assertEquals("[0, 100]", set.toString());

        set = new CircularIntervalSet<Integer>(0, 100);
        set.add(new Interval<Integer>(50, 50));
        Assert.assertEquals("[0, 100]", set.toString());

        set = new CircularIntervalSet<Integer>(0, 100);
        set.add(new Interval<Integer>(50, 51));
        set.add(new Interval<Integer>(63, 64));
        Assert.assertEquals("[50, 51] [63, 64]", set.toString());
        set.add(new Interval<Integer>(57, 95));
        Assert.assertEquals("[50, 51] [57, 95]", set.toString());
    }

}
