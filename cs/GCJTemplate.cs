using System;
using System.IO;

public class MyProblem
{
	static void Main(string[] args)
	{
		TextReader sr = new StreamReader(@"c:\temp\c.in");
//		TextReader sr = Console.In;
//		TextWriter sw = new StreamWriter(@"c:\temp\c.out");
		TextWriter sw = Console.Out;

		int N = int.Parse(sr.ReadLine());
		for (int caseNo = 1; caseNo <= N; caseNo++)
		{
			sw.Write("Case #" + caseNo + ": ");
			Solve(sr, sw);
		}
		sw.Close();
	}

	private static void Solve(TextReader sr, TextWriter sw)
	{

	}

}