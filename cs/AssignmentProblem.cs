using System;

namespace Algorithms
{
	public class AssignmentProblem
	{
		public bool[,] HungarianAlgorithm(int[,] Array)
		{
			const int INF = int.MaxValue;
			
			int ysize = Array.GetLength(0), xsize = Array.GetLength(1);
			bool[,] Result = new bool[ysize, xsize];
			
			int s;
			int[] col_mate = new int[ysize];
			int[] row_mate = new int[xsize];
			int[] parent_row = new int[xsize];
			int[] unchosen_row = new int[ysize];
			int[] row_dec = new int[ysize];
			int[] col_inc = new int[xsize];
			int[] slack = new int[xsize];
			int[] slack_row = new int[xsize];
			int cost = 0;

			// Begin subtract column minima in order to start with lots of zeroes 12
//			Console.WriteLine("Using heuristic\n");
			for (int i = 0; i < xsize; i++)
			{
				s = Array[0, i];
				for (int j = 1; j < xsize; j++)
					if (Array[j, i] < s)
						s = Array[j, i];
				cost += s;
				if (s != 0)
					for (int j = 0; j < xsize; j++)
						Array[j, i] -= s;
			}
			// End subtract column minima in order to start with lots of zeroes 12

			// Begin initial state 16
			int t = 0;
			for (int i = 0; i < xsize; i++)
			{
				row_mate[i] = -1;
				parent_row[i] = -1;
				col_inc[i] = 0;
				slack[i] = INF;
			}
			for (int i = 0; i < ysize; i++)
			{
				s = Array[i, 0];
				for (int j = 1; j < xsize; j++)
					if (Array[i, j] < s)
						s = Array[i, j];
				row_dec[i] = s;
				bool rowDone = false;
				for (int j = 0; j < xsize && !rowDone; j++)
					if (s == Array[i, j] && row_mate[j] < 0)
					{
						col_mate[i] = j;
						row_mate[j] = i;
						//	      if (verbose)
						//	        printf("matching col %d==row %d\n",l,k);
						rowDone = true;
					}
				if (!rowDone)
				{
					col_mate[i] = -1;
					//	  if (verbose)
					//	    printf("node %d: unmatched row %d\n",t,k);
					unchosen_row[t++] = i;
				}
			}
			// End initial state 16

			// Begin Hungarian algorithm 18
			if (t == 0)
				goto done;
			int k, l;
			int unmatched = t;
			while (true)
			{
				//	  if (verbose)
				//	    printf("Matched %d rows.\n",m-t);
				int q = 0;
				while (true)
				{
					while (q < t)
					{
						// Begin explore node q of the forest 19
						{
							k = unchosen_row[q];
							s = row_dec[k];
							for (l = 0; l < xsize; l++)
								if (slack[l] != 0)
								{
									int del = Array[k, l] - s + col_inc[l];
									if (del < slack[l])
									{
										if (del == 0)
										{
											if (row_mate[l] < 0)
												goto breakthru;
											slack[l] = 0;
											parent_row[l] = k;
											//	                if (verbose)
											//	                  printf("node %d: row %d==col %d--row %d\n", t,row_mate[l],l,k);
											unchosen_row[t++] = row_mate[l];
										}
										else
										{
											slack[l] = del;
											slack_row[l] = k;
										}
									}
								}
						}
						// End explore node q of the forest 19
						q++;
					}

					// Begin introduce a new zero into the matrix 21
					s = INF;
					for (l = 0; l < xsize; l++)
						if (slack[l] != 0 && slack[l] < s)
							s = slack[l];
					for (q = 0; q < t; q++)
						row_dec[unchosen_row[q]] += s;
					for (l = 0; l < xsize; l++)
						if (slack[l] != 0)
						{
							slack[l] -= s;
							if (slack[l] == 0)
							{
								// Begin look at a new zero 22
								k = slack_row[l];
								//	          if (verbose)
								//	            printf("Decreasing uncovered elements by %d produces zero at [%d,%d]\n", s,k,l);
								if (row_mate[l] < 0)
								{
									for (int j = l + 1; j < xsize; j++)
										if (slack[j] == 0)
											col_inc[j] += s;
									goto breakthru;
								}
								parent_row[l] = k;
								//	            if (verbose)
								//	              printf("node %d: row %d==col %d--row %d\n",t,row_mate[l],l,k);
								unchosen_row[t++] = row_mate[l];
								// End look at a new zero 22
							}
						}
						else
							col_inc[l] += s;
					// End introduce a new zero into the matrix 21
				}
			breakthru:
				// Begin update the matching 20
				//	  if (verbose)
				//	    printf("Breakthrough at node %d of %d!\n",q,t);
				while (true)
				{
					int j = col_mate[k];
					col_mate[k] = l;
					row_mate[l] = k;
					//	    if (verbose)
					//	      printf("rematching col %d==row %d\n",l,k);
					if (j < 0)
						break;
					k = parent_row[j];
					l = j;
				}
				// End update the matching 20
				if (--unmatched == 0)
					goto done;
				// Begin get ready for another stage 17
				t = 0;
				for (int i = 0; i < xsize; i++)
				{
					parent_row[i] = -1;
					slack[i] = INF;
				}
				for (int i = 0; i < ysize; i++)
					if (col_mate[i] < 0)
					{
						//	      if (verbose)
						//	        printf("node %d: unmatched row %d\n",t,k);
						unchosen_row[t++] = i;
					}
				// End get ready for another stage 17
			}
		done:

			// Begin doublecheck the solution 23
			for (int y = 0; y < ysize; y++)
				for (int x = 0; x < xsize; x++)
					if (Array[y, x] < row_dec[y] - col_inc[x])
						throw new Exception();
			for (int y = 0; y < ysize; y++)
			{
				l = col_mate[y];
				if (l < 0 || Array[y, l] != row_dec[y] - col_inc[l])
					throw new Exception();
			}
			k = 0;
			for (int x = 0; x < xsize; x++)
				if (col_inc[x] != 0)
					k++;
			if (k > ysize)
				throw new Exception();
			// End doublecheck the solution 23
			// End Hungarian algorithm 18

			for (int y = 0; y < ysize; ++y)
			{
				Result[y, col_mate[y]] = true;
				/*TRACE("%d - %d\n", i, col_mate[i]);*/
			}
			for (int y = 0; y < ysize; ++y)
			{
				for (int x = 0; x < xsize; ++x)
				{
					/*TRACE("%d ",Array[k][l]-row_dec[k]+col_inc[l]);*/
					Array[y, x] = Array[y, x] - row_dec[y] + col_inc[x];
				}
				/*TRACE("\n");*/
			}
			for (int i = 0; i < ysize; i++)
				cost += row_dec[i];
			for (int i = 0; i < xsize; i++)
				cost -= col_inc[i];
			//	printf("Cost is %d\n",cost);
			return Result;
		}
	}
}