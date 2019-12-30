package yarin.yal;

import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

public class TestDynamicRangeMinimumQuery {

  @Test
  public void testSimpleMinQuery() {
    DynamicRangeMinimumQuery mq = new DynamicRangeMinimumQuery();

    mq.enqueue(3);
    mq.enqueue(5);
    mq.enqueue(2);
    mq.enqueue(7);
    mq.enqueue(8);
    mq.enqueue(6);
    mq.enqueue(9);
    mq.enqueue(10);

    Assert.assertEquals(2, mq.currentMin());
    mq.dequeue(); // 3
    Assert.assertEquals(2, mq.currentMin());
    mq.dequeue(); // 5
    Assert.assertEquals(2, mq.currentMin());
    mq.dequeue(); // 2
    Assert.assertEquals(6, mq.currentMin());
    mq.dequeue(); // 7
    Assert.assertEquals(6, mq.currentMin());
    mq.dequeue(); // 8
    Assert.assertEquals(6, mq.currentMin());
    mq.dequeue(); // 6
    Assert.assertEquals(9, mq.currentMin());
    mq.dequeue(); // 9
    Assert.assertEquals(10, mq.currentMin());
    mq.dequeue(); // 10
    Assert.assertEquals(Integer.MAX_VALUE, mq.currentMin());
  }

  @Test
  public void testRandom() {
    Random random = new Random(0);
    for (int cases = 0; cases < 100; cases++) {
      int[] a = new int[1000];
      for (int i = 0; i < a.length; i++) {
        a[i] = random.nextInt(1000);
      }
      DynamicRangeMinimumQuery mq = new DynamicRangeMinimumQuery();
      int head = 0, tail = 0;
      while (tail < a.length) {
        if (tail > head) throw new RuntimeException();
        if (head < a.length && random.nextDouble() > (head - tail) / 20.0) {
          mq.enqueue(a[head++]);
        } else {
          mq.dequeue();
          tail++;
        }
        int expected = Integer.MAX_VALUE;
        for (int i = tail; i < head; i++) {
          expected = Math.min(expected, a[i]);
        }
        Assert.assertEquals(expected, mq.currentMin());
      }
      Assert.assertEquals(a.length, head);
      Assert.assertEquals(a.length, tail);
    }
  }

}
