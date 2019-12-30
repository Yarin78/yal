using System;
using System.Collections;
using System.Collections.Generic;
using System.Runtime.InteropServices;

namespace Algorithms
{
	[Serializable]
	public class TreeSet<T> : ICollection<T>
	{
		private readonly IComparer<T> comparer;
		private int count;
		private Node _root;
		private Node Root
		{
			get { return _root; }
			set
			{
				_root = value;
				if (value != null)
					_root.Parent = null;
			}
		}
		
		private int version;

		public TreeSet()
		{
			comparer = Comparer<T>.Default;
		}

		public TreeSet(IComparer<T> comparer)
		{
			if (comparer == null) throw new ArgumentNullException("comparer");
			this.comparer = comparer;
		}

		public IComparer<T> Comparer
		{
			get { return comparer; }
		}

		#region ICollection<T> Members

		public void Add(T item)
		{
			if (Root == null)
			{
				Root = new Node(item, null, false);
				count = 1;
			}
			else
			{
				Node r = Root;
				Node node = null;
				Node grandParent = null;
				Node greatGrandParent = null;
				int num = 0;
				while (r != null)
				{
					num = comparer.Compare(item, r.Item);
					if (num == 0)
					{
						Root.IsRed = false;
						throw new ArgumentException("AddingDuplicate");
					}
					if (Is4Node(r))
					{
						Split4Node(r);
						if (IsRed(node))
							InsertionBalance(r, ref node, grandParent, greatGrandParent);
					}
					greatGrandParent = grandParent;
					grandParent = node;
					node = r;
					r = (num < 0) ? r.Left : r.Right;
				}
				Node current = new Node(item, node);
				if (num > 0)
					node.Right = current;
				else
					node.Left = current;
				if (node.IsRed)
					InsertionBalance(current, ref node, grandParent, greatGrandParent);
				Root.IsRed = false;
				count++;
				version++;
			}
		}

		public void Clear()
		{
			Root = null;
			count = 0;
			version++;
		}

		public bool Contains(T item)
		{
			return FindNode(item) != null;
		}

		public void CopyTo(T[] array, int index)
		{
			if (array == null)
				throw new ArgumentNullException();
			if (index < 0)
				throw new ArgumentOutOfRangeException();
			if ((array.Length - index) < Count)
				throw new ArgumentException();

			InOrderTreeWalk(delegate(Node node)
			                	{
			                		array[index++] = node.Item;
			                		return true;
			                	});
		}

		public IEnumerator GetEnumerator()
		{
			return new Enumerator(this);
		}

		public bool Remove(T item)
		{
			if (Root == null)
			{
				return false;
			}
			Node r = Root;
			Node parent = null;
			Node node3 = null;
			Node match = null;
			Node parentOfMatch = null;
			bool flag = false;
			while (r != null)
			{
				if (Is2Node(r))
				{
					if (parent == null)
					{
						r.IsRed = true;
					}
					else
					{
						Node sibling = GetSibling(r, parent);
						if (sibling.IsRed)
						{
							if (parent.Right == sibling)
							{
								RotateLeft(parent);
							}
							else
							{
								RotateRight(parent);
							}
							parent.IsRed = true;
							sibling.IsRed = false;
							ReplaceChildOfNodeOrRoot(node3, parent, sibling);
							node3 = sibling;
							if (parent == match)
							{
								parentOfMatch = sibling;
							}
							sibling = (parent.Left == r) ? parent.Right : parent.Left;
						}
						if (Is2Node(sibling))
						{
							Merge2Nodes(parent, r, sibling);
						}
						else
						{
							TreeRotation rotation = RotationNeeded(parent, r, sibling);
							Node newChild = null;
							switch (rotation)
							{
								case TreeRotation.LeftRotation:
									sibling.Right.IsRed = false;
									newChild = RotateLeft(parent);
									break;

								case TreeRotation.RightRotation:
									sibling.Left.IsRed = false;
									newChild = RotateRight(parent);
									break;

								case TreeRotation.RightLeftRotation:
									newChild = RotateRightLeft(parent);
									break;

								case TreeRotation.LeftRightRotation:
									newChild = RotateLeftRight(parent);
									break;
							}
							newChild.IsRed = parent.IsRed;
							parent.IsRed = false;
							r.IsRed = true;
							ReplaceChildOfNodeOrRoot(node3, parent, newChild);
							if (parent == match)
							{
								parentOfMatch = newChild;
							}
							node3 = newChild;
						}
					}
				}
				int num = flag ? -1 : comparer.Compare(item, r.Item);
				if (num == 0)
				{
					flag = true;
					match = r;
					parentOfMatch = parent;
				}
				node3 = parent;
				parent = r;
				if (num < 0)
				{
					r = r.Left;
				}
				else
				{
					r = r.Right;
				}
			}
			if (match != null)
			{
				ReplaceNode(match, parentOfMatch, parent, node3);
				count--;
			}
			if (this.Root != null)
			{
				this.Root.IsRed = false;
			}
			version++;
			return flag;
		}

