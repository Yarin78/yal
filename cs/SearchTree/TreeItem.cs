using System;
using System.Text;

namespace Algorithms.SearchTree
{
	/// <summary>
	/// Summary description for TreeItem.
	/// </summary>
	public class TreeItem
	{
		public IComparable Key;
		public object Data;

		public TreeItem(IComparable key, object data)
		{
			Key = key;
			Data = data;
		}
	}

	public abstract class TreeItemProxy
	{
		private TreeItem _item;
		private TreeItemProxy _parent;
		private bool _isLeftChild;

		public TreeItem Item
		{
			get { return _item; }
		}

		public TreeItemProxy(TreeItem item, TreeItemProxy parent, bool isLeftChild)
		{
			this._item = item;
			this._isLeftChild = isLeftChild;
		}

		public void ShowStructure(StringBuilder sb)
		{
			/*
			sb.Append('(');
			if (LeftChild != null)
				LeftChild.ShowStructure(sb);
			sb.AppendFormat("){0}(", Item.Key.ToString());
			if (RightChild != null)
				RightChild.ShowStructure(sb);
			sb.Append(')');
			*/
			if (LeftChild != null)
				LeftChild.ShowStructure(sb);
			sb.AppendFormat("{0}: {1} {2}\r\n", Item.Key.ToString(), LeftChild == null ? "x" : LeftChild.Item.Key, RightChild == null ? "x" : RightChild.Item.Key );
			if (RightChild != null)
				RightChild.ShowStructure(sb);
		}

		public abstract TreeItemProxy LeftChild
		{
			get; set;
		}

		public abstract TreeItemProxy RightChild
		{
			get; set;
		}

		public TreeItemProxy Parent
		{
			get { return _parent; }
		}

		public bool IsLeftChild
		{
			get { return _isLeftChild; }
		}
		
		public TreeItemProxy GetElement(IComparable key)
		{
			int dif = key.CompareTo(_item.Key);
			if (dif == 0)
				return this;
			if (dif < 0)
				return LeftChild == null ? null : LeftChild.GetElement(key);
			return RightChild == null ? null : RightChild.GetElement(key);
		}

		public TreeItemProxy GetParentElement(IComparable key)
		{
			int dif = key.CompareTo(_item.Key);
			if (dif == 0)
				throw new ArgumentException("An element with the same key already exist in the tree.");
			if (dif < 0)
			{
				if (LeftChild != null)
					return LeftChild.GetParentElement(key);
				return this;
			}
			if (RightChild != null)
				return RightChild.GetParentElement(key);
			return this;
		}
	}

	/*
	public class ExternalTreeItemProxy : TreeItemProxy
	{
		public TreeItemProxy Parent;
		public bool IsLeftChild;

		public override TreeItemProxy LeftChild
		{
			get { return null; }
			set { }
		}

		public override TreeItemProxy RightChild
		{
			get { return null; }
			set { }
		}

		public ExternalTreeItemProxy(TreeItemProxy parent, bool isLeftChild) : base(null)
		{
			Parent = parent;
			IsLeftChild = isLeftChild;
		}
	}
	*/

}
