using System;
using System.Text;

namespace Algorithms
{
	public class BinaryTree<T>
		where T : IComparable<T>
	{
		public T Value { get; private set; }
		private int Count { get; set; }
		private BinaryTree<T> Parent { get; set; }
		private BinaryTree<T> Left { get; set; }
		private BinaryTree<T> Right { get; set; }
		protected int RightCount { get { return Right == null ? 0 : Right.Count;}}
		protected int LeftCount { get { return Left == null ? 0 : Left.Count; } }

		public BinaryTree()
			: this(default(T), null)
		{
		}

		protected BinaryTree(T value, BinaryTree<T> parent)
		{
			Value = value;
			Count = 1;
			Parent = parent;
		}

		public override string ToString()
		{
			StringBuilder sb = new StringBuilder();
			sb.Append(Value.ToString());
			if (Left != null || Right != null)
				sb.AppendFormat(" ({0}) ({1})", Left == null ? "" : Left.ToString(), Right == null ? "" : Right.ToString());
			return sb.ToString();
		}

		public void Validate()
		{
			int count = 1;
			if (Left != null)
			{
				if (!IsRoot() && Left.Value.CompareTo(Value) >= 0)
					throw new Exception();
				count += Left.Count;
			}
			if (Right != null)
			{
				if (IsRoot() || Right.Value.CompareTo(Value) <= 0)
					throw new Exception();
				count += Right.Count;
			}
			if (count != Count)
				throw new Exception();
		}
        
		// Returns the added node
		public BinaryTree<T> Add(T newValue)
		{
			int dif = IsRoot() ? -1 : newValue.CompareTo(Value);
			if (dif == 0)
				throw new InvalidOperationException();
			Count++;
			if (dif < 0)
			{
				if (Left == null)
					return Left = new BinaryTree<T>(newValue, this);
				return Left.Add(newValue);
			}
			if (Right == null)
				return Right = new BinaryTree<T>(newValue, this);
			return Right.Add(newValue);
		}

		public bool IsRoot()
		{
			return Parent == null;
		}

		// Returns the next node, or null if at the fictional last node
		public BinaryTree<T> Next()
		{
			BinaryTree<T> current = this;
			if (Right != null)
			{
				current = Right;
				while (current.Left != null)
					current = current.Left;
				return current;
			}
			while (current.Parent != null && current == current.Parent.Right)
				current = current.Parent;
			return current.Parent;
		}

		public BinaryTree<T> FindRoot()
		{
			if (IsRoot())
				return this;
			return Parent.FindRoot();
		}

		// Gets the index of the current node in the tree
		public int Index()
		{
			if (IsRoot())
				return Count - 1;

			BinaryTree<T> current = FindRoot();
			int skipped = 0;
			while (true)
			{
				int dif = current.IsRoot() ? -1 : Value.CompareTo(current.Value);
				if (dif == 0)
					return skipped + current.LeftCount;
				if (dif < 0)
					current = current.Left;
				if (dif > 0)
				{
					skipped += 1 + current.LeftCount;
					current = current.Right;
				}
			}
		}

		// Returns the node noNodes step ahead
		public BinaryTree<T> Step(int noNodes)
		{
			if (noNodes == 0)
				return this;
			if (IsRoot())
			{
				if (noNodes > 0)
					throw new ArgumentOutOfRangeException();
				if (-noNodes > LeftCount)
					throw new ArgumentOutOfRangeException();
			}

			if (noNodes > 0)
			{
				if (RightCount >= noNodes)
					return Right.Step(noNodes - Right.LeftCount - 1);
				if (Parent.Right == this)
					return Parent.Step(noNodes + LeftCount + 1);
				return Parent.Step(noNodes - RightCount - 1);
			}
			if (LeftCount >= -noNodes)
				return Left.Step(noNodes + Left.RightCount + 1);
			if (Parent.Left == this)
				return Parent.Step(noNodes - RightCount - 1);
			return Parent.Step(noNodes + LeftCount + 1);
		}

		protected void Replace(BinaryTree<T> oldNode, BinaryTree<T> newNode)
		{
			if (Left == oldNode)
			{
				Left = newNode;
				if (Left != null)
					Left.Parent = this;
			}
			else if (Right == oldNode)
			{
				Right = newNode;
				if (Right != null)
					Right.Parent = this;
			}
			else
				throw new ArgumentException();
		}

		public BinaryTree<T> Remove()
		{
			if (IsRoot())
				throw new InvalidOperationException("Can't delete the root node.");

			if (Left == null)
			{
				BinaryTree<T> next = Next();
				Parent.Replace(this, Right);
				BinaryTree<T> current = Parent;
				while (current != null)
				{
					current.Count--;
					current = current.Parent;
				}
				return next;
			}
			if (Right == null)
			{
				BinaryTree<T> next = Next();
				Parent.Replace(this, Left);
				BinaryTree<T> current = Parent;
				while (current != null)
				{
					current.Count--;
					current = current.Parent;
				}
				return next;
			}
			BinaryTree<T> successor = Next();
			if (successor.Left != null)
				throw new Exception();
			Value = successor.Value;
			successor.Remove();
			return this;
		}

		public BinaryTree<T> Find(T x)
		{
			int dif = IsRoot() ? -1 : x.CompareTo(Value);
			if (dif == 0)
				return this;
			if (dif < 0)
				return Left == null ? null : Left.Find(x);
			return Right == null ? null : Right.Find(x);
		}

		public BinaryTree<T> FindIndex(int index)
		{
			if (index < LeftCount)
				return Left.FindIndex(index);
			if (index == LeftCount)
				return this;
			if (Right == null)
				throw new ArgumentOutOfRangeException("index");
			return Right.FindIndex(index - LeftCount - 1);
		}
	}
}