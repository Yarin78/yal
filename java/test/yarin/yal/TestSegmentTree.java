package yarin.yal;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class TestSegmentTree {

    @Test
    public void testSegmentTree() {
        // Test static
        Random r = new Random();
        List<Integer> data = new ArrayList<Integer>(1000);
        for (int i = 0; i < 1000; i++) {
            data.add(r.nextInt(1999) + 1);
        }
        SegmentTree<Integer> minTree = new SegmentTree<Integer>(data,
                                                                new Comparator<Integer>() {
                                                                    public int compare(Integer first, Integer second) {
                                                                        return first - second;
                                                                    }
                                                                });

        for (int i = 0; i < 100000; i++) {
            int start = r.nextInt(data.size());
            int stop = start + r.nextInt(Math.min(data.size() - start + 1, 101) - 1) + 1;

            int expectedMin = Integer.MAX_VALUE, expectedMinIndex = -1;
            for (int j = start; j < stop; j++) {
                if (data.get(j) < expectedMin) {
                    expectedMin = data.get(j);
                    expectedMinIndex = j;
                }
            }

            int actualMinIndex = minTree.query(start, stop);
            int actualMin = minTree.get(actualMinIndex);

            Assert.assertEquals(expectedMinIndex, actualMinIndex);
            Assert.assertEquals(expectedMin, actualMin);

            int ix = r.nextInt(data.size());
            int v = r.nextInt(1999) + 1;
            data.set(ix, v);
            minTree.set(ix, v);
        }
    }
}
