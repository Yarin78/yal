using System;

namespace Algorithms.SearchTree
{
	/// <summary>
	/// Summary description for AVLTree.
	/// </summary>
	public class AVLTree
	{
		public AVLTree()
		{
			//
			// TODO: Add constructor logic here
			//
		}
	}

	public class AVLTreeItem : TreeItem
	{
		private byte _heightDifference;

		public byte HeightDifference
		{
			get { return _heightDifference; }
			set { _heightDifference = value; }
		}

		public AVLTreeItem(IComparable key, object data) : base(key, data)
		{
			_heightDifference = 0;
		}
	}
}
