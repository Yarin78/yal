package yarin.yal;

import java.util.ArrayList;
import java.util.List;

public class LongestCommonSubstring {
  public static class Match {
    public int length;
    public String substring;
    public int[] ofs;

    public Match(int length, String substring, int... ofs) {
      this(length, ofs);
      this.substring = substring;
    }

    public Match(int length, int... ofs) {
      this.length = length;
      this.ofs = ofs;
    }
  }

  private static class Window {
    public int start, end;

    public Window(int start, int end) {
      this.start = start;
      this.end = end;
    }
  }

  /**
   * Finds the longest common substring between two strings
   * If there are several candidates, one of them will be returned
   */
  public Match find(String s, String t) {
    // Assumes \0 is not part of either string
    SuffixArray sab = new SuffixArray(s + '\0' + t);
    int[] sa = sab.buildSuffixArray();
    int[] lcp = sab.buildLongestCommonPrefix();
    Match match = new Match(0, "");
    for (int i = 1; i < sa.length; i++) {
      // Don't compare suffixes from the same string
      if (sa[i-1] < s.length() && sa[i] < s.length()) continue;
      if (sa[i-1] >= s.length() && sa[i] >= s.length()) continue;
      if (lcp[i] > match.length) {
        if (sa[i-1] < s.length()) {
          match = new Match(lcp[i], sa[i - 1], sa[i] - s.length() - 1);
        } else {
          match = new Match(lcp[i], sa[i], sa[i - 1] - s.length() - 1);
        }
      }
    }
    if (match.length > 0) {
      match.substring = s.substring(match.ofs[0], match.ofs[0] + match.length);
    }
    return match;
  }

  public List<Match> findAll(List<String> strings, int k) {
    return find(strings, k, Integer.MAX_VALUE, true);
  }

  public Match find(List<String> strings, int k) {
    List<Match> matches = find(strings, k, 1, true);
    if (matches.size() > 0) {
      return matches.get(0);
    }
    return new Match(0, "");
  }

  /**
   * Finds the longest common substring existing in at least k strings
   * If a substring exists in more than k strings, it will merged into a single match
   */
  public List<Match> find(List<String> strings, int k, int max, boolean includeOffsets) {
    StringBuilder sb = new StringBuilder();
    char sentinel = (char)256;
    int[] start = new int[strings.size()];
    for (int i = 0; i < strings.size(); i++) {
      start[i] = sb.length();
      String s = strings.get(i);
      sb.append(s);
      sb.append(sentinel++);
    }

    String string = sb.toString();
    SuffixArray sab = new SuffixArray(string);
    int[] sa = sab.buildSuffixArray();
    int[] lcp = sab.buildLongestCommonPrefix();
    int[] cnt = new int[strings.size()];
    int[] type = new int[string.length()];

    for (int i = 0, j = 0, m = 0; i < type.length; i++) {
      type[i] = m;
      if (++j > strings.get(m).length()) {
        m++;
        j = 0;
      }
    }
    ArrayList<Window> longestWindows = new ArrayList<>();
    int npos = 0, head = 0, tail = 0, longestMatch = 0;
    DynamicRangeMinimumQuery mq = new DynamicRangeMinimumQuery();
    while (true) {
      // Extend window so at least k different input strings are included.
      while (head < lcp.length && npos < k) {
        if (head > 0) mq.enqueue(lcp[head]);
        if (cnt[type[sa[head++]]]++ == 0) npos++;
      }
      if (npos < k) break;

      while (npos >= k) {
        if (mq.currentMin() > longestMatch) {
          longestMatch = mq.currentMin();
          longestWindows.clear();
        }
        if (longestMatch > 0 && mq.currentMin() == longestMatch && longestWindows.size() < max) {
          // This will cause multiple matches with same substring
          // if substring exists in more than k strings
          longestWindows.add(new Window(tail, head));
        }

        // Decrease size of window
        if (--cnt[type[sa[tail++]]] == 0) npos--;
        mq.dequeue();
      }
    }

    // Convert "internal matches" to external matches
    ArrayList<Match> matches = new ArrayList<>(longestWindows.size());
    for (int i = 0; i < longestWindows.size(); i++) {
      Window w = longestWindows.get(i);
      // Merge overlapping matches so we can find where more than k strings matches
      // Will also eliminate substring duplicates.
      int wstart = w.start, wend = w.end;
      while (i + 1 < longestWindows.size() && longestWindows.get(i+1).start < wend) {
        wend = Math.max(wend, longestWindows.get(++i).end);
      }

      int ofs[] = null;
      if (includeOffsets) {
        // In case of many matches and many strings, returning all offsets
        // could affect complexity. Therefore make it optional.
        ofs = new int[strings.size()];
        for (int j = 0; j < strings.size(); j++) {
          ofs[j] = -1;
        }
        for (int j = wstart; j < wend; j++) {
          int t = type[sa[j]], res = sa[j] - start[t];
          // Pick first matching
          if (ofs[t] == -1 || res < ofs[t]) {
            ofs[t] = res;
          }
        }
      }
      matches.add(new Match(longestMatch, string.substring(sa[wstart], sa[wstart] + longestMatch), ofs));
    }
    return matches;
  }
}
