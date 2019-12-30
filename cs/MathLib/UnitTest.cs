using System;
using System.Collections.Generic;
using NUnit.Framework;

namespace Algorithms.MathLib
{
	[TestFixture]
	public class UnitTest
	{
		[Test]
		public void TestPrimes()
		{
			Primes p = new Primes(1000);
			Assert.IsTrue(p.PrimeList.Count == 168);
			Assert.IsTrue(p.PrimeList[0] == 2);
			Assert.IsTrue(p.PrimeList[1] == 3);
			Assert.IsTrue(p.PrimeList[2] == 5);
			Assert.IsTrue(p.PrimeList[167] == 997);
			int noPrimes = 0;
			for (int i = 1; i <= 1000000; i++)
			{
				bool prime = p.IsPrime(i);
				if (prime)
					noPrimes++;
			}
			Assert.IsTrue(noPrimes == 78498);
		}

		[Test]
		public void TestPrimeFactorize()
		{
			Primes p = new Primes(1000);
			int[] all = p.GetAllPrimeFactors(2 * 3 * 5 * 5 * 19 * 23);
			Assert.IsTrue(all.Length == 6);
			Assert.IsTrue(all[0] == 2 && all[1] == 3 && all[2] == 5 && all[3] == 5 && all[4] == 19 && all[5] == 23);
			int[] unique = p.GetUniquePrimeFactors(2 * 3 * 5 * 5 * 19 * 23);
			Assert.IsTrue(unique.Length == 5);
			Assert.IsTrue(unique[0] == 2 && unique[1] == 3 && unique[2] == 5 && unique[3] == 19 && unique[4] == 23);

			all = p.GetAllPrimeFactors(1009);
			Assert.IsTrue(all.Length == 1 && all[0] == 1009);
		}

		[Test]
		[ExpectedException(typeof(ArgumentException))]
		public void TestFailedPrimeFactorize()
		{
			Primes p = new Primes(10);
			p.GetAllPrimeFactors(101);
		}

		[Test]
		public void TestBigInt()
		{
			for (int k = 0; k < 4; k++)
			{
				for (int i = 0; i < 10000; i += 17)
				{
					for (int j = 0; j < 10000; j += 293)
					{
						int ii = k % 2 == 1 ? i : -i, jj = k / 2 == 1 ? j : -j;
						int jjd = jj == 0 ? 1 : jj;
						int iim = ii < 0 ? -ii : ii, jjm = jjd < 0 ? -jjd : jjd;

						BigInt b1 = new BigInt(ii), b1p = new BigInt(iim);
						BigInt b2 = new BigInt(jj);
						BigInt prod = b1 * b2, sum = b1 + b2, dif = b1 - b2, smallDiv = b1 / jjd;
						long smallMod = b1p % jjm;

						long prodResult = long.Parse(prod.ToString());
						long sumResult = long.Parse(sum.ToString());
						long difResult = long.Parse(dif.ToString());
						long smallDivResult = long.Parse(smallDiv.ToString());
						long smallModResult = long.Parse(smallMod.ToString());

						Assert.IsTrue((ii * jj).ToString() == prod.ToString());

						Assert.IsTrue(ii * jj == prodResult);
						Assert.IsTrue(ii + jj == sumResult);
						Assert.IsTrue(ii - jj == difResult);
						Assert.IsTrue(ii / jjd == smallDivResult);
						Assert.IsTrue(iim % jjm == smallModResult);
					}
				}
			}
		}

		[Test]
		public void TestModInverse()
		{
			var ml = new MathStuff();

			int prime = 100000007;
			for (int x = 2; x * x <= prime; x++)
				if (prime % x == 0)
					throw new Exception();

			for (long i = 1; i <= 1000000; i++)
			{
				long j = ml.ModInverse((int)i, prime);
				Assert.AreEqual(1, (i * j) % prime);
			}
		}

