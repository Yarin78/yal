package yarin.yal;

import org.junit.Assert;
import org.junit.Test;

public class TestSetRange {

    @Test
    public void testSetRange() {
        FenwickTree setRange = new FenwickTree(15);

        setRange.insert(5, 3);
        setRange.insert(8, 3);
        setRange.insert(11, 5);
        setRange.insert(4, 1);
        setRange.insert(5, -1);

        Assert.assertEquals(setRange.query(4), 0);
        Assert.assertEquals(setRange.query(5), 1);
        Assert.assertEquals(setRange.query(6), 3);
        Assert.assertEquals(setRange.query(15), 11);

        Assert.assertEquals(setRange.queryRange(5, 9), 5);
        Assert.assertEquals(setRange.queryRange(5, 11), 5);
        Assert.assertEquals(setRange.queryRange(5, 12), 10);
    }
}
