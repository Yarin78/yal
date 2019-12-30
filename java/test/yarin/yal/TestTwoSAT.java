package yarin.yal;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import yarin.yal.TwoSAT;

public class TestTwoSAT {

    @Test
    public void testSimple() {
        TwoSAT sat = new TwoSAT(5);
        // a = true, b = false, c = false, d = true, e = true;
        // (a OR b) AND (!b OR !c) AND (!c OR !e) AND (d OR e) AND (!e OR a) AND (d OR c) AND (e OR e)
        sat.addClause(0, false, 1, false);
        sat.addClause(1, true, 2, true);
        sat.addClause(2, true, 4, true);
        sat.addClause(3, false, 4, false);
        sat.addClause(4, true, 0, false);
        sat.addClause(3, false, 2, false);
        sat.addClause(4, false, 4, false);

        boolean[] solve = sat.solve();

        Assert.assertTrue(solve[0]);
        Assert.assertFalse(solve[1]);
        Assert.assertFalse(solve[2]);
        Assert.assertTrue(solve[3]);
        Assert.assertTrue(solve[4]);
    }

    @Test
    public void testSimple2() {
        TwoSAT sat = new TwoSAT(7);
        String s = "(0|2)&(0|!3)&(1|!3)&(1|!4)&(2|!4)&(0|!5)&(1|!5)&(2|!5)&(3|6)&(4|6)&(5|6)";
        List<Clause> clauses = new ArrayList<>();
        for (String clause : s.split("&")) {
            String[] parts = clause.substring(1, clause.length() - 1).split("\\|");
            boolean negA = false, negB = false;
            if (parts[0].charAt(0) == '!') {
                parts[0] = parts[0].substring(1);
                negA = true;
            }
            if (parts[1].charAt(0) == '!') {
                parts[1] = parts[1].substring(1);
                negB = true;
            }
            int term1 = Integer.parseInt(parts[0]);
            int term2 = Integer.parseInt(parts[1]);
            clauses.add(new Clause(term1, term2, negA, negB));
            sat.addClause(term1, negA, term2, negB);
        }

        boolean[] result = sat.solve();

        // Verify solution is correct (may be different from the "expected" solution!)
        for (Clause clause : clauses) {
            Assert.assertTrue(clause.isTrue(result));
        }
    }

    @Test
    public void testRandom() {
        Random rnd = new Random(0);
        for(int cases = 0; cases < 10; cases ++) {
            int n = 10000, m = 30000;
            boolean[] expected = new boolean[n];
            ArrayList<Clause> clauses = new ArrayList<>(m);
            // Generate a possible solution
            for (int i = 0; i < n; i++) {
                expected[i] = rnd.nextBoolean();
            }
            TwoSAT sat = new TwoSAT(n);
            // Generate clauses
            while (clauses.size() < m) {
                Clause clause = new Clause(rnd.nextInt(n), rnd.nextInt(n), rnd.nextBoolean(), rnd.nextBoolean());
                if (clause.isTrue(expected)) {
                    clauses.add(clause);
                    sat.addClause(clause.a, clause.negA, clause.b, clause.negB);
                }
            }

            // Run
            boolean[] answer = sat.solve();

            // Verify solution is correct (may be different from the "expected" solution!)
            for (Clause clause : clauses) {
                Assert.assertTrue(clause.isTrue(answer));
            }
        }
    }

    @Test
    public void testImpossible() {
        TwoSAT sat = new TwoSAT(3);

        sat.addClause(0, false, 1, false);
        sat.addClause(0, false, 1, true);
        sat.addClause(0, true, 1, false);
        sat.addClause(0, true, 1, true);

        Assert.assertNull(sat.solve());
    }

    public static class Clause {
        public int a, b;
        public boolean negA, negB;

        public Clause(int a, int b, boolean negA, boolean negB) {
            this.a = a;
            this.b = b;
            this.negA = negA;
            this.negB = negB;
        }

        public boolean isTrue(boolean[] values) {
            boolean ba = values[a], bb = values[b];
            if (negA) ba = !ba;
            if (negB) bb = !bb;
            return ba || bb;
        }
    }
}
