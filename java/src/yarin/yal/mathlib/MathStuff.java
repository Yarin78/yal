package yarin.yal.mathlib;

public class MathStuff {
    /*
      If d=dioph(A,B,C,x,y) returns d>0, then the equation

         Ax + By = C

      is a valid Diophantine Equation and we have

         specific solution: x,y
         general solution:  x+n*B/d, y-n*A/d
    */

    public static class Result {
        public int x, y;
    }

    public static int diophSolver(int A, int B, int C, Result result) {
        int d = extendedEuclid(Math.abs(A), Math.abs(B), result);
        if (d == 0 || C % d != 0) return 0;
        result.x *= Math.abs(C) / d * (A * C > 0 ? 1 : -1);
        result.y *= Math.abs(C) / d * (B * C > 0 ? 1 : -1);
        return d;
    }

    public static int extendedEuclid(int a, int b, Result qr) {
        if (b == 0) { qr.x = 1; qr.y = 0; return a; }
        Result pq = new Result();
        int d = extendedEuclid(b, a % b, pq);
        qr.x = pq.y;
        qr.y = pq.x - a / b * pq.y;
        return d;
    }

    public int lcm(int a, int b) {
        return a*b/ gcd(a, b);
    }

    public int gcd(int a, int b) {
        return b == 0 ? a : gcd(b, a % b);
    }

    /*
        Solves the system x = a (mod m), x = b (mod n). chinese returns
        the unique solution with 0 <= x < lcm(m,n). If gcd(m,n) = 1, chinese
        may be used, otherwise, chineseCommon must be used, which returns
        -1 if there is no solution.
    */
    public int chinese(int a, int m, int b, int n) {
        Result p = new Result();
        extendedEuclid(m, n, p);
        return (a*n*(p.y < 0 ? p.y + m : p.y) +
                b*m*(p.x < 0 ? p.x + n : p.x))%(m*n);
    }

    public int chineseCommon(int a, int m, int b, int n)
    {
        int d = gcd(m, n);
        b = (b - a)%n;
        if (b < 0)
            b += n;
        if (b%d != 0)
            return -1; // No solution
        return d* chinese(0, m / d, b / d, n / d) + a;
    }

    public static long[] generateModInverse(int n, long modPrime) {
        long inverse[] = new long[n+1];
        inverse[1] = 1;
        for (int i = 2; i <= n; i++) {
            inverse[i] = modPrime - (modPrime / i) * inverse[(int) (modPrime % i)] % modPrime;
        }
        return inverse;
    }

    public static int modInverse(int x, int prime) {
        // p*x + q*prime = 1
        Result p = new Result();
        if (extendedEuclid(x, prime, p) == 0)
            throw new RuntimeException();
        if (p.x > 0) return p.x%prime;
        return (p.x+prime)%prime;
    }

    private int[][] choose;

    public void precalcChoose(int n, int mod) {
        choose = new int[n+1][n+1];
        for (int i = 0; i <= n; i++) {
            choose[i][0] = choose[i][i] = 1;
            for (int j = 1; j < i; j++) {
                choose[i][j] = choose[i - 1][j - 1] + choose[i - 1][j];
                if (mod > 0) {
                    choose[i][j] %= mod;
                }
            }
        }
    }

    public int choose(int n, int k) {
        if (k > n)
            return 0;
        return choose[n][k];
    }

    public int chooseMod(int n, int k, int mod) {
        if (n < mod)
            return choose(n, k)%mod;
        int v = 1;
        while (n > 0) {
            v *= chooseMod(n % mod, k % mod, mod);
            v %= mod;
            n /= mod;
            k /= mod;
        }
        return v;
    }

    public double[] quadraticEquation(double a, double b, double c) {
        double d = b*b-4*a*c;
        if (d < 0)
            return null;
        d = Math.sqrt(d);
        return new double[] {(-b + d)/(2*a), (-b - d)/(2*a)};
    }


    public static int josephus(int n, int k) {
        // Returns value 1-indexed
        int d = 1;
        while (d <= (k - 1) * n)
            d = (k * d + k - 2) / (k - 1);
        return k * n + 1 - d;
    }

    public static int[][] magicSquare(int n) {
        // CreateBlockCutpointGraph a magic square of size n using integers 1 - n^2
        int[][] a = new int[n][n];
        if (n%2 == 1) { // n = 2k+1
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++)
                {
                    int x = ((n/2) + j - i + n)%n;
                    int y = (-j + 2*i + n)%n;
                    a[y][x] = i*n + j + 1;
                }
            return a;
        }

        if (n%4 == 0) {// n = 4k
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++) {
                    int x = j%4 < 2 ? j%4 : 3 - j%4;
                    int y = i%4 < 2 ? i%4 : 3 - i%4;
                    int k = i*n + j + 1;
                    a[i][j] = (x == y) ? k : n*n + 1 - k;
                }
            return a;
        }

        // n = 4k+2
        int[] v = new int[2];
        for (int i = 0, flag = 0; i < n/2; i++) {
            for (int j = 0; j < n; j++) {
                int k = (n/2 + i - 1)%(n/2);
                int x = (k + n/2 - 1)%(n/2);
                v[0] = ((j == x) || (j == n - x - 1))
                        ? i*n + (n - j - 1)
                        : (i*n + j + (j == k ? n*(1 + 2*(n/2 - 1 - i)) : 0));
                v[1] = n*n - i*n - j - 1 - (j == n - k - 1 ? n*(1 + 2*(n/2 - 1 - i)) : 0);
                a[i][j] = v[flag] + 1;
                a[n - i - 1][n - j - 1] = v[flag ^ 1] + 1;
                if ((j != n/2 - 1) && (j != k - 1) && (j != n - k - 1))
                    flag ^= 1;
            }
        }
        return a;
    }
}
