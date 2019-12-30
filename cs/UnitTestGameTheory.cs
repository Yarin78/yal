using System;
using System.Collections.Generic;
using NUnit.Framework;

namespace Algorithms
{
	[TestFixture]
	public class UnitTestGameTheory
	{
		[Test]
		public void TestFiniteGameEvaluator()
		{
			// Squaring the number
			int[] expected = new int[] { 0, 1, 0, 1, 2, 0, 1, 0, 1, 2, 0, 1, 0, 1, 2, 0, 1, 0, 1, 2, 0, 1, 0, 1, 2, 3, 2, 3, 4, 5 };
			GameEvaluation ge = new GameEvaluation();
			for (int i = 0; i < expected.Length; i++)
			{
				int actual = ge.EvaluateFiniteGame(new SquaringTheNumberState(i));
				Assert.AreEqual(expected[i], actual);
			}

			// Nim city

			string[] map = new string[]
			               	{
			               		"", "h", "hA", "hB", "h", "hABD", "BCE", "CF", "D", "hDEGH", "CFI", "CGJ"
			               	};
			ge = new GameEvaluation();
			for (int i = 1; i < 12; i++)
			{
				int v = ge.EvaluateFiniteGame(new NimCity(map, i));
				//				Console.WriteLine((char)('A' + i -1) + ": " + v);
			}

			int g1 = ge.EvaluateFiniteGame(new NimCity(map, 6));
			int g2 = ge.EvaluateFiniteGame(new NimCity(map, 10));
			int g3 = ge.EvaluateFiniteGame(new NimCity(map, 11));
			Assert.AreEqual(2, g1 ^ g2 ^ g3);

			// Treblecross
			string[] input = new[]
			                 	{
			                 		".....",
			                 		"X.....X..X.............X....X..X",
			                 		".X.X...X",
			                 		"...............................................",
			                 		
									"..................",
			                 		"...........................................",
			                 		"X..............................X..........X............X.X..X..X.......................",
			                 		"....................................................X.................................................................",
			                 		"......................................................................................"
			                 	};
			;
			var expectedOutput = new[]
			                     	{
			                     		new[] {3},
			                     		new int[0],
			                     		new[] {3},
			                     		new[] {1, 12, 15, 17, 20, 24, 28, 31, 33, 36, 47},
			                     		
			                     		new[] {5, 6, 13, 14},
			                     		new[] {6, 9, 22, 35, 38},
			                     		new[] {57},
			                     		new[] {58, 64, 68, 69, 74, 77, 78, 79, 85, 89, 95, 96, 97, 100, 105, 106, 110, 116},
										new int[0]
			                     	};

			ge = new GameEvaluation();
			for (int caseNo = 0; caseNo < input.Length; caseNo++)
			{
				List<int> winningMoves = new List<int>();
				string s = input[caseNo];
				for (int i = 0; i < s.Length; i++)
				{
					if (s[i] == 'X') continue;
					string t = s.Substring(0, i) + "X" + s.Substring(i + 1);
					if (t.Contains("XXX"))
						winningMoves.Add(i + 1);
					else if (!t.Contains("XX") && !t.Contains("X.X"))
					{
						CombinedGameState game = TrebleCross.CreateGames(t);
						if (ge.EvaluateFiniteGame(game) == 0)
							winningMoves.Add(i + 1);
					}
				}

				Assert.AreEqual(expectedOutput[caseNo].Length, winningMoves.Count);
				for (int i = 0; i < winningMoves.Count; i++)
					Assert.AreEqual(expectedOutput[caseNo][i], winningMoves[i]);
				//				Console.Write("#" + caseNo + ": ");
				//				foreach (int x in winningMoves)
				//					Console.Write(x + " ");
				//				Console.WriteLine();
			}
		}

