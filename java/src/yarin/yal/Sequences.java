package yarin.yal;

public class Sequences {
    public static int longestIncreasingSubsequenceLength(int[] a) {
        int[] x = new int[a.length + 1];
        int m = 0, n = a.length;

        // x[i] == Smallest number that can be at position i in an increasing sequence of length i.
        x[0] = Integer.MIN_VALUE;
        for (int i = 0; i < n; i++) {
            int lo = 0, hi = m + 1;
            while (lo + 1 < hi) {
                int p = (lo + hi) / 2;
                if (x[p] >= a[i]) // Change to > for NonDecreasing
                    hi = p;
                else
                    lo = p;
            }
            x[lo + 1] = a[i];
            if (lo == m)
                m++;
        }
        return m;
    }

    public static int[] longestIncreasingSubsequence(int[] x) {
        int[] m = new int[x.length + 1];
        int[] pre = new int[x.length];
        int len = 0, n = x.length;
        m[0] = -1;

        for (int i = 0; i < n; i++) {
            int lo = 1, hi = len, j = 0;
            while (lo <= hi) {
                int p = (lo + hi) / 2;
                if (x[m[p]] < x[i]) { // Change to <= for NonDecreasing
                    j = p;
                    lo = p + 1;
                } else {
                    hi = p - 1;
                }
            }

            pre[i] = m[j];
            if (j == len || x[i] < x[m[j+1]]) { // Change to <= for NonDecreasing
                m[j+1] = i;
                len = Math.max(len, j+1);
            }
        }

        int[] res = new int[len];
        int cur = m[len];
        for (int i = len - 1; i >= 0; i--) {
            res[i] = x[cur];
            cur = pre[cur];
        }
        return res;
    }

    public static <T extends Comparable<T>> boolean nextPermutation(T[] array) {
        return nextPermutation(array, 0, array.length);
    }

    public static <T extends Comparable<T>> boolean nextPermutation(T[] array, int begin, int end) {
        if (begin == end)
            return false;

        int i = begin + 1;
        if (i == end)
            return false;
        i = end - 1;

        while (true) {
            int ii = i--;
            if (array[i].compareTo(array[ii]) < 0) {
                int j = end;
                while (!(array[i].compareTo(array[--j]) < 0));

                T tmp = array[i];
                array[i] = array[j];
                array[j] = tmp;
                reverse(array, ii, end);
                return true;
            }
            if (i == 0) {
                reverse(array, 0, end);
                return false;
            }
        }
    }

    public static <T> void reverse(T[] array, int begin, int end) {
        for (int i = 0; i < (end-begin)/2; i++) {
            T tmp = array[begin + i];
            array[begin + i] = array[end - i - 1];
            array[end - i - 1] = tmp;
        }
    }
}
