using System;
using System.Collections;
using System.Collections.Generic;
using System.IO;
using System.Text;
using Algorithms.Geometry;
using NUnit.Framework;

namespace Algorithms
{
	[TestFixture]
	public class UnitTest
	{
		[Test]
		public void TestPrioQueue()
		{
			Random r = new Random();
			PrioQueue<int> pq = new PrioQueue<int>();
			int noOp = 0;
			while (noOp < 10000)
			{
				int n = r.Next(100);
				while (n-- > 0)
				{
					pq.Push(r.Next(100));
					Assert.IsTrue(pq.IsHeap(0));
					noOp++;
				}
				n = r.Next(100);
				while (n-- > 0 && !pq.Empty)
				{
					pq.Pop();
					Assert.IsTrue(pq.IsHeap(0));
					noOp++;
				}
			}
		}

		[Test]
		public void TestPrioQueueMod()
		{
			Random r = new Random(0);
			int[] values = new int[10000];
			var notYetInserted = new TreeSet<int>();
			var notYetDeleted = new TreeSet<int>();
			for (int i = 0; i < 10000; i++)
			{
				values[i] = r.Next(1000);
				notYetInserted.Add(i);
			}

			var pq = new PrioQueueMod<int>((x, y) => values[x] - values[y], new ArrayLocator<int>(10000));
			
			while (notYetInserted.Count > 0 || notYetDeleted.Count > 0)
			{
				int n = r.Next(Math.Min(100, notYetInserted.Count+1));
				while (n-- > 0)
				{
					int id = notYetInserted.GetItem(r.Next(notYetInserted.Count));
					notYetInserted.Remove(id);
					notYetDeleted.Add(id);
					pq.Insert(id);
					Assert.IsTrue(pq.IsHeap(0));
				}
				n = r.Next(Math.Min(100, notYetDeleted.Count + 1));
				while (n-- > 0)
				{
					int id = notYetDeleted.GetItem(r.Next(notYetDeleted.Count));
					notYetDeleted.Remove(id);
					pq.Erase(id);
					Assert.IsTrue(pq.Empty || pq.IsHeap(0));
				}
			}
		}

		[Test]
		public void TestDijkstraHelper()
		{
			int n = 6;
			int[,] edges = new int[n,n];
			DijkstraHelper<int> dh = new DijkstraHelper<int>(n);
			edges[0, 1] = 3;
			edges[0, 2] = 8;
			edges[1, 2] = 4;
			edges[1, 3] = 6;
			edges[2, 4] = 7;
			edges[3, 5] = 5;
			edges[4, 1] = 5;
			edges[4, 3] = 2;
			edges[4, 5] = 8;

			dh.Add(0, 0);
			int cur = dh.GetNext();
			while (cur >= 0)
			{
				int curDist = dh.GetDistance(cur);
				Console.WriteLine("Node {0}, distance = {1}", cur, curDist);
				for (int i = 0; i < n; i++)
				{
					if (edges[i,cur] > 0)
						dh.Add(i, curDist + edges[i, cur]);
					if (edges[cur, i] > 0)
						dh.Add(i, curDist + edges[cur, i]);
				}
				cur = dh.GetNext();
			}
		}
            
        [Test]
        public void TestStringMatch()
        {
            StringMatch sm = new StringMatch();

            string s = "ababcabccabcdacdabcbcbdbaabacabbaabccabb";
            for(int i=0;i<s.Length;i++)
                for(int j=1;i+j<=s.Length;j++)
                {
                    string t = s.Substring(i, j);
                    Assert.AreEqual(s.IndexOf(t), sm.Find(s, t));
                }
        }
        
        [Test]
        public void TestLIS()
            {
            for (int tests = 0; tests < 3; tests++)
            {
                Random r = new Random(tests);
                int[] a = new int[20];
                for (int i = 0; i < a.Length; i++)
                    a[i] = r.Next(0, 100);

                int lis = Sequences.LongestIncreasingSubsequence(a);
                  /*  
                for (int i = 0; i < a.Length; i++)
                    Console.Write(a[i] + " ");
                Console.WriteLine();
                for (int i = 0; i < lis.Length; i++)
                    Console.Write(lis[i] + " ");
                Console.WriteLine();
                Console.WriteLine();*/
            }
        }