		[Test]
		public void TestInfiniteGameEvaluation()
		{
			string[] map = new string[]
			               	{
			               		"", "h", "AE", "h", "hAC", "hADGH", "hCE", "CEG", "BEG"
			               	};

			var game = new NimCity2Game(map);
			var ge = new GameEvaluation();

			int[] expected = new[] {0, 1, 0, 1, 2, -1, 2, -1, 1};
			int ix = 0;
			foreach (IGameState state in game.AllStates())
			{
				int res = ge.EvaluateInfiniteGame(game, state);
				Assert.AreEqual(expected[ix++], res);
			}

			var cgs = new CombinedGameState(new[] { game.states[2], game.states[4], game.states[8]});
			int cgsRes = ge.EvaluateInfiniteGame(game, cgs);
			Assert.IsTrue(cgsRes > 0);

			cgs = new CombinedGameState(new[] {game.states[7]});
			cgsRes = ge.EvaluateInfiniteGame(game, cgs);
			Assert.IsTrue(cgsRes < 0);

			cgs = new CombinedGameState(new[] { game.states[5] });
			cgsRes = ge.EvaluateInfiniteGame(game, cgs);
			Assert.IsTrue(cgsRes > 0);

			cgs = new CombinedGameState(new[] { game.states[5], game.states[3] });
			cgsRes = ge.EvaluateInfiniteGame(game, cgs);
			Assert.IsTrue(cgsRes > 0);

			cgs = new CombinedGameState(new[] { game.states[5], game.states[3], game.states[4] });
			cgsRes = ge.EvaluateInfiniteGame(game, cgs);
			Assert.IsTrue(cgsRes < 0);
		}

		#region Finite game states

		private class SquaringTheNumberState : IGameState
		{
			public int Value;

			public SquaringTheNumberState(int value)
			{
				Value = value;
			}

			public ICollection<IGameState[]> NextStates()
			{
				ICollection<IGameState[]> result = new List<IGameState[]>();
				for (int i = 1; i * i <= Value; i++)
					result.Add(new IGameState[] { new SquaringTheNumberState(Value - i * i) });
				return result;
			}

			public bool Equals(SquaringTheNumberState obj)
			{
				if (ReferenceEquals(null, obj)) return false;
				if (ReferenceEquals(this, obj)) return true;
				return obj.Value == Value;
			}

			public override bool Equals(object obj)
			{
				if (ReferenceEquals(null, obj)) return false;
				if (ReferenceEquals(this, obj)) return true;
				if (obj.GetType() != typeof(SquaringTheNumberState)) return false;
				return Equals((SquaringTheNumberState)obj);
			}

			public override int GetHashCode()
			{
				return Value;
			}
		}

		private class NimCity : IGameState
		{
			private readonly string[] _map;
			private readonly int _city;

			public NimCity(string[] map, int city)
			{
				_map = map;
				_city = city;
			}

			public ICollection<IGameState[]> NextStates()
			{
				var states = new List<IGameState[]>();
				foreach (char c in _map[_city])
				{
					int next = (c == 'h') ? 0 : (c - 'A' + 1);
					states.Add(new IGameState[] { new NimCity(_map, next) });
				}
				return states;
			}

			public bool Equals(NimCity obj)
			{
				if (ReferenceEquals(null, obj)) return false;
				if (ReferenceEquals(this, obj)) return true;
				return Equals(obj._map, _map) && obj._city == _city;
			}

			public override bool Equals(object obj)
			{
				if (ReferenceEquals(null, obj)) return false;
				if (ReferenceEquals(this, obj)) return true;
				if (obj.GetType() != typeof(NimCity)) return false;
				return Equals((NimCity)obj);
			}

			public override int GetHashCode()
			{
				unchecked
				{
					return ((_map != null ? _map.GetHashCode() : 0) * 397) ^ _city;
				}
			}
		}

		private class TrebleCross : IGameState
		{
			private readonly int n, m;

			public TrebleCross(int n, int m)
			{
				this.n = n;
				this.m = m;
			}