		[Test]
		public void TestChoose()
		{
			var lib = new MathStuff();
			int p = 13, max = 40;
			lib.PrecalcChoose(max, p);
			for (int i = 1; i <= max; i++)
			{
				for (int j = 0; j <= i; j++)
				{
					int expected = lib.Choose(i, j) % p;
					int actual = lib.ChooseMod(i, j, p);
					Assert.AreEqual(expected, actual);
				}
			}
		}

		[Test]
		public void TestSolveLinearEquation()
		{
			// Case #1: Standard equation with 3 unknowns, one unique solution
			Matrix lhs = new Matrix(new double[,] { { 2, 1, -1 }, { -3, -1, 2 }, { -2, 1, 2 } });
			Matrix rhs = new Matrix(new double[,] { { 8 }, { -11 }, { -3 } });
			Matrix aug = lhs.GetAugmentedMatrix(rhs);

			Assert.AreEqual(3, lhs.GetRank());
			Assert.AreEqual(3, aug.GetRank());

			var res = aug.SolveLinearEquation();
			Assert.AreEqual(3, res.Length);
			Assert.AreEqual(2, res[0], 1e-8);
			Assert.AreEqual(3, res[1], 1e-8);
			Assert.AreEqual(-1, res[2], 1e-8);

			// Case #2: More equations than necessary, but still unique solution
			lhs = new Matrix(new double[,] { { 4, -1, 3 }, { 7, 0, 2 }, { 9, 4, 0 }, { -2, -5, 4 } });
			rhs = new Matrix(new double[,] { { 10 }, { 39 }, { 75 }, { -49 } });
			aug = lhs.GetAugmentedMatrix(rhs);

			Assert.AreEqual(3, lhs.GetRank());
			Assert.AreEqual(3, aug.GetRank());

			res = aug.SolveLinearEquation();
			Assert.AreEqual(3, res.Length);
			Assert.AreEqual(7, res[0], 1e-8);
			Assert.AreEqual(3, res[1], 1e-8);
			Assert.AreEqual(-5, res[2], 1e-8);

			// Case #3: Unsolveable
			lhs = new Matrix(new double[,] { { 4, -1, 3 }, { 7, 0, 2 }, { 9, 4, 0 }, { -2, -5, 4 } });
			rhs = new Matrix(new double[,] { { 10 }, { 39 }, { 75 }, { -48 } });
			aug = lhs.GetAugmentedMatrix(rhs);

			Assert.AreEqual(3, lhs.GetRank());
			Assert.AreEqual(4, aug.GetRank()); // Higher rank, no solution!

			res = aug.SolveLinearEquation();
			Assert.IsNull(res);

			// Case #4: Multiple solutions
			lhs = new Matrix(new double[,] { { 2, 3, -4 }, { 2, 3, -4 }, { 1, -1, -1 } });
			rhs = new Matrix(new double[,] { { 36 }, { 36 }, { -1 } });
			aug = lhs.GetAugmentedMatrix(rhs);

			Assert.AreEqual(2, lhs.GetRank());
			Assert.AreEqual(2, aug.GetRank()); // Rank is less than number of variables, so multiple solutions

			res = aug.SolveLinearEquation(); // Another solution is { 10, 8, 3 }
			Assert.AreEqual(6.6, res[0], 1e-8);
			Assert.AreEqual(7.6, res[1], 1e-8);
			Assert.IsNaN(res[2]); // { 6.6, 7.6, 0} is the returned solution
		}

		[Test]
		public void TestInverse()
		{
			// Case #1
			var matrix = new Matrix(new double[,] { { 2, -1, 0 }, { -1, 2, -1 }, { 0, -1, 2 } });

			var det = matrix.GetDeterminant();
			Assert.AreEqual(4, det);

			var inverse = matrix.GetInverse();
			var expMatrix = new Matrix(new[,] { { 3.0 / 4, 1.0 / 2, 1.0 / 4 }, { 1.0 / 2, 1, 1.0 / 2 }, { 1.0 / 4, 1.0 / 2, 3.0 / 4 } });

			Assert.AreEqual(expMatrix, inverse);

			// Case #2
			matrix = new Matrix(new double[,] { { 1, 7, 3 }, { 0, 0, 2 }, { 0, 1, -1 } });
			inverse = matrix.GetInverse();
			expMatrix = new Matrix(new[,] { { 1, -5, -7 }, { 0, 0.5, 1 }, { 0, 0.5, 0 } });

			Assert.AreEqual(expMatrix, inverse);
		}

