package yarin.viz;

import java.awt.*;

import yarin.yal.geometry.Circle;
import yarin.yal.geometry.LineD;
import yarin.yal.geometry.Point;

public class GeoMain {
    public static void main(String args[]) {
        GeoVisualizer viz = new GeoVisualizer(false);
        viz.setVisibilityArea(0, 0, 500, 300);

        viz.addCircle(new Circle(new Point(10, 10), 10), "hejsan");
        viz.addCircle(new Circle(new Point(40, 30), 10), "boo");
        viz.addRect(new LineD(100, 50, 200, 70), Color.red);
        viz.show();
    }

}