		[Test]
		public void TestBinaryTree()
		{
			int MAX = 1000;
			for (int cases = 0; cases < 100; cases++)
			{
				BinaryTree<int> tree = CreateTree(MAX);

				// Test tree traversal, node by node
				BinaryTree<int> current = tree.Find(1);
				for (int i = 1; i <= MAX; i++)
				{
					Assert.AreEqual(i, current.Value);
					Assert.AreEqual(i-1, current.Index());
					current = current.Next();
				}
				Assert.IsTrue(current.IsRoot());
				Assert.AreEqual(MAX, current.Index());

				// Test tree traversal, random jumps
				for (int i = 0; i < MAX; i++)
				{
					int start = _random.Next(1, MAX+2);
					int jumpTo = _random.Next(1, MAX+2);

					BinaryTree<int> x = start == MAX + 1 ? tree : tree.Find(start);
					BinaryTree<int> y = x.Step(jumpTo - start);
					if (jumpTo == MAX+1)
						Assert.IsTrue(y.IsRoot());
					else
						Assert.AreEqual(jumpTo, y.Value);
				}
				
				// Find indexes
				for (int i = 0; i < MAX; i++)
				{
					BinaryTree<int> bt = tree.FindIndex(i);
					Assert.AreEqual(i + 1, bt.Value);
				}
				BinaryTree<int> findRoot = tree.FindIndex(MAX);
				Assert.IsTrue(findRoot.IsRoot());

				if (cases % 2 == 0)
				{
					// remove in order
					current = tree.Find(1);
					for (int i = 1; i <= MAX; i++)
					{
						Assert.AreEqual(i, current.Value);
						current = current.Remove();
						tree.Validate();
					}
					Assert.IsTrue(current.IsRoot());
				}
				else
				{
					// remove in random order
					for (int i = 0; i < MAX; i++)
					{
						int pos = _random.Next(0,MAX-i);
						current = tree.FindIndex(pos);
						Assert.AreEqual(pos, current.Index());
						int oldValue = current.Value;
						current = current.Remove();
						Assert.AreEqual(pos, current.Index());
						if (!current.IsRoot())
						{
							int newValue = current.Value;
							Assert.IsTrue(oldValue < newValue);
						}
						tree.Validate();
					}
					Assert.IsTrue(current.IsRoot());
				}
			}
		}

		Random _random = new Random(0);

		private BinaryTree<int> CreateTree(int n)
		{
			int[] values = new int[n];
			for (int i = 0; i < n; i++)
				values[i] = i + 1;

			// Randomize insert order
			for (int i = 0; i < n - 1; i++)
			{
				int v = _random.Next(0, n - i);
				int tmp = values[i];
				values[i] = values[i + v];
				values[i + v] = tmp;
			}

			BinaryTree<int> bt = new BinaryTree<int>();
			for (int i = 0; i < n; i++)
				bt.Add(values[i]);
			bt.Validate();
			return bt;
		}

		[Test]
		public void TestSetRange()
		{
			var range = new SetRange(10);
			int[] chk = new int[1024];

			for (int i = 0; i < 100000; i++)
			{
				int pos = _random.Next(0, 1024);
				int val = _random.Next(0, 10);
				range.Insert(pos, val);
				chk[pos] += val;

				int a = _random.Next(0, 1024); // Inclusive
				int b = _random.Next(a+1, 1025); // Exclusive
				int expected = 0;
				for (int j = a; j < b; j++)
					expected += chk[j];
				int actual = range.Query(b) - range.Query(a);
				Assert.AreEqual(expected, actual);
			}
		}
        
		[Test]
		public void TestAggregationTree()
		{
			// Test static
			Random r = new Random();
			int[] data = new int[1000];
			const int P = 10007;
			for (int i = 0; i < data.Length; i++)
				data[i] = r.Next(1, P);
			var multTree = new AggregationTree<int>(data, (x,y) => (x*y)%P );
			var maxTree = new AggregationTree<int>(data, Math.Max);

			for (int i = 0; i < 100000; i++)
			{
				int start = r.Next(0, data.Length);
				int stop = start + r.Next(1, Math.Min(data.Length - start + 1, 101));

				int expectedProd = 1, expectedMax = 0;
				for (int j = start; j < stop; j++)
				{
					expectedProd = (expectedProd*data[j])%P;
					expectedMax = Math.Max(expectedMax, data[j]);
				}

				int actualProd = multTree.Query(start, stop);
				int actualMax = maxTree.Query(start, stop);

				Assert.AreEqual(expectedProd, actualProd);
				Assert.AreEqual(expectedMax, actualMax);

				int ix = r.Next(0, data.Length);
				int v = r.Next(1, P);
				data[ix] = v;
				multTree.Set(ix, v);
				maxTree.Set(ix, v);
			}
		}

