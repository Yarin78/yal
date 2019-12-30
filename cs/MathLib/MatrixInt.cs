using System;
using System.Globalization;
using System.Text;

namespace Algorithms.MathLib
{
	public class MatrixInt : ICloneable, IEquatable<MatrixInt>
	{
		private readonly int[,] m;
		private readonly int rows, columns, mod;

		public int Rows
		{
			get { return rows; }
		}

		public int Columns
		{
			get { return columns; }
		}

		public int this[int y, int x]
		{
			get { return m[y, x]; }
			set { m[y, x] = Fix(value); }
		}

		public MatrixInt(int rows, int columns, int mod)
		{
			m = new int[rows,columns];
			this.rows = rows;
			this.columns = columns;
			this.mod = mod;
		}

		public MatrixInt(int[,] m, int mod)
		{
			this.m = (int[,])m.Clone();
			rows = m.GetLength(0);
			columns = m.GetLength(1);
			this.mod = mod;

			FixAll();
		}

		private int Fix(long x)
		{
			x %= mod;
			if (x<0)
				x += mod;
			return (int) x;
		}

		private void FixAll()
		{
			for (int r = 0; r < Rows; r++)
				for (int c = 0; c < Columns; c++)
					m[r, c] = Fix(m[r, c]);
		}

		public static MatrixInt CreateIdentity(int size, int mod)
		{
			MatrixInt m = new MatrixInt(size, size, mod);
			for (int i = 0; i < size; i++)
				m[i, i] = 1;
			return m;
		}

		#region Elementar row operations

		public void SwitchRows(int rowA, int rowB)
		{
			for (int i = 0; i < columns ; i++)
			{
				int tmp = m[rowA, i];
				m[rowA, i] = m[rowB, i];
				m[rowB, i] = tmp;
			}
		}

		public void MultiplyRow(int row, int factor)
		{
			factor = Fix(factor);

			for (int i = 0; i < columns; i++)
				m[row, i] = Fix(((long) m[row, i])*factor);
		}

		public void AddRow(int targetRow, int multRow, int factor)
		{
			factor = Fix(factor);
			for (int i = 0; i < columns; i++)
				m[targetRow, i] = Fix(m[targetRow, i] + ((long) m[multRow, i])*factor);
		}

		#endregion

		#region Operators

		public static MatrixInt operator*(MatrixInt a, MatrixInt b)
		{
			if (a.Columns != b.Rows || a.mod != b.mod)
				throw new ArgumentException();

			var matrix = new MatrixInt(a.Rows, b.Columns, a.mod);
			for (int i = 0; i < a.Rows; i++)
			{
				for (int j = 0; j < b.Columns; j++)
				{
					long sum = 0;
					for (int k = 0; k < a.Columns; k++)
						sum += (long) a[i, k]*b[k, j];
					matrix[i, j] = (int) (sum % a.mod);
				}
			}
			return matrix;
		}

		private static MatrixInt Transform(MatrixInt a, Func<int, int> func)
		{
			var matrix = new MatrixInt(a.Rows, a.Rows, a.mod);
			for (int i = 0; i < a.Rows; i++)
				for (int j = 0; j < a.Rows; j++)
					matrix[i, j] = func(a[i, j]);
			return matrix;
		}

		private static MatrixInt Transform(MatrixInt a, MatrixInt b, Func<int, int, int> func)
		{
			if (a.Rows != b.Rows || a.Columns != b.Columns || a.mod != b.mod)
				throw new ArgumentException();
			var matrix = new MatrixInt(a.Rows, a.Columns, a.mod);
			for (int i = 0; i < a.Rows; i++)
				for (int j = 0; j < a.Columns; j++)
					matrix[i, j] = func(a[i, j], b[i, j]);
			return matrix;
		}

		public static MatrixInt operator+(MatrixInt a, MatrixInt b)
		{
			return Transform(a, b, (x, y) => x + y);
		}

		public static MatrixInt operator -(MatrixInt a, MatrixInt b)
		{
			return Transform(a, b, (x, y) => x - y);
		}

		public static MatrixInt operator *(MatrixInt a, int b)
		{
			return Transform(a, x => (int) ((long) x * b) % a.mod);
		}

		public static MatrixInt operator +(MatrixInt a, int b)
		{
			return Transform(a, x => x + b);
		}

		public static MatrixInt operator -(MatrixInt a, int b)
		{
			return Transform(a, x => x - b);
		}

		#endregion

		public int[] GaussElimination()
		{
			int[] rowPivot = new int[rows];
			for (int i = 0; i < rows; i++)
				rowPivot[i] = i;
			// Turns the matrix into row-echelon form
			for (int i = 0, j = 0; i < rows && j < columns; j++)
			{
				int maxi = i;
				for (int k = i + 1; k < rows; k++)
					if (m[k, j] > m[maxi, j])
						maxi = k;

				if (m[maxi, j] != 0)
				{
					int tmp = rowPivot[i];
					rowPivot[i] = rowPivot[maxi];
					rowPivot[maxi] = tmp;
					SwitchRows(i, maxi);
					int inv = GetModInverse(m[i, j]);
					MultiplyRow(i, inv);
					for (int u = i + 1; u < rows; u++)
						AddRow(u, i, -m[u, j]);
					i++;
				}
			}
			return rowPivot;
		}

