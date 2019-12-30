package yarin.yal;

import org.junit.Assert;
import org.junit.Test;

public class TestExpressionEvaluator {

    private final static double DELTA = 1e-9;

    @Test
    public void testEvaluator() {
        // TODO: Unary minus in input is not allowed
        double d = ExpressionEvaluator.Evaluate("5+(9*(0-7))");
        Assert.assertEquals(-58.0, d, 1e-6);

        d = ExpressionEvaluator.Evaluate("sin(2^9*3)");
        Assert.assertEquals(Math.sin(512 * 3), d, DELTA);

        d = ExpressionEvaluator.Evaluate("5.3+2.9");
        Assert.assertEquals(8.2, d, 1e-8);

        d = ExpressionEvaluator.Evaluate("1+2*3^4");
        Assert.assertEquals(d, 163.0, 1e-6);

        d = ExpressionEvaluator.Evaluate("1^2*3+4");
        Assert.assertEquals(d, 7.0, 1e-6);
    }
}