		[Test]
		public void TestSegmentTree()
		{
			// Test static
			Random r = new Random();
			int[] data = new int[1000];
			for (int i = 0; i < data.Length; i++)
				data[i] = r.Next(1, 2000);
			var minTree = new SegmentTree<int>(data, (x,y) => x-y);

			for (int i = 0; i < 100000; i++)
			{
				int start = r.Next(0, data.Length);
				int stop = start + r.Next(1, Math.Min(data.Length - start + 1, 101));

				int expectedMin = int.MaxValue, expectedMinIndex = -1;
				for (int j = start; j < stop; j++)
				{
					if (data[j] < expectedMin)
					{
						expectedMin = data[j];
						expectedMinIndex = j;
					}
				}

				int actualMinIndex = minTree.Query(start, stop);
				int actualMin = minTree.Get(actualMinIndex);

				Assert.AreEqual(expectedMinIndex, actualMinIndex);
				Assert.AreEqual(expectedMin, actualMin);

				int ix = r.Next(0, data.Length);
				int v = r.Next(1, 2000);
				data[ix] = v;
				minTree.Set(ix, v);
			}
		}

		[Test]
		public void TestFixedMinimumRange()
		{
			var frmq = new FixedRangeMinimumQuery<int>();

			Random r = new Random(0);

			for (int size = 1; size < 1000; size++)
			{
				int[] a = new int[size];
				for (int i = 0; i < size; i++)
					a[i] = r.Next(1, 1000);

				for (int range = 1; range <= 20 && range <= size; range++)
				{
					int[] expected = new int[size - range + 1];
					for (int i = 0; i < expected.Length; i++)
					{
						int min = a[i];
						for (int j = 1; j < range; j++)
							min = Math.Min(min, a[i + j]);
						expected[i] = min;
					}

					int[] actual = frmq.FindMinimum(a, range);

					Assert.AreEqual(expected.Length, actual.Length);
					for (int i = 0; i < expected.Length; i++)
						Assert.AreEqual(expected[i], actual[i]);
				}
			}
		}

		[Test]
		public void TestTreeSet()
		{
			TreeSet<int> ts = new TreeSet<int>();
			SortedDictionary<int, object> sd = new SortedDictionary<int, object>();
			Random r = new Random(0);

			List<int> s = new List<int>();
			for (int i = 0; i < 1000; i++)
			{
				s.Add(i);
				s.Add(i);
			}
			for (int i = 0; i < 2000; i++)
			{
				var next = r.Next(2000-i);
				int tmp = s[i];
				s[i] = s[i + next];
				s[i + next] = tmp;
			}
			
			foreach(int x in s)
			{
				if (sd.ContainsKey(x))
				{
					bool a = sd.Remove(x);
					bool b = ts.Remove(x);
					Assert.AreEqual(a, b);
				}
				else
				{
					sd.Add(x, null);
					ts.Add(x);
				}

//				Console.WriteLine(ts.ToString());
				ts.Validate();


				var p = r.Next(1000);
				Assert.AreEqual(sd.ContainsKey(p), ts.Contains(p));

				Assert.AreEqual(sd.Count, ts.Count);
				int[] v = new int[sd.Count], w = new int[ts.Count];
				sd.Keys.CopyTo(v, 0);
				ts.CopyTo(w, 0);
				for (int i = 0; i < v.Length; i++)
					Assert.AreEqual(v[i], w[i]);
			}
		}

		[Test]
		public void TestTreeSetIndexing()
		{
			var set = new TreeSet<int>();

			for (int i = 0; i < 1000; i++)
			{
				set.Add(i);

				for (int j = 0; j <= i; j++)
				{
					Assert.AreEqual(j, set.GetIndex(j));
					Assert.AreEqual(j, set.GetItem(j));
				}
				for (int j = i+1; j < 1000; j++)
				{
					Assert.AreEqual(-1, set.GetIndex(j));
					Assert.AreEqual(0, set.GetItem(j));
				}
			}

			for (int i = 0; i < 1000; i++)
			{
				set.Remove(i);

				for (int j = 0; j < 1000; j++)
				{
					int expected = j - i - 1;
					if (expected < 0)
						expected = -1;
					int actual = set.GetIndex(j);
					Assert.AreEqual(expected, actual);

					expected = i + j + 1;
					if (expected >= 1000)
						expected = 0;
                    actual = set.GetItem(j);
					Assert.AreEqual(expected, actual);
				}
			}
		}

