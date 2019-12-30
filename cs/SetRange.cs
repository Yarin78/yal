namespace Algorithms
{
	/// <summary>
	/// Datastructure for storing and updating integer values in an array in log(n) time
	/// and answering queries "what is the sum of all value in the array between 0 and x?" in log(n) time
	/// </summary>
	public class SetRange
	{
		// 0..(2^EXP)-1 is max range
		private int[] t;
		private readonly int EXP;

		public SetRange(int exp)
		{
			EXP = exp;
			t = new int[1<<(exp + 1)];
		}

		public int Query(int x)
		{
			return Query(x, EXP);
		}

		private int Query(int x, int i)
		{
			return x!=0 ? (x&1) * t[(1<<i)+x-1] + Query(x/2,i-1) : 0;
		}
		
		public void Insert(int x, int v)
		{
			Insert(x, v, EXP);
		}

		private int Insert(int x, int v, int i)
		{
			return (t[(1<<i)+x]+=v) + (i > 0 ? Insert(x/2,v,i-1) : 0);
		}

	}
}