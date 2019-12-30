package yarin.yal.mathlib;

// The equation solver has been tested with https://open.kattis.com/problems/equationsolverplus
public class Matrix {
    private final double[][] m;
    private final int rows, columns;

    public int getRows() { return rows; }

    public int getColumns() { return columns; }

    public double get(int y, int x) { return m[y][x]; }

    public void set(int y, int x, double value) { m[y][x] = value; }

    private static boolean almostZero(double v) {
        return Math.abs(v) < 1e-9;
    }

    public Matrix(int rows, int columns) {
        this.m = new double[rows][columns];
        this.rows = rows;
        this.columns = columns;
    }

    public Matrix(double[][] m) {
        this.m = m.clone();
        this.rows = m.length;
        this.columns = m[0].length;
    }

    public static Matrix createIdentity(int size) {
        Matrix m = new Matrix(size, size);
        for (int i = 0; i < size; i++)
            m.set(i,i,1);
        return m;
    }

    // Elementary row operations

    public void switchRows(int rowA, int rowB) {
        for (int i = 0; i < columns ; i++) {
            double tmp = m[rowA][i];
            m[rowA][i] = m[rowB][i];
            m[rowB][i] = tmp;
        }
    }

    public void multiplyRow(int row, double factor) {
        for (int i = 0; i < columns; i++)
            m[row][i] *= factor;
    }

    public void addRow(int targetRow, int multRow, double factor) {
        for (int i = 0; i < columns; i++)
            m[targetRow][i] += m[multRow][i]*factor;
    }

    // Operators

    public static interface UnaryTransformation {
        double transform(double x);
    }

    public static interface BinaryTransformation {
        double transform(double x, double y);
    }

    public static Matrix mul(Matrix a, Matrix b) {
        if (a.columns != b.rows)
            throw new IllegalArgumentException();

        Matrix matrix = new Matrix(a.rows, b.columns);
        for (int i = 0; i < a.rows; i++) {
            for (int j = 0; j < b.columns; j++) {
                double sum = 0.0;
                for (int k = 0; k < a.columns; k++)
                    sum += a.get(i,k) * b.get(k,j);
                matrix.set(i,j,sum);
            }
        }
        return matrix;
    }

    private static Matrix transform(Matrix a, UnaryTransformation func) {
        Matrix matrix = new Matrix(a.rows, a.columns);
        for (int i = 0; i < a.rows; i++)
            for (int j = 0; j < a.columns; j++)
                matrix.set(i,j,func.transform(a.get(i, j)));
        return matrix;
    }

    private static Matrix transform(Matrix a, Matrix b, BinaryTransformation func) {
        if (a.rows != b.rows || a.columns != b.columns)
            throw new IllegalArgumentException();
        Matrix matrix = new Matrix(a.rows, a.columns);
        for (int i = 0; i < a.rows; i++)
            for (int j = 0; j < a.columns; j++)
                matrix.set(i,j,func.transform(a.get(i, j), b.get(i, j)));
        return matrix;
    }

    public static Matrix add(Matrix a, Matrix b) {
        return transform(a, b, new BinaryTransformation() {
            public double transform(double x, double y) {
                return x + y;
            }
        });
    }

    public static Matrix sub(Matrix a, Matrix b) {
        return transform(a, b, new BinaryTransformation() {
            public double transform(double x, double y) {
                return x - y;
            }
        });
    }

    public static Matrix mul(Matrix a, final double b) {
        return transform(a, new UnaryTransformation() {
            public double transform(double x) {
                return x * b;
            }
        });
    }

    public static Matrix add(Matrix a, final double b) {
        return transform(a, new UnaryTransformation() {
            public double transform(double x) {
                return x + b;
            }
        });
    }

    public static Matrix sub(Matrix a, final double b) {
        return transform(a, new UnaryTransformation() {
            public double transform(double x) {
                return x - b;
            }
        });
    }

    // Algorithms

    public int[] gaussElimination() {
        int[] rowPivot = new int[rows];
        for (int i = 0; i < rows; i++)
            rowPivot[i] = i;
        // Turns the matrix into reduced row-echelon form
        for (int i = 0, j = 0; i < rows && j < columns; j++) {
            int maxi = i;
            for (int k = i + 1; k < rows; k++)
                if (Math.abs(m[k][j]) > Math.abs(m[maxi][j]))
                    maxi = k;

            if (!almostZero(m[maxi][j])) {
                int tmp = rowPivot[i];
                rowPivot[i] = rowPivot[maxi];
                rowPivot[maxi] = tmp;
                switchRows(i, maxi);
                multiplyRow(i, 1 / m[i][j]);
                for (int u = i + 1; u < rows; u++)
                    addRow(u, i, -m[u][j]);
                i++;
            }
        }
        return rowPivot;
    }

    public void completeGaussElimination() {
        // Assumes matrix is in reduced row-echelon form
        // and tries to reduce it even further

        for (int i = rows - 1; i >= 0; i--) {
            int j = 0;
            while (j < columns - 1 && almostZero(m[i][j])) j++;
            if (j < columns - 1) {
                for (int k = 0; k < i; k++) {
                    addRow(k, i, -m[k][j] / m[i][j]);
                }
            }
        }
    }