        [Test]
        public void TestTreeSetNextPrevious()
        {
            var treeSet = new TreeSet<int>();
            for (int i = 0; i < 1000; i++)
            {
                treeSet.Add(i);
            }

            var node = treeSet.FindNode(0);
            int expectedIndex = 0;
            while (node != null)
            {
                Assert.AreEqual(expectedIndex, node.Item);
                var actualIndex = treeSet.FindIndexByNode(node);
                Assert.AreEqual(expectedIndex, actualIndex);
                expectedIndex++;
                node = node.Next();
            }
            Assert.AreEqual(1000, expectedIndex);

            expectedIndex = 999;
            node = treeSet.FindNode(999);
            while (node != null)
            {
                Assert.AreEqual(expectedIndex, node.Item);
                var actualIndex = treeSet.FindIndexByNode(node);
                Assert.AreEqual(expectedIndex, actualIndex);
                expectedIndex--;
                node = node.Previous();
            }
            Assert.AreEqual(-1, expectedIndex);
        }

        [Test]
        public void TestTreeSetStartEnd()
        {
            int[] contents = new[] {2, 4, 7, 8, 12, 14, 16, 18, 19, 23, 27, 35, 37, 40};
            var treeSet = new TreeSet<int>();
            for (int index = 0; index < contents.Length; index++)
            {
                treeSet.Add(contents[index]);
            }
            Assert.AreEqual(contents.Length, treeSet.Count);

            for (int i = 0; i < 50; i++)
            {
                int expectedStart = -1, expectedEnd = -1;
                int j = 0;
                while (j < contents.Length && contents[j] < i) j++;
                if (j < contents.Length) expectedStart = contents[j];
                j = contents.Length - 1;
                while (j >= 0 && contents[j] > i) j--;
                if (j >= 0) expectedEnd = contents[j];

                var startNode = treeSet.FindStartNode(i);
                var stopNode = treeSet.FindEndNode(i);
                
                int actualStart = startNode == null ? -1 : startNode.Item;
                int actualEnd = stopNode == null ? -1 : stopNode.Item;

                Assert.AreEqual(expectedStart, actualStart);
                Assert.AreEqual(expectedEnd, actualEnd);
            }

            for (int i = 0; i < 50; i++)
            {
                for (int j = 0; j < 50; j++)
                {
                    int actualCount = 0, last = -1;
                    foreach(int x in treeSet.CreateWindow(i, j))
                    {
                        Assert.IsTrue(x > last);
                        Assert.IsTrue(Array.IndexOf(contents, x) >= 0);
                        last = x;
                        actualCount++;
                    }
                    int expectedCount = 0;
                    foreach(int x in contents)
                    {
                        if (x >= i && x <= j)
                            expectedCount++;
                    }
                    Assert.AreEqual(expectedCount, actualCount);
                }
            }
        }

	    [Test]
		public void TestNextPermutation()
		{
			Assert.IsFalse(Sequences.NextPermutation(new int[0]));
			Assert.IsFalse(Sequences.NextPermutation(new int[] { 7 }));

			var a = new int[8];
			for (int i = 0; i < a.Length; i++)
				a[i] = i;

			int[] previous = (int[]) a.Clone();
			int noPerm = 1;
			while (Sequences.NextPermutation(a))
			{
				noPerm++;
				int g = 0;
				for (int i = 0; i < 8 && g == 0; i++)
				{
					g = a[i] - previous[i];
				}
				Assert.IsTrue(g > 0);
				previous = (int[]) a.Clone();
			}
			Assert.AreEqual(40320, noPerm);

			a = new int[] {7, 10, 10, 5, 12, 7, 10};
			noPerm = 1; 
			while (Sequences.NextPermutation(a))
				noPerm++;
			Assert.AreEqual(292, noPerm);
		}

