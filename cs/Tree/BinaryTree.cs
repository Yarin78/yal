using System;

namespace Algorithms.Tree
{
	/// <summary>
	/// Summary description for BinaryTree.
	/// </summary>
	public class BinaryTree : IBinaryTree
	{
		private class BinaryTreeNode : IBinaryTreeNode
		{
			public int RecordNo;
			public BinaryTreeNode Parent, LeftChild, RightChild;
			private object _data;

			public override bool Equals(object obj)
			{
				return this == obj;
			}

			public object Data
			{
				get { return _data; }
				set { _data = value; }
			}
		}

		private IBinaryTreeNode _root = null;

		public IBinaryTreeNode Root()
		{
			return _root;
		}

		public IBinaryTreeNode Parent(IBinaryTreeNode v)
		{
			return ((BinaryTreeNode) v).Parent;
		}

		public IBinaryTreeNode LeftChild(IBinaryTreeNode v)
		{
			return ((BinaryTreeNode) v).LeftChild;
		}

		public IBinaryTreeNode RightChild(IBinaryTreeNode v)
		{
			return ((BinaryTreeNode) v).RightChild;
		}

		public void SetRoot(IBinaryTreeNode v)
		{
			_root = v;
		}

		public void SetLeftChild(IBinaryTreeNode v, IBinaryTreeNode childNode)
		{
			((BinaryTreeNode) v).LeftChild = (BinaryTreeNode) childNode;
			((BinaryTreeNode) childNode).Parent = (BinaryTreeNode) v;
		}

		public void SetRightChild(IBinaryTreeNode v, IBinaryTreeNode childNode)
		{
			((BinaryTreeNode) v).LeftChild = (BinaryTreeNode) childNode;
			((BinaryTreeNode) childNode).Parent = (BinaryTreeNode) v;
		}

		public bool IsLeaf(IBinaryTreeNode v)
		{
			BinaryTreeNode tn = (BinaryTreeNode) v;
			return tn.LeftChild == null && tn.RightChild == null;
		}

		public bool IsRoot(IBinaryTreeNode v)
		{
			return ((BinaryTreeNode) v).Parent == null;
		}

		public void DeleteLeaf(IBinaryTreeNode v)
		{
			if (!IsLeaf(v))
				throw new ArgumentException("Node is not a leaf");
			if (IsRoot(v))
			{
				SetRoot(null);
				return;
			}

			if (((BinaryTreeNode) v).Parent.RightChild == v)
				((BinaryTreeNode) v).Parent.RightChild = null;
			else if (((BinaryTreeNode) v).Parent.LeftChild == v)
				((BinaryTreeNode) v).Parent.LeftChild = null;
			else
				throw new Exception("Tree messed up");
		}

		public IBinaryTreeNode CreateNode()
		{
			return new BinaryTreeNode();
		}
	}
}
