package yarin.yal.geometry;

import junit.framework.Assert;
import org.junit.Test;

public class TestLine {

    @Test
    public void testLine() {
        Line l1 = new Line(2, 2, 11, 5);
        Line l2 = new Line(3, 7, 5, 3);
        Line l3 = new Line(6, 0, 9, 6);
        Line l4 = new Line(10, 2, 10, 8);
        Line l5 = new Line(12, 1, 13, 3);
        Line l6 = new Line(0, 7, 12, 7);
        Point p = new Point();

        Assert.assertTrue(Line.isParallel(l3, l5));
        Assert.assertFalse(Line.isParallel(l2, l4));
        Assert.assertTrue(Line.intersect(l2, l6, p));
        Assert.assertTrue(p.equals(new Point(3, 7)));
        Assert.assertTrue(Line.intersect(l3, l1, p));
        Assert.assertTrue(p.equals(new Point(8, 4)));
        p.x = -100;
        Assert.assertTrue(Line.intersect(l1, l4, p));
        Assert.assertTrue(p.x == -100);
        Assert.assertTrue(Line.intersect(l4, l6, p));
        Assert.assertTrue(p.equals(new Point(10, 7)));
        Assert.assertTrue(Line.intersect(l5, l1));
        Assert.assertFalse(Line.intersect(l3, l5));

        Assert.assertTrue(new Point(4, 5).liesOn(l2));
        Assert.assertTrue(new Point(12, 1).liesOn(l5));
        Assert.assertTrue(new Point(-1, 1).liesOn(l1));
        Assert.assertTrue(new Point(14, 6).liesOn(l1));
        Assert.assertTrue(new Point(5, 3).liesOn(l1));
        Assert.assertFalse(new Point(5, 2).liesOn(l1));
        double d1 = new Point(4, 2).distance(l1);
        Assert.assertTrue(Math.abs(d1 - 0.63245553203367588) < 1e-10);
        double d2 = new Point(-23, -7).distance(l3);
        Assert.assertTrue(Math.abs(d2 - 22.807893370497855) < 1e-10);
        Assert.assertTrue(new Point(14832, 6).distance(l6) == 1);
    }

    @Test
    public void testLineSeg() {
        Line l1 = new Line(2, 2, 11, 5);
        Line l2 = new Line(3, 7, 5, 3);
        Line l3 = new Line(6, 0, 9, 6);
        Line l4 = new Line(10, 2, 10, 8);
        Line l5 = new Line(12, 1, 13, 3);
        Line l6 = new Line(0, 7, 12, 7);
        Point p = new Point();

        Assert.assertTrue(Line.isParallel(l3, l5));
        Assert.assertFalse(Line.isParallel(l2, l4));
        Assert.assertTrue(Line.intersectSegment(l2, l6, p));
        Assert.assertTrue(p.equals(new Point(3, 7)));
        Assert.assertTrue(Line.intersectSegment(l3, l1, p));
        Assert.assertTrue(p.equals(new Point(8, 4)));
        p.x = -100;
        Assert.assertTrue(Line.intersectSegment(l1, l4, p));
        Assert.assertTrue(p.x == -100);
        Assert.assertTrue(Line.intersectSegment(l4, l6, p));
        Assert.assertTrue(p.equals(new Point(10, 7)));
        Assert.assertFalse(Line.intersectSegment(l5, l1));
        Assert.assertFalse(Line.intersectSegment(l3, l5));
        Assert.assertFalse(Line.intersectSegment(l3, l6));
        Assert.assertFalse(Line.intersectSegment(l6, l3));

        Assert.assertTrue(new Point(4, 5).liesOnSegment(l2));
        Assert.assertTrue(new Point(12, 1).liesOnSegment(l5));
        Assert.assertFalse(new Point(-1, 1).liesOnSegment(l1));
        Assert.assertFalse(new Point(14, 6).liesOnSegment(l1));
        Assert.assertTrue(new Point(5, 3).liesOnSegment(l1));
        Assert.assertFalse(new Point(5, 2).liesOnSegment(l1));
        double d1 = new Point(4, 2).distanceSegment(l1);
        Assert.assertTrue(Math.abs(d1 - 0.63245553203367588) < 1e-10);
        double d2 = new Point(-23, -7).distanceSegment(l3);
        Assert.assertTrue(Math.abs(d2 - 29.832867780352597) < 1e-10);
        double d3 = new Point(14832, 7).distanceSegment(l6);
        Assert.assertTrue(Math.abs(d3 - 14820) < 1e-10);

        Assert.assertTrue(Math.abs(l5.segmentLength() - Math.sqrt(5)) < 1e-10);

        Assert.assertNull(Line.mergeSegments(l3, l5));
        Line l7 = new Line(8, 4, 7, 2);
        Assert.assertTrue(Line.mergeSegments(l3, l7).equals(l3));
        Line l8 = new Line(4, -4, 7, 2);
        Assert.assertTrue(Line.mergeSegments(l8, l3).equals(new Line(9, 6, 4, -4)));
        Assert.assertTrue(Line.mergeSegments(new Line(9, 6, 10, 8), l3).equals(new Line(10, 8, 6, 0)));
    }