		[Test]
		public void TestEvaluator()
		{
			// TODO: Unary minus in input is not allowed
			double d = Evaluator.Evaluate("5+(9*(0-7))");
			Assert.AreEqual(-58.0, d);

			d = Evaluator.Evaluate("sin(2^9*3)");
			Assert.AreEqual(Math.Sin(512*3), d, 1e-8);

			d = Evaluator.Evaluate("5.3+2.9");
			Assert.AreEqual(8.2, d, 1e-8);
		}		
		
		[Test]
		public void TestShape3D()
		{
			var allShapes = new List<Shape3D>
			                	{
			                		new Shape3D(new[,]
			                		            	{
			                		            		{
			                		            			"###",
			                		            			"#.."
			                		            		},
			                		            			{
			                		            			"...",
			                		            			"#.."
			                		            		}
			                		            	}, true),
			                		new Shape3D(new[,]
			                		            	{
			                		            		{
			                		            			"###",
			                		            			"#.."
			                		            		},
			                		            		{
			                		            			".#.",
			                		            			"..."
			                		            		}
			                		            	}, true),
			                		new Shape3D(new[,]
			                		            	{
			                		            		{
			                		            			"##.",
			                		            			".#."
			                		            		},
			                		            		{
			                		            			"...",
			                		            			".##"
			                		            		}
			                		            	}, true),
			                		new Shape3D(new[,]
			                		            	{
			                		            		{
			                		            			"#.",
			                		            			"#."
			                		            		},
			                		            		{
			                		            			"##",
			                		            			".."
			                		            		}
			                		            	}, true),
			                		new Shape3D(new[,]
			                		            	{
			                		            		{
			                		            			"#.",
			                		            			"#."
			                		            		},
			                		            		{
			                		            			"##",
			                		            			".."
			                		            		}
			                		            	}, true),
			                		new Shape3D(new[,]
			                		            	{
			                		            		{
			                		            			"###",
			                		            			"#.."
			                		            		}
			                		            	}, true)
			                	};
			                		

			var masks = new List<long>[allShapes.Count];
			var shapes = new List<Shape3D>[allShapes.Count];
			for (int i = 0; i < allShapes.Count; i++)
			{
				masks[i] = new List<long>();
				shapes[i] = new List<Shape3D>();

				Shape3D[] rotations = i > 0 ? allShapes[i].GenerateRotations() : new[] {allShapes[i]};
				
				foreach (Shape3D rotatedShape in rotations)
				{
					foreach (Shape3D pos in rotatedShape.GenerateTranslations(3, 3, 3))
					{
						shapes[i].Add(pos);
						long mask = pos.GetBitMask(3, 3, 3);
						if (masks[i].Contains(mask))
							throw new Exception();
						masks[i].Add(mask);
					}
				}
			}
			var selected = new Shape3D[shapes.Length];
			int solutionCount = ShapeSearch(masks, shapes, selected, 0, 0);
			Console.WriteLine(solutionCount + " solutions");
		}

		private static void ShowSolution(Shape3D[] selected)
		{
			for (int z = 0; z < 3; z++)
			{
				for (int y = 0; y < 3; y++)
				{
					for (int x = 0; x < 3; x++)
					{
						int j = -1;
						for (int i = 0; i < selected.Length; i++)
						{
							if (selected[i].Points.Contains(new Shape3D.Point(x,y,z)))
							{
								if (j >= 0)
									throw new Exception();
								j = i;
							}
						}
						char c = (j < 0 ? '.' : (char) ('A' + j));
						Console.Write(c);
					}
					Console.WriteLine();
				}
				Console.WriteLine();
			}
			Console.WriteLine("-----");
		}

		static int ShapeSearch(List<long>[] shapeMasks, List<Shape3D>[] shapes, Shape3D[] selected, int curShape, long mask)
		{
			if (curShape == shapeMasks.Length)
			{
				ShowSolution(selected);
				return 1;
			}
			int noSolutions = 0;
			for (int i = 0; i < shapeMasks[curShape].Count; i++)
			{
				if ((mask & shapeMasks[curShape][i]) > 0)
					continue;
				selected[curShape] = shapes[curShape][i];
				int sols = ShapeSearch(shapeMasks, shapes, selected, curShape + 1, mask | shapeMasks[curShape][i]);
				noSolutions += sols;
			}
			return noSolutions;
		}

