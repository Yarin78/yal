package yarin.yal.mathlib;

import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

public class TestMathStuff {
    @Test
    public void testModInverse() {
        MathStuff ml = new MathStuff();

        int prime = 100000007;
        for (int x = 2; x * x <= prime; x++)
            if (prime % x == 0)
                throw new RuntimeException();

        for (long i = 1; i <= 1000000; i++) {
            long j = ml.modInverse((int) i, prime);
            Assert.assertEquals(1, (i * j) % prime);
        }
    }

    @Test
    public void testGenerateModInverse() {
        int prime = 100000007, limit = 10000;
        long[] inverse = MathStuff.generateModInverse(limit, prime);

        for (long i = 1; i <= limit; i++) {
            Assert.assertEquals(1, (i * inverse[(int)i]) % prime);
        }
    }

    @Test
    public void testChoose() {
        MathStuff lib = new MathStuff();
        int p = 13, max = 40;
        lib.precalcChoose(max, p);
        for (int i = 1; i <= max; i++) {
            for (int j = 0; j <= i; j++) {
                int expected = lib.choose(i, j) % p;
                int actual = lib.chooseMod(i, j, p);
                Assert.assertEquals(expected, actual);
            }
        }
    }

    @Test
    public void testJosephus() {
        Random r = new Random(0);
        for (int i = 0; i < 100; i++) {
            int N = r.nextInt(400) + 100;
            int K = r.nextInt(1999) + 1;

            // Naive solution
            boolean[] gone = new boolean[N];
            int cur = 0;
            for (int j = 0; j < N - 1; j++) {
                int d = K - 1;
                while (d > 0) {
                    cur = (cur + 1)%N;
                    if (!gone[cur])
                        d--;
                }
                gone[cur] = true;
                while (gone[cur])
                    cur = (cur + 1) % N;
            }

            int expLeft = 0;
            while (gone[expLeft]) expLeft++;

            int actLeft = MathStuff.josephus(N, K);

            Assert.assertEquals(expLeft, actLeft - 1);
        }
    }

    @Test
    public void testMagicSquares() {
        for (int size = 1; size <= 100; size++) {
            if (size == 2)
                continue;
            int[][] a = MathStuff.magicSquare(size);

            boolean[] used = new boolean[size*size];
            int d1sum = 0, d2sum = 0, expSum = 0;
            for (int y = 0; y < size; y++) {
                d1sum += a[y][y];
                d2sum += a[y][size - y - 1];
                int rowSum = 0, colSum = 0;
                for (int x = 0; x < size; x++) {
                    Assert.assertFalse(used[a[y][x] - 1]);
                    used[a[y][x] - 1] = true;
                    rowSum += a[y][x];
                    colSum += a[x][y];
                }
                if (y == 0)
                    expSum = rowSum;
                Assert.assertEquals(expSum, rowSum);
                Assert.assertEquals(expSum, colSum);
            }
            Assert.assertEquals(expSum, d1sum);
            Assert.assertEquals(expSum, d2sum);
        }
    }

    @Test
    public void testChineseRemainderTheorem() {
        MathStuff lib = new MathStuff();
        for (int m = 2; m < 100; m++) {
            for (int n = 2; n < 100; n++) {
                int limit = Math.min(1000, lib.lcm(m, n));
                for (int x = 0; x < limit; x++) {
                    int a = x%m, b = x%n;
                    int y = lib.gcd(m, n) == 1 ? lib.chinese(a, m, b, n) : lib.chineseCommon(a, m, b, n);
                    Assert.assertEquals(x, y);
                }
            }
        }
    }

    @Test
    public void testDiophSolver() {
        Random r = new Random();
        for (int i = 0; i < 100; i++) {
            int A = r.nextInt(1000) - 500, B = r.nextInt(1000) - 500, C = r.nextInt(1000) - 500;
            MathStuff.Result res = new MathStuff.Result();
            int d = MathStuff.diophSolver(A, B, C, res);
            if (d > 0) {
                Assert.assertEquals(A*res.x + B*res.y, C);
                for (int j = 1; j < 10; j++) {
                    Assert.assertEquals(A*(res.x + j * B / d) + B*(res.y - j * A / d), C);
                }
            } else {
                i--;
            }
        }
    }
}
