using System;

namespace Algorithms.SearchTree
{
	public class InMemoryTreeItemRetriever : ITreeItemRetriever
	{
		private TreeItemProxy _root; // null if not set

		public InMemoryTreeItemRetriever()
		{
			// An in-memory tree is always empty on start
			_root = null;
		}

		public TreeItemProxy CreateItemProxy(TreeItem item)
		{
			return new InMemoryTreeItemProxy(item);
		}

		public void SetRoot(TreeItemProxy rootItem)
		{
			_root = rootItem;
		}
		
		public TreeItemProxy GetRoot()
		{
			return _root;
		}
	}

	public class InMemoryTreeItemProxy : TreeItemProxy
	{
		private TreeItemProxy _leftChild, _rightChild;

		public override TreeItemProxy LeftChild
		{
			get { return _leftChild; }
			set { _leftChild = value; }
		}

		public override TreeItemProxy RightChild
		{
			get { return _rightChild; }
			set { _rightChild = value; }
		}

		public InMemoryTreeItemProxy(TreeItem item) : base(item)
		{
		}
	}

}
