using System;
using System.Collections.Generic;
using System.Linq;

namespace Algorithms
{
	public interface IGame
	{
		ICollection<IGameState> AllStates();
		ICollection<IGameState> AllLosingStates();
	}

	public interface IGameState
	{
		/// <summary>
		/// Gets a list of all possible new games that may occur after all legal moves from this state.
		/// A move may lead to several independent games.
		/// </summary>
		ICollection<IGameState[]> NextStates();
	}
	
	public class CombinedGameState
	{
		private readonly IGameState[] _subGames;

		public CombinedGameState(ICollection<IGameState> subGames)
		{
			_subGames = subGames.ToArray();
//			Array.Sort(_subGames, (x, y) => x.GetHashCode() - y.GetHashCode());
		}

		public IGameState[] SubGames
		{
			get { return _subGames; }
		}
	}


	public class GameEvaluation
	{
		private readonly Dictionary<IGameState, int> nimValue = new Dictionary<IGameState, int>();

		public int EvaluateFiniteGame(CombinedGameState state)
		{
			int xor = 0;
			foreach (IGameState gameState in state.SubGames)
				xor ^= EvaluateFiniteGame(gameState);
			return xor;
		}

		public int EvaluateFiniteGame(IGameState state)
		{
			int value;
			if (nimValue.TryGetValue(state, out value))
				return value;

			ICollection<IGameState[]> nextStates = state.NextStates();
			var found = new bool[nextStates.Count + 1];
			foreach (IGameState[] states in nextStates)
			{
				int xor = 0;
				foreach (IGameState gameState in states)
				{
					int v = EvaluateFiniteGame(gameState);
					xor ^= v;
				}
				if (xor < found.Length)
					found[xor] = true;
			}
			int result = Array.IndexOf(found, false);
			nimValue[state] = result;
			return result;
		}

		public int EvaluateInfiniteGame(IGame game, CombinedGameState state)
		{
			// -1 = draw, 0 = losing, >0 = winning
			int xor = 0;
			IGameState infiniteSubGame = null;
			foreach (IGameState gameState in state.SubGames)
			{
				int res = EvaluateInfiniteGame(game, gameState);
				if (res < 0)
				{
					if (infiniteSubGame != null)
						return -1;
					infiniteSubGame = gameState;
				}
				else
					xor ^= res;
			}
			if (infiniteSubGame == null)
				return xor;

			foreach (IGameState[] states in infiniteSubGame.NextStates())
			{
				int v = EvaluateInfiniteGame(game, states[0]);
				if (v >= 0 && (v ^ xor) == 0)
					return 1;
			}
			return -1;
		}

		public int EvaluateInfiniteGame(IGame game, IGameState startState)
		{
			foreach (IGameState state in game.AllLosingStates())
				nimValue[state] = 0;

			bool updated;
			do
			{
				if (nimValue.ContainsKey(startState))
					return nimValue[startState];

				updated = false;
				foreach (IGameState state in game.AllStates())
				{
					if (nimValue.ContainsKey(state))
						continue;
					ICollection<IGameState[]> nextStates = state.NextStates();
					var found = new bool[nextStates.Count + 1];
					foreach (IGameState[] states in nextStates)
					{
						if (states.Length > 1)
							throw new NotSupportedException();
						int v;
						if (nimValue.TryGetValue(states[0], out v))
							if (v < found.Length)
								found[v] = true;
					}
					int result = Array.IndexOf(found, false);
					bool ok = true;
					foreach (IGameState[] states in nextStates)
					{
						if (!nimValue.ContainsKey(states[0]))
						{
							ok = false;
							foreach (IGameState[] nextState in states[0].NextStates())
							{
								int w;
								if (nimValue.TryGetValue(nextState[0], out w) && w == result)
									ok = true;
							}
						}
					}
					if (ok)
					{
						updated = true;
						nimValue.Add(state, result);
					}
				}
			} while (updated);
			return -1;
		}
	}
}