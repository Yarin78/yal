package yarin.yal;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Construct a suffix array in O(n log^2 n)
 * Longest common prefix array is create in O(n)
 *
 * Suffix array : Represents the lexicographic rank of each suffix of an array.
 * LCP array: Contains the maximum length prefix match between two consecutive suffixes,
 *            after they are sorted lexicographically.
 *
 * lcp[1] = max matching prefix length of suffixes start at sa[0] and sa[1], and so on
 *
 * For a faster implementation, see SuffixArray
 */
public class SuffixArrayBuilder {
  private String s;
  private int n, gap;
  private int pos[];
  public Integer sa[];

  public SuffixArrayBuilder(String s) {
    this.s = s;
    this.n = s.length();
  }

  public int suffixCompare(int i, int j) {
    if (pos[i] != pos[j])
      return pos[i] - pos[j];
    i += gap;
    j += gap;
    return (i < n && j < n) ? (pos[i] - pos[j]) : (j - i);
  }

  private class SuffixComparator implements Comparator<Integer> {
    @Override
    public int compare(Integer i, Integer j) {
      return suffixCompare(i, j);
    }
  }

  public int[] buildSuffixArray() {
    pos = new int[n];
    if (n == 0) return new int[0];
    sa = new Integer[n];

    int tmp[] = new int[n];
    for (int i = 0; i < n; i++) {
      sa[i] = i;
      pos[i] = (int) s.charAt(i);
    }
    SuffixComparator comp = new SuffixComparator();
    for (gap = 1;; gap *= 2) {
      Arrays.sort(sa, comp);
      for (int i = 0; i < n - 1; i++) {
        tmp[i+1] = tmp[i] + (suffixCompare(sa[i], sa[i + 1]) < 0 ? 1 : 0);
      }
      for (int i = 0; i < n; i++) {
        pos[sa[i]] = tmp[i];
      }
      if (tmp[n - 1] == n - 1) break;
    }
    int res[] = new int[n];
    for (int i = 0; i < n; i++) {
      res[i] = sa[i];
    }
    return res;
  }

  public int[] buildLongestCommonPrefix() {
    int lcp[] = new int[n];
    for (int i = 0, k = 0; i < n; lcp[pos[i++]] = k) {
      if (pos[i] > 0) {
        if (k != 0)
          k--;
        for (int j = sa[pos[i] - 1]; i+k < n && j+k < n && s.charAt(i + k) == s.charAt(j + k); k++);
      } else
        k = 0;
    }
    return lcp;
  }
}
