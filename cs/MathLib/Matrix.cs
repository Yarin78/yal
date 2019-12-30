using System;
using System.Globalization;
using System.Text;

namespace Algorithms.MathLib
{
	public class Matrix : ICloneable, IEquatable<Matrix>
	{
		private readonly double[,] m;
		private readonly int rows, columns;

		public int Rows
		{
			get { return rows; }
		}

		public int Columns
		{
			get { return columns; }
		}

		public double this[int y, int x]
		{
			get { return m[y, x]; }
			set { m[y, x] = value;}
		}

		private static bool AlmostZero(double v)
		{
			return Math.Abs(v) < 1e-9;
		}

		public Matrix(int rows, int columns)
		{
			m = new double[rows,columns];
			this.rows = rows;
			this.columns = columns;
		}

		public Matrix(double[,] m)
		{
			this.m = (double[,]) m.Clone();
			rows = m.GetLength(0);
			columns = m.GetLength(1);
		}

		public static Matrix CreateIdentity(int size)
		{
			Matrix m = new Matrix(size, size);
			for (int i = 0; i < size; i++)
				m[i, i] = 1;
			return m;
		}

		#region Elementar row operations

		public void SwitchRows(int rowA, int rowB)
		{
			for (int i = 0; i < columns ; i++)
			{
				double tmp = m[rowA, i];
				m[rowA, i] = m[rowB, i];
				m[rowB, i] = tmp;
			}
		}

		public void MultiplyRow(int row, double factor)
		{
			for (int i = 0; i < columns; i++)
				m[row, i] *= factor;
		}

		public void AddRow(int targetRow, int multRow, double factor)
		{
			for (int i = 0; i < columns; i++)
				m[targetRow, i] += m[multRow, i]*factor;
		}

		#endregion

		#region Operators

		public static Matrix operator*(Matrix a, Matrix b)
		{
			if (a.Columns != b.Rows)
				throw new ArgumentException();

			var matrix = new Matrix(a.Rows, b.Columns);
			for (int i = 0; i < a.Rows; i++)
			{
				for (int j = 0; j < b.Columns; j++)
				{
					double sum = 0.0;
					for (int k = 0; k < a.Columns; k++)
						sum += a[i, k] * b[k, j];
					matrix[i, j] = sum;
				}
			}
			return matrix;
		}

		private static Matrix Transform(Matrix a, Func<double, double> func)
		{
			var matrix = new Matrix(a.Rows, a.Rows);
			for (int i = 0; i < a.Rows; i++)
				for (int j = 0; j < a.Rows; j++)
					matrix[i, j] = func(a[i, j]);
			return matrix;
		}

		private static Matrix Transform(Matrix a, Matrix b, Func<double, double, double> func)
		{
			if (a.Rows != b.Rows || a.Columns != b.Columns)
				throw new ArgumentException();
			var matrix = new Matrix(a.Rows, a.Columns);
			for (int i = 0; i < a.Rows; i++)
				for (int j = 0; j < a.Columns; j++)
					matrix[i, j] = func(a[i, j], b[i, j]);
			return matrix;
		}

		public static Matrix operator+(Matrix a, Matrix b)
		{
			return Transform(a, b, (x, y) => x + y);
		}

		public static Matrix operator -(Matrix a, Matrix b)
		{
			return Transform(a, b, (x, y) => x - y);
		}

		public static Matrix operator *(Matrix a, double b)
		{
			return Transform(a, x => x * b);
		}

		public static Matrix operator +(Matrix a, double b)
		{
			return Transform(a, x => x + b);
		}

		public static Matrix operator -(Matrix a, double b)
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
					if (Math.Abs(m[k, j]) > Math.Abs(m[maxi, j]))
						maxi = k;

