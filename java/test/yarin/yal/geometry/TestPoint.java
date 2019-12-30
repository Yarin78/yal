package yarin.yal.geometry;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class TestPoint {
    @Test
    public void testPointClass() {
        Point p1 = new Point(5, 7);
        Point p2 = new Point(5, 7);
        Point p3 = new Point(5, 2);
        Assert.assertFalse(p1 == p2);
        Assert.assertTrue(p1 != p2);
        Assert.assertTrue(p1.equals(p2));
        Assert.assertFalse(p1.equals(p3));
        Point p4 = Point.add(p1, p3);
        Assert.assertTrue(p4.equals(new Point(10, 9)));
        Point p5 = new Point(2, 7);
        Assert.assertTrue(Point.sub(p4, p5).equals(new Point(8, 2)));
        Assert.assertTrue(Point.mul(p4, 2).equals(new Point(20, 18)));
        Assert.assertTrue(Point.div(p3, 3).equals(new Point(1, 0)));

        Assert.assertTrue(Point.det(p1, p4) == -25);
        Assert.assertTrue(Point.dot(p1, p4) == 113);
        Assert.assertTrue(p1.getNormSqr() == 74);

        Assert.assertTrue(Point.cross(p1, p2, p3) == 0);
        Assert.assertTrue(Point.cross(new Point(2, 7), new Point(-13, 2), new Point(32, 17)) == 0);
        Assert.assertTrue(Point.cross(new Point(2, 7), new Point(-13, 1), new Point(32, 17)) > 0);
        Assert.assertTrue(Point.cross(new Point(2, 7), new Point(-13, 3), new Point(32, 17)) < 0);
    }



    @Test
    public void testSortingPoints1() {
        List<Point> points = new ArrayList<Point>();
        for (int y = -4; y <= 4; y++) {
            for(int x=-4;x <=4;x++)	{
                if (Math.abs(x)+Math.abs(y)==4)	{
                    points.add(new Point(x, y));
                }
            }
        }

        points.add(new Point(6, 2));
        points.add(new Point(-4, 4));
        List<Point> points2 = new ArrayList<Point>(points);
        Collections.sort(points, new Polygon.PolarPointComparer());
        Collections.sort(points2, new AnglePointComparer());
        for (int i = 0; i < points.size(); i++) {
            Point point = points.get(i);
            Point point2 = points2.get(i);
            Assert.assertEquals(point, point2);
        }

        // Make sure the special case when x=0 is working properly
        points = new ArrayList<Point>()
        {{
                add(new Point(0, -5));
                add(new Point(0, -2));
                add(new Point(0, 5));
                add(new Point(0, 2));
                add(new Point(0, 8));
                add(new Point(0, -8));
            }};

        List<Point> expectedPoints = new ArrayList<Point>()
        {{
                add(new Point(0, 2));
                add(new Point(0, 5));
                add(new Point(0, 8));
                add(new Point(0, -2));
                add(new Point(0, -5));
                add(new Point(0, -8));
            }};
        Collections.sort(points, new Polygon.PolarPointComparer());
        for (int i = 0; i < points.size(); i++) {
            Assert.assertEquals(expectedPoints.get(i), points.get(i));
        }
    }

    @Test
    public void testSortingPoints2() {
        Random random = new Random(0);
        Polygon.PolarPointComparer polyComparer = new Polygon.PolarPointComparer();
        AnglePointComparer angleComparer = new AnglePointComparer();
        for (int test = 0; test < 100; test++) {
            List<Point> points1 = new ArrayList<Point>();
            List<Point> points2 = new ArrayList<Point>();
            for (int i = 0; i < 100; i++) {
                int x = random.nextInt(21) - 10;
                int y = random.nextInt(21) - 10;
                if (x == 0 && y == 0) {
                    i--;
                    continue;
                }
                Point point = new Point(x,y);
                points1.add(point);
                points2.add(point);
            }

            for (int i = 0; i < 100; i++) {
                for (int j = 0; j < 100; j++) {
                    int d1 = angleComparer.compare(points1.get(i), points1.get(j));
                    int d2 = polyComparer.compare(points1.get(i), points1.get(j));
                    if (d1 < 0) d1 = -1;
                    if (d1 > 0) d1 = 1;
                    if (d2 < 0) d2 = -1;
                    if (d2 > 0) d2 = 1;
                    if (d1 != d2) {
                        System.out.println(points1.get(i) + " - " + points1.get(j) + " " + d1 + " " + d2);
                    }
                    Assert.assertEquals(d1, d2);
                }
            }

            Collections.sort(points1, angleComparer);
            Collections.sort(points2, polyComparer);

            for (int i = 0; i < 100; i++) {
                Assert.assertEquals(points1.get(i), points2.get(i));
            }
        }
    }

    public static class AnglePointComparer implements Comparator<Point> {
        public int compare(Point x, Point y)	{
            double a1 = Math.atan2(x.y, x.x), a2 = Math.atan2(y.y, y.x);
            if (Math.abs(a1 - a2) < 1e-9)
                return x.getNormSqr() - y.getNormSqr();
            a1 = Math.PI/2 - a1;
            a2 = Math.PI/2 - a2;
            if (a1 < 0) a1 += 2*Math.PI;
            if (a2 < 0) a2 += 2*Math.PI;
            if (a1 < a2)
                return -1;
            return 1;
        }
    }

    @Test
    public void testClosestPairSimple() {
        ArrayList<PointD> points = new ArrayList<>();
        points.add(new PointD(1,2));
        points.add(new PointD(2,4));
        points.add(new PointD(3,3));

        double v = PointD.closestPair(points, null);
        Assert.assertEquals(v, Math.sqrt(2), 1e-6);
    }

    @Test
    public void testClosestPairRandom() {
        Random random = new Random(0);
        for (int i = 0; i < 100; i++) {
            int n = i*5+3;
            ArrayList<PointD> points = new ArrayList<>(n);
            double closest = Double.MAX_VALUE;
            for (int j = 0; j < n; j++) {
                PointD p = new PointD(random.nextDouble() * 1000, random.nextDouble() * 1000);
                points.add(p);
                for (int k = 0; k < j; k++) {
                    closest = Math.min(closest, PointD.sub(points.get(k), p).getNormSqr());
                }
            }
            closest = Math.sqrt(closest); // expected

            PointD[] pair = new PointD[2];
            double actual = PointD.closestPair(points, pair);

            Assert.assertEquals(closest, actual, 1e-6);
            Assert.assertTrue(points.contains(pair[0]));
            Assert.assertTrue(points.contains(pair[1]));
            Assert.assertEquals(Math.sqrt(PointD.sub(pair[0], pair[1]).getNormSqr()), actual, 1e-6);
        }
    }

    @Test
    public void testClosestPointSame() {
        // There are two identical points
        Random random = new Random(0);
        for (int i = 0; i < 100; i++) {
            int n = i*5+3;
            ArrayList<PointD> points = new ArrayList<>(n);
            for (int j = 0; j < n; j++) {
                PointD p = new PointD(random.nextDouble() * 1000, random.nextDouble() * 1000);
                points.add(p);
            }

            int a, b;
            do {
                a = random.nextInt(n);
                b = random.nextInt(n);
            } while (a == b);
            points.set(b, new PointD(points.get(a).x, points.get(a).y));

            PointD[] pair = new PointD[2];
            double actual = PointD.closestPair(points, pair);

            Assert.assertEquals(0.0, actual, 1e-6);
            Assert.assertTrue(points.contains(pair[0]));
            Assert.assertTrue(points.contains(pair[1]));
            Assert.assertTrue(pair[0] != pair[1]);
            Assert.assertEquals(pair[0], pair[1]);
        }
    }
}
