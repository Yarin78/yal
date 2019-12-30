package yarin.yal;

/**
 * Datastructure for storing and updating integer values in an array in log(n) time
 * and answering queries "what is the sum of all value in the array between 0 and x?" in log(n) time
 *
 * Also called Binary Indexed Tree (BIT). See http://codeforces.com/blog/entry/619
 */
public class FenwickTree {
    // 0..(2^EXP)-1 is max range
    private int[] t;
    private final int EXP;

    public FenwickTree(int exp) {
        EXP = exp;
        t = new int[1<<(exp + 1)];
    }

    /**
     * Gets the sum of the values in the range [0, x)
     */
    public int query(int x) {
        return query(x, EXP);
    }

    /**
     * Gets the sum of the values in the range [x, y)
     */
    public int queryRange(int x, int y) {
        return query(y) - query(x);
    }

    private int query(int x, int i)	{
        return x!=0 ? (x&1) * t[(1<<i)+x-1] + query(x/2,i-1) : 0;
    }

    /**
     * Adds the value v to the position x
     */
    public void insert(int x, int v) {
        insert(x, v, EXP);
    }

    private int insert(int x, int v, int i)	{
        return (t[(1<<i)+x]+=v) + (i > 0 ? insert(x/2,v,i-1) : 0);
    }

}
