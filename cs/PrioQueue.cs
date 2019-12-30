using System;
using System.Collections.Generic;

namespace Algorithms
{
    public class PrioQueue<T> where T : IComparable
    {
        private List<T> _data = new List<T>();

        private void Swap(int a, int b) { T tmp = _data[a]; _data[a] = _data[b]; _data[b] = tmp; }

        private void TrinkleUp(int k)
        {
            while (k > 0)
            {
                int p = (k - 1) / 2;
                if (_data[k].CompareTo(_data[p]) >= 0) return;
                Swap(k, p);
                k = p;
            }
        }

        private void TrinkleDown(int k)
        {
            while (true)
            {
                int lc = k * 2 + 1, rc = k * 2 + 2;
                if (lc >= _data.Count) return;
                int c = rc < _data.Count ? ((_data[lc]).CompareTo(_data[rc]) < 0 ? lc : rc) : lc;
                if ((_data[c]).CompareTo(_data[k]) >= 0) return;
                Swap(k, c);
                k = c;
            }
        }

        public bool Empty { get { return _data.Count == 0; } }

        public void Push(T item)
        {
            _data.Add(item);
            TrinkleUp(_data.Count - 1);
        }

		public T Front()
		{
			if (Empty)
				return default(T);
			return _data[0];
		}

        public T Pop()
        {
            if (Empty)
                return default(T);
            T item = _data[0];
            _data[0] = _data[_data.Count - 1];
            _data.RemoveAt(_data.Count - 1);
            TrinkleDown(0);
            return item;
        }

        public bool IsHeap(int k)
        {
            int lc = k * 2 + 1, rc = k * 2 + 2;
            if ((lc < _data.Count) && ((_data[lc]).CompareTo(_data[k]) < 0 || !IsHeap(lc))) return false;
            if ((rc < _data.Count) && ((_data[rc]).CompareTo(_data[k]) < 0 || !IsHeap(rc))) return false;
            return true;
        }
    }
}