		[Test]
		public void TestMatrixMultiplicationAndAddition()
		{
			var a = new Matrix(new double[,] { { 1, 0, 2 }, { -1, 3, 1 } });
			var b = new Matrix(new double[,] { { 3, 1 }, { 2, 1 }, { 1, 0 } });

			var c = a * b;

			Assert.AreEqual(2, c.Rows);
			Assert.AreEqual(2, c.Columns);

			Assert.AreEqual(5, c[0, 0]);
			Assert.AreEqual(1, c[0, 1]);
			Assert.AreEqual(4, c[1, 0]);
			Assert.AreEqual(2, c[1, 1]);

			b = new Matrix(new double[,] { { 5, -2, 3 }, { -2, 1, 0 } });
			c = a + b;

			Assert.AreEqual(2, c.Rows);
			Assert.AreEqual(3, c.Columns);

			Assert.AreEqual(6, c[0, 0]);
			Assert.AreEqual(-2, c[0, 1]);
			Assert.AreEqual(5, c[0, 2]);
			Assert.AreEqual(-3, c[1, 0]);
			Assert.AreEqual(4, c[1, 1]);
			Assert.AreEqual(1, c[1, 2]);
		}

		[Test]
		public void TestMatrixInt()
		{
			const int p = 10007;

			var lhs = new MatrixInt(new int[,] { { 15, -30, 180 }, { 8350, 7, -1234 }, { 5000, -1, 3 } }, p);
			var rhs = new MatrixInt(new int[,] { { 3234 }, { 4836 }, { 5274 } }, p);

			var aug = lhs.GetAugmentedMatrix(rhs);

			var res = aug.SolveLinearEquation();
			Assert.AreEqual(3, res.Length);
			Assert.AreEqual(73, res[0]);
			Assert.AreEqual(20, res[1]);
			Assert.AreEqual(182, res[2]);

			var inverse = lhs.GetInverse();
			Assert.AreEqual(lhs * inverse, MatrixInt.CreateIdentity(3, p));
		}

		[Test]
		public void TestMatrixIntRand()
		{
			Random r = new Random(0);

			List<int> largePrimes = new List<int>();

			int cp = 2000000000;
			while (largePrimes.Count < 100)
			{
				bool bad = false;
				for (int i = 2; i * i <= cp && !bad; i++)
				{
					if (cp % i == 0)
						bad = true;
				}
				if (!bad)
					largePrimes.Add(cp);
				cp++;
			}

			foreach (int p in largePrimes)
			{
				int size = r.Next(2, 75);
				int[] expectedRes = new int[size];
				for (int i = 0; i < size; i++)
					expectedRes[i] = r.Next(p);

				MatrixInt lhs = new MatrixInt(size, size, p);
				MatrixInt rhs = new MatrixInt(size, 1, p);

				for (int y = 0; y < size; y++)
				{
					long sum = 0;
					for (int x = 0; x < size; x++)
					{
						int v = r.Next(p);
						lhs[y, x] = v;
						sum += (long)v * expectedRes[x];
						sum %= p;
					}
					rhs[y, 0] = (int)sum;
				}

				var determinant = lhs.GetDeterminant();
				Assert.IsTrue(determinant != 0);

				MatrixInt aug = lhs.GetAugmentedMatrix(rhs);
				var res = aug.SolveLinearEquation();

				Assert.IsNotNull(res);

				for (int i = 0; i < size; i++)
					Assert.AreEqual(expectedRes[i], res[i]);
			}
		}


