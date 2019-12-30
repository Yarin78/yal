package yarin.yal.mathlib;

public class MatrixInt {
    private final int[][] m;
    private final int rows, columns, mod;

    public int getRows() { return rows; }

    public int getColumns() { return columns; }

    public int get(int y, int x) { return m[y][x]; }

    public void set(int y, int x, int value) { m[y][x] = value; }

    public MatrixInt(int rows, int columns, int mod) {
        this.m = new int[rows][columns];
        this.rows = rows;
        this.columns = columns;
        this.mod = mod;
    }

    public MatrixInt(int[][] m, int mod) {
        this.m = m.clone();
        this.rows = m.length;
        this.columns = m[0].length;
        this.mod = mod;

        fixAll();
    }

    private int fix(long x) {
        x %= mod;
        if (x<0)
            x += mod;
        return (int) x;
    }

    private void fixAll() {
        for (int r = 0; r < rows; r++)
            for (int c = 0; c < columns; c++)
                m[r][c] = fix(m[r][c]);
    }

    public static MatrixInt createIdentity(int size, int mod) {
        MatrixInt m = new MatrixInt(size, size, mod);
        for (int i = 0; i < size; i++)
            m.set(i,i,1);
        return m;
    }

    // Elementary row operations

    public void switchRows(int rowA, int rowB) {
        for (int i = 0; i < columns ; i++) {
            int tmp = m[rowA][i];
            m[rowA][i] = m[rowB][i];
            m[rowB][i] = tmp;
        }
    }

    public void multiplyRow(int row, int factor) {
        factor = fix(factor);

        for (int i = 0; i < columns; i++)
            m[row][i] = fix(((long) m[row][i])*factor);
    }

    public void addRow(int targetRow, int multRow, int factor) {
        factor = fix(factor);
        for (int i = 0; i < columns; i++)
            m[targetRow][i] = fix(m[targetRow][i] + ((long) m[multRow][i])*factor);
    }

    // Operators

    public static interface UnaryTransformation {
        int transform(int x);
    }

    public static interface BinaryTransformation {
        int transform(int x, int y);
    }

    public static MatrixInt mul(MatrixInt a, MatrixInt b)	{
        if (a.getColumns() != b.getRows() || a.mod != b.mod)
            throw new IllegalArgumentException();

        MatrixInt matrix = new MatrixInt(a.getRows(), b.getColumns(), a.mod);
        for (int i = 0; i < a.getRows(); i++) {
            for (int j = 0; j < b.getColumns(); j++) {
                long sum = 0;
                for (int k = 0; k < a.getColumns(); k++)
                    sum += (long) a.get(i,k) * b.get(k,j);
                matrix.set(i,j, (int) (sum % a.mod));
            }
        }
        return matrix;
    }

    private static MatrixInt transform(MatrixInt a, UnaryTransformation func) {
        MatrixInt matrix = new MatrixInt(a.rows, a.columns, a.mod);
        for (int i = 0; i < a.rows; i++)
            for (int j = 0; j < a.columns; j++) {
                int value = func.transform(a.get(i, j));
                value %= a.mod;
                if (value < 0) value += a.mod;
                matrix.set(i,j, value);
            }
        return matrix;
    }

    private static MatrixInt transform(MatrixInt a, MatrixInt b, BinaryTransformation func) {
        if (a.rows != b.rows || a.columns != b.columns || a.mod != b.mod)
            throw new IllegalArgumentException();
        MatrixInt matrix = new MatrixInt(a.rows, a.columns, a.mod);
        for (int i = 0; i < a.rows; i++)
            for (int j = 0; j < a.columns; j++) {
                int value = func.transform(a.get(i, j), b.get(i, j));
                value %= a.mod;
                if (value < 0) value += a.mod;
                matrix.set(i,j, value);
            }
        return matrix;
    }

    public static MatrixInt add(MatrixInt a, MatrixInt b) {
        return transform(a, b, new BinaryTransformation() {
            public int transform(int x, int y) {
                return x + y;
            }
        });
    }

    public static MatrixInt sub(MatrixInt a, MatrixInt b) {
        return transform(a, b, new BinaryTransformation() {
            public int transform(int x, int y) {
                return x - y;
            }
        });
    }

    public static MatrixInt mul(final MatrixInt a, final int b) {
        return transform(a, new UnaryTransformation() {
            public int transform(int x) {
                return (int) ((long) x * b) % a.mod;
            }
        });
    }

    public static MatrixInt add(MatrixInt a, final int b) {
        return transform(a, new UnaryTransformation() {
            public int transform(int x) {
                return x + b;
            }
        });
    }

    public static MatrixInt sub(MatrixInt a, final int b) {
        return transform(a, new UnaryTransformation() {
            public int transform(int x) {
                return x - b;
            }
        });
    }

    // Algorithms

