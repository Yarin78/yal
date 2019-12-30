package yarin.yal;

/**
 * Datastructure for storing and updating integer values in an array in log(n) time
 * and answering queries "what is the sum of all value in the array between 0 and x?" in log(n) time
 *
 * Also called Binary Indexed Tree (BIT). See http://codeforces.com/blog/entry/619
 */
public class FenwickTreeLong {

  // 0..(2^EXP)-1 is max range
  private long[] t;
  private final int EXP;

  public FenwickTreeLong(int exp) {
    EXP = exp;
    t = new long[1<<(exp + 1)];
  }

  /**
   * Gets the sum of the values in the range [0, x)
   */
  public long query(int x) {
    return query(x, EXP);
  }

  /**
   * Gets the sum of the values in the range [x, y)
   */
  public long queryRange(int x, int y) {
    return query(y) - query(x);
  }

  private long query(int x, int i)	{
    return x!=0 ? (x&1) * t[(1<<i)+x-1] + query(x/2,i-1) : 0;
  }

  /**
   * Adds the value v to the position x
   */
  public void insert(int x, long v) {
    insert(x, v, EXP);
  }

  private long insert(int x, long v, int i)	{
    return (t[(1<<i)+x]+=v) + (i > 0 ? insert(x/2,v,i-1) : 0);
  }

}