		IEnumerator<T> IEnumerable<T>.GetEnumerator()
		{
			return new Enumerator(this);
		}

		public int Count
		{
			get { return count; }
		}

		bool ICollection<T>.IsReadOnly
		{
			get { return false; }
		}

		#endregion

		public T GetItem(int index)
		{
			Node node = FindNodeByIndex(index);
			if (node == null)
				return default(T);
			return node.Item;
		}

		public int GetIndex(T item)
		{
			Node node = FindNode(item);
			if (node == null)
				return -1;
			return FindIndexByNode(node);
		}

	    public Node FindNodeByIndex(int index)
		{
			if (Root == null)
				return null;
			if (index < 0 || index >= Root.Count)
				return null;
			
			Node node = Root;
			while (true)
			{
				int leftCount = node.Left == null ? 0 : node.Left.Count;
				if (index < leftCount)
					node = node.Left;
				else if (index == leftCount)
					return node;
				else
				{
					index -= leftCount + 1;
					node = node.Right;
				}
			}
		}

	    public int FindIndexByNode(Node node)
		{
			Node current = node;
			while (current.Parent != null)
				current = current.Parent;
			int skipped = 0;
			while (true)
			{
				int dif = comparer.Compare(node.Item, current.Item);
				int leftCount = current.Left == null ? 0 : current.Left.Count;
				if (dif == 0)
					return skipped + leftCount;
				if (dif < 0)
					current = current.Left;
				else if (dif > 0)
				{
					skipped += 1 + leftCount;
					current = current.Right;
				}
			}
		}

        /// <summary>
        /// Finds the first node in the set where they key is greater than or equal to <paramref name="item"/>.
        /// </summary>
        /// <param name="item">The key to find.</param>
        /// <returns>A node in the tree, or null if all nodes in the tree has a key less than the one specified.</returns>
        public Node FindStartNode(T item)
        {
            int num;
            Node start = null;
            for (Node node = Root; node != null; node = (num <= 0) ? node.Left : node.Right)
            {
                num = comparer.Compare(item, node.Item);
                if (num <= 0)
                {
                    start = node;
                }
            }
            return start;
        }
        
        /// <summary>
        /// Finds the last node in the set where they key is less than or equal to <paramref name="item"/>.
        /// </summary>
        /// <param name="item">The key to find.</param>
        /// <returns>A node in the tree, or null if all nodes in the tree has a key greater than the one specified.</returns>
        public Node FindEndNode(T item)
        {
            int num;
            Node stop = null;
            for (Node node = Root; node != null; node = (num < 0) ? node.Left : node.Right)
            {
                num = comparer.Compare(item, node.Item);
                if (num >= 0)
                {
                    stop = node;
                }
            }
            return stop;
        }

	    public Node FindNode(T item)
		{
			int num;
			for (Node node = Root; node != null; node = (num < 0) ? node.Left : node.Right)
			{
				num = comparer.Compare(item, node.Item);
				if (num == 0)
				{
					return node;
				}
			}
			return null;
		}

        public IEnumerable<T> CreateWindow(T start, T end)
        {
            Node startNode = FindStartNode(start);
            Node endNode = FindEndNode(end);
            if (startNode == null || endNode == null)
                yield break;
            if (comparer.Compare(startNode.Item, endNode.Item) > 0)
                yield break;

            Node current = startNode;
            yield return current.Item;
            while (current != endNode)
            {
                current = current.Next();
                yield return current.Item;
            }
        }

	    private static Node GetSibling(Node node, Node parent)
		{
			return parent.Left == node ? parent.Right : parent.Left;
		}


	    public bool InOrderTreeWalk(TreeWalkAction<T> action)
		{
			if (Root != null)
			{
				Stack<Node> stack = new Stack<Node>(2*((int) Math.Log(Count + 1)));
				Node r = Root;
				while (r != null)
				{
					stack.Push(r);
					r = r.Left;
				}
				while (stack.Count != 0)
				{
					r = stack.Pop();
					if (!action(r))
					{
						return false;
					}
					for (Node node2 = r.Right; node2 != null; node2 = node2.Left)
					{
						stack.Push(node2);
					}
				}
			}
			return true;
		}