		[Test]
		public void TestPolynomial()
		{
			// (x-3) * (2x+1.3) * (7x-9) = (2x^2 +1.3x -6x -3.9) * (7x-9) = (2x^2-4.7x-3.9)*(7x-9) 
			// = (14x^3-18x^2-32.9x^2+42.3x-27.3x+35.1) = 14x^3 - 50.9x^2 + 15x + 35.1
			// (2x+1.3)*(7x-9) = 14x^2 -18x+9.1x - 11.7 = 14x^2 -8.9x - 11.7
			const double EPS = 1e-9;
			Polynomial p = new Polynomial(35.1, 15, -50.9, 14);
			Assert.AreEqual(0, p.Eval(3), EPS);
			Assert.AreEqual(0, p.Eval(-1.3/2), EPS);
			Assert.AreEqual(0, p.Eval(9/7.0), EPS);
			Assert.AreEqual(14 - 50.9 + 15 + 35.1, p.Eval(1), EPS);

			Polynomial divp = p.DivRoot(3);
			Assert.AreEqual(-11.7, divp[0], EPS);
			Assert.AreEqual(-8.9, divp[1], EPS);
			Assert.AreEqual(14, divp[2], EPS);

			Polynomial diff = p.Diff();
			Assert.AreEqual(15, diff[0], EPS);
			Assert.AreEqual(-50.9*2, diff[1], EPS);
			Assert.AreEqual(14*3, diff[2], EPS);

			List<double> roots = p.FindRoots(-1000, 1000);
			Assert.AreEqual(-0.65, roots[0], EPS);
			Assert.AreEqual(9/7.0, roots[1], EPS);
			Assert.AreEqual(3, roots[2], EPS);
		}

		[Test]
		public void TestJosephus()
		{
			Random r = new Random(0);
			for (int i = 0; i < 100; i++)
			{
				int N = r.Next(100, 500);
				int K = r.Next(1, 2000);

				// Naive solution
				bool[] gone = new bool[N];
				int cur = 0;
				for (int j = 0; j < N - 1; j++)
				{
					int d = K - 1;
					while (d > 0)
					{
						cur = (cur + 1)%N;
						if (!gone[cur])
							d--;
					}
//					Console.WriteLine("Kicked out " + cur);
					gone[cur] = true;
					while (gone[cur])
						cur = (cur + 1) % N;
				}
				int expLeft = Array.IndexOf(gone, false);
//				Console.WriteLine(expLeft);

				int actLeft = MathStuff.Josephus(N, K);
//				Console.WriteLine(actLeft - 1);

				Assert.AreEqual(expLeft, actLeft - 1);
			}
		}

		[Test]
		public void TestMagicSquares()
		{
			for (int size = 1; size <= 100; size++)
			{
				if (size == 2)
					continue;
				int[,] a = MathStuff.MagicSquare(size);

				bool[] used = new bool[size*size];
				int d1sum = 0, d2sum = 0, expSum = 0;
				for (int y = 0; y < size; y++)
				{
					d1sum += a[y, y];
					d2sum += a[y, size - y - 1];
					int rowSum = 0, colSum = 0;
					for (int x = 0; x < size; x++)
					{
						Assert.IsFalse(used[a[y, x] - 1]);
						used[a[y, x] - 1] = true;
						rowSum += a[y, x];
						colSum += a[x, y];
					}
					if (y == 0)
						expSum = rowSum;
					Assert.AreEqual(expSum, rowSum);
					Assert.AreEqual(expSum, colSum);
				}
				Assert.AreEqual(expSum, d1sum);
				Assert.AreEqual(expSum, d2sum);
			}
		}

		[Test]
		public void TestChineseRemainderTheorem()
		{
			MathStuff lib = new MathStuff();
			for (int m = 2; m < 100; m++)
			{
				for (int n = 2; n < 100; n++)
				{
					int limit = Math.Min(1000, lib.LCM(m,n));
					for (int x = 0; x < limit; x++)
					{
						int a = x%m, b = x%n;
						int y = lib.GCD(m, n) == 1 ? lib.Chinese(a, m, b, n) : lib.ChineseCommon(a, m, b, n);
						Assert.AreEqual(x, y);
					}
				}
			}
		}

	}
}