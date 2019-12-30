package yarin.yal;

import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

public class TestSequences {

    @Test
    public void testLIS() {
        for (int tests = 0; tests < 100; tests++) {
            Random r = new Random(tests);
            int[] a = new int[20];
            for (int i = 0; i < a.length; i++)
                a[i] = r.nextInt(20);

            int len = Sequences.longestIncreasingSubsequenceLength(a);
            int[] lis = Sequences.longestIncreasingSubsequence(a);

            if (lis.length != len) throw new RuntimeException();

            /*for (int i = 0; i < a.length; i++)
                System.out.print(a[i] + " ");
            System.out.println();
            for (int i = 0; i < lis.length; i++)
                System.out.print(lis[i] + " ");
            System.out.println("   (" + len + ")");
            System.out.println();*/
        }
    }


    @Test
    public void testNextPermutation() {
        Assert.assertFalse(Sequences.nextPermutation(new Integer[0]));
        Assert.assertFalse(Sequences.nextPermutation(new Integer[] { 7 }));

        Integer[] a = new Integer[8];
        for (int i = 0; i < a.length; i++) {
            a[i] = i;
        }

        Integer[] previous = a.clone();
        int noPerm = 1;
        while (Sequences.nextPermutation(a)) {
            noPerm++;
            int g = 0;
            for (int i = 0; i < 8 && g == 0; i++) {
                g = a[i] - previous[i];
            }
            Assert.assertTrue(g > 0);
            previous = a.clone();
        }
        Assert.assertEquals(40320, noPerm);

        a = new Integer[] {7, 10, 10, 5, 12, 7, 10};
        noPerm = 1;
        while (Sequences.nextPermutation(a)) {
            noPerm++;
        }
        Assert.assertEquals(292, noPerm);
    }
}