    public double[] solveLinearEquation() {
        double[] sol = new double[columns-1];
        for (int i = 0; i < sol.length; i++) {
            sol[i] = Double.NaN;
        }
        return clone().internalSolveLinearEquation(sol);
    }

    // Solves the equation with the extra requirement that some variables are fixed
    // double.NaN means that the value can be anything
    public double[] solveLinearEquation(double[] sol) {
        return clone().internalSolveLinearEquation(sol);
    }

    double[] internalSolveLinearEquation(double[] sol) {
        // Returns null if unsolveable, double.NaN for variables that are not fixed

        // Apply fixed variables
        for (int i = 0; i < sol.length; i++) {
            if (!Double.isNaN(sol[i])) {
                for (int j = 0; j < rows; j++) {
                    m[j][columns-1] -= m[j][i] * sol[i];
                    m[j][i] = 0.0;
                }
            }
        }

        gaussElimination();
        completeGaussElimination();
        int last = columns - 1;
        for (int i = rows - 1; i >= 0; i--) {
            int j = 0;
            while (almostZero(m[i][j]) && j < last)
                j++;
            if (j == last) {
                if (almostZero(m[i][columns - 1]))
                    continue;
                return null;
            }
            double v = m[i][columns - 1];
            boolean free = false;
            for (int k = j + 1; k < columns - 1; k++) {
                if (!almostZero(m[i][k]) && Double.isNaN(sol[k]))
                    free = true;
                else
                    v -= m[i][k] * (Double.isNaN(sol[k]) ? 0 : sol[k]);
            }
            if (!free) {
                if (!Double.isNaN(sol[j]) && !almostZero(sol[j] - v))
                    return null;
                sol[j] = v;
                // Update previous rows

            }
            last = j;
        }

        return sol;
    }

    public int getRank() {
        return clone().internalGetRank();
    }

    public double getDeterminant() {
        if (rows != columns)
            throw new IllegalArgumentException("Matrix must be square.");
        return clone().internalGetDeterminant();
    }

    int internalGetRank() {
        gaussElimination();
        int rank = 0;
        for (int i = 0; i < rows; i++) {
            boolean nonEmpty = false;
            for (int j = 0; j < columns && !nonEmpty; j++) {
                if (!almostZero(m[i][j]))
                    nonEmpty = true;
            }
            if (nonEmpty)
                rank++;
        }
        return rank;
    }

    double internalGetDeterminant() {
        double det = 1;
        for (int i = 0, j = 0; i < rows && j < columns; j++) {
            int maxi = i;
            for (int k = i + 1; k < rows; k++)
                if (Math.abs(m[k][j]) > Math.abs(m[maxi][j]))
                    maxi = k;

            if (almostZero(m[maxi][j]))
                return 0;

            if (i != maxi)
                det = -det;
            switchRows(i, maxi);
            det *= m[i][j];
            multiplyRow(i, 1 / m[i][j]);
            for (int u = i + 1; u < rows; u++)
                addRow(u, i, -m[u][j]);
            i++;
        }
        return det;
    }

    public Matrix getSubMatrix(int r0, int r1, int c0, int c1) {
        if (r1 > rows || c1 > columns)
            throw new IllegalArgumentException("Invalid subsize.");

        Matrix subMatrix = new Matrix(r1-r0, c1-c0);
        for (int i = r0; i < r1; i++)
            for (int j = c0; j < c1; j++)
                subMatrix.set(i - r0, j - c0, m[i][j]);
        return subMatrix;
    }

    public Matrix getAugmentedMatrix(Matrix matrix) {
        if (rows != matrix.rows)
            throw new IllegalArgumentException("Wrong number of rows.");

        Matrix augmentedMatrix = new Matrix(rows, columns + matrix.columns);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++)
                augmentedMatrix.set(i,j, m[i][j]);
            for (int j = 0; j < matrix.columns; j++)
                augmentedMatrix.set(i, j + columns, matrix.get(i,j));
        }
        return augmentedMatrix;
    }

    public Matrix getInverse() {
        if (rows != columns)
            throw new IllegalArgumentException("Matrix must be square.");

        // No check if inverse exists is done here
        Matrix dirty = getAugmentedMatrix(createIdentity(rows));
        dirty.gaussElimination();

        for (int i = rows - 1; i >= 0; i--)
            for (int j = 0; j < i; j++)
                dirty.addRow(j, i, -dirty.get(j, i));

        return dirty.getSubMatrix(0, rows, columns, dirty.columns);
    }

    public boolean equals(Matrix other) {
        if (rows != other.rows || columns != other.columns)
            return false;
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < columns; x++) {
                if (!almostZero(m[y][x] - other.get(y,x)))
                    return false;
            }
        }
        return true;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Matrix))
            return false;
        return equals((Matrix) obj);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            sb.append("[ ");
            for (int j = 0; j < columns; j++) {
                sb.append(String.format("%7.3f", m[i][j]));
                sb.append(" ");
            }
            sb.append("]\n");
        }
        return sb.toString();
    }

    public Matrix clone() {
        return getSubMatrix(0, rows, 0, columns);
    }
}
