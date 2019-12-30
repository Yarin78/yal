package yarin.yal;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestAggregationTree {
    @Test
    public void testAggregationTree() {
        // Test static
        Random r = new Random();
        List<Integer> data = new ArrayList<Integer>(1000);
        final int P = 10007;
        for (int i = 0; i < 1000; i++) {
            data.add(r.nextInt(P - 1) + 1);
        }
        AggregationTree<Integer> multTree = new AggregationTree<Integer>(data, 0,
                                                                         new AggregationTree.Aggregator<Integer>() {
                                                                             public Integer aggregate(Integer first, Integer second) {
                                                                                 return (first * second) % P;
                                                                             }
                                                                         });
        AggregationTree<Integer> maxTree = new AggregationTree<Integer>(data, 0,
                                                                        new AggregationTree.Aggregator<Integer>() {
                                                                            public Integer aggregate(Integer first, Integer second) {
                                                                                return Math.max(first, second);
                                                                            }
                                                                        });

        for (int i = 0; i < 100000; i++) {
            int start = r.nextInt(data.size());
            int stop = start + r.nextInt(Math.min(data.size() - start + 1, 101) - 1) + 1;

            int expectedProd = 1, expectedMax = 0;
            for (int j = start; j < stop; j++) {
                expectedProd = (expectedProd*data.get(j))%P;
                expectedMax = Math.max(expectedMax, data.get(j));
            }

            int actualProd = multTree.query(start, stop);
            int actualMax = maxTree.query(start, stop);

            Assert.assertEquals(expectedProd, actualProd);
            Assert.assertEquals(expectedMax, actualMax);

            int ix = r.nextInt(data.size());
            int v = r.nextInt(P-1) + 1;
            data.set(ix, v);
            multTree.set(ix, v);
            maxTree.set(ix, v);
        }
    }
}
