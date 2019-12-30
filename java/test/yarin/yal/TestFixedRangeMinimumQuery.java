package yarin.yal;

import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

public class TestFixedRangeMinimumQuery {

    @Test
    public void testFixedMinimumRange() {
        FixedRangeMinimumQuery frmq = new FixedRangeMinimumQuery();

        Random r = new Random(0);

        for (int size = 1; size < 1000; size++) {
            int[] a = new int[size];
            for (int i = 0; i < size; i++)
                a[i] = r.nextInt(999) + 1;

            for (int range = 1; range <= 20 && range <= size; range++) {
                int[] expected = new int[size - range + 1];
                for (int i = 0; i < expected.length; i++) {
                    int min = a[i];
                    for (int j = 1; j < range; j++)
                        min = Math.min(min, a[i + j]);
                    expected[i] = min;
                }

                int[] actual = frmq.findMinimum(a, range);

                Assert.assertEquals(expected.length, actual.length);
                for (int i = 0; i < expected.length; i++)
                    Assert.assertEquals(expected[i], actual[i]);
            }
        }
    }
}