		[Test]
		public void TestTreeSetString()
		{
			var set = new TreeSet<string>(StringComparer.InvariantCultureIgnoreCase);
			string[] contents =
				{
					"water supply",
					"Threat",
					"emergency",
					"admiration",
					"public relations",
					"lignite",
					"water vapor",
					"Tranquilizer",
					"insurrectionist",
					"Threatened",
					"Cocaine",
					"water level",
					"neuromuscular",
					"Proton Magnetic Resonance",
					"Recession",
					"treacherous",
					"carbon monoxide poisoning"
				};
			foreach (var s in contents)
			{
				set.Add(s);
			}

			string prefix = "t";
			int count = 0;
			foreach (var item in set.CreateWindow("t", "tZZZZZZZ"))
			{
				Console.WriteLine(item);
				count++;
			}
			Assert.AreEqual(4, count);
		}

		[Test]
		public void TestIntervalSet()
		{
			// Test various "suffix" extensions
			var set = new IntervalSet<int>();
			set.Add(new Interval<int>(5, 7));
			Assert.AreEqual("[5, 7]", set.ToString());
			set.Add(new Interval<int>(6, 12));
			Assert.AreEqual("[5, 12]", set.ToString());
			set.Add(new Interval<int>(12, 14));
			Assert.AreEqual("[5, 14]", set.ToString());
			set.Add(new Interval<int>(15, 20));
			Assert.AreEqual("[5, 14] [15, 20]", set.ToString());
			set.Add(new Interval<int>(30, 40));
			Assert.AreEqual("[5, 14] [15, 20] [30, 40]", set.ToString());
			set.Add(new Interval<int>(6, 23));
			Assert.AreEqual("[5, 23] [30, 40]", set.ToString());
		
			// Test various "prefix" extensions
			set = new IntervalSet<int>();
			set.Add(new Interval<int>(30, 35));
			Assert.AreEqual("[30, 35]", set.ToString());
			set.Add(new Interval<int>(27, 32));
			Assert.AreEqual("[27, 35]", set.ToString());
			set.Add(new Interval<int>(24, 27));
			Assert.AreEqual("[24, 35]", set.ToString());
			set.Add(new Interval<int>(19, 23));
			Assert.AreEqual("[19, 23] [24, 35]", set.ToString());
			set.Add(new Interval<int>(2, 7));
			Assert.AreEqual("[2, 7] [19, 23] [24, 35]", set.ToString());
			set.Add(new Interval<int>(9, 25));
			Assert.AreEqual("[2, 7] [9, 35]", set.ToString());

			// Test extensions in both directions
			set = new IntervalSet<int>();
			set.Add(new Interval<int>(30, 35));
			Assert.AreEqual("[30, 35]", set.ToString());
			set.Add(new Interval<int>(27, 38));
			Assert.AreEqual("[27, 38]", set.ToString());

			// Test merge multiple subintervals
			set = new IntervalSet<int>();
			set.Add(new Interval<int>(30, 35));
			Assert.AreEqual("[30, 35]", set.ToString());
			set.Add(new Interval<int>(10, 15));
			Assert.AreEqual("[10, 15] [30, 35]", set.ToString());
			set.Add(new Interval<int>(20, 25));
			Assert.AreEqual("[10, 15] [20, 25] [30, 35]", set.ToString());
			set.Add(new Interval<int>(13, 32));
			Assert.AreEqual("[10, 35]", set.ToString());
		}

		private class IntervalSetDummy
		{
			private bool[] covered = new bool[Max];
			private static int Max = 10000;

			public void Add(Interval<int> interval)
			{
				for (int i = interval.Start; i < interval.End; i++)
				{
					covered[i] = true;
				}
			}

			public IEnumerable<Interval<int>> GetIntervals()
			{
				var intervals = new List<Interval<int>>();
				int x = 0;
				while (x < Max && !covered[x]) x++;
				while (x < Max)
				{
					int start = x;
					while (covered[x]) x++;
					intervals.Add(new Interval<int>(start, x));
					while (x < Max && !covered[x]) x++;
				}
				return intervals.ToArray();
			}

			public override string ToString()
			{
				var sb = new StringBuilder();
				foreach (var interval in GetIntervals())
				{
					if (sb.Length > 0)
						sb.Append(' ');
					sb.Append(interval.ToString());
				}
				return sb.ToString();
			}

			public Interval<int> FindOverlappingInterval(Interval<int> interval)
			{
				int x = interval.Start;
				while (x < interval.End && !covered[x]) x++;
				if (x == interval.End)
					return null;
				int start = x, end = x;
				while (start > 0 && covered[start - 1])
					start--;
				while (end < Max && covered[end])
					end++;
				if (start == end) throw new Exception();
				return new Interval<int>(start, end);
			}
		}

