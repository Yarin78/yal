package yarin.yal.mathlib;

import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

public class TestMatrix {

    private static final double DELTA = 1e-9;

    @Test
    public void testSolveLinearEquation() {
        // Case #1: Standard equation with 3 unknowns, one unique solution
        Matrix lhs = new Matrix(new double[][] { { 2, 1, -1 }, { -3, -1, 2 }, { -2, 1, 2 } });
        Matrix rhs = new Matrix(new double[][] { { 8 }, { -11 }, { -3 } });
        Matrix aug = lhs.getAugmentedMatrix(rhs);

        Assert.assertEquals(3, lhs.getRank());
        Assert.assertEquals(3, aug.getRank());

        double[] res = aug.solveLinearEquation();
        Assert.assertEquals(3, res.length);
        Assert.assertEquals(2, res[0], 1e-8);
        Assert.assertEquals(3, res[1], 1e-8);
        Assert.assertEquals(-1, res[2], 1e-8);

        // Case #2: More equations than necessary, but still unique solution
        lhs = new Matrix(new double[][] { { 4, -1, 3 }, { 7, 0, 2 }, { 9, 4, 0 }, { -2, -5, 4 } });
        rhs = new Matrix(new double[][] { { 10 }, { 39 }, { 75 }, { -49 } });
        aug = lhs.getAugmentedMatrix(rhs);

        Assert.assertEquals(3, lhs.getRank());
        Assert.assertEquals(3, aug.getRank());

        res = aug.solveLinearEquation();
        Assert.assertEquals(3, res.length);
        Assert.assertEquals(7, res[0], 1e-8);
        Assert.assertEquals(3, res[1], 1e-8);
        Assert.assertEquals(-5, res[2], 1e-8);

        // Case #3: Unsolveable
        lhs = new Matrix(new double[][] { { 4, -1, 3 }, { 7, 0, 2 }, { 9, 4, 0 }, { -2, -5, 4 } });
        rhs = new Matrix(new double[][] { { 10 }, { 39 }, { 75 }, { -48 } });
        aug = lhs.getAugmentedMatrix(rhs);

        Assert.assertEquals(3, lhs.getRank());
        Assert.assertEquals(4, aug.getRank()); // Higher rank, no solution!

        res = aug.solveLinearEquation();
        Assert.assertNull(res);

        // Case #4: Multiple solutions
        lhs = new Matrix(new double[][] { { 2, 3, -4 }, { 2, 3, -4 }, { 1, -1, -1 } });
        rhs = new Matrix(new double[][] { { 36 }, { 36 }, { -1 } });
        aug = lhs.getAugmentedMatrix(rhs);

        Assert.assertEquals(2, lhs.getRank());
        Assert.assertEquals(2, aug.getRank()); // Rank is less than number of variables, so multiple solutions

        res = aug.solveLinearEquation();
        Assert.assertTrue(Double.isNaN(res[0]));
        Assert.assertTrue(Double.isNaN(res[1]));
        Assert.assertTrue(Double.isNaN(res[2]));
    }

    @Test
    public void moreTestSolveLinearEquation() {
        Matrix lhs = new Matrix(new double[][] { { 1, -2, 0}, {2, -4, 0}, {1, -2, 1}});
        Matrix rhs = new Matrix(new double[][] { { 3 }, { 6 }, { 4 } });
        Matrix aug = lhs.getAugmentedMatrix(rhs);

        double[] res = aug.solveLinearEquation();
        Assert.assertTrue(Double.isNaN(res[0]));
        Assert.assertTrue(Double.isNaN(res[1]));
        Assert.assertEquals(1, res[2], 1e-8);
    }

    @Test
    public void testApplyFixedVariables() {
        Matrix lhs = new Matrix(new double[][] { { 1, -2, 0}, {2, -4, 0}, {1, -2, 1}});
        Matrix rhs = new Matrix(new double[][] { { 3 }, { 6 }, { 4 } });
        Matrix aug = lhs.getAugmentedMatrix(rhs);

        double[] res = aug.solveLinearEquation(new double[] { 5, Double.NaN, 1});
        Assert.assertEquals(5, res[0], 1e-8);
        Assert.assertEquals(1, res[1], 1e-8);
        Assert.assertEquals(1, res[2], 1e-8);
    }