		private void InsertionBalance(Node current, ref Node parent, Node grandParent, Node greatGrandParent)
		{
			Node node;
			bool flag = grandParent.Right == parent;
			bool flag2 = parent.Right == current;
			if (flag == flag2)
			{
				node = flag2 ? RotateLeft(grandParent) : RotateRight(grandParent);
			}
			else
			{
				node = flag2 ? RotateLeftRight(grandParent) : RotateRightLeft(grandParent);
				parent = greatGrandParent;
			}
			grandParent.IsRed = true;
			node.IsRed = false;
			ReplaceChildOfNodeOrRoot(greatGrandParent, grandParent, node);
		}

		private static bool Is2Node(Node node)
		{
			return (IsBlack(node) && IsNullOrBlack(node.Left)) && IsNullOrBlack(node.Right);
		}

		private static bool Is4Node(Node node)
		{
			return IsRed(node.Left) && IsRed(node.Right);
		}

		private static bool IsBlack(Node node)
		{
			return (node != null) && !node.IsRed;
		}

		private static bool IsNullOrBlack(Node node)
		{
			if (node != null)
				return !node.IsRed;
			return true;
		}

		private static bool IsRed(Node node)
		{
			return (node != null) && node.IsRed;
		}

		private static void Merge2Nodes(Node parent, Node child1, Node child2)
		{
			parent.IsRed = false;
			child1.IsRed = true;
			child2.IsRed = true;
		}

		private void ReplaceChildOfNodeOrRoot(Node parent, Node child, Node newChild)
		{
			if (parent != null)
			{
				if (parent.Left == child)
				{
					parent.Left = newChild;
				}
				else
				{
					parent.Right = newChild;
				}
			}
			else
			{
				Root = newChild;
			}
		}

		private void ReplaceNode(Node match, Node parentOfMatch, Node succesor, Node parentOfSuccesor)
		{
			if (succesor == match)
			{
				succesor = match.Left;
			}
			else
			{
				if (succesor.Right != null)
				{
					succesor.Right.IsRed = false;
				}
				if (parentOfSuccesor != match)
				{
					parentOfSuccesor.Left = succesor.Right;
					succesor.Right = match.Right;
				}
				succesor.Left = match.Left;
			}
			if (succesor != null)
			{
				succesor.IsRed = match.IsRed;
			}
			ReplaceChildOfNodeOrRoot(parentOfMatch, match, succesor);
		}

		private static Node RotateLeft(Node node)
		{
			Node right = node.Right;
			node.Right = right.Left;
			right.Left = node;
			return right;
		}

		private static Node RotateLeftRight(Node node)
		{
			Node left = node.Left;
			Node right = left.Right;
			node.Left = right.Right;
			right.Right = node;
			left.Right = right.Left;
			right.Left = left;
			return right;
		}

		private static Node RotateRight(Node node)
		{
			Node left = node.Left;
			node.Left = left.Right;
			left.Right = node;
			return left;
		}

		private static Node RotateRightLeft(Node node)
		{
			Node right = node.Right;
			Node left = right.Left;
			node.Right = left.Left;
			left.Left = node;
			right.Left = left.Right;
			left.Right = right;
			return left;
		}

		private static TreeRotation RotationNeeded(Node parent, Node current, Node sibling)
		{
			if (IsRed(sibling.Left))
			{
				if (parent.Left == current)
					return TreeRotation.RightLeftRotation;
				return TreeRotation.RightRotation;
			}
			if (parent.Left == current)
				return TreeRotation.LeftRotation;
			return TreeRotation.LeftRightRotation;
		}

		private static void Split4Node(Node node)
		{
			node.IsRed = true;
			node.Left.IsRed = false;
			node.Right.IsRed = false;
		}

		internal void UpdateVersion()
		{
			version++;
		}

		#region Nested type: Enumerator

		[StructLayout(LayoutKind.Sequential)]
		public struct Enumerator : IEnumerator<T>
		{
			private readonly TreeSet<T> tree;
			private readonly int version;
			private readonly Stack<Node> stack;
			private Node current;
			private static Node dummyNode;

			internal Enumerator(TreeSet<T> set)
			{
				tree = set;
				version = tree.version;
				stack = new Stack<Node>(2*((int) Math.Log((set.Count + 1))));
				current = null;
				Intialize();
			}

			private void Intialize()
			{
				current = null;
				for (Node node = tree.Root; node != null; node = node.Left)
				{
					stack.Push(node);
				}
			}

