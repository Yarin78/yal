package yarin.yal.geometry;

import java.util.Arrays;
import java.util.List;

/**
 * A shape contained within a <= x + y <= b and c <= x-y <= d
 */
public class SkewedDiamond {
  public int a, b, c, d;

  /**
   * Construct a pure diamond with specified radius
   */
  public SkewedDiamond(int x, int y, int radius) {
    this.a = x+y-radius;
    this.b = x+y+radius;
    this.c = x-y-radius;
    this.d = x-y+radius;
  }

  public SkewedDiamond(int a, int b, int c, int d) {
    this.a = a;
    this.b = b;
    this.c = c;
    this.d = d;
  }

  public double area() {
    return (b-a)*(d-c)/2.0;
  }

  public SkewedDiamond intersect(SkewedDiamond other) {
    int a = Math.max(this.a, other.a), b = Math.min(this.b, other.b);
    int c = Math.max(this.c, other.c), d = Math.min(this.d, other.d);
    if (a > b || c > d) return null; // No overlap
    return new SkewedDiamond(a, b, c, d); // Overlap, but may have zero area
  }

  /**
   * Gets the four corners in clockwise order
   * @return
   */
  public List<PointD> getCorners() {
    double x1 = (a+c) / 2.0, y1 = a - x1;
    double x2 = (b+c) / 2.0, y2 = b - x2;
    double x3 = (b+d) / 2.0, y3 = b - x3;
    double x4 = (a+d) / 2.0, y4 = a - x4;
    return Arrays.asList(new PointD(x1, y1), new PointD(x2, y2), new PointD(x3, y3), new PointD(x4, y4));
  }
}
