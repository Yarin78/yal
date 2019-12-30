using System;
using NUnit.Framework;

namespace Algorithms.SearchTree
{
	[TestFixture]
	public class UnitTest
	{
		[Test]
		public void TestStandardBinaryTree()
		{
			SearchTree tree = new SearchTree(new InMemoryTreeItemRetriever());
			int[] keys = new int[] { 7, 3, 5, 1, 2, 9, 8, 6, 4 };
			foreach(int key in keys)
			{
				tree.AddElement(key, key);
			}
			tree.RemoveElement(3);
			tree.ShowStructure();
			Console.ReadLine();
		}
	}
}
