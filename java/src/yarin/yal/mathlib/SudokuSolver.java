package yarin.yal.mathlib;

public class SudokuSolver {

  private int[][] board;
  private boolean[][] rowUsed, colUsed, sqUsed;
  private int backTracks = 0;

  public SudokuSolver(String[] start) {
    board = new int[9][9];
    for (int y = 0; y < 9; y++) {
      for (int x = 0; x < 9; x++) {
        char c = start[y].charAt(x);
        if (c >= '1' && c <= '9') {
          board[y][x] = c - '0';
        }
      }
    }
    init();
  }

  public SudokuSolver(int[][] start) {
    board = start.clone();
    init();
  }

  public int getBackTracks() {
    return backTracks;
  }

  private void init() {
    rowUsed = new boolean[9][10];
    colUsed = new boolean[9][10];
    sqUsed = new boolean[9][10];
    for (int y = 0; y < 9; y++) {
      for (int x = 0; x < 9; x++) {
        int d = board[y][x];
        if (d > 0) {
          int sq = (y/3)*3+(x/3);
          rowUsed[y][d] = true;
          colUsed[x][d] = true;
          sqUsed[sq][d] = true;
        }
      }
    }
  }

  public int[][] solve() {
    int bx=0, by=0, best = 100;
    for (int y = 0; y < 9; y++) {
      for (int x = 0; x < 9; x++) {
        if (board[y][x] == 0) {
          int sq = (y/3)*3+(x/3), poss = 0;
          for (int d = 1; d <= 9; d++) {
            if (!(rowUsed[y][d] || colUsed[x][d] || sqUsed[sq][d])) {
              poss++;
            }
          }
          if (poss == 0) {
            backTracks++;
            return null;
          }
          if (poss < best) {
            best = poss;
            bx = x;
            by = y;
          }
        }
      }
    }

    if (best == 100) return board;

    int sq = (by/3)*3+(bx/3);
    for (int d = 1; d <= 9; d++) {
      if (!(rowUsed[by][d] || colUsed[bx][d] || sqUsed[sq][d])) {
        board[by][bx] = d;
        rowUsed[by][d] = true;
        colUsed[bx][d] = true;
        sqUsed[sq][d] = true;

        if (solve() != null) return board;

        board[by][bx] = 0;
        rowUsed[by][d] = false;
        colUsed[bx][d] = false;
        sqUsed[sq][d] = false;
      }
    }

    backTracks++;
    return null;
  }

  public String[] getTextBoard() {
    String[] res = new String[9];
    for (int y = 0; y < 9; y++) {
      StringBuilder sb = new StringBuilder();
      for (int x = 0; x < 9; x++) {
        sb.append((char) ('0' + board[y][x]));
      }
      res[y] = sb.toString();
    }
    return res;
  }

}
