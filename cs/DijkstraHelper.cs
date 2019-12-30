using System;
using System.Collections.Generic;
using System.Text;

namespace Algorithms
{
	class DijkstraHelper<TDist>
		where TDist : struct, IComparable<TDist>
	{
		private struct State : IComparable<State>
		{
			public readonly int Node;
			public readonly TDist Distance;

			public State(int node, TDist distance)
			{
				Node = node;
				Distance = distance;
			}

			public int CompareTo(State other)
			{
				int cmp = Distance.CompareTo(other.Distance);
				if (cmp != 0)
					return cmp;
				return Node - other.Node;
			}
		}

		private readonly SortedDictionary<State, object> _stateByDistance;
		private readonly TDist[] _distanceByNode;
		private readonly byte[] _state;
		
		public DijkstraHelper(int noNodes)
		{
			_distanceByNode = new TDist[noNodes];
			_stateByDistance = new SortedDictionary<State, object>();
			_state = new byte[noNodes];
		}

		public void Add(int node, TDist distance)
		{
			switch (_state[node])
			{
				case 0:
					_distanceByNode[node] = distance;
					_state[node] = 1;
					_stateByDistance.Add(new State(node, distance), null);
					break;
				case 1:
					if (distance.CompareTo(_distanceByNode[node]) < 0)
					{
						_stateByDistance.Remove(new State(node, _distanceByNode[node]));
						_distanceByNode[node] = distance;
						_stateByDistance.Add(new State(node, distance), null);
					}
					break;
			}
		}

		public int GetNext()
		{
			SortedDictionary<State, object>.Enumerator e = _stateByDistance.GetEnumerator();
			if (!e.MoveNext())
				return -1;
			State state = e.Current.Key;
			_state[state.Node] = 2;
			_stateByDistance.Remove(state);
			return state.Node;
		}

		public TDist GetDistance(int node)
		{
			return _distanceByNode[node];
		}
	}
}