    @Test
    public void testIntersectSegments() {
        assertIntersectLineSegments(0,5,3,8, 3,5);
        assertIntersectLineSegments(0,5,5,8, 5,5);
        assertIntersectLineSegments(0,3,5,8, -1, -1);

        Assert.assertNull(Line.parallelIntersectSegments(new Line(0, 0, 5, 0), new Line(0, 0, 0, 5)));
    }

    private void assertIntersectLineSegments(int x1, int x2, int x3, int x4, int ex1, int ex2) {
        Line expected = ex1 < 0 ? null : new Line(ex1, 0, ex2, 0);
        Assert.assertEquals(expected, Line.parallelIntersectSegments(new Line(x1, 0, x2, 0), new Line(x3, 0, x4, 0)));
        Assert.assertEquals(expected, Line.parallelIntersectSegments(new Line(x2, 0, x1, 0), new Line(x3, 0, x4, 0)));
        Assert.assertEquals(expected, Line.parallelIntersectSegments(new Line(x1, 0, x2, 0), new Line(x4, 0, x3, 0)));
        Assert.assertEquals(expected, Line.parallelIntersectSegments(new Line(x2, 0, x1, 0), new Line(x4, 0, x3, 0)));

        Assert.assertEquals(expected, Line.parallelIntersectSegments(new Line(x3, 0, x4, 0), new Line(x1, 0, x2, 0)));
        Assert.assertEquals(expected, Line.parallelIntersectSegments(new Line(x3, 0, x4, 0), new Line(x2, 0, x1, 0)));
        Assert.assertEquals(expected, Line.parallelIntersectSegments(new Line(x4, 0, x3, 0), new Line(x1, 0, x2, 0)));
        Assert.assertEquals(expected, Line.parallelIntersectSegments(new Line(x4, 0, x3, 0), new Line(x2, 0, x1, 0)));
    }

    @Test
    public void testOverlappingLineSegments() {
        assertOverlappingLineSegments(0, 5, 3, 8, true);
        assertOverlappingLineSegments(0, 8, 3, 5, true);
        assertOverlappingLineSegments(0, 3, 5, 8, false);

        assertOverlappingLineSegments(0,3,3,8,false);
        assertOverlappingLineSegments(0,3,0,3,true);
        assertOverlappingLineSegments(0,5,0,0,false);
        assertOverlappingLineSegments(0,0,0,0,false);

        assertOverlappingLineSegments(0,3,0,8,true);
        assertOverlappingLineSegments(3,8,0,8,true);
    }

    private void assertOverlappingLineSegments(int x1, int x2, int x3, int x4, boolean expected) {
        Assert.assertEquals(expected, Line.overlapSegments(new Line(x1,0,x2,0), new Line(x3,0,x4,0)));
        Assert.assertEquals(expected, Line.overlapSegments(new Line(x2,0,x1,0), new Line(x3,0,x4,0)));
        Assert.assertEquals(expected, Line.overlapSegments(new Line(x1,0,x2,0), new Line(x4,0,x3,0)));
        Assert.assertEquals(expected, Line.overlapSegments(new Line(x2,0,x1,0), new Line(x4,0,x3,0)));

        Assert.assertEquals(expected, Line.overlapSegments(new Line(x3,0,x4,0), new Line(x1,0,x2,0)));
        Assert.assertEquals(expected, Line.overlapSegments(new Line(x3,0,x4,0), new Line(x2,0,x1,0)));
        Assert.assertEquals(expected, Line.overlapSegments(new Line(x4,0,x3,0), new Line(x1,0,x2,0)));
        Assert.assertEquals(expected, Line.overlapSegments(new Line(x4,0,x3,0), new Line(x2,0,x1,0)));
    }
}
