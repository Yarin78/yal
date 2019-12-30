package yarin.yal.mathlib;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class TestPolynomial {

    @Test
    public void testPolynomial() {
        // (x-3) * (2x+1.3) * (7x-9) = (2x^2 +1.3x -6x -3.9) * (7x-9) = (2x^2-4.7x-3.9)*(7x-9)
        // = (14x^3-18x^2-32.9x^2+42.3x-27.3x+35.1) = 14x^3 - 50.9x^2 + 15x + 35.1
        // (2x+1.3)*(7x-9) = 14x^2 -18x+9.1x - 11.7 = 14x^2 -8.9x - 11.7
        final double EPS = 1e-9;
        Polynomial p = new Polynomial(new double[] { 35.1, 15, -50.9, 14 });
        Assert.assertEquals(0, p.eval(3), EPS);
        Assert.assertEquals(0, p.eval(-1.3 / 2), EPS);
        Assert.assertEquals(0, p.eval(9 / 7.0), EPS);
        Assert.assertEquals(14 - 50.9 + 15 + 35.1, p.eval(1), EPS);

        Polynomial divp = p.divRoot(3);
        Assert.assertEquals(-11.7, divp.get(0), EPS);
        Assert.assertEquals(-8.9, divp.get(1), EPS);
        Assert.assertEquals(14, divp.get(2), EPS);

        Polynomial diff = p.diff();
        Assert.assertEquals(15, diff.get(0), EPS);
        Assert.assertEquals(-50.9 * 2, diff.get(1), EPS);
        Assert.assertEquals(14 * 3, diff.get(2), EPS);

        List<Double> roots = p.findRoots(-1000, 1000);
        Assert.assertEquals(-0.65, roots.get(0), EPS);
        Assert.assertEquals(9 / 7.0, roots.get(1), EPS);
        Assert.assertEquals(3, roots.get(2), EPS);
    }

}
