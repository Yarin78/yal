using System;
using System.IO;
using System.Runtime.Serialization.Formatters.Binary;

namespace Algorithms.SearchTree
{
	public interface ITreeItemRetriever
	{
		TreeItemProxy CreateItemProxy(TreeItem item);
		void SetRoot(TreeItemProxy rootItem);
		TreeItemProxy GetRoot();
	}
}
