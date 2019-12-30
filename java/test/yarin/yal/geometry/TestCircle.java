package yarin.yal.geometry;

import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

public class TestCircle {

  private static final double EPS = 1e-9;

  @Test
  public void testTangentLines() {
    Random random = new Random(0);
    for (int i = 0; i < 1000; i++) {
      CircleD circle = new CircleD(
          random.nextDouble() * 200 - 100,
          random.nextDouble() * 200 - 100,
          random.nextDouble() * 20);
      PointD p = new PointD(random.nextDouble() * 200 - 100, random.nextDouble() * 200 - 100);

      if (PointD.sub(circle.getCenter(), p).getNormSqr() < circle.getRadius() * circle.getRadius() + EPS) {
        // Point must be outside circle
        i--;
        continue;
      }

      PointD[] tp = circle.findTangentPoints(p);
      Assert.assertTrue(PointD.sub(tp[0], tp[1]).getNormSqr() > EPS);
      verifyTangent(circle, p, tp[0]);
      verifyTangent(circle, p, tp[1]);
    }
  }

  private void verifyTangent(CircleD circle, PointD point, PointD tangentPoint) {
    Assert.assertEquals(PointD.sub(tangentPoint, circle.getCenter()).getNormSqr(), circle.getRadius() * circle.getRadius(), EPS);

    Assert.assertEquals(PointD.sub(point, tangentPoint).getNormSqr() + circle.getRadius() * circle.getRadius(), PointD.sub(point, circle.getCenter()).getNormSqr(), EPS);
  }

}
