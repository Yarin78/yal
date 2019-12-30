using System;
using System.Collections;
using System.Globalization;

namespace Algorithms.Tree
{
	public class Item
	{
		public object Key;
		public object Data;
	}

	/// <summary>
	/// Summary description for BinarySearchTree.
	/// </summary>
	public class BinarySearchTree
	{
		private IBinaryTree _tree;
		private IComparer _comparer;

		public BinarySearchTree(IBinaryTree tree) : this(tree, Comparer.Default)
		{
		}
		
		public BinarySearchTree(IBinaryTree tree, IComparer comparer)
		{
			_tree = tree;
			_comparer = comparer;
		}
	}
}