		private static int ExtendedEuclid(int a, int b, out int q, out int r)
		{
			int p;
			if (b == 0) { q = 1; r = 0; return a; }
			int d = ExtendedEuclid(b, a % b, out p, out q);
			r = p - a / b * q;
			return d;
		}

		private int GetModInverse(int x)
		{
			int p, q;
			if (ExtendedEuclid(x, mod, out p, out q) == 0)
				throw new Exception(); // No inverse exists
			p = Fix(p);

			return p;
		}

		public int[] SolveLinearEquation()
		{
			return ((MatrixInt)Clone()).InternalSolveLinearEquation();
		}

		internal int[] InternalSolveLinearEquation()
		{
			// Returns null if unsolveable, -1 for free variables (should be treated as 0)

			GaussElimination();
			int[] sol = new int[columns - 1];
			int last = columns - 1;
			for (int i = rows - 1; i >= 0; i--)
			{
				int j = 0;
				while (m[i, j] == 0 && j < last)
					j++;
				if (j == last)
				{
					if (m[i, columns-1] == 0)
						continue;
					return null;
				}
				for (int k = j + 1; k < last; k++)
					sol[k] = -1; // Free variable (multiple solutions)

				long v = m[i, columns - 1];
				for (int k = j + 1; k < columns - 1; k++)
					v = Fix(v - (long) m[i, k]*(sol[k] == -1 ? 0 : sol[k]));
				sol[j] = (int) v;
				last = j;
			}
			
			return sol;
		}

		public int GetRank()
		{
			return ((MatrixInt) Clone()).InternalGetRank();
		}

		public int GetDeterminant()
		{
			if (Rows != Columns)
				throw new ArgumentException("Matrix must be square.");
			return ((MatrixInt)Clone()).InternalGetDeterminant();
		}

		internal int InternalGetRank()
		{
			GaussElimination();
			int rank = 0;
			for (int i = 0; i < rows; i++)
			{
				bool nonEmpty = false;
				for (int j = 0; j < columns && !nonEmpty; j++)
				{
					if (m[i, j] != 0)
						nonEmpty = true;
				}
				if (nonEmpty)
					rank++;
			}
			return rank;
		}

		internal int InternalGetDeterminant()
		{
			long det = 1;
			for (int i = 0, j = 0; i < rows && j < columns; j++)
			{
				int maxi = i;
				for (int k = i + 1; k < rows; k++)
					if (m[k, j] > m[maxi, j])
						maxi = k;

				if (m[maxi, j] == 0)
					return 0;

				if (i != maxi)
					det = -det;
				SwitchRows(i, maxi);
				det = Fix(det*m[i, j]);
				MultiplyRow(i, GetModInverse(m[i, j]));
				for (int u = i + 1; u < rows; u++)
					AddRow(u, i, -m[u, j]);
				i++;
			}
			return (int) det;
		}

		public MatrixInt GetSubMatrix(int r0, int r1, int c0, int c1)
		{
			if (r1 > Rows || c1 > Columns)
				throw new ArgumentException("Invalid subsize.");

			MatrixInt subMatrix = new MatrixInt(r1-r0, c1-c0, mod);
			for (int i = r0; i < r1; i++)
				for (int j = c0; j < c1; j++)
					subMatrix[i - r0, j - c0] = m[i, j];
			return subMatrix;
		}

		public MatrixInt GetAugmentedMatrix(MatrixInt matrix)
		{
			if (Rows != matrix.Rows || mod != matrix.mod)
				throw new ArgumentException("Wrong number of rows.");

			MatrixInt augmentedMatrix = new MatrixInt(rows, columns + matrix.columns, mod);
			for (int i = 0; i < rows; i++)
			{
				for (int j = 0; j < columns; j++)
					augmentedMatrix[i, j] = m[i, j];
				for (int j = 0; j < matrix.columns; j++)
					augmentedMatrix[i, j + columns] = matrix[i, j];
			}
			return augmentedMatrix;
		}

		public MatrixInt GetInverse()
		{
			if (rows != columns)
				throw new ArgumentException("Matrix must be square.");
			
			// No check if inverse exists is done here
			MatrixInt dirty = GetAugmentedMatrix(CreateIdentity(rows, mod));
			dirty.GaussElimination();

			for (int i = rows - 1; i >= 0; i--)
				for (int j = 0; j < i; j++)
					dirty.AddRow(j, i, -dirty[j, i]);
			
			return dirty.GetSubMatrix(0, Rows, Columns, dirty.Columns);
		}

		public bool Equals(MatrixInt other)
		{
			if (Rows != other.Rows || Columns != other.Columns)
				return false;
			for (int y = 0; y < Rows; y++)
			{
				for (int x = 0; x < Columns; x++)
				{
					if (m[y,x] != other[y,x])
						return false;
				}
			}
			return true;
		}

		public override bool Equals(object obj)
		{
			if (!(obj is MatrixInt))
				return false;
			return Equals((MatrixInt) obj);
		}

		public override string ToString()
		{
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < rows; i++)
			{
				sb.Append("[ ");
				for (int j = 0; j < columns; j++)
				{
					sb.AppendFormat(CultureInfo.InvariantCulture, "{0,5:D}", m[i, j]);
					sb.Append(" ");
				}
				sb.AppendLine("]");
			}
			return sb.ToString();
		}

		public object Clone()
		{
			return GetSubMatrix(0, Rows, 0, Columns);
		}
	}
}