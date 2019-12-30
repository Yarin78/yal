using System;
using System.Text;

namespace Algorithms.SearchTree
{
	/// <summary>
	/// Summary description for Tree.
	/// </summary>
	public class SearchTree
	{
		private ITreeItemRetriever _retriever;

		public SearchTree(ITreeItemRetriever treeItemRetriever)
		{
			if (treeItemRetriever == null)
				throw new ArgumentNullException("nodeRetriever");

			_retriever = treeItemRetriever;
		}

		public void ShowStructure()
		{
			TreeItemProxy tip = _retriever.GetRoot();
			if (tip.Item == null)
				Console.WriteLine("empty");
			else
			{
				StringBuilder sb = new StringBuilder();
				sb.Append("Root = " + tip.Item.Key.ToString() + "\r\n");
				tip.ShowStructure(sb);
				Console.WriteLine(sb.ToString());
			}
		}

		public void AddElement(IComparable key, object data)
		{
			if (key == null)
				throw new ArgumentNullException("key");
			if (data == null)
				throw new ArgumentNullException("data");

			TreeItem newItem = CreateItem(key, data);
			TreeItemProxy root = _retriever.GetRoot();
			if (root == null)
			{
				TreeItemProxy newItemProxy = _retriever.CreateItemProxy(newItem);
				_retriever.SetRoot(newItemProxy);
			}
			else
			{
				TreeItemProxy insertElement = root.GetParentElement(key);
				TreeItemProxy newItemProxy = _retriever.CreateItemProxy(newItem);
				if (key.CompareTo(insertElement.Item.Key) < 0)
					insertElement.LeftChild = newItemProxy;
				else
					insertElement.RightChild = newItemProxy;
			}
		}

		public void RemoveElement(IComparable key)
		{
			TreeItemProxy root = _retriever.GetRoot();
			if (root == null)
				throw new ArgumentException("key does not exist");
			TreeItemProxy tip = root.GetElement(key);
			if (tip == null)
				throw new ArgumentException("key does not exist");
			
			// Make element leaf
			if (tip.LeftChild != null)
			{
				TreeItemProxy swapElement = tip.LeftChild;
				while (swapElement.RightChild != null)
					swapElement = swapElement.RightChild;
				SwapElements(tip, swapElement);
			}
			else
			if (tip.RightChild != null)
			{
				TreeItemProxy swapElement = tip.RightChild;
				while (swapElement.LeftChild != null)
					swapElement = swapElement.LeftChild;
				SwapElements(tip, swapElement);
			}
		}

		public void SwapElements(TreeItemProxy innerNode, TreeItemProxy leaf)
		{
		}

		protected virtual TreeItem CreateItem(IComparable key, object data)
		{
			return new TreeItem(key, data);
		}

		/*
		/// <summary>
		/// Gets the data connected to a key, or null if the key does not exist in the tree
		/// </summary>
		/// <param name="key">The key for the element to get</param>
		/// <returns>The element object, or null if the key does not exist in the tree</returns>
		public object GetElement(IComparable key)
		{
			TreeItemProxy itemProxy = InternalGetElement(key);
			return itemProxy.Item.Data;
		}

		/// <summary>
		/// Locates an element in the tree by key
		/// </summary>
		/// <param name="key">The key used to located the element</param>
		/// <returns>A proxy to the tree item</returns>
		protected TreeItemProxy InternalGetElement(IComparable key)
		{
			TreeItemProxy root = _retriever.GetRoot();
			if (root is ExternalTreeItemProxy)
				return root;
			return root.GetElement(key);
		}
		*/
	}
}
