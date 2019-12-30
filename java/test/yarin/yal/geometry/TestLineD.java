package yarin.yal.geometry;

import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

public class TestLineD {

  public static final double EPS = 1e-9;

  @Test
  public void testGetEquation() {
    Random random = new Random(0);
    for (int i = 0; i < 1000; i++) {
      double x1 = random.nextDouble() * 200 - 100;
      double y1 = random.nextDouble() * 200 - 100;
      double x2 = random.nextDouble() * 200 - 100;
      double y2 = random.nextDouble() * 200 - 100;
      LineD line = new LineD(x1, y1, x2, y2);
      double[] eq = line.getEquation();
      verifyEquation(line, eq[0], eq[1], eq[2]);
    }
  }

  @Test
  public void testGetEquationStraightLines() {
    LineD line = new LineD(0, 0, 5, 0);
    double[] eq = line.getEquation();
    verifyEquation(line, eq[0], eq[1], eq[2]);

    line = new LineD(0, 0, 0, 5);
    eq = line.getEquation();
    verifyEquation(line, eq[0], eq[1], eq[2]);
  }

  public void verifyEquation(LineD line, double a, double b, double c) {
    Assert.assertEquals(a*a+b*b, 1, EPS);
    Assert.assertEquals(a * line.a.x + b * line.a.y, c, EPS);
    Assert.assertEquals(a * line.b.x + b * line.b.y, c, EPS);
  }


}
