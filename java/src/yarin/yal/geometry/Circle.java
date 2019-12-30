package yarin.yal.geometry;

public class Circle {
    private Point center;
    private int radius;

    public Point getCenter() {
        return center;
    }

    public int getRadius() {
        return radius;
    }

    public Circle(Point center, int radius)	{
        this.center = center;
        this.radius = radius;
    }

    /*
                public static int CheckFourthPoint(Point p1, Point p2, Point p3, Point p4)
                {
                    // axay  ax2ay2 1   a-d
                    // TODO
                    return 0;
                }*/
}

