using System;
using System.Collections.Generic;

namespace Algorithms
{
	/// <summary>
	/// Priority queue storing id's of the items rather than the items themselves.
	/// This allows for deletion of any element in the queue using a <see cref="ILocator{TItem,TLocation}"/>.
	/// </summary>
	/// <seealso cref="PrioQueue{T}"/>
	public class PrioQueueMod<T>
	{
		private readonly List<T> _ids = new List<T>();
		private readonly Comparison<T> _comparison;
		private readonly ILocator<T, int> _locator;

		/// <summary>
		/// Initializes a new instance of the class <see cref="PrioQueueMod{T}"/>
		/// </summary>
		/// <remarks>
		/// <code>
		/// var pq = new PrioQueueMod&lt;int&gt;((x,y) => values[x]-values[y], new ArrayLocator&lt;int&gt;(10000));
		/// </code>
		/// will create a min-heap that can store elements with ids between 0 and 9999.
		/// </remarks>
		public PrioQueueMod(Comparison<T> comparison, ILocator<T, int> locator)
		{
			this._comparison = comparison;
			this._locator = locator;
		}

		private void Swap(int a, int b)
		{
			T tmp = _ids[a]; 
			_ids[a] = _ids[b];
			_ids[b] = tmp;
			_locator.Set(_ids[a], a);
			_locator.Set(_ids[b], b);
		}

		private void TrinkleUp(int k)
		{
			while (k > 0)
			{
				int p = (k - 1) / 2;
				if (_comparison(_ids[k], _ids[p]) >= 0) return;
				Swap(k, p);	
				k = p;
			}
		}

		private void TrinkleDown(int k)
		{
			while (true)
			{
				int lc = k * 2 + 1, rc = k * 2 + 2;
				if (lc >= _ids.Count) return;
				int c = rc < _ids.Count ? (_comparison(_ids[lc], _ids[rc]) < 0 ? lc : rc) : lc;
				if (_comparison(_ids[c], _ids[k]) >= 0) return;
				Swap(k, c);
				k = c;
			}
		}

		public bool Empty { get { return _ids.Count == 0; } }

		public void Insert(T id)
		{
			_locator.Set(id, _ids.Count);
			_ids.Add(id);
			TrinkleUp(_ids.Count - 1);
		}

		public void Erase(T id)
		{
			int pos = _locator.Get(id);
			Swap(pos, _ids.Count - 1);
			T outOfPlaceId = _ids[pos];
			_ids.RemoveAt(_ids.Count - 1);
			_locator.Remove(id);
			if (!Empty && !Equals(outOfPlaceId, id))
			{
				TrinkleUp(_locator.Get(outOfPlaceId));
				TrinkleDown(_locator.Get(outOfPlaceId));
			}
		}

		public T Front()
		{
			if (Empty)
				throw new InvalidOperationException();
			return _ids[0];
		}

		public T PopFront()
		{
			if (Empty)
				throw new InvalidOperationException();
			Swap(0, _ids.Count - 1);
			T id = _ids[_ids.Count - 1];
			_ids.RemoveAt(_ids.Count - 1);
			_locator.Remove(id);
			TrinkleDown(0);
			return id;
		}

		public bool IsHeap(int k)
		{
			int lc = k * 2 + 1, rc = k * 2 + 2;
			if (_locator.Get(_ids[k]) != k) return false;
			if (lc < _ids.Count && (_comparison(_ids[lc], _ids[k]) < 0 || !IsHeap(lc))) return false;
			if (rc < _ids.Count && (_comparison(_ids[rc], _ids[k]) < 0 || !IsHeap(rc))) return false;
			return true;
		}
	}
}