			public bool MoveNext()
			{
				if (version != tree.version)
					throw new InvalidOperationException();
				if (stack.Count == 0)
				{
					current = null;
					return false;
				}
				current = stack.Pop();
				for (Node node = current.Right; node != null; node = node.Left)
					stack.Push(node);
				return true;
			}
			
			public T Current
			{
				get
				{
					if (current != null)
						return current.Item;
					return default(T);
				}
			}

			object IEnumerator.Current
			{
				get
				{
					if (current == null)
					{
						throw new InvalidOperationException();
					}
					return current.Item;
				}
			}

			internal bool NotStartedOrEnded
			{
				get { return (current == null); }
			}

			internal void Reset()
			{
				if (version != tree.version)
				{
					throw new InvalidOperationException();
				}
				stack.Clear();
				Intialize();
			}

			void IEnumerator.Reset()
			{
				Reset();
			}

			static Enumerator()
			{
				dummyNode = new Node(default(T), null);
			}

			public void Dispose()
			{
			}
		}

		#endregion

		#region Nested type: Node

	    public class Node
		{
			public Node(T item, Node parent)
			{
				Item = item;
				IsRed = true;
				Count = 1;
				_parent = parent;
			}

			public Node(T item, Node parent, bool isRed)
			{
				Item = item;
				IsRed = isRed;
				Count = 1;
				_parent = parent;
			}

			public void UpdateCount()
			{
				int curCount = Count;
				Count = 1 + (Left == null ? 0 : Left.Count) + (Right == null ? 0 : Right.Count);
				if (curCount != Count && Parent != null)
					Parent.UpdateCount();
			}

			private Node _left, _right, _parent;
	    	private bool isRed;
	    	private T item;
	    	private int count;

	    	public bool IsRed
	    	{
	    		get { return isRed; }
	    		set { isRed = value; }
	    	}

	    	public T Item
	    	{
	    		get { return item; }
	    		set { item = value; }
	    	}

	    	public Node Parent
			{
				get { return _parent;}
				internal set { _parent = value;}
			}

	    	public Node Left
			{
				get { return _left; }
				set
				{
					_left = value;
					if (_left != null)
						_left.Parent = this;
					UpdateCount();
				}
			}


	    	public Node Right
			{
				get { return _right; }
				set
				{
					_right = value;
					if (_right != null)
						_right.Parent = this;
					UpdateCount();
				}
			}

	    	public int Count
	    	{
	    		get { return count; }
	    		private set { count = value; }
	    	}

	    	public void Validate()
			{
				int count = 1;
				if (Left != null)
				{
					Left.Validate();
					count += Left.Count;
					if (Left.Parent != this)
						throw new Exception();
				}
				if (Right != null)
				{
					Right.Validate();
					count += Right.Count;
					if (Right.Parent != this)
						throw new Exception();
				}
				
				if (count != Count)
					throw new Exception();
			}

			public override string ToString()
			{
				string ls = Left == null ? "" : Left.ToString();
				string rs = Right == null ? "" : Right.ToString();
				return Item + " (cnt = " + Count + ")  L=(" + ls + ") R=(" + rs + ")";
			}

            private bool IsLeftChild()
            {
                return Parent != null && Parent.Left == this;
            }

            private bool IsRightChild()
            {
                return Parent != null && Parent.Right == this;
            }

	        public Node Next()
	        {
	            Node current;
	            if (Right != null)
	            {
	                current = Right;
                    while (current.Left != null)
                    {
                        current = current.Left;
                    }
	                return current;
	            }
	            current = this;
                while (current.IsRightChild())
                {
                    current = current.Parent;
                }
	            if (current.IsLeftChild())
	            {
	                return current.Parent;
	            }
	            return null;            
	        }

            public Node Previous()
            {
                Node current;
                if (Left != null)
                {
                    current = Left;
                    while (current.Right != null)
                    {
                        current = current.Right;
                    }
                    return current;
                }
                current = this;
                while (current.IsLeftChild())
                {
                    current = current.Parent;
                }
                if (current.IsRightChild())
                {
                    return current.Parent;
                }
                return null;
            }
		}

		#endregion
        
		public void Validate()
		{
			if (Root == null)
				return;
			Root.Validate();
		}

		public override string ToString()
		{
			if (Root == null)
				return "";
			return Root.ToString();
		}
	}

	internal enum TreeRotation
	{
		LeftRightRotation = 4,
		LeftRotation = 1,
		RightLeftRotation = 3,
		RightRotation = 2
	}

    public delegate bool TreeWalkAction<T>(TreeSet<T>.Node node);
}