			public static CombinedGameState CreateGames(string state)
			{
				string[] split = state.Split(new char[] { 'X' }, StringSplitOptions.RemoveEmptyEntries);
				List<IGameState> states = new List<IGameState>();
				for (int i = 0; i < split.Length; i++)
				{
					int m = 0;
					if (i > 0 || state.StartsWith("X"))
						m++;
					if (i < split.Length - 1 || state.EndsWith("X"))
						m++;
					states.Add(new TrebleCross(split[i].Length, m));
				}
				return new CombinedGameState(states);
			}

			public ICollection<IGameState[]> NextStates()
			{
				var list = new List<IGameState[]>();
				for (int i = 0; i < n; i++)
				{
					if (i == 0 && m > 0)
						continue;
					if (i == n - 1 && m == 2)
						continue;
					IGameState[] moveResult = Move(i);
					if (moveResult != null)
						list.Add(moveResult);
				}
				return list;
			}

			private IGameState[] Move(int pos)
			{
				TrebleCross a, b;
				if (pos == 0 || pos == n - 1)
				{
					if (n == 2)
						return null;
					return new IGameState[] { new TrebleCross(n - 1, m + 1) };
				}

				if (m == 2)
				{
					a = new TrebleCross(pos, 2);
					b = new TrebleCross(n - pos - 1, 2);
				}
				else if (m == 1)
				{
					a = new TrebleCross(pos, 1);
					b = new TrebleCross(n - pos - 1, 2);
				}
				else
				{
					a = new TrebleCross(pos, 1);
					b = new TrebleCross(n - pos - 1, 1);
				}

				if ((a.n == 1 && a.m == 2) || (b.n == 1 && b.m == 2))
					return null;
				return new[] { a, b };
			}


			public bool Equals(TrebleCross obj)
			{
				if (ReferenceEquals(null, obj)) return false;
				if (ReferenceEquals(this, obj)) return true;
				return obj.n == n && obj.m == m;
			}

			public override bool Equals(object obj)
			{
				if (ReferenceEquals(null, obj)) return false;
				if (ReferenceEquals(this, obj)) return true;
				if (obj.GetType() != typeof(TrebleCross)) return false;
				return Equals((TrebleCross)obj);
			}

			public override int GetHashCode()
			{
				unchecked
				{
					return (n * 397) ^ m;
				}
			}
		}

		private class NimCity2State : IGameState
		{
			public bool Equals(NimCity2State obj)
			{
				if (ReferenceEquals(null, obj)) return false;
				if (ReferenceEquals(this, obj)) return true;
				return obj._city == _city;
			}

			public override bool Equals(object obj)
			{
				if (ReferenceEquals(null, obj)) return false;
				if (ReferenceEquals(this, obj)) return true;
				if (obj.GetType() != typeof (NimCity2State)) return false;
				return Equals((NimCity2State) obj);
			}

			public override int GetHashCode()
			{
				return _city;
			}

			private NimCity2Game _game;
			private int _city;
			private List<int> _edges;

			public NimCity2State(NimCity2Game game, int city, List<int> edges)
			{
				_game = game;
				_city = city;
				_edges = edges;
			}

			public ICollection<IGameState[]> NextStates()
			{
				var nextStates = new List<IGameState[]>();
				foreach (int i in _edges)
					nextStates.Add(new[] { _game.states[i] });
				return nextStates;
			}
		}

		private class NimCity2Game : IGame
		{
			internal NimCity2State[] states;

			public NimCity2Game(string[] map)
			{
				states = new NimCity2State[map.Length];
				for (int i = 0; i < map.Length; i++)
				{
					List<int> edges = new List<int>();
					foreach (char c in map[i])
					{
						int next = (c == 'h') ? 0 : (c - 'A' + 1);
						edges.Add(next);
					}
					states[i] = new NimCity2State(this, i, edges);
				}
			}

			public ICollection<IGameState> AllStates()
			{
				return states;
			}

			public ICollection<IGameState> AllLosingStates()
			{
				return new[] {states[0]};
			}
		}

		#endregion
	}
}