		[Test]
		public void TestIntervalSetRandom()
		{
			var random = new Random(0);

			for (int i = 0; i < 1000; i++)
			{
				var dummySet = new IntervalSetDummy();
				var fastSet = new IntervalSet<int>();

				for (int j = 0; j < 50; j++)
				{
					int start = random.Next(0, 9000);
					int end = start + random.Next(1, 300);
					var interval = new Interval<int>(start, end);

					dummySet.Add(interval);
					fastSet.Add(interval);

					int chkStart = random.Next(0, 9000);
					var chk = new Interval<int>(chkStart, chkStart + 1);
					var dummyOverlap = dummySet.FindOverlappingInterval(chk);
					var fastOverlap = fastSet.FindOverlappingInterval(chk);
					Assert.AreEqual(dummyOverlap, fastOverlap);
				}

				Assert.AreEqual(fastSet.ToString(), dummySet.ToString());
			}
		}
		
		[Test]
		public void TestCircularIntervalSet()
		{
			var set = new CircularIntervalSet<int>(0, 100);
			set.Add(new Interval<int>(12, 17));
			set.Add(new Interval<int>(77, 95));
			set.Add(new Interval<int>(92, 7));
			Assert.AreEqual("[77, 7] [12, 17]", set.ToString());
			set.Add(new Interval<int>(8, 15));
			Assert.AreEqual("[77, 7] [8, 17]", set.ToString());
			set.Add(new Interval<int>(17, 77));
			Assert.AreEqual("[8, 7]", set.ToString());
			set.Add(new Interval<int>(7, 8));
			Assert.AreEqual("[0, 100]", set.ToString());
			set.Add(new Interval<int>(70, 30));
			Assert.AreEqual("[0, 100]", set.ToString());

			set = new CircularIntervalSet<int>(0, 100);
			set.Add(new Interval<int>(50, 50));
			Assert.AreEqual("[0, 100]", set.ToString());

			set = new CircularIntervalSet<int>(0, 100);
			set.Add(new Interval<int>(50, 51));
			set.Add(new Interval<int>(63, 64));
			Assert.AreEqual("[50, 51] [63, 64]", set.ToString());
			set.Add(new Interval<int>(57, 95));
			Assert.AreEqual("[50, 51] [57, 95]", set.ToString());
		}

		[Test]
		public void TestTwoSAT()
		{
			/*var twoSat = new TwoSAT(7);
			const int A = 0, B = 2, C = 4, D = 6, E = 8, F = 10, G = 12;
			const int nA = 1, nB = 3, nC = 5, nD = 7, nE = 9, nF = 11, nG = 13;
			twoSat.Add(A, E);
			twoSat.Add(nB, C);
			twoSat.Add(D, F);
			twoSat.Add(G, F);
			twoSat.Add(nF, nD);
			twoSat.Add(B, E);
			twoSat.Add(nA, nB);
			twoSat.Add(nE, G);
			twoSat.Add(nC, nG);
			twoSat.Add(A, B);

			var variables = twoSat.Solve();
			Assert.IsNotNull(variables);
			for (int i = 0; i < 7; i++)
			{
				Console.WriteLine(variables[i]);
			}*/

			Random r = new Random(0);
			for (int caseNo = 0; caseNo < 20; caseNo++)
			{
				int noLiterals = 100, noClauses = 10000;
				
				bool[] value = new bool[noLiterals];
				for (int i = 0; i < noLiterals; i++)
				{
					value[i] = r.Next(2) == 0;
				}
				var twoSat = new TwoSAT(noLiterals);
				for (int i = 0; i < noClauses; i++)
				{
					int c1 = r.Next(noLiterals);
					int c2 = r.Next(noLiterals);
					bool n1 = r.Next(2) == 0;
					bool n2 = r.Next(2) == 0;
					if (value[c1] == n1 && value[c2] == n2)
					{
						i--;
						continue;
					}
					twoSat.AddClause(c1, n1, c2, n2);
				}
				var variables = twoSat.Solve();
				Assert.IsNotNull(variables);
				for (int i = 0; i < noLiterals; i++)
				{
					Assert.AreEqual(value[i], variables[i]);
				}
			}
		}
	}
}