    @Test
    public void testApplyFixedVariablesNoSolution() {
        Matrix lhs = new Matrix(new double[][] { { 1, -2, 0}, {2, -4, 0}, {1, -2, 1}});
        Matrix rhs = new Matrix(new double[][] { { 3 }, { 6 }, { 4 } });
        Matrix aug = lhs.getAugmentedMatrix(rhs);

        double[] res = aug.solveLinearEquation(new double[] { Double.NaN, Double.NaN, 2});
        Assert.assertNull(res);
    }

    @Test
    public void moreTestSolveLinearEquation2() {
        Matrix lhs = new Matrix(new double[][] {
            { 1, -2, 0, 0, 0},
            { 2, -4, 0, 0, 0},
            { 0,  0, 0, 0, 0},
            { 0,  0, 0, 1, 1},
            { 0,  0, 0, 0, 2}
        });
        Matrix rhs = new Matrix(new double[][] { { 3 }, { 6 }, { 0 }, { 3 }, { 4 }} );
        Matrix aug = lhs.getAugmentedMatrix(rhs);

        double[] res = aug.solveLinearEquation();
        Assert.assertTrue(Double.isNaN(res[0]));
        Assert.assertTrue(Double.isNaN(res[1]));
        Assert.assertTrue(Double.isNaN(res[2]));
        Assert.assertEquals(1, res[3], 1e-8);
        Assert.assertEquals(2, res[4], 1e-8);

        res[0] = 3;
        aug.solveLinearEquation(res);
        Assert.assertEquals(3, res[0], 1e-8);
        Assert.assertEquals(0, res[1], 1e-8);
        Assert.assertTrue(Double.isNaN(res[2]));
        Assert.assertEquals(1, res[3], 1e-8);
        Assert.assertEquals(2, res[4], 1e-8);
    }

    @Test
    public void testRandomEquations() {
        Random random = new Random(0);
        int n = 100;
        for (int caseNo = 0; caseNo < 1000; caseNo++) {
            Matrix matrix = new Matrix(n,n+1);
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n+1; j++) {
                    matrix.set(i, j, random.nextDouble());
                }
            }

            double[] res = matrix.solveLinearEquation();

