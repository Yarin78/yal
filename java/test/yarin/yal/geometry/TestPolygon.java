package yarin.yal.geometry;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestPolygon {

    @Test
    public void testConvexHull1() {
        List<Point> al = new ArrayList<Point>();
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++)
                al.add(new Point(i, j));
        Polygon p = Polygon.convexHull(new Polygon(al));
        Assert.assertEquals(4, p.points.size());
    }

    @Test
    public void testConvexHull2() {
        List<Point> inputPoly = new ArrayList<Point>() {{
            add(new Point(1,2)); add(new Point(4,1)); add(new Point(6,1)); add(new Point(5,3));
            add(new Point(3,3)); add(new Point(7,3)); add(new Point(8,4)); add(new Point(6,5));
            add(new Point(4,5)); add(new Point(2,5)); add(new Point(3,7)); add(new Point(5,6));
        }};

        List<Point> expectedPoly = new ArrayList<Point>() {{
            add(new Point(1,2)); add(new Point(2,5)); add(new Point(3,7)); add(new Point(5,6));
            add(new Point(8,4)); add(new Point(6,1)); add(new Point(4,1));
        }};

        Polygon actual = Polygon.convexHull(new Polygon(inputPoly));

        Assert.assertEquals(expectedPoly.size(), actual.points.size());
        for (int i = 0; i < actual.points.size(); i++) {
            Assert.assertEquals(expectedPoly.get(i), actual.points.get(i));
        }
    }
}
