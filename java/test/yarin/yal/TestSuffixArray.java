package yarin.yal;

import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

public class TestSuffixArray {

  @Test
  public void bananaTest() {
    SuffixArrayBuilder sab = new SuffixArrayBuilder("banana");
    int[] sa = sab.buildSuffixArray();
    int[] lcp = sab.buildLongestCommonPrefix();
    Assert.assertArrayEquals(new int[]{5, 3, 1, 0, 4, 2}, sa);
    Assert.assertArrayEquals(new int[]{0, 1, 3, 0, 0, 2}, lcp);
  }

  @Test
  public void trivialTest() {
    SuffixArrayBuilder sab = new SuffixArrayBuilder("x");
    int[] sa = sab.buildSuffixArray();
    int[] lcp = sab.buildLongestCommonPrefix();
    Assert.assertArrayEquals(new int[] { 0 }, sa);
    Assert.assertArrayEquals(new int[] { 0 }, lcp);
  }

  @Test
  public void randomTest() {
    Random random = new Random(0);
    long t1 = 0, t2 = 0;
    for (int cases = 0; cases < 10; cases++) {
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < 10000; i++) {
        sb.append('a' + random.nextInt(10));
      }

      long s1 = System.currentTimeMillis();
      SuffixArrayBuilder sab = new SuffixArrayBuilder(sb.toString());
      int[] sa = sab.buildSuffixArray();
      int[] lcp = sab.buildLongestCommonPrefix();
      long s2 = System.currentTimeMillis();
      SuffixArray sab2 = new SuffixArray(sb.toString());
      int[] sa2 = sab2.buildSuffixArray();
      int[] lcp2 = sab2.buildLongestCommonPrefix();

      long s3 = System.currentTimeMillis();
      Assert.assertArrayEquals(sa, sa2);
      Assert.assertArrayEquals(lcp, lcp2);
      t1 += (s2-s1);
      t2 += (s3-s2);
    }
    System.out.println(t1);
    System.out.println(t2);
  }
}
