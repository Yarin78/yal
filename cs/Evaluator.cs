using System;
using System.Globalization;

namespace Algorithms
{
	/// <summary>
	/// Summary description for Class1.
	/// </summary>
	public class Evaluator
	{
		private static readonly char[][] operators = new[] {
			new[] { '+', '~' },
			new[] { '*', '/'},
			new[] { '^' }
		};

		private static double Eval(string expr, int level)
		{
			if (level == 3) return (expr.Length == 0) ? 0 : double.Parse(expr, CultureInfo.InvariantCulture);
			string[] parts = expr.Split(operators[level]);
			double val = Eval(parts[0], level + 1);
			for (int i = 1, cur = parts[0].Length; i < parts.Length; cur += parts[i++].Length + 1)
			{
				double nval = Eval(parts[i], level + 1);
				switch (expr[cur])
				{
					case '+': val += nval; break;
					case '~': val -= nval; break;
					case '*': val *= nval; break;
					case '/': val /= nval; break;
					case '^': val = Math.Pow(val, nval); break;
				}
			}
			return val;
		}

		public static double FuncEval(string func, double val)
		{
			switch (func)
			{
				case "": return val;
				case "sin": return Math.Sin(val);
				case "cos": return Math.Cos(val);
			}
			throw new Exception("Unknown function: " + func);
		}

		public static double Evaluate(string expr)
		{
			// TODO: This doesn't allow unary minus in the input
			expr = expr.Replace('-', '~'); // Distinguish between unary and binary minus
			int rp;
			while ((rp = expr.IndexOf(')')) >= 0)
			{
				int lp = expr.LastIndexOf('(', rp, rp + 1), llp = lp;
				double val = Eval(expr.Substring(lp + 1, rp - lp - 1), 0);
				while (llp > 0 && Char.IsLetter(expr[llp - 1])) llp--;
				expr = expr.Substring(0, llp) + FuncEval(expr.Substring(llp, lp - llp).ToLower(), val).ToString(CultureInfo.InvariantCulture) + expr.Substring(rp + 1);
			}
			return Eval(expr, 0);
		}
	}
}