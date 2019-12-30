package yarin.yal.mathlib;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestMatrixInt {
    @Test
    public void testMatrixInt() {
        final int p = 10007;

        MatrixInt lhs = new MatrixInt(new int[][] { { 15, -30, 180 }, { 8350, 7, -1234 }, { 5000, -1, 3 } }, p);
        MatrixInt rhs = new MatrixInt(new int[][] { { 3234 }, { 4836 }, { 5274 } }, p);

        MatrixInt aug = lhs.getAugmentedMatrix(rhs);

        int[] res = aug.solveLinearEquation();
        Assert.assertEquals(3, res.length);
        Assert.assertEquals(73, res[0]);
        Assert.assertEquals(20, res[1]);
        Assert.assertEquals(182, res[2]);

        MatrixInt inverse = lhs.getInverse();
        Assert.assertEquals(MatrixInt.mul(lhs, inverse), MatrixInt.createIdentity(3, p));
    }

    @Test
    public void testMatrixIntRand() {
        Random r = new Random(0);

        List<Integer> largePrimes = new ArrayList<Integer>();

        int cp = 2000000000;
        while (largePrimes.size() < 100) {
            boolean bad = false;
            for (int i = 2; i * i <= cp && !bad; i++) {
                if (cp % i == 0)
                    bad = true;
            }
            if (!bad)
                largePrimes.add(cp);
            cp++;
        }

        for (int p : largePrimes) {
            int size = r.nextInt(73) + 2;
            int[] expectedRes = new int[size];
            for (int i = 0; i < size; i++)
                expectedRes[i] = r.nextInt(p);

            MatrixInt lhs = new MatrixInt(size, size, p);
            MatrixInt rhs = new MatrixInt(size, 1, p);

            for (int y = 0; y < size; y++) {
                long sum = 0;
                for (int x = 0; x < size; x++) {
                    int v = r.nextInt(p);
                    lhs.set(y,x,v);
                    sum += (long)v * expectedRes[x];
                    sum %= p;
                }
                rhs.set(y,0,(int) sum);
            }

            int determinant = lhs.getDeterminant();
            Assert.assertTrue(determinant != 0);

            MatrixInt aug = lhs.getAugmentedMatrix(rhs);
            int[] res = aug.solveLinearEquation();

            Assert.assertNotNull(res);

            for (int i = 0; i < size; i++)
                Assert.assertEquals(expectedRes[i], res[i]);
        }
    }
}
