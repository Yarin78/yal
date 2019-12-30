package yarin.yal;

import org.junit.Assert;
import org.junit.Test;

public class TestStringMatch {

    @Test
    public void testStringMatch() {
        StringMatch sm = new StringMatch();

        String s = "ababcabccabcdacdabcbcbdbaabacabbaabccabb";
        for(int i=0;i<s.length();i++) {
            for(int j=1;i+j<=s.length();j++) {
                String t = s.substring(i, j + i);
                Assert.assertEquals(s.indexOf(t), sm.find(s, t));
            }
        }
    }
}