            for(int i=0;i<n;i++) {
                double sum = 0;
                for(int j=0;j<n;j++) {
                    sum += matrix.get(i,j) * res[j];
                }
                Assert.assertEquals(matrix.get(i, n), sum, 1e-8);
            }
        }
    }

    @Test
    public void testRandomSmallEquationsAndFixFreeVariables() {
        Random random = new Random(0);
        int n = 6, cntFree = 0, cntImp = 0, cntError = 0;
        for (int caseNo = 0; caseNo < 1000; caseNo++) {
            Matrix matrix = new Matrix(n,n+1);
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n+1; j++) {
                    if (random.nextDouble() < 0.2)
                        matrix.set(i, j, random.nextInt(7) - 3);
                }
            }

            double[] res = matrix.solveLinearEquation();
            if (res == null) {
                cntImp++;
            } else {
                boolean free = false;
                for (int i = 0; i < n; i++) {
                    if (Double.isNaN(res[i])) {
                        if (!free) res[i] = -7;
                        free = true;
                    }
                }
                if (free) {
                    cntFree++;
                    if (matrix.solveLinearEquation(res) == null) {
                        cntError++;
//                        System.out.println(matrix);
//                        return;
                        throw new RuntimeException("No solution when assigning free variable!");
                    }
                }
            }
        }
        System.out.println("free  = " + cntFree);
        System.out.println("imp   = " + cntImp);
        System.out.println("error = " + cntError);
    }

    @Test
    public void testBug() {
//        Matrix matrix = new Matrix(new double[][] { { -2, -2, -2, 0}, {-2, 0, 0, 0}, {-2, 0, 0, 0}});
        Matrix matrix = new Matrix(new double[][] { { 1, 1, 1, 0}, {1, 0, 0, 0}, {1, 0, 0, 0}});

        System.out.println(matrix);
        double[] res = matrix.solveLinearEquation(new double[] { Double.NaN, Double.NaN, Double.NaN});
        for (int i = 0; i < 3; i++) {
            System.out.println(res[i]);
        }
    }

    @Test
    public void testNullEquation() {
        Matrix lhs = new Matrix(new double[][] { { 0, 0, 0}, {0, 0, 0}, {0, 0, 0}});
        Matrix rhs = new Matrix(new double[][] { { 0 }, { 0 }, { 0 } });
        Matrix aug = lhs.getAugmentedMatrix(rhs);

        double[] res = aug.solveLinearEquation();
        Assert.assertTrue(Double.isNaN(res[0]));
        Assert.assertTrue(Double.isNaN(res[1]));
        Assert.assertTrue(Double.isNaN(res[2]));

        lhs = new Matrix(new double[][] { { 0, 0, 0}, {0, 4, 0}, {0, 0, 0}});
        rhs = new Matrix(new double[][] { { 0 }, { 1 }, { 0 } });
        aug = lhs.getAugmentedMatrix(rhs);

        res = aug.solveLinearEquation();
        Assert.assertTrue(Double.isNaN(res[0]));
        Assert.assertEquals(0.25, res[1], 1e-8);
        Assert.assertTrue(Double.isNaN(res[2]));
    }

    @Test
    public void testInverse() {
        // Case #1
        Matrix matrix = new Matrix(new double[][] { { 2, -1, 0 }, { -1, 2, -1 }, { 0, -1, 2 } });

        System.out.println(matrix);

        double det = matrix.getDeterminant();
        Assert.assertEquals(4.0, det, DELTA);

        Matrix inverse = matrix.getInverse();
        Matrix expMatrix = new Matrix(new double[][] { { 3.0 / 4, 1.0 / 2, 1.0 / 4 }, { 1.0 / 2, 1, 1.0 / 2 }, { 1.0 / 4, 1.0 / 2, 3.0 / 4 } });

        Assert.assertEquals(expMatrix, inverse);

        // Case #2
        matrix = new Matrix(new double[][] { { 1, 7, 3 }, { 0, 0, 2 }, { 0, 1, -1 } });
        inverse = matrix.getInverse();
        expMatrix = new Matrix(new double[][] { { 1, -5, -7 }, { 0, 0.5, 1 }, { 0, 0.5, 0 } });

        Assert.assertEquals(expMatrix, inverse);
    }

    @Test
    public void testMatrixMultiplicationAndAddition() {
        Matrix a = new Matrix(new double[][] { { 1, 0, 2 }, { -1, 3, 1 } });
        Matrix b = new Matrix(new double[][] { { 3, 1 }, { 2, 1 }, { 1, 0 } });

        Matrix c = Matrix.mul(a,b);

        Assert.assertEquals(2, c.getRows());
        Assert.assertEquals(2, c.getColumns());

        Assert.assertEquals(5.0, c.get(0, 0), DELTA);
        Assert.assertEquals(1.0, c.get(0, 1), DELTA);
        Assert.assertEquals(4.0, c.get(1, 0), DELTA);
        Assert.assertEquals(2.0, c.get(1, 1), DELTA);

        b = new Matrix(new double[][] { { 5, -2, 3 }, { -2, 1, 0 } });
        c = Matrix.add(a, b);

        Assert.assertEquals(2, c.getRows());
        Assert.assertEquals(3, c.getColumns());

        Assert.assertEquals(6.0, c.get(0,0), DELTA);
        Assert.assertEquals(-2.0, c.get(0,1), DELTA);
        Assert.assertEquals(5.0, c.get(0,2), DELTA);
        Assert.assertEquals(-3.0, c.get(1,0), DELTA);
        Assert.assertEquals(4.0, c.get(1,1), DELTA);
        Assert.assertEquals(1.0, c.get(1,2), DELTA);
    }
}
