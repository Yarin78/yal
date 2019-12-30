package yarin.yal;

public class StringMatch {
    public int find(String s, String t) {
        int m = t.length();
        int[] next = new int[m];
        next[0] = -1;

        for (int i = 1; i < m; i++) {
            int j = next[i - 1];
            while (j >= 0 && t.charAt(i - 1) != t.charAt(j)) {
                j = next[j];
            }
            next[i] = j + 1;
        }

        int sp = 0, tp = 0;
        while (sp < s.length())	{
            if (t.charAt(tp) == s.charAt(sp)) {
                tp++;
                sp++;
            }else {
                tp = next[tp];
                if (tp == -1) {
                    tp = 0;
                    sp++;
                }
            }
            if (tp == m) {
                return sp - m;
            }
        }

        return -1;
    }
}

