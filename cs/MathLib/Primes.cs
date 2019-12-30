using System;
using System.Collections.Generic;

namespace Algorithms.MathLib
{
	public class Primes
	{
		public readonly List<int> PrimeList;

		private int maxPrime;
		private byte[] _sieve;

		public Primes(int largestSieve)
		{
			maxPrime = largestSieve + (largestSieve & 1);
			PrimeList = new List<int>();
			PrimeList.Add(2);
			_sieve = new byte[maxPrime / 16 + 2];
			_sieve[0] = 1;
			for (int i = 1; i < maxPrime / 2; i++)
				if ((_sieve[i >> 3] & (1 << (i & 7))) == 0)
				{
					PrimeList.Add(i * 2 + 1);
					for (int j = i + i + i + 1; j < maxPrime / 2; j += i + i + 1)
						_sieve[j >> 3] |= (byte)(1 << (j & 7));
				}
		}

		public bool IsPrime(int value)
		{
			if (value == 2)
				return true;
			if (value < 2 || (value % 2) == 0)
				return false;
			if (value <= maxPrime)
				return (_sieve[value >> 4] & (1 << ((value >> 1) & 7))) == 0;
			if (value > ((long)maxPrime) * maxPrime)
				throw new ArgumentException("Value too large.");
			foreach (int p in PrimeList)
			{
				if (((long)p) * p > value)
					return true;
				if (value % p == 0)
					return false;
			}
			return true;
		}

		public int[] GetAllPrimeFactors(int value)
		{
			if (value < 2)
				return new int[0];
			List<int> factors = new List<int>();
			for (int i = 0; i < PrimeList.Count && value > 1; i++)
			{
				int p = PrimeList[i];
				while (value%p == 0)
				{
					factors.Add(p);
					value /= p;
				}
			}
			if (value <= ((long)maxPrime) * maxPrime)
			{
				if (value > 1)
					factors.Add(value);
				return factors.ToArray();
			}
			throw new ArgumentException(value + " has too large prime factors.");
		}

		public int[] GetUniquePrimeFactors(int value)
		{
			int[] allPrimeFactors = GetAllPrimeFactors(value);
			List<int> unique = new List<int>();
			foreach(int p in allPrimeFactors)
				if (unique.Count == 0 || p != unique[unique.Count-1])
					unique.Add(p);
			return unique.ToArray();
		}
	}
}
