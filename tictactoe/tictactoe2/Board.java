package tictactoe2;

public class Board {

  char[][] board;
  int rows, cols;

  public Board(int rows, int cols) {
    this.rows = rows;
    this.cols = cols;

    board = new char[rows][cols];

    for (int row = 0; row < rows; row++) {
      for (int col = 0; col < cols; col++) {
        board[row][col] = App.EMPTY;
      }
    }
  }

  public void print() {
    for (int row = 0; row < rows; row++) {
      for (int col = 0; col < cols; col++) {
        System.out.print(board[row][col]);
        boolean notAtEnd = col != cols - 1;
        if (notAtEnd) {
          System.out.print('|');
        }
      }
      System.out.println();
    }
  }

  public boolean inRange(int row, int col) {
    return 0 <= row && row < rows && 0 <= col && col <= cols;
  }

  public char get(int row, int col) {
    return board[row][col];
  }

  public void set(int row, int col, char value) {
    board[row][col] = value;
  }

  public boolean hasWon(char player) {
    // Check any filled rows.
    for (int rowI = 0; rowI < 3; rowI++) {
      boolean hasRow =
        board[rowI][0] == player &&
        board[rowI][1] == player &&
        board[rowI][2] == player;
      if (hasRow) {
        return true;
      }
    }

    // Columns.
    for (int colI = 0; colI < 3; colI++) {
      boolean hasColumn =
        board[0][colI] == player &&
        board[1][colI] == player &&
        board[2][colI] == player;
      if (hasColumn) {
        return true;
      }
    }

    // Diagonals.
    boolean hasTopLeftDiagonal =
      board[0][0] == player && board[1][1] == player && board[2][2] == player;
    if (hasTopLeftDiagonal) {
      return true;
    }

    boolean hasTopRightDiagonal =
      board[0][2] == player && board[1][1] == player && board[2][0] == player;
    if (hasTopRightDiagonal) {
      return true;
    }

    return false;
  }

  public boolean full() {
    for (int row = 0; row < rows; row++) {
      for (int col = 0; col < cols; col++) {
        if (get(row, col) != App.EMPTY) {
          return false;
        }
      }
    }

    return true;
  }
}
