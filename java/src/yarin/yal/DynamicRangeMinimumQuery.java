package yarin.yal;

import java.util.Stack;

public class DynamicRangeMinimumQuery {

  /**
   * Keeps track of the minimum value among a current set of active values
   * in amoritzed O(n) time (n = number of elements)
   */

  private Stack<Integer> s1 = new Stack<>(), s2 = new Stack<>();
  private Stack<Integer> m1 = new Stack<>(), m2 = new Stack<>();
  private int min1 = Integer.MAX_VALUE, min2 = Integer.MAX_VALUE;

  public void enqueue(int value) {
    s2.push(value);
    m2.push(min2);
    min2 = Math.min(value, min2);
  }

  public void dequeue() {
    if (s1.size() == 0) {
      while (s2.size() > 0) {
        int old = s2.pop();
        min2 = m2.pop();
        s1.push(old);
        m1.push(min1);
        min1 = Math.min(old, min1);
      }
    }
    min1 = m1.pop();
    s1.pop();
  }

  public int[] getAll() {
    Integer[] t1 = new Integer[s1.size()], t2 = new Integer[s2.size()];
    s1.copyInto(t1);
    s2.copyInto(t2);
    int[] tmp = new int[t1.length + t2.length];
    for (int i = 0; i < t1.length; i++) {
      tmp[i] = t1[i];
    }
    for (int i = 0; i < t2.length; i++) {
      tmp[i + t1.length] = t2[i];
    }
    return tmp;
  }

  public int currentMin() {
    return Math.min(min1, min2);
  }

}
