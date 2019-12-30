package yarin.yal;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class TestLongestCommonSubstring {

  @Test
  public void trivialTest() {
    LongestCommonSubstring.Match res = new LongestCommonSubstring().find("abcde", "xyz");
    Assert.assertEquals(0, res.length);
    Assert.assertEquals("", res.substring);

    res = new LongestCommonSubstring().find("a", "");
    Assert.assertEquals(0, res.length);
    Assert.assertEquals("", res.substring);

    res = new LongestCommonSubstring().find("", "");
    Assert.assertEquals(0, res.length);
    Assert.assertEquals("", res.substring);
  }

  @Test
  public void simpleTest() {
    LongestCommonSubstring.Match res = new LongestCommonSubstring().find("abcde", "bcdef");
    Assert.assertEquals(4, res.length);
    Assert.assertEquals(1, res.ofs[0]);
    Assert.assertEquals(0, res.ofs[1]);

    res = new LongestCommonSubstring().find("bcdef", "abcde");
    Assert.assertEquals(4, res.length);
    Assert.assertEquals(0, res.ofs[0]);
    Assert.assertEquals(1, res.ofs[1]);
  }

  @Test
  public void overlappingTest() {
    LongestCommonSubstring.Match res = new LongestCommonSubstring().find("bc", "abca");
    Assert.assertEquals(2, res.length);
  }

  @Test
  public void randomTest() {
    Random random = new Random(0);
    for (int cases = 0; cases < 100; cases++) {
      StringBuilder sb1 = new StringBuilder(), sb2 = new StringBuilder();
      for (int i = 0; i < cases * 10 + 10; i++) {
        sb1.append((char)('a' + random.nextInt(5)));
        sb2.append((char)('a' + random.nextInt(5)));
      }
      String s = sb1.toString(), t = sb2.toString();
      LongestCommonSubstring.Match match = new LongestCommonSubstring().find(s, t);
      Assert.assertEquals(s.substring(match.ofs[0], match.ofs[0] + match.length),
                          t.substring(match.ofs[1], match.ofs[1] + match.length));

      int expected = longestMatchNaive(s, t);
      Assert.assertEquals(expected, match.length);
    }
  }

  private int longestMatchNaive(String s, String t) {
    int best = 0;
    for (int tofs = -t.length(); tofs < s.length() ; tofs++) {
      int cur = 0;
      for (int i = 0; i < s.length() ; i++) {
        if (i - tofs >= 0 && i-tofs < t.length() && s.charAt(i) == t.charAt(i - tofs)) {
          cur++;
          best = Math.max(best, cur);
        } else {
          cur = 0;
        }
      }
    }
    return best;
  }

  @Test
  public void noKSubstringMatch() {
    LongestCommonSubstring.Match match
        = new LongestCommonSubstring().find(Arrays.asList("ab", "bc", "cd"), 3);
    Assert.assertEquals(0, match.length);
    Assert.assertEquals("", match.substring);
  }

  @Test
  public void simpleKSubstringTest() {
    LongestCommonSubstring.Match match
        = new LongestCommonSubstring().find(Arrays.asList("abcdef", "bcdefg", "cdefgh"), 3);
    Assert.assertEquals(4, match.length);
    Assert.assertEquals(2, match.ofs[0]);
    Assert.assertEquals(1, match.ofs[1]);
    Assert.assertEquals(0, match.ofs[2]);
  }

  @Test
  public void simpleKSubstringTest2() {
    LongestCommonSubstring.Match match
        = new LongestCommonSubstring().find(Arrays.asList(
        "dccecacceddeeacbcbbb",
        "bdbccbeddedbcadacbda",
        "cbeaebeaeeaccdcceecb",
        "cadeaeacabebcccdecca",
        "dbdaebbcbacbaaeeecca"), 3);
    Assert.assertEquals(3, match.length);
    Assert.assertEquals("acb", match.substring);
    Assert.assertEquals(13, match.ofs[0]);
    Assert.assertEquals(15, match.ofs[1]);
    Assert.assertEquals(-1, match.ofs[2]);
    Assert.assertEquals(-1, match.ofs[3]);
    Assert.assertEquals(9, match.ofs[4]);
  }

  @Test
  public void sameSubstringMultipleTimes() {
    List<LongestCommonSubstring.Match> matches
        = new LongestCommonSubstring().findAll(Arrays.asList(
        "abcdefabcdef",
        "defghidefghi",
        "ghijklghijkl"), 2);
    Assert.assertEquals(2, matches.size());

    LongestCommonSubstring.Match m1 = matches.get(0);
    Assert.assertEquals(3, m1.length);
    Assert.assertEquals("def", m1.substring);
    Assert.assertEquals(3, m1.ofs[0]);
    Assert.assertEquals(0, m1.ofs[1]);
    Assert.assertEquals(-1, m1.ofs[2]);

    LongestCommonSubstring.Match m2 = matches.get(1);
    Assert.assertEquals(3, m2.length);
    Assert.assertEquals("ghi", m2.substring);
    Assert.assertEquals(-1, m2.ofs[0]);
    Assert.assertEquals(3, m2.ofs[1]);
    Assert.assertEquals(0, m2.ofs[2]);
  }

  @Test
  public void moreThanKSubstringsWithLongestMatch() {
    List<LongestCommonSubstring.Match> matches
        = new LongestCommonSubstring().findAll(Arrays.asList(
        "abcdef",
        "gfgabcdh",
        "xabcdxe",
        "tttababcabcdt"), 3);
    Assert.assertEquals(1, matches.size());
    LongestCommonSubstring.Match match = matches.get(0);
    Assert.assertEquals(4, match.length);
    Assert.assertEquals("abcd", match.substring);
    Assert.assertEquals(0, match.ofs[0]);
    Assert.assertEquals(3, match.ofs[1]);
    Assert.assertEquals(1, match.ofs[2]);
    Assert.assertEquals(8, match.ofs[3]);
  }

  @Test
  public void randomKSubstringTest() {
    Random random = new Random(0);
    for (int cases = 0; cases < 200; cases++) {
      int n = random.nextInt(10) + 3;
      ArrayList<StringBuilder> sba = new ArrayList<>();
      for (int j = 0; j < n; j++) {
        for (int i = 0; i < cases * 10 + 10; i++) {
          if (j == 0) sba.add(new StringBuilder());
          sba.get(j).append((char)('a' + random.nextInt(5)));
        }
      }
      ArrayList<String> strings = new ArrayList<>();
      for (int i = 0; i < n; i++) {
        strings.add(sba.get(i).toString());
      }
      int k = (n+1)/2;
      LongestCommonSubstring.Match match = new LongestCommonSubstring().find(strings, k);
      Assert.assertTrue(match.length > 0);

      // Check that matching matches and offsets are okay
      int m = 0;
      for (int i = 0; i < n; i++) {
        if (match.ofs[i] >= 0) {
          m++;
          String q = strings.get(i).substring(match.ofs[i], match.ofs[i] + match.length);
          if (!match.substring.equals(q)) {
            for (String string : strings) {
              System.out.println(string);
            }
            System.out.println(k);
          }
          Assert.assertEquals(match.substring, q);
        }
      }
      Assert.assertTrue(m >= k);


      // TODO: Check naively that there is no longer match
    }
  }
}
