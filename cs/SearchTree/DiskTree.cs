using System;
using System.IO;

namespace Algorithms.SearchTree
{
	public class DiskTreeItemRetriever : ITreeItemRetriever
	{
		private FileStream _treeFileStream;

		public DiskTreeItemRetriever(string fileName, bool readOnly)
		{
			if (readOnly)
				_treeFileStream = File.Open(fileName, FileMode.Open, FileAccess.Read);
			else
			{
				_treeFileStream = File.Open(fileName, FileMode.OpenOrCreate, FileAccess.ReadWrite);
				if (_treeFileStream.Length == 0)
				{
					// TODO: Create header
				}
			}
		}

		public TreeItemProxy CreateItemProxy(TreeItem item)
		{
			// TODO:  Add DiskTreeItemRetriever.CreateItemProxy implementation
			return null;
		}

		public void SetRoot(TreeItemProxy rootItem)
		{
			// TODO:  Add DiskTreeItemRetriever.SetRoot implementation
		}

		public TreeItemProxy GetRoot()
		{
			// TODO:  Add DiskTreeItemRetriever.GetRoot implementation
			return null;
		}
		
		public DiskTreeItemProxy GetRecord(int recordId)
		{
			return new DiskTreeItemProxy(null);
		}
	}

	public class DiskTreeItemProxy : TreeItemProxy
	{
		private DiskTreeItemRetriever _retriever;

		private int _leftRecordId, _rightRecordId; // -1 = non-existent

		public DiskTreeItemProxy(TreeItem item) : base(item)
		{
		}

		public override TreeItemProxy LeftChild
		{
			get { return _retriever.GetRecord(_leftRecordId); }
			set { ; }
		}

		public override TreeItemProxy RightChild
		{
			get { return _retriever.GetRecord(_rightRecordId); }
			set { ; }
		}
		
	}
}