    public int[] gaussElimination() {
        int[] rowPivot = new int[rows];
        for (int i = 0; i < rows; i++)
            rowPivot[i] = i;
        // Turns the matrix into row-echelon form
        for (int i = 0, j = 0; i < rows && j < columns; j++) {
            int maxi = i;
            for (int k = i + 1; k < rows; k++)
                if (m[k][j] > m[maxi][j])
                    maxi = k;

            if (m[maxi][j] != 0) {
                int tmp = rowPivot[i];
                rowPivot[i] = rowPivot[maxi];
                rowPivot[maxi] = tmp;
                switchRows(i, maxi);
                int inv = MathStuff.modInverse(m[i][j], mod);
                multiplyRow(i, inv);
                for (int u = i + 1; u < rows; u++)
                    addRow(u, i, -m[u][j]);
                i++;
            }
        }
        return rowPivot;
    }


    public int[] solveLinearEquation() {
        return clone().internalSolveLinearEquation();
    }

    int[] internalSolveLinearEquation() {
        // Returns null if unsolveable, -1 for free variables (should be treated as 0)

        gaussElimination();
        int[] sol = new int[columns - 1];
        int last = columns - 1;
        for (int i = rows - 1; i >= 0; i--) {
            int j = 0;
            while (m[i][j] == 0 && j < last)
                j++;
            if (j == last) {
                if (m[i][columns-1] == 0)
                    continue;
                return null;
            }
            for (int k = j + 1; k < last; k++)
                sol[k] = -1; // Free variable (multiple solutions)

            long v = m[i][columns - 1];
            for (int k = j + 1; k < columns - 1; k++)
                v = fix(v - (long) m[i][k]*(sol[k] == -1 ? 0 : sol[k]));
            sol[j] = (int) v;
            last = j;
        }

        return sol;
    }

    public int getRank() {
        return clone().internalGetRank();
    }

    public int getDeterminant() {
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
                if (m[i][j] != 0)
                    nonEmpty = true;
            }
            if (nonEmpty)
                rank++;
        }
        return rank;
    }

    int internalGetDeterminant() {
        long det = 1;
        for (int i = 0, j = 0; i < rows && j < columns; j++) {
            int maxi = i;
            for (int k = i + 1; k < rows; k++)
                if (m[k][j] > m[maxi][j])
                    maxi = k;

            if (m[maxi][j] == 0)
                return 0;

            if (i != maxi)
                det = -det;
            switchRows(i, maxi);
            det = fix(det * m[i][j]);
            multiplyRow(i, MathStuff.modInverse(m[i][j], mod));
            for (int u = i + 1; u < rows; u++)
                addRow(u, i, -m[u][j]);
            i++;
        }
        return (int) det;
    }

    public MatrixInt getSubMatrix(int r0, int r1, int c0, int c1) {
        if (r1 > rows || c1 > columns)
            throw new IllegalArgumentException("Invalid subsize.");

        MatrixInt subMatrix = new MatrixInt(r1-r0, c1-c0, mod);
        for (int i = r0; i < r1; i++)
            for (int j = c0; j < c1; j++)
                subMatrix.set(i - r0, j - c0, m[i][j]);
        return subMatrix;
    }

    public MatrixInt getAugmentedMatrix(MatrixInt matrix) {
        if (rows != matrix.rows || mod != matrix.mod)
            throw new IllegalArgumentException("Wrong number of rows.");

        MatrixInt augmentedMatrix = new MatrixInt(rows, columns + matrix.columns, mod);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++)
                augmentedMatrix.set(i,j, m[i][j]);
            for (int j = 0; j < matrix.columns; j++)
                augmentedMatrix.set(i, j + columns, matrix.get(i,j));
        }
        return augmentedMatrix;
    }

    public MatrixInt getInverse() {
        if (rows != columns)
            throw new IllegalArgumentException("Matrix must be square.");

        // No check if inverse exists is done here
        MatrixInt dirty = getAugmentedMatrix(createIdentity(rows, mod));
        dirty.gaussElimination();

        for (int i = rows - 1; i >= 0; i--)
            for (int j = 0; j < i; j++)
                dirty.addRow(j, i, -dirty.get(j,i));

        return dirty.getSubMatrix(0, rows, columns, dirty.columns);
    }

    public boolean equals(MatrixInt other) {
        if (rows != other.rows || columns != other.columns || mod != other.mod)
            return false;
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < columns; x++) {
                if (m[y][x] != other.get(y,x))
                    return false;
            }
        }
        return true;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof MatrixInt))
            return false;
        return equals((MatrixInt) obj);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rows; i++) {
            sb.append("[ ");
            for (int j = 0; j < columns; j++) {
                sb.append(String.format("%5d", m[i][j]));
                sb.append(" ");
            }
            sb.append("]\n");
        }
        return sb.toString();
    }

    public MatrixInt clone() {
        return getSubMatrix(0, rows, 0, columns);
    }
}
