package yarin.yal.mathlib;

import org.junit.Assert;
import org.junit.Test;

public class TestSudokoSolver {

  @Test
  public void test1() {
    String[] input = {
        "060104050",
        "200000001",
        "008305600",
        "800407006",
        "006000300",
        "700901004",
        "500000002",
        "040508070",
        "007206900"
    };
    SudokuSolver solver = new SudokuSolver(input);

    int[][] sol = solver.solve();

    verifySolution(input, sol);
    System.out.println(solver.getBackTracks());
  }

  @Test
  public void test2() {
    String[] input = {
        "6...9.7.4",
        ".8....2..",
        "2...5.3..",
        ".6.2.....",
        ".9.631.4.",
        ".....4.8.",
        "..6.4...1",
        "..1....3.",
        "3.8.1...9"
    };
    SudokuSolver solver = new SudokuSolver(input);

    int[][] sol = solver.solve();

    verifySolution(input, sol);
    System.out.println(solver.getBackTracks());
  }

  @Test
  public void test3() {
    String[] input = {
        ".4..237..",
        "......98.",
        ".....72..",
        ".2..7.63.",
        "8.......2",
        ".37.9..4.",
        "..24.....",
        ".91......",
        "..563..9."
    };
    SudokuSolver solver = new SudokuSolver(input);

    int[][] sol = solver.solve();

    verifySolution(input, sol);
    System.out.println(solver.getBackTracks());
  }

  private void verifySolution(String[] input, int[][] sol) {
    int[][] inp = new int[9][9];

    for (int y = 0; y < 9; y++) {
      for (int x = 0; x < 9; x++) {
        char c = input[y].charAt(x);
        if (c >= '1' && c <= '9') inp[y][x] = c - '0';

        if (inp[y][x] > 0) {
          Assert.assertEquals(inp[y][x], sol[y][x]);
        }
      }
    }

    int rowMask[] = new int[9], colMask[] = new int[9], sqMask[] = new int[9];
    for (int y = 0; y < 9; y++) {
      for (int x = 0; x < 9; x++) {
        int sq = (y/3)*3+x/3;
        int m = 1 << (sol[y][x] - 1);
        rowMask[y] |= m;
        colMask[x] |= m;
        sqMask[sq] |= m;
      }
    }

    for (int i = 0; i < 9; i++) {
      Assert.assertEquals(511, rowMask[i]);
      Assert.assertEquals(511, colMask[i]);
      Assert.assertEquals(511, sqMask[i]);
    }
  }

}
