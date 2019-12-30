package yarin.yal.geometry;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class TestSkewedDiamond {
  @Test
  public void constructTest() {
    SkewedDiamond d1 = new SkewedDiamond(0, 0, 2);
    SkewedDiamond d2 = new SkewedDiamond(2, 1, 2);

    Assert.assertEquals(-2, d1.a);
    Assert.assertEquals(2, d1.b);
    Assert.assertEquals(-2, d1.c);
    Assert.assertEquals(2, d1.d);

    Assert.assertEquals(1, d2.a);
    Assert.assertEquals(5, d2.b);
    Assert.assertEquals(-1, d2.c);
    Assert.assertEquals(3, d2.d);
  }

  @Test
  public void intersectTest() {
    SkewedDiamond d1 = new SkewedDiamond(0, 0, 2);
    SkewedDiamond d2 = new SkewedDiamond(2, 1, 2);
    SkewedDiamond d3 = d1.intersect(d2);

    Assert.assertEquals(1, d3.a);
    Assert.assertEquals(2, d3.b);
    Assert.assertEquals(-1, d3.c);
    Assert.assertEquals(2, d3.d);

    Assert.assertEquals(1.5, d3.area(), 1e-9);
  }

  @Test
  public void intersectTestEmpty() {
    SkewedDiamond d1 = new SkewedDiamond(0, 0, 2);
    SkewedDiamond d2 = new SkewedDiamond(4, 0, 2);
    SkewedDiamond d3 = d1.intersect(d2);

    Assert.assertNotNull(d3);
    Assert.assertEquals(0, d3.area(), 1e-9);

    SkewedDiamond d4 = d1.intersect(new SkewedDiamond(4, 1, 2));
    Assert.assertNull(d4);
  }

  @Test
  public void cornersTest() {
    SkewedDiamond d1 = new SkewedDiamond(0, 0, 2);
    SkewedDiamond d2 = new SkewedDiamond(2, 1, 2);
    SkewedDiamond d3 = d1.intersect(d2);

    List<PointD> corners = d3.getCorners();

    Assert.assertEquals(new PointD(0, 1), corners.get(0));
    Assert.assertEquals(new PointD(0.5, 1.5), corners.get(1));
    Assert.assertEquals(new PointD(2.0, 0.0), corners.get(2));
    Assert.assertEquals(new PointD(1.5, -0.5), corners.get(3));
  }
}
