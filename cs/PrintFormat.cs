using System;

namespace Algorithms
{
	/// <summary>
	/// Summary description for PrintFormat.
	/// </summary>
	public class PrintFormat
	{
		public PrintFormat()
		{
			Console.WriteLine(String.Format("[{0:N2}]", 1.2345)); // [1.23]
			Console.WriteLine(String.Format("[{0}]", 1.2345));  // [1.2345]
			Console.WriteLine(String.Format("[{0,6:N2}]", 1.2345));  // [  1.23]
			Console.WriteLine(String.Format("[{0,6:D4}]", 3));  // [  0003]
			Console.WriteLine(String.Format("[{0:x3}]", 27));  // [01b]
			Console.WriteLine(String.Format("[{0,3:X}]", 255));  // [ FF]
			Console.Out.Flush();
		}
		
	}
}