				if (!AlmostZero(m[maxi, j]))
				{
					int tmp = rowPivot[i];
					rowPivot[i] = rowPivot[maxi];
					rowPivot[maxi] = tmp;
					SwitchRows(i, maxi);
					MultiplyRow(i, 1/m[i, j]);
					for (int u = i + 1; u < rows; u++)
						AddRow(u, i, -m[u, j]);
					i++;
				}
			}
			return rowPivot;
		}

		public double[] SolveLinearEquation()
		{
			return ((Matrix)Clone()).InternalSolveLinearEquation();
		}

		internal double[] InternalSolveLinearEquation()
		{
			// Returns null if unsolveable, double.NaN for free variables (should be treated as 0)

			GaussElimination();
			double[] sol = new double[columns-1];
			int last = columns - 1;
			for (int i = rows - 1; i >= 0; i--)
			{
				int j = 0;
				while (AlmostZero(m[i, j]) && j < last)
					j++;
				if (j == last)
				{
					if (AlmostZero(m[i, columns-1]))
						continue;
					return null;
				}
				for (int k = j + 1; k < last; k++)
					sol[k] = double.NaN; // Free variable (multiple solutions)

				double v = m[i, columns - 1];
				for (int k = j + 1; k < columns - 1; k++)
					v -= m[i, k]*(double.IsNaN(sol[k]) ? 0 : sol[k]);
				sol[j] = v;
				last = j;
			}
			
			return sol;
		}

		public int GetRank()
		{
			return ((Matrix) Clone()).InternalGetRank();
		}

		public double GetDeterminant()
		{
			if (Rows != Columns)
				throw new ArgumentException("Matrix must be square.");
			return ((Matrix)Clone()).InternalGetDeterminant();
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
					if (!AlmostZero(m[i, j]))
						nonEmpty = true;
				}
				if (nonEmpty)
					rank++;
			}
			return rank;
		}

		internal double InternalGetDeterminant()
		{
			double det = 1;
			for (int i = 0, j = 0; i < rows && j < columns; j++)
			{
				int maxi = i;
				for (int k = i + 1; k < rows; k++)
					if (Math.Abs(m[k, j]) > Math.Abs(m[maxi, j]))
						maxi = k;

				if (AlmostZero(m[maxi, j]))
					return 0;

				if (i != maxi)
					det = -det;
				SwitchRows(i, maxi);
				det *= m[i, j];
				MultiplyRow(i, 1 / m[i, j]);
				for (int u = i + 1; u < rows; u++)
					AddRow(u, i, -m[u, j]);
				i++;
			}
			return det;
		}

		public Matrix GetSubMatrix(int r0, int r1, int c0, int c1)
		{
			if (r1 > Rows || c1 > Columns)
				throw new ArgumentException("Invalid subsize.");

			Matrix subMatrix = new Matrix(r1-r0, c1-c0);
			for (int i = r0; i < r1; i++)
				for (int j = c0; j < c1; j++)
					subMatrix[i - r0, j - c0] = m[i, j];
			return subMatrix;
		}

		public Matrix GetAugmentedMatrix(Matrix matrix)
		{
			if (Rows != matrix.Rows)
				throw new ArgumentException("Wrong number of rows.");

			Matrix augmentedMatrix = new Matrix(rows, columns + matrix.columns);
			for (int i = 0; i < rows; i++)
			{
				for (int j = 0; j < columns; j++)
					augmentedMatrix[i, j] = m[i, j];
				for (int j = 0; j < matrix.columns; j++)
					augmentedMatrix[i, j + columns] = matrix[i, j];
			}
			return augmentedMatrix;
		}

		public Matrix GetInverse()
		{
			if (rows != columns)
				throw new ArgumentException("Matrix must be square.");
			
			// No check if inverse exists is done here
			Matrix dirty = GetAugmentedMatrix(CreateIdentity(rows));
			dirty.GaussElimination();

			for (int i = rows - 1; i >= 0; i--)
				for (int j = 0; j < i; j++)
					dirty.AddRow(j, i, -dirty[j, i]);
			
			return dirty.GetSubMatrix(0, Rows, Columns, dirty.Columns);
		}

		public bool Equals(Matrix other)
		{
			if (Rows != other.Rows || Columns != other.Columns)
				return false;
			for (int y = 0; y < Rows; y++)
			{
				for (int x = 0; x < Columns; x++)
				{
					if (!AlmostZero(m[y,x] - other[y,x]))
						return false;
				}
			}
			return true;
		}

		public override bool Equals(object obj)
		{
			if (!(obj is Matrix))
				return false;
			return Equals((Matrix) obj);
		}

		public override string ToString()
		{
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < rows; i++)
			{
				sb.Append("[ ");
				for (int j = 0; j < columns; j++)
				{
					sb.AppendFormat(CultureInfo.InvariantCulture, "{0,7:N3}", m[i, j]);
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