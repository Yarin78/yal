using System;

namespace Algorithms.Tree
{
	public interface IBinaryTree
	{
		IBinaryTreeNode Root();
		IBinaryTreeNode Parent(IBinaryTreeNode v);
		IBinaryTreeNode LeftChild(IBinaryTreeNode v);
		IBinaryTreeNode RightChild(IBinaryTreeNode v);
		void SetRoot(IBinaryTreeNode v);
		void SetLeftChild(IBinaryTreeNode v, IBinaryTreeNode childNode);
		void SetRightChild(IBinaryTreeNode v, IBinaryTreeNode childNode);
		bool IsLeaf(IBinaryTreeNode v);
		bool IsRoot(IBinaryTreeNode v);
		void DeleteLeaf(IBinaryTreeNode v);
		IBinaryTreeNode CreateNode();
	}

	public interface IBinaryTreeNode
	{
		public object Data { get; set; }
	}